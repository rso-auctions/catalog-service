package si.fri.rso.auctions.catalog.api.specs.common;

import io.quarkus.smallrye.openapi.OpenApiFilter;
import org.eclipse.microprofile.openapi.OASFactory;
import org.eclipse.microprofile.openapi.OASFilter;
import org.eclipse.microprofile.openapi.models.OpenAPI;
import org.eclipse.microprofile.openapi.models.parameters.Parameter;
import org.eclipse.microprofile.openapi.models.parameters.Parameter.In;
import org.eclipse.microprofile.openapi.models.media.Schema.SchemaType;
import si.fri.rso.auctions.catalog.tenancy.TenantRequestFilter;

/**
 * Documents the required tenant header on every API operation, the ones {@link TenantRequestFilter} guards.
 * MicroProfile {@code @Parameter} cannot be used as a meta-annotation, so it is added here once.
 */
@OpenApiFilter(OpenApiFilter.RunStage.BUILD)
public class TenantHeaderOpenApiFilter implements OASFilter {

  @Override
  public void filterOpenAPI(OpenAPI openApi) {
    if (openApi.getPaths() == null || openApi.getPaths().getPathItems() == null) {
      return;
    }
    openApi.getPaths().getPathItems().entrySet().stream()
        .filter(entry -> entry.getKey().contains("/api/"))
        .flatMap(entry -> entry.getValue().getOperations().values().stream())
        .forEach(operation -> operation.addParameter(tenantHeader()));
  }

  private static Parameter tenantHeader() {
    return OASFactory.createParameter()
        .name(TenantRequestFilter.TENANT_HEADER)
        .in(In.HEADER)
        .required(true)
        .description("Tenant (auction house). Set by the API gateway from the request host.")
        .example("avto")
        .schema(OASFactory.createSchema().addType(SchemaType.STRING));
  }
}
