package si.fri.rso.auctions.catalog.tenancy;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;

import io.agroal.api.AgroalDataSource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import org.junit.jupiter.api.Test;

/** A row written into one tenant's schema is visible to that tenant only. */
@QuarkusTest
class TenantIsolationTest {

  private static final String CODE = "ISOLATION_TEST";

  @Inject AgroalDataSource dataSource;

  @Test
  void categoryOfOneTenantIsInvisibleToAnother() throws SQLException {
    execute(
        "insert into tenant_tech.categories (id, code, name, created_at, updated_at, version)"
            + " values (gen_random_uuid(), '" + CODE + "', 'Samo tech', now(), now(), 0)");
    try {
      given()
          .header(TenantRequestFilter.TENANT_HEADER, "tech")
          .when()
          .get("/api/v1/categories")
          .then()
          .statusCode(200)
          .body("code", hasItem(CODE))
          .body("$", hasSize(4));

      given()
          .header(TenantRequestFilter.TENANT_HEADER, "avto")
          .when()
          .get("/api/v1/categories")
          .then()
          .statusCode(200)
          .body("code", not(hasItem(CODE)))
          .body("$", hasSize(3));
    } finally {
      execute("delete from tenant_tech.categories where code = '" + CODE + "'");
    }
  }

  private void execute(String sql) throws SQLException {
    try (Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement()) {
      statement.execute(sql);
    }
  }
}
