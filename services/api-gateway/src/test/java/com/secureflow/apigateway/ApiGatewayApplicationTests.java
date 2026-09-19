package com.secureflow.apigateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ApiGatewayApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoads() {
    }

    @Test
    void protectedEndpointWithoutJwtReturns401() throws Exception {
        mockMvc.perform(
                get("/api/auth/me")
        ).andExpect(
                status().isUnauthorized()
        );
    }

    @Test
    void protectedEndpointWithInvalidJwtReturns401() throws Exception {
        mockMvc.perform(
                get("/api/auth/me")
                        .header(
                                "Authorization",
                                "Bearer invalid.jwt.token"
                        )
        ).andExpect(
                status().isUnauthorized()
        );
    }

    @Test
    void protectedEndpointWithAuthenticatedJwtIsNotRejectedBySecurity()
            throws Exception {

        int status = mockMvc.perform(
                        get("/api/auth/me")
                                .with(jwt())
                ).andReturn()
                .getResponse()
                .getStatus();

        // Authentication must not be rejected.
        // MockMvc does not execute the downstream Gateway route.
        assert status != 401;
    }
}