package com.odigeo.interview.coding.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.odigeo.interview.coding.config.TestConfig;
import com.odigeo.interview.coding.domain.contract.*;
import com.odigeo.interview.coding.exception.handler.ExceptionResponse;
import com.odigeo.interview.coding.util.ShipDeploymentBuilder;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GameControllerTest {

    private static final String PLAYER_COMPUTER = "Computer";
    private final static String PLAYER_ONE = "Player1";
    private static ObjectMapper objectMapper;
    private static String gameId;

    private static RequestSpecification initSpecification(String url) {
        return new RequestSpecBuilder()
                .addHeader(TestConfig.HEADER_PARAM_ORIGIN, "http://localhost")
                .setBasePath(TestConfig.BASE_URL_GAME_CONTROLLER.concat(url))
                .setPort(TestConfig.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();
    }

    @BeforeAll
    public static void init() {
        objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    @Test
    @Order(1)
    public void testStartNewGame() throws JsonProcessingException {

        GameStartCommand gameStartCommand = new GameStartCommand();
        gameStartCommand.setPlayerId(PLAYER_ONE);
        gameStartCommand.setVsComputer(true);

        var content = RestAssured.given().spec(initSpecification("/new"))
                .contentType(TestConfig.CONTENT_TYPE_JSON)
                .body(gameStartCommand)
                .when()
                .post()
                .then()
                .statusCode(201)
                .extract()
                .body()
                .asString();

        GameResponse gameResponse = objectMapper.readValue(content, GameResponse.class);
        gameId = gameResponse.getId();

        assertNotNull(gameResponse);
        assertNotNull(gameResponse.getId());
    }

    @Test
    @Order(21)
    public void testComputerJoinGame() {

        GameJoinCommand gameJoinCommand = new GameJoinCommand();
        gameJoinCommand.setPlayerId(PLAYER_COMPUTER);

        var content = given().spec(initSpecification("/{gameId}/join"))
                .contentType(TestConfig.CONTENT_TYPE_JSON)
                .pathParam("gameId", gameId)
                .body(gameJoinCommand)
                .when()
                .post()
                .then()
                .assertThat().statusCode(is(200));
    }

    @Test
    @Order(30)
    public void testPlayerOneDeployShips() {
        DeployShipsCommand deployShipsCommand = new DeployShipsCommand();
        deployShipsCommand.setPlayerId(PLAYER_ONE);
        deployShipsCommand.setShipsDeploy(ShipDeploymentBuilder.buildShipsDeployment());

        var content = given().spec(initSpecification("/{gameId}/fields/ships/deploy"))
                .contentType(TestConfig.CONTENT_TYPE_JSON)
                .pathParam("gameId", gameId)
                .body(deployShipsCommand)
                .when()
                .post()
                .then()
                .assertThat().statusCode(is(200));
    }

    @Test
    @Order(31)
    public void testPlayerOneFireThrowsPlayerNotReady() throws JsonProcessingException {
        GameFireCommand gameFireCommand = new GameFireCommand();
        gameFireCommand.setCoordinate("0,0");
        gameFireCommand.setPlayerId(PLAYER_ONE);

        var content = RestAssured.given().spec(initSpecification("/{gameId}/fields/fire"))
                .contentType(TestConfig.CONTENT_TYPE_JSON)
                .pathParam("gameId", gameId)
                .body(gameFireCommand)
                .when()
                .post()
                .then()
                .statusCode(400)
                .extract()
                .body()
                .asString();

        ExceptionResponse response = objectMapper.readValue(content, ExceptionResponse.class);
        assertNotNull(response);
        assertEquals("Players not ready", response.getMessage());
        assertTrue(response.getDetail().contains("/fields/fire"));
    }

    @Test
    @Order(32)
    public void testComputerDeployShips() {
        DeployShipsCommand deployShipsCommand = new DeployShipsCommand();
        deployShipsCommand.setPlayerId(PLAYER_COMPUTER);
        deployShipsCommand.setShipsDeploy(ShipDeploymentBuilder.buildShipsDeployment());

        var content = given().spec(initSpecification("/{gameId}/fields/ships/deploy"))
                .contentType(TestConfig.CONTENT_TYPE_JSON)
                .pathParam("gameId", gameId)
                .body(deployShipsCommand)
                .when()
                .post()
                .then()
                .assertThat().statusCode(is(200));
    }

    @Test
    @Order(33)
    public void testPlayerOneFire() throws JsonProcessingException {
        GameFireCommand gameFireCommand = new GameFireCommand();
        gameFireCommand.setCoordinate("A1");
        gameFireCommand.setPlayerId(PLAYER_ONE);

        var content = RestAssured.given().spec(initSpecification("/{gameId}/fields/fire"))
                .contentType(TestConfig.CONTENT_TYPE_JSON)
                .pathParam("gameId", gameId)
                .body(gameFireCommand)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        GameFireResponse response = objectMapper.readValue(content, GameFireResponse.class);
        assertNotNull(response);
    }

    @Test
    @Order(34)
    public void testComputerFire() throws JsonProcessingException {
        GameFireCommand gameFireCommand = new GameFireCommand();
        gameFireCommand.setCoordinate("A1");
        gameFireCommand.setPlayerId(PLAYER_COMPUTER);

        var content = RestAssured.given().spec(initSpecification("/{gameId}/fields/fire"))
                .contentType(TestConfig.CONTENT_TYPE_JSON)
                .pathParam("gameId", gameId)
                .body(gameFireCommand)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        GameFireResponse response = objectMapper.readValue(content, GameFireResponse.class);
        assertNotNull(response);
    }

}
