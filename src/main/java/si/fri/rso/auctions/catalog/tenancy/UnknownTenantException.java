package si.fri.rso.auctions.catalog.tenancy;

/** A tenant id that is not in {@code rso.tenants.ids}. */
public class UnknownTenantException extends RuntimeException {

  public UnknownTenantException(String tenantId) {
    super("Unknown tenant '" + tenantId + "'");
  }
}
