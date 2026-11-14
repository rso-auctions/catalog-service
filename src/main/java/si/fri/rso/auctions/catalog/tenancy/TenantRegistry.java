package si.fri.rso.auctions.catalog.tenancy;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

/**
 * Known tenants and their schemas. Application-scoped, so beans created early (JAX-RS filters are built during
 * static init) get a lazy proxy and the runtime configuration is read only on first use.
 */
@ApplicationScoped
public class TenantRegistry {

  private final TenantsConfig tenantsConfig;

  public TenantRegistry(TenantsConfig tenantsConfig) {
    this.tenantsConfig = tenantsConfig;
  }

  public List<String> ids() {
    return tenantsConfig.ids();
  }

  public boolean isKnown(String tenantId) {
    return tenantsConfig.ids().contains(tenantId);
  }

  /** Database schema that holds the tenant's data. */
  public static String schemaFor(String tenantId) {
    return "tenant_" + tenantId;
  }
}
