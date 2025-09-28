package data.hardData;

public class WiremockData {
    public static final String
            CONTAINER_NAME = "wiremock", START_DOCKER_COMMAND = "docker run --rm -d --name %s -p 8080:8080 -v mappings:/home/wiremock wiremock/wiremock:2.35.0",
            DOCKER_STOP_COMMAND = "docker stop wiremock";
}
