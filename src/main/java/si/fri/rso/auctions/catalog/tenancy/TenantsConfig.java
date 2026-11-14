package si.fri.rso.auctions.catalog.tenancy;

import io.smallrye.config.ConfigMapping;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import java.util.List;

/**
 * Tenants (auction houses) served by this instance. Ids end up in schema names ({@code tenant_<id>}), so they are
 * restricted to characters that are safe there.
 */
@ConfigMapping(prefix = "rso.tenants")
public interface TenantsConfig {

  /** Ids of all tenants, e.g. {@code avto,tech}. */
  @NotEmpty
  List<@Pattern(regexp = "[a-z][a-z0-9_]{0,30}") String> ids();
}
