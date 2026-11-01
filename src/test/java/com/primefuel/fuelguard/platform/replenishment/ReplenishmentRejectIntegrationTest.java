package com.primefuel.fuelguard.platform.replenishment;

import com.primefuel.fuelguard.platform.iam.api.MembershipAccess;
import com.primefuel.fuelguard.platform.iam.api.TenantAccess;
import com.primefuel.fuelguard.platform.replenishment.domain.model.aggregates.ReplenishmentRequest;
import com.primefuel.fuelguard.platform.replenishment.domain.model.valueobjects.ReplenishmentSource;
import com.primefuel.fuelguard.platform.replenishment.domain.model.valueobjects.ReplenishmentStatus;
import com.primefuel.fuelguard.platform.replenishment.domain.repositories.ReplenishmentRequestRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * T09 (TS-15): contrato HTTP de {@code POST /api/replenishment-requests/{id}/reject}.
 *
 * <p>Cubre el camino feliz, la validación del motivo, la propiedad del distribuidor y las transiciones
 * de estado inválidas. Las solicitudes se guardan directo en el repositorio y la identidad del distribuidor
 * se simula con {@link TenantAccess}, igual que en {@link ReplenishmentInboxIntegrationTest}.</p>
 */
@SpringBootTest(properties = {
        "spring.profiles.active=test",
        "spring.datasource.url=jdbc:h2:mem:reject;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "authorization.jwt.secret=0123456789abcdef0123456789abcdef"
})
@AutoConfigureMockMvc
class ReplenishmentRejectIntegrationTest {

    private static final long PROVIDER_ID = 911L;
    private static final String REASON_BODY = "{\"reason\":\"Sin stock disponible\"}";

    @Autowired MockMvc mvc;
    @Autowired ReplenishmentRequestRepository repository;
    @MockitoBean TenantAccess tenantAccess;
    @MockitoBean MembershipAccess membershipAccess;
    @MockitoBean JavaMailSender mail;

    @Test
    void providerRejectsItsPendingRequestAndTheReasonIsStored() throws Exception {
        var request = save(PROVIDER_ID, ReplenishmentStatus.PENDING);
        when(tenantAccess.currentProviderId()).thenReturn(Optional.of(PROVIDER_ID));

        mvc.perform(post("/api/replenishment-requests/{id}/reject", request.getId())
                        .with(user("provider").roles("PROVIDER"))
                        .contentType(MediaType.APPLICATION_JSON).content(REASON_BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(request.getId()))
                .andExpect(jsonPath("$.status").value("REJECTED"))
                .andExpect(jsonPath("$.rejectionReason").value("Sin stock disponible"));

        var stored = repository.findById(request.getId()).orElseThrow();
        assertThat(stored.getStatus()).isEqualTo(ReplenishmentStatus.REJECTED);
        assertThat(stored.getRejectionReason()).isEqualTo("Sin stock disponible");
    }

    @Test
    void blankOrMissingReasonReturns400AndKeepsTheRequestPending() throws Exception {
        var request = save(PROVIDER_ID, ReplenishmentStatus.PENDING);
        when(tenantAccess.currentProviderId()).thenReturn(Optional.of(PROVIDER_ID));

        for (var body : new String[]{"{\"reason\":\"   \"}", "{\"reason\":\"\"}", "{}"}) {
            mvc.perform(post("/api/replenishment-requests/{id}/reject", request.getId())
                            .with(user("provider").roles("PROVIDER"))
                            .contentType(MediaType.APPLICATION_JSON).content(body))
                    .andExpect(status().isBadRequest());
        }

        assertThat(repository.findById(request.getId()).orElseThrow().getStatus())
                .isEqualTo(ReplenishmentStatus.PENDING);
    }

    @Test
    void anotherProviderCannotRejectAndTheRequestStaysPending() throws Exception {
        var request = save(PROVIDER_ID, ReplenishmentStatus.PENDING);
        when(tenantAccess.currentProviderId()).thenReturn(Optional.of(PROVIDER_ID + 1));

        mvc.perform(post("/api/replenishment-requests/{id}/reject", request.getId())
                        .with(user("other-provider").roles("PROVIDER"))
                        .contentType(MediaType.APPLICATION_JSON).content(REASON_BODY))
                .andExpect(status().isForbidden());

        var stored = repository.findById(request.getId()).orElseThrow();
        assertThat(stored.getStatus()).isEqualTo(ReplenishmentStatus.PENDING);
        assertThat(stored.getRejectionReason()).isNull();
    }

    @Test
    void aUserWithoutProviderIdentityCannotReject() throws Exception {
        var request = save(PROVIDER_ID, ReplenishmentStatus.PENDING);
        when(tenantAccess.currentProviderId()).thenReturn(Optional.empty());

        mvc.perform(post("/api/replenishment-requests/{id}/reject", request.getId())
                        .with(user("buyer").roles("BUYER"))
                        .contentType(MediaType.APPLICATION_JSON).content(REASON_BODY))
                .andExpect(status().isForbidden());

        assertThat(repository.findById(request.getId()).orElseThrow().getStatus())
                .isEqualTo(ReplenishmentStatus.PENDING);
    }

    /**
     * El Swagger del endpoint documenta 404 para una solicitud inexistente, pero hoy el controller comprueba
     * primero la propiedad del distribuidor y responde 403. Este test fija el comportamiento real.
     */
    @Test
    void anUnknownRequestIdReturns403BecauseOwnershipIsCheckedFirst() throws Exception {
        when(tenantAccess.currentProviderId()).thenReturn(Optional.of(PROVIDER_ID));

        mvc.perform(post("/api/replenishment-requests/{id}/reject", 987654321L)
                        .with(user("provider").roles("PROVIDER"))
                        .contentType(MediaType.APPLICATION_JSON).content(REASON_BODY))
                .andExpect(status().isForbidden());
    }

    @Test
    void rejectingAnAlreadyRejectedRequestReturns409AndKeepsTheFirstReason() throws Exception {
        var request = save(PROVIDER_ID, ReplenishmentStatus.PENDING);
        when(tenantAccess.currentProviderId()).thenReturn(Optional.of(PROVIDER_ID));
        mvc.perform(post("/api/replenishment-requests/{id}/reject", request.getId())
                        .with(user("provider").roles("PROVIDER"))
                        .contentType(MediaType.APPLICATION_JSON).content(REASON_BODY))
                .andExpect(status().isOk());

        mvc.perform(post("/api/replenishment-requests/{id}/reject", request.getId())
                        .with(user("provider").roles("PROVIDER"))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"reason\":\"Otro motivo\"}"))
                .andExpect(status().isConflict());

        assertThat(repository.findById(request.getId()).orElseThrow().getRejectionReason())
                .isEqualTo("Sin stock disponible");
    }

