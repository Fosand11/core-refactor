package org.milianz.inmomarketbackend.Config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de OpenAPI/Swagger para la documentación de la API.
 *
 * Esta configuración define la información general de la API, esquemas de seguridad
 * y servidores disponibles.
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "InmoMarket API",
                version = "1.0.0",
                description = """
                        API REST para la plataforma InmoMarket - Sistema de publicación y gestión de propiedades inmobiliarias.

                        ## Características principales:
                        - Autenticación y registro de usuarios con JWT
                        - Gestión de publicaciones de propiedades
                        - Sistema de favoritos para usuarios
                        - Sistema de reportes con gestión administrativa
                        - Gestión de perfiles de usuario con imágenes
                        - Filtros avanzados de búsqueda de propiedades

                        ## Autenticación:
                        La mayoría de los endpoints requieren autenticación mediante JWT Bearer token.
                        Primero debe registrarse o iniciar sesión para obtener un token.
                        """,
                contact = @Contact(
                        name = "InmoMarket Team",
                        email = "support@inmomarket.com"
                )
        ),
        servers = {
                @Server(
                        description = "Local Development Server",
                        url = "http://localhost:8080"
                ),
                @Server(
                        description = "Production Server",
                        url = "https://api.inmomarket.com"
                )
        }
)
@SecurityScheme(
        name = "Bearer Authentication",
        description = "JWT Bearer token authentication. Obtenga el token mediante /api/auth/signin y úselo en el formato: Bearer {token}",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenAPIConfig {
}
