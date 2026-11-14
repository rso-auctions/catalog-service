package si.fri.rso.auctions.catalog.tenancy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import io.agroal.api.AgroalDataSource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import liquibase.exception.LiquibaseException;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;

@QuarkusTest
class TenantSchemaMigratorTest {

  @Inject TenantRegistry tenantRegistry;
  @Inject TenantSchemaMigrator migrator;
  @Inject AgroalDataSource dataSource;
  @Inject SessionFactory sessionFactory;

  @Test
  void everyTenantHasItsOwnMigratedSchema() throws SQLException {
    for (String tenantId : tenantRegistry.ids()) {
      assertEquals(3, countCategories(tenantId), "seed categories in tenant " + tenantId);
    }
  }

  @Test
  void entitiesMatchTheMigratedSchemaOfEveryTenant() {
    // Replaces schema-management=validate, which cannot work with schema-per-tenant: it runs at boot, before the
    // tenant schemas exist, and against the default schema.
    for (String tenantId : tenantRegistry.ids()) {
      sessionFactory.getSchemaManager().forSchema(TenantRegistry.schemaFor(tenantId)).validateMappedObjects();
    }
  }

  @Test
  void migratingAgainChangesNothing() throws SQLException, LiquibaseException {
    migrator.migrateAll();

    for (String tenantId : tenantRegistry.ids()) {
      assertEquals(3, countCategories(tenantId), "seed categories in tenant " + tenantId);
    }
  }

  @Test
  void bootSchemaHoldsNoTenantTables() throws SQLException {
    try (Connection connection = dataSource.getConnection();
        PreparedStatement statement =
            connection.prepareStatement(
                "select 1 from information_schema.tables where table_schema = ? and table_name = ?")) {
      statement.setString(1, SchemaTenantResolver.BOOT_SCHEMA);
      statement.setString(2, "categories");
      try (ResultSet result = statement.executeQuery()) {
        assertFalse(result.next());
      }
    }
  }

  private int countCategories(String tenantId) throws SQLException {
    try (Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement();
        ResultSet result =
            statement.executeQuery(
                "select count(*) from " + TenantRegistry.schemaFor(tenantId) + ".categories")) {
      result.next();
      return result.getInt(1);
    }
  }
}