    @Test
    void aCancelledRequestCannotBeRejected() throws Exception {
        var request = save(PROVIDER_ID, ReplenishmentStatus.CANCELLED);
        when(tenantAccess.currentProviderId()).thenReturn(Optional.of(PROVIDER_ID));

        mvc.perform(post("/api/replenishment-requests/{id}/reject", request.getId())
                        .with(user("provider").roles("PROVIDER"))
                        .contentType(MediaType.APPLICATION_JSON).content(REASON_BODY))
                .andExpect(status().isConflict());

        assertThat(repository.findById(request.getId()).orElseThrow().getStatus())
                .isEqualTo(ReplenishmentStatus.CANCELLED);
    }

    @Test
    void aRejectedRequestCannotBeAcceptedAfterwards() throws Exception {
        var request = save(PROVIDER_ID, ReplenishmentStatus.PENDING);
        when(tenantAccess.currentProviderId()).thenReturn(Optional.of(PROVIDER_ID));
        mvc.perform(post("/api/replenishment-requests/{id}/reject", request.getId())
                        .with(user("provider").roles("PROVIDER"))
                        .contentType(MediaType.APPLICATION_JSON).content(REASON_BODY))
                .andExpect(status().isOk());

        mvc.perform(post("/api/replenishment-requests/{id}/accept", request.getId())
                        .with(user("provider").roles("PROVIDER")))
                .andExpect(status().isConflict());

        var stored = repository.findById(request.getId()).orElseThrow();
        assertThat(stored.getStatus()).isEqualTo(ReplenishmentStatus.REJECTED);
        assertThat(stored.getOrderId()).isNull();
    }

    private ReplenishmentRequest save(Long providerId, ReplenishmentStatus status) {
        var request = new ReplenishmentRequest();
        request.setOrganizationId(501L);
        request.setCustomerAccountId(501L);
        request.setProviderId(providerId);
        request.setFuelProductId(1L);
        request.setQuantity(100);
        request.setUnit("LITRE");
        request.setUnitPrice(4.25);
        request.setStatus(status);
        request.setSource(ReplenishmentSource.MANUAL);
        request.setDeliveryAddress("Lima");
        request.setDeliveryDate(LocalDate.now().plusDays(1));
        return repository.save(request);
    }
}
