package si.fri.rso.auctions.catalog.api.specs.common;

import jakarta.ws.rs.core.MediaType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import si.fri.rso.auctions.catalog.lib.dto.ErrorResponse;

/**
 * Error responses shared by every tenant-scoped operation. The tenant header itself is added to the OpenAPI
 * document by {@link TenantHeaderOpenApiFilter}.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@APIResponse(
    responseCode = "400",
    description = "Missing (TENANT_MISSING) or unknown (TENANT_UNKNOWN) tenant",
    content =
        @Content(
            mediaType = MediaType.APPLICATION_JSON,
            schema = @Schema(implementation = ErrorResponse.class)))
public @interface TenantApiResponses {}
