package utils;
import data.hardData.WiremockData;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import static io.restassured.RestAssured.given;

public class WireMockUtils {
    private static final Logger logger = LogManager.getLogger(WireMockUtils.class);

    public static void startWireMock() {
        try {
            logger.info("Starting WireMock Docker container...");

            String command = String.format(
                    WiremockData.START_DOCKER_COMMAND,
                    WiremockData.CONTAINER_NAME
            );
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();

            logger.debug("Docker command executed: {}", command);
            Thread.sleep(5000);

            RestAssured.baseURI = "http://localhost:8080";
            given()
                    .post("http://localhost:8080/__admin/mappings/reset")
                    .then()
                    .statusCode(200);
            logger.info("WireMock started and mappings reset successfully.");

        } catch (IOException | InterruptedException e) {
            logger.error("Error while starting WireMock Docker: {}", e.getMessage(), e);
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
        } catch (Exception e) {
            logger.error("Unexpected exception while starting WireMock Docker: {}", e.getMessage(), e);
        }
    }

    public static void registerStubs(List<String> jsonFileNames) {
        String projectRoot = System.getProperty("user.dir");

        for (String fileName : jsonFileNames) {
            try {
                Path filePath = Paths.get(projectRoot, "src", "main", "resources",
                        "mappings", fileName);

                logger.info("Registering stub from file: {}", fileName);

                String stubJson = Files.readString(filePath);

                Response response = given()
                        .header("Content-Type", "application/json")
                        .body(stubJson)
                        .post("http://localhost:8080/__admin/mappings");

                if (response.statusCode() == 201) {
                    logger.info("Stub [{}] registered successfully.", fileName);
                } else {
                    logger.warn("Stub [{}] registration failed. Status code: {}", fileName, response.statusCode());
                }
            } catch (Exception e) {
                logger.error("Failed to register stub [{}]: {}", fileName, e.getMessage(), e);
            }
        }
    }

    public static void stopWireMock() {
        try {
            logger.info("Stopping WireMock Docker container...");

            Process stop = Runtime.getRuntime().exec(WiremockData.DOCKER_STOP_COMMAND);
            stop.waitFor();

            logger.info("WireMock Docker container stopped.");
        } catch (IOException | InterruptedException e) {
            logger.error("Error while stopping WireMock Docker: {}", e.getMessage(), e);
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
        } catch (Exception e) {
            logger.error("Unexpected error while stopping WireMock Docker: {}", e.getMessage(), e);
        }
    }
}
