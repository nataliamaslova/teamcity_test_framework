package com.example.teamcity.api;

import com.example.teamcity.api.spec.Specifications;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import lombok.SneakyThrows;
import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

public class AuthorizeAgentTest extends BaseApiTest {
    @SneakyThrows
    @Test
    public void authorizeAgent() {
        Thread.sleep(1000 * 20);

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
}
