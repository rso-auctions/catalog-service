package si.fri.rso.auctions.catalog.api;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.contains;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
class CategoryResourceTest {

  @Test
  void returnsSeededCategoriesSortedByName() {
    given()
        .when()
        .get("/api/v1/categories")
        .then()
        .statusCode(200)
        .body("name", contains("Elektronika", "Vozila", "Zbirateljstvo"));
  }
}
