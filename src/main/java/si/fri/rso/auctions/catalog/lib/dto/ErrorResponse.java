package si.fri.rso.auctions.catalog.lib.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

/** Error body of API responses with a 4xx status. */
public record ErrorResponse(
    @Schema(required = true, examples = "TENANT_UNKNOWN") String code,
    @Schema(required = true, examples = "Unknown tenant 'xyz'.") String message) {}
