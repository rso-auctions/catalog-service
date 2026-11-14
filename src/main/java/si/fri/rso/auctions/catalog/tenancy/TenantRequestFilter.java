package si.fri.rso.auctions.catalog.tenancy;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import si.fri.rso.auctions.catalog.lib.dto.ErrorResponse;

/**
 * Reads the tenant from the {@value #TENANT_HEADER} header of every API request and rejects requests without a
 * known tenant. The API gateway overwrites the header from the request host, so clients cannot choose it. Health
 * and OpenAPI endpoints are not JAX-RS resources, so they are not affected.
 */
@Provider
@Priority(Priorities.AUTHENTICATION - 100)
public class TenantRequestFilter implements ContainerRequestFilter {

  public static final String TENANT_HEADER = "X-Tenant-Id";

  private final TenantRegistry tenantRegistry;
  private final TenantContext tenantContext;

  public TenantRequestFilter(TenantRegistry tenantRegistry, TenantContext tenantContext) {
    this.tenantRegistry = tenantRegistry;
    this.tenantContext = tenantContext;
  }

  @Override
  public void filter(ContainerRequestContext request) {
    String tenantId = request.getHeaderString(TENANT_HEADER);
    if (tenantId == null || tenantId.isBlank()) {
      request.abortWith(
          badRequest("TENANT_MISSING", "Header " + TENANT_HEADER + " is required."));
      return;
    }
    if (!tenantRegistry.isKnown(tenantId)) {
      // The header value is not echoed back: it is client input of arbitrary length.
      request.abortWith(badRequest("TENANT_UNKNOWN", "Unknown tenant in " + TENANT_HEADER + "."));
      return;
    }
    tenantContext.setTenantId(tenantId);
  }

  private static Response badRequest(String code, String message) {
    return Response.status(Response.Status.BAD_REQUEST)
        .type(MediaType.APPLICATION_JSON)
        .entity(new ErrorResponse(code, message))
        .build();
  }
}
