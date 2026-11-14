package si.fri.rso.auctions.catalog.tenancy;

import io.quarkus.hibernate.orm.PersistenceUnitExtension;
import io.quarkus.hibernate.orm.runtime.tenant.TenantResolver;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Tells Hibernate which schema to use. With {@code quarkus.hibernate-orm.multitenant=SCHEMA} the returned tenant
 * identifier is the schema name.
 */
@PersistenceUnitExtension
@ApplicationScoped
public class SchemaTenantResolver implements TenantResolver {

  /** Used by Hibernate only at boot, to get a connection for its metadata. Holds no tenant tables. */
  static final String BOOT_SCHEMA = "public";

  private final TenantContext tenantContext;

  public SchemaTenantResolver(TenantContext tenantContext) {
    this.tenantContext = tenantContext;
  }

  @Override
  public String getDefaultTenantId() {
    return BOOT_SCHEMA;
  }

  /**
   * Returns the current tenant's schema. Fails if no tenant is set, so a query can never fall back to a shared
   * schema and read data that is not the caller's.
   */
  @Override
  public String resolveTenantId() {
    return tenantContext
        .getTenantId()
        .map(TenantRegistry::schemaFor)
        .orElseThrow(
            () ->
                new IllegalStateException(
                    "No tenant in context. Use TenantScope for calls outside an HTTP request."));
  }
}
