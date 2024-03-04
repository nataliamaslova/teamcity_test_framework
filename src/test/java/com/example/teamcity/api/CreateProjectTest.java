package com.example.teamcity.api;

import com.example.teamcity.api.enums.Role;
import com.example.teamcity.api.generators.TestDataGenerator;
import com.example.teamcity.api.requests.UncheckedRequests;
import com.example.teamcity.api.requests.checked.CheckedProject;
import com.example.teamcity.api.spec.Specifications;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

public class CreateProjectTest extends BaseApiTest {
    @Test
    public void createProjectTest() {
        var testData = testDataStorage.addTestData();

        testData.getUser().setRoles(TestDataGenerator.generateRoles(Role.SYSTEM_ADMIN, "g"));

        checkedWithSuperUser.getUserRequest().create(testData.getUser());

        var project = new CheckedProject(Specifications.getSpec()
                .authSpec(testData.getUser()))
                .create(testData.getProject());

        softy.assertThat(project.getId()).isEqualTo(testData.getProject().getId());
    }

    @Test
    public void projectIsNotCreatedForNotSystemAdminRole() {
        var testData = testDataStorage.addTestData();

        testData.getUser().setRoles(TestDataGenerator.generateRoles(Role.PROJECT_DEVELOPER, "g"));

        new UncheckedRequests(Specifications.getSpec().authSpec(testData.getUser())).getProjectRequest()
                .create(testData.getProject())
                .then().assertThat().statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body(Matchers.containsString("Incorrect username or password"));

        uncheckedWithSuperUser.getProjectRequest()
                .get(testData.getProject().getId())
                .then().assertThat().statusCode(HttpStatus.SC_NOT_FOUND)
                .body(Matchers.containsString("No project found by locator" +
                        " 'count:1,id:" + testData.getProject().getId() + "'"));
    }

}
