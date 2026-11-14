package si.fri.rso.auctions.catalog;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
class HealthEndpointTest {

  @Test
  void livenessIsUp() {
    given().when().get("/q/health/live").then().statusCode(200).body("status", is("UP"));
  }

  @Test
  void readinessIsUp() {
    given().when().get("/q/health/ready").then().statusCode(200).body("status", is("UP"));
  }

  @Test
  void openApiDocumentIsServed() {
    given().when().get("/q/openapi").then().statusCode(200);
  }
}
