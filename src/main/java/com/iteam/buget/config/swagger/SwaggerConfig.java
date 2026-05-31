package com.iteam.buget.config.swagger;
/**
 * SWAGGER TESTING GUIDE
 * ─────────────────────────────────────────────────────────────────
 * 1. Start the app:   mvn spring-boot:run
 * 2. Open:            http://localhost:8081/swagger-ui.html
 *
 * ADMIN CREDENTIALS (seeded on first startup):
 *    email:    admin@budget.app
 *    password: Admin@1234
 *
 * LOGIN & AUTHORIZE:
 * 3. Call POST /api/v1/auth/login  → copy "accessToken" from response
 * 4. Click the green "Authorize 🔒" button at the top of the page
 * 5. Paste the token (WITHOUT "Bearer ") → click Authorize → Close
 * 6. All subsequent requests will include  Authorization: Bearer <token>
 *
 * HAPPY PATH TEST SEQUENCE:
 *  a) POST /api/v1/auth/register              → create a new user
 *  b) POST /api/v1/auth/login                 → login as admin, authorize
 *  c) POST /api/v1/admin/users/{id}/validate  → validate the new user
 *  d) POST /api/v1/auth/login                 → login as new user, authorize
 *  e) GET  /api/v1/categories                 → see default categories
 *  f) POST /api/v1/budgets                    → create a budget
 *  g) POST /api/v1/transactions               → add incomes / expenses
 *  h) GET  /api/v1/dashboard/{budgetId}       → full dashboard stats
 *  i) GET  /api/v1/alerts/budget/{budgetId}   → check triggered alerts
 * ─────────────────────────────────────────────────────────────────
 */


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, jwtSecurityScheme()));
    }

    private Info apiInfo() {
        return new Info()
                .title("Budget App API")
                .version("v1.0")
                .description("""
                        Collaborative personal finance management API.
                        
                        Allows users to track income & expenses, manage shared budgets,
                        set category ceilings and receive real-time budget alerts.
                        
                        **Authentication:** All protected endpoints require a JWT Bearer token.
                        Use `POST /api/v1/auth/login` to obtain a token, then click
                        the **Authorize 🔒** button and paste it (without "Bearer ").
                        """)
                .contact(new Contact()
                        .name("iTeam")
                        .email("contact@iteam.com"));
    }

    private SecurityScheme jwtSecurityScheme() {
        return new SecurityScheme()
                .name(SECURITY_SCHEME_NAME)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("Paste your JWT access token here (without the 'Bearer ' prefix).");
    }
}