package com.primefuel.fuelguard.platform.replenishment;

import com.primefuel.fuelguard.platform.iam.infrastructure.authorization.sfs.model.UserDetailsImpl;
import com.primefuel.fuelguard.platform.replenishment.domain.model.aggregates.ReplenishmentRequest;
import com.primefuel.fuelguard.platform.replenishment.domain.model.valueobjects.ReplenishmentSource;
import com.primefuel.fuelguard.platform.replenishment.domain.model.valueobjects.ReplenishmentStatus;
import com.primefuel.fuelguard.platform.replenishment.domain.repositories.ReplenishmentRequestRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * T09 (TS-15): reglas de acceso de {@code POST /api/replenishment-requests/{id}/accept} con la identidad
 * real del usuario. El camino feliz y la creación de la orden ya los cubre
 * {@link ReplenishmentAcceptanceIntegrationTest}; aquí se fija quién no puede aceptar y que, en ese caso,
 * la solicitud queda intacta.
 */
@SpringBootTest(properties = {
        "spring.profiles.active=test",
        "spring.datasource.url=jdbc:h2:mem:accept_access;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "authorization.jwt.secret=0123456789abcdef0123456789abcdef"
})
@AutoConfigureMockMvc
class ReplenishmentAcceptAccessIntegrationTest {

    private static final long PROVIDER_ID = 9601L;

    @Autowired MockMvc mvc;
    @Autowired ReplenishmentRequestRepository repository;
    @MockitoBean JavaMailSender mail;

    @Test
    void anotherProviderCannotAcceptAndTheRequestStaysPending() throws Exception {
        var request = savePending(PROVIDER_ID);

        mvc.perform(post("/api/replenishment-requests/{id}/accept", request.getId())
                        .with(principal(9602L, null, PROVIDER_ID + 1, "ROLE_PROVIDER")))
                .andExpect(status().isForbidden());

        assertUntouched(request.getId());
    }

    /**
     * El Swagger del endpoint declara 404 para una solicitud inexistente, pero el controller comprueba
     * primero que la solicitud sea del distribuidor y responde 403. Este test fija el comportamiento real.
     */
    @Test
    void anUnknownRequestIdReturns403BecauseOwnershipIsCheckedFirst() throws Exception {
        mvc.perform(post("/api/replenishment-requests/{id}/accept", 987654321L)
                        .with(principal(PROVIDER_ID, null, PROVIDER_ID, "ROLE_PROVIDER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void buyerCannotAcceptEvenIfItsPrincipalCarriesTheProviderId() throws Exception {
        var request = savePending(PROVIDER_ID);

        mvc.perform(post("/api/replenishment-requests/{id}/accept", request.getId())
                        .with(principal(9603L, 501L, PROVIDER_ID, "ROLE_BUYER")))
                .andExpect(status().isForbidden());

        assertUntouched(request.getId());
    }

    @Test
    void providerRoleWithoutProviderIdentityCannotAccept() throws Exception {
        var request = savePending(PROVIDER_ID);

        mvc.perform(post("/api/replenishment-requests/{id}/accept", request.getId())
                        .with(principal(9604L, null, null, "ROLE_PROVIDER")))
                .andExpect(status().isForbidden());

        assertUntouched(request.getId());
    }

    @Test
    void unauthenticatedRequestGets401() throws Exception {
        var request = savePending(PROVIDER_ID);

        mvc.perform(post("/api/replenishment-requests/{id}/accept", request.getId()))
                .andExpect(status().isUnauthorized());

        assertUntouched(request.getId());
    }

    private void assertUntouched(Long requestId) {
        var stored = repository.findById(requestId).orElseThrow();
        assertThat(stored.getStatus()).isEqualTo(ReplenishmentStatus.PENDING);
        assertThat(stored.getOrderId()).isNull();
        assertThat(stored.isAcceptanceConsumed()).isFalse();
    }

    private static RequestPostProcessor principal(long userId, Long companyId, Long providerId, String role) {
        var details = new UserDetailsImpl(userId, "user-" + userId, "encoded", companyId, providerId,
                List.of(new SimpleGrantedAuthority(role)));
        return SecurityMockMvcRequestPostProcessors.authentication(
                new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities()));
    }

    private ReplenishmentRequest savePending(Long providerId) {
        var request = new ReplenishmentRequest();
        request.setOrganizationId(501L);
        request.setCustomerAccountId(501L);
        request.setProviderId(providerId);
        request.setFuelProductId(1L);
        request.setQuantity(100);
        request.setUnit("LITRE");
        request.setUnitPrice(4.25);
        request.setStatus(ReplenishmentStatus.PENDING);
        request.setSource(ReplenishmentSource.MANUAL);
        request.setDeliveryAddress("Lima");
        request.setDeliveryDate(LocalDate.now().plusDays(1));
        return repository.save(request);
    }
}
