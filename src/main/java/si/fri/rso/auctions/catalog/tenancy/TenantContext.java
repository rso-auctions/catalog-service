package si.fri.rso.auctions.catalog.tenancy;

import jakarta.enterprise.context.RequestScoped;
import java.util.Optional;

/** Tenant of the current request. Set by {@link TenantRequestFilter}, read by {@link SchemaTenantResolver}. */
@RequestScoped
public class TenantContext {

  private String tenantId;

  public Optional<String> getTenantId() {
    return Optional.ofNullable(tenantId);
  }

  public void setTenantId(String tenantId) {
    this.tenantId = tenantId;
  }
}
