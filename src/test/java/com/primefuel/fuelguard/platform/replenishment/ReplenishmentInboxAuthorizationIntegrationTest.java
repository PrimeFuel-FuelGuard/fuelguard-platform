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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * T09 (TS-15): {@code GET /api/replenishment-requests/inbox} con la identidad real del usuario.
 *
 * <p>A diferencia de {@link ReplenishmentInboxIntegrationTest}, aquí {@code TenantAccess} no se simula:
 * el distribuidor sale del principal autenticado, así que se prueba la regla real de roles. También
 * fija el contrato JSON completo de cada elemento, la lista vacía y el acceso sin autenticar.</p>
 */
@SpringBootTest(properties = {
        "spring.profiles.active=test",
        "spring.datasource.url=jdbc:h2:mem:inbox_authz;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "authorization.jwt.secret=0123456789abcdef0123456789abcdef"
})
@AutoConfigureMockMvc
class ReplenishmentInboxAuthorizationIntegrationTest {

    private static final String INBOX = "/api/replenishment-requests/inbox";

    @Autowired MockMvc mvc;
    @Autowired ReplenishmentRequestRepository repository;
    @MockitoBean JavaMailSender mail;

    @Test
    void providerWithoutRequestsGetsAnEmptyArray() throws Exception {
        mvc.perform(get(INBOX).with(principal(9101L, null, 9101L, "ROLE_PROVIDER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void providerSeesOnlyItsOwnRequestsUsingTheAuthenticatedIdentity() throws Exception {
        var own = save(9201L, 9202L, ReplenishmentStatus.PENDING);
        save(9201L, 9203L, ReplenishmentStatus.PENDING);

        mvc.perform(get(INBOX).with(principal(9202L, null, 9202L, "ROLE_PROVIDER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(own.getId()))
                .andExpect(jsonPath("$[0].providerId").value(9202));
    }

    @Test
    void inboxItemExposesTheFullRequestContract() throws Exception {
        var request = save(9301L, 9302L, ReplenishmentStatus.PENDING);

        mvc.perform(get(INBOX).with(principal(9302L, null, 9302L, "ROLE_PROVIDER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(request.getId()))
                .andExpect(jsonPath("$[0].organizationId").value(9301))
                .andExpect(jsonPath("$[0].customerAccountId").value(9301))
                .andExpect(jsonPath("$[0].providerId").value(9302))
                .andExpect(jsonPath("$[0].fuelProductId").value(1))
                .andExpect(jsonPath("$[0].quantity").value(100.0))
                .andExpect(jsonPath("$[0].unit").value("LITRE"))
                .andExpect(jsonPath("$[0].unitPrice").value(4.25))
                .andExpect(jsonPath("$[0].status").value("PENDING"))
                .andExpect(jsonPath("$[0].source").value("MANUAL"))
                .andExpect(jsonPath("$[0].rejectionReason").doesNotExist())
                .andExpect(jsonPath("$[0].orderId").doesNotExist())
                .andExpect(jsonPath("$[0].deliveryAddress").value("Lima"))
                .andExpect(jsonPath("$[0].deliveryDate").value(request.getDeliveryDate().toString()))
                .andExpect(jsonPath("$[0].version").isNumber());
    }

    @Test
    void buyerCannotReadTheInboxEvenIfItsPrincipalCarriesAProviderId() throws Exception {
        save(9401L, 9402L, ReplenishmentStatus.PENDING);

        mvc.perform(get(INBOX).with(principal(9403L, 9401L, null, "ROLE_BUYER")))
                .andExpect(status().isForbidden());
        mvc.perform(get(INBOX).with(principal(9404L, 9401L, 9402L, "ROLE_BUYER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void providerRoleWithoutProviderIdentityGets403() throws Exception {
        save(9501L, 9502L, ReplenishmentStatus.PENDING);

        mvc.perform(get(INBOX).with(principal(9503L, null, null, "ROLE_PROVIDER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void unauthenticatedRequestGets401() throws Exception {
        mvc.perform(get(INBOX)).andExpect(status().isUnauthorized());
    }

    private static RequestPostProcessor principal(long userId, Long companyId, Long providerId, String role) {
        var details = new UserDetailsImpl(userId, "user-" + userId, "encoded", companyId, providerId,
                List.of(new SimpleGrantedAuthority(role)));
        return SecurityMockMvcRequestPostProcessors.authentication(
                new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities()));
    }

    private ReplenishmentRequest save(Long organization, Long provider, ReplenishmentStatus status) {
        var request = new ReplenishmentRequest();
        request.setOrganizationId(organization);
        request.setCustomerAccountId(organization);
        request.setProviderId(provider);
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
