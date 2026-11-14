package si.fri.rso.auctions.catalog.api.specs;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import si.fri.rso.auctions.catalog.api.specs.common.TenantApiResponses;
import si.fri.rso.auctions.catalog.lib.dto.Category;

/** API spec for item categories. */
@Tag(name = "Categories", description = "Item categories.")
@Path("/api/v1/categories")
@Produces(MediaType.APPLICATION_JSON)
public interface CategoryApiSpec {

  @Operation(summary = "List categories", description = "Returns all categories sorted by name.")
  @TenantApiResponses
  @APIResponse(
      responseCode = "200",
      description = "All categories",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON,
              schema = @Schema(type = SchemaType.ARRAY, implementation = Category.class)))
  @GET
  List<Category> getAll();
}
