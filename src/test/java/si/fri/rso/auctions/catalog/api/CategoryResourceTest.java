package si.fri.rso.auctions.catalog.api;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import si.fri.rso.auctions.catalog.tenancy.TenantRequestFilter;

@QuarkusTest
class CategoryResourceTest {

  @Test
  void returnsSeededCategoriesSortedByName() {
    given()
        .header(TenantRequestFilter.TENANT_HEADER, "avto")
        .when()
        .get("/api/v1/categories")
        .then()
        .statusCode(200)
        .body("name", contains("Elektronika", "Vozila", "Zbirateljstvo"));
  }

  @Test
  void rejectsRequestWithoutTenant() {
    given()
        .when()
        .get("/api/v1/categories")
        .then()
        .statusCode(400)
        .body("code", is("TENANT_MISSING"));
  }

  @Test
  void rejectsUnknownTenant() {
    given()
        .header(TenantRequestFilter.TENANT_HEADER, "unknown")
        .when()
        .get("/api/v1/categories")
        .then()
        .statusCode(400)
        .body("code", is("TENANT_UNKNOWN"));
  }
}
