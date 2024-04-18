package com.example.teamcity.api;

import com.example.teamcity.api.spec.Specifications;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.awaitility.Awaitility;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

public class AuthorizeAgentTest extends BaseApiTest {
    @Test
    public void authorizeAgent() {
        Awaitility.await().atMost(60, TimeUnit.SECONDS).until(() -> this.getStatusCode() == 200);

        String agentName = RestAssured
                .given()
                .spec(Specifications.getSpec().superUserSpec())
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .get("/app/rest/agents?locator=authorized:false")
                .then().assertThat().statusCode(HttpStatus.SC_OK)
                .extract().jsonPath().getString("agent.name");

        agentName = (agentName.substring(1, agentName.length() - 1)); // ip_172.17.0.1

        RestAssured
                .given()
                .spec(Specifications.getSpec().superUserSpec())
                .contentType(ContentType.TEXT)
                .accept(ContentType.TEXT)
                .body("true")
                .put("/app/rest/agents/" + agentName + "/authorized")
                .then().assertThat().statusCode(HttpStatus.SC_OK);
    }

    private int getStatusCode() {
        return RestAssured.given()
                .spec(Specifications.getSpec().superUserSpec())
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .get("/app/rest/agents?locator=authorized:false")
                .then().extract().statusCode();
    }

}
