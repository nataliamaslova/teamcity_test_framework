package com.example.teamcity.api;

import com.example.teamcity.api.spec.Specifications;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

public class AuthorizeAgentTest extends BaseApiTest {
    @Test
    public void authorizeAgent() {
       RestAssured
                .given()
                .spec(Specifications.getSpec().superUserSpec())
                .contentType(ContentType.TEXT)
                .accept(ContentType.TEXT)
                .body("true")
                .put("/app/rest/agents/ip_172.17.0.1/authorized")
                .then().assertThat().statusCode(HttpStatus.SC_OK);
    }
}
