package com.example.teamcity.api;

import com.example.teamcity.api.models.User;
import com.example.teamcity.api.requests.checked.AuthRequest;
import com.example.teamcity.api.spec.Specifications;
import io.restassured.RestAssured;
import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

public class ExperimentTest extends BaseApiTest {

    @Test
    public void firstStepGetToken() {
        var token = RestAssured.get("http://admin:admin@192.168.0.165:8111/authenticationTest.html?csrf")
                .then().assertThat().statusCode(HttpStatus.SC_OK)
                .extract().asString();
        System.out.println("Token: " + token);
    }

    @Test
    public void secondStepGetToken() {
        var user = User.builder()
                .username("admin")
                .password("admin")
                .build();

        var token = RestAssured
                .given()
                .spec(Specifications.getSpec().authSpec(user))
                .get("authenticationTest.html?csrf")
                .then().assertThat().statusCode(HttpStatus.SC_OK)
                .extract().asString();

        System.out.println("Token: " + token);
    }

    @Test
    public void thirdStepGetToken() {
        var user = User.builder()
                .username("admin")
                .password("admin")
                .build();

        var token = new AuthRequest(user).getCsrfToken();

        System.out.println("Token: " + token);
    }
}
