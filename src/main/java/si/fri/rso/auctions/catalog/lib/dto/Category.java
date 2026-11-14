package si.fri.rso.auctions.catalog.lib.dto;

import java.util.UUID;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/** Item category as exposed through the API. */
public record Category(
    @Schema(required = true, examples = "8e3d4f50-483a-44d5-8b21-dab0b533e64b") UUID id,
    @Schema(required = true, examples = "VEHICLES") String code,
    @Schema(required = true, examples = "Vozila") String name,
    @Schema(examples = "Avtomobili, motorji in druga vozila") String description) {}
