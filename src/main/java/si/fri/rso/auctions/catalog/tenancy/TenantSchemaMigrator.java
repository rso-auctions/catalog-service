package si.fri.rso.auctions.catalog.tenancy;

import io.agroal.api.AgroalDataSource;
import io.quarkus.liquibase.LiquibaseFactory;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import org.jboss.logging.Logger;

/**
 * Creates a schema for every configured tenant and applies the Liquibase changelog to it on startup. Quarkus'
 * built-in {@code migrate-at-start} only migrates the default schema, so it is turned off. The HTTP port opens
 * only after this finishes, and a failed migration stops the startup.
 */
@ApplicationScoped
public class TenantSchemaMigrator {

  private static final Logger LOG = Logger.getLogger(TenantSchemaMigrator.class);

  /**
   * Key of the PostgreSQL advisory lock that serialises migrations when several pods start at once. Without it
   * two pods can race on {@code CREATE SCHEMA} and on Liquibase's own lock table.
   */
  private static final long MIGRATION_LOCK_KEY = 7_311_002L;

  private final TenantRegistry tenantRegistry;
  private final AgroalDataSource dataSource;
  private final LiquibaseFactory liquibaseFactory;

  public TenantSchemaMigrator(
      TenantRegistry tenantRegistry, AgroalDataSource dataSource, LiquibaseFactory liquibaseFactory) {
    this.tenantRegistry = tenantRegistry;
    this.dataSource = dataSource;
    this.liquibaseFactory = liquibaseFactory;
  }

  void onStart(@Observes StartupEvent event) throws SQLException, LiquibaseException {
    migrateAll();
  }

  /** Migrates every tenant schema. Safe to run again: Liquibase skips changesets that are already applied. */
  void migrateAll() throws SQLException, LiquibaseException {
    // The lock belongs to this session, so the connection stays open until all tenants are migrated.
    try (Connection lockConnection = dataSource.getConnection();
        Statement lock = lockConnection.createStatement()) {
      lock.execute("SELECT pg_advisory_lock(" + MIGRATION_LOCK_KEY + ")");
      try {
        for (String tenantId : tenantRegistry.ids()) {
          migrate(TenantRegistry.schemaFor(tenantId));
        }
      } finally {
        lock.execute("SELECT pg_advisory_unlock(" + MIGRATION_LOCK_KEY + ")");
      }
    }
  }

  private void migrate(String schema) throws SQLException, LiquibaseException {
    LOG.infof("Migrating schema %s", schema);
    // The schema name is safe to concatenate: TenantsConfig restricts tenant ids to [a-z][a-z0-9_]{0,30}.
    try (Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement()) {
      statement.execute("CREATE SCHEMA IF NOT EXISTS " + schema);
    }
    try (Liquibase liquibase = liquibaseFactory.createLiquibase()) {
      liquibase.getDatabase().setDefaultSchemaName(schema);
      liquibase.getDatabase().setLiquibaseSchemaName(schema);
      try {
        liquibase.update(liquibaseFactory.createContexts(), liquibaseFactory.createLabels());
      } finally {
        resetSearchPath(liquibase);
      }
    }
  }

  /**
   * Liquibase points the connection's search path at the tenant schema. The pool does not reset it, so it is reset
   * here; otherwise raw JDBC with unqualified table names could later hit this tenant's tables.
   */
  private static void resetSearchPath(Liquibase liquibase) throws SQLException {
    if (liquibase.getDatabase().getConnection() instanceof JdbcConnection jdbcConnection) {
      try (Statement statement = jdbcConnection.getUnderlyingConnection().createStatement()) {
        statement.execute("RESET search_path");
      }
    }
  }
}
