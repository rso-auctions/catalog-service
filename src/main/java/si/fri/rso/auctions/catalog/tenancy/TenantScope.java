package si.fri.rso.auctions.catalog.tenancy;

import io.quarkus.arc.Arc;
import io.quarkus.arc.ManagedContext;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.function.Supplier;

/**
 * Runs code as a given tenant outside an HTTP request, e.g. in NATS consumers or gRPC handlers, where {@link
 * TenantRequestFilter} does not run. Activates a request context if none is active, so {@link TenantContext} and
 * the schema resolver work as they do for REST calls.
 */
@ApplicationScoped
public class TenantScope {

  private final TenantRegistry tenantRegistry;
  private final TenantContext tenantContext;

  public TenantScope(TenantRegistry tenantRegistry, TenantContext tenantContext) {
    this.tenantRegistry = tenantRegistry;
    this.tenantContext = tenantContext;
  }

  /**
   * Calls {@code action} with {@code tenantId} as the current tenant.
   *
   * @throws UnknownTenantException if the tenant is not configured
   */
  public <T> T call(String tenantId, Supplier<T> action) {
    if (!tenantRegistry.isKnown(tenantId)) {
      throw new UnknownTenantException(tenantId);
    }
    ManagedContext requestContext = Arc.container().requestContext();
    boolean activatedHere = !requestContext.isActive();
    if (activatedHere) {
      requestContext.activate();
    }
    String previousTenantId = tenantContext.getTenantId().orElse(null);
    try {
      tenantContext.setTenantId(tenantId);
      return action.get();
    } finally {
      if (activatedHere) {
        requestContext.terminate();
      } else {
        tenantContext.setTenantId(previousTenantId);
      }
    }
  }

  /** Like {@link #call} for code without a result. */
  public void run(String tenantId, Runnable action) {
    call(
        tenantId,
        () -> {
          action.run();
          return null;
        });
  }
}
