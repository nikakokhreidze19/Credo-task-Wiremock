import data.dataProvider.UserTestData;
import io.restassured.RestAssured;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import utils.WireMockUtils;
import java.util.List;
import static io.restassured.RestAssured.given;

public class UserTest {
    private static final Logger logger = LogManager.getLogger(UserTest.class);

    @BeforeSuite
    void setUp() {
        WireMockUtils.startWireMock();
        WireMockUtils.registerStubs(List.of(
                "get-users.json",
                "get-users-bad-request.json"));
    }

    @AfterSuite
    void tearDown() {
        WireMockUtils.stopWireMock();
    }

    @Test(dataProvider = "Stubs", dataProviderClass = UserTestData.class)
    void getUsers(String endpoint, int expectedStatus) {
        logger.info("validating that endpoint:{} returns statusCode:{}",endpoint,expectedStatus);

        given()
                .when()
                .get(RestAssured.baseURI + endpoint)
                .then()
                .statusCode(expectedStatus)
                .extract()
                .response().then().log().all();
    }
}
