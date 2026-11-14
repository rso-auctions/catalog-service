package si.fri.rso.auctions.catalog.tenancy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.quarkus.arc.Arc;
import io.quarkus.arc.ManagedContext;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import si.fri.rso.auctions.catalog.service.CategoryService;

/** The path NATS consumers and gRPC handlers will take: no HTTP request, tenant set by {@link TenantScope}. */
@QuarkusTest
class TenantScopeTest {

  @Inject TenantScope tenantScope;
  @Inject CategoryService categoryService;

  @Test
  void runsServiceAsTenantWithoutHttpRequest() {
    assertEquals(3, tenantScope.call("tech", () -> categoryService.getAll()).size());
  }

  @Test
  void rejectsUnknownTenant() {
    assertThrows(UnknownTenantException.class, () -> tenantScope.call("unknown", categoryService::getAll));
  }

  @Test
  void queryWithoutTenantFailsInsteadOfFallingBack() {
    ManagedContext requestContext = Arc.container().requestContext();
    requestContext.activate();
    try {
      assertThrows(RuntimeException.class, () -> categoryService.getAll());
    } finally {
      requestContext.terminate();
    }
  }
}
