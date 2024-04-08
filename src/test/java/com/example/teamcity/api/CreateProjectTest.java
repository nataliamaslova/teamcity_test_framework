package com.example.teamcity.api;

import com.example.teamcity.api.models.Project;
import com.example.teamcity.api.requests.UncheckedRequests;
import com.example.teamcity.api.requests.checked.CheckedProject;
import com.example.teamcity.api.requests.checked.CheckedUser;
import com.example.teamcity.api.requests.unchecked.UncheckedProject;
import com.example.teamcity.api.spec.Specifications;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

public class CreateProjectTest extends BaseApiTest {
    @Test
    public void projectCreatedForStandardPositiveCaseTest() {
        var testData = testDataStorage.addTestData();

        checkedWithSuperUser.getUserRequest().create(testData.getUser());

        var project = new CheckedProject(Specifications.getSpec()
                .authSpec(testData.getUser()))
                .create(testData.getProject());

        softy.assertThat(project.getId()).isEqualTo(testData.getProject().getId());
    }
    @Test
    public void projectIsNotCreatedForUnauthorizedUserTest() {
        var testData = testDataStorage.addTestData();

        new UncheckedRequests(Specifications.getSpec().unauthSpec()).getProjectRequest()
                .create(testData.getProject())
                .then().assertThat().statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body(Matchers.containsString("Authentication required"));

        uncheckedWithSuperUser.getProjectRequest()
                .get(testData.getProject().getId())
                .then().assertThat().statusCode(HttpStatus.SC_NOT_FOUND)
                .body(Matchers.containsString(String.format("No project found by locator 'count:1,id:%s'",
                        testData.getProject().getId())));
  }

    @Test
    public void projectIsNotCreatedForInvalidParentIdTest() {
        var testData = testDataStorage.addTestData();

        // valid parentId equals "_Root"
        var invalidParentId = RandomStringUtils.randomAlphabetic(5);

        testData.getProject().setParentProject(Project.builder()
                .locator(invalidParentId)
                .build());

        new CheckedUser(Specifications.getSpec().superUserSpec())
                .create(testData.getUser());

        new UncheckedProject(Specifications.getSpec()
                .authSpec(testData.getUser()))
                .create(testData.getProject())
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .body(Matchers.containsString(String.format("No project found by name or internal/external id '%s'.",
                        invalidParentId)));
    }
    @Test
    public void projectIsNotCreatedForTheSameIdTest() {
        var testData = testDataStorage.addTestData();

        new CheckedUser(Specifications.getSpec().superUserSpec())
                .create(testData.getUser());

        new UncheckedProject(Specifications.getSpec()
                .authSpec(testData.getUser()))
                .create(testData.getProject())
                .then().assertThat().statusCode(HttpStatus.SC_OK);

        new UncheckedProject(Specifications.getSpec()
                .authSpec(testData.getUser()))
                .create(testData.getProject())
                .then().assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.containsString(String.format("DuplicateProjectNameException: " +
                                "Project with this name already exists: %s",
                                testData.getProject().getName())));
    }

    @Test
    public void projectIsNotCreatedForEmptyNameTest() {
        var testData = testDataStorage.addTestData();

        testData.getProject().setName("");

        checkedWithSuperUser.getUserRequest().create(testData.getUser());

        new UncheckedProject(Specifications.getSpec()
                .authSpec(testData.getUser()))
                .create(testData.getProject())
                .then().assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.containsString("Project name cannot be empty"));
    }

    @Test
    public void projectIsNotCreatedForEmptyIdTest() {
        var testData = testDataStorage.addTestData();

        testData.getProject().setId("");

        checkedWithSuperUser.getUserRequest().create(testData.getUser());

        new UncheckedProject(Specifications.getSpec()
                .authSpec(testData.getUser()))
                .create(testData.getProject())
                .then().assertThat()
                .statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                .body(Matchers.containsString("Project ID must not be empty"));
    }

    @Test
    public void projectCreatedFor4000SymbolsInNameTest() {
        var testData = testDataStorage.addTestData();
        var expectedProjectName = RandomStringUtils.randomAlphabetic(4000);

        checkedWithSuperUser.getUserRequest().create(testData.getUser());

        testData.getProject().setName(expectedProjectName);

        var project = new CheckedProject(Specifications.getSpec()
                .authSpec(testData.getUser()))
                .create(testData.getProject());

        softy.assertThat(project.getName()).isEqualTo(expectedProjectName);
    }

    @Test
    public void projectCreatedForRussianPolandEncodingInNameTest() {
        var testData = testDataStorage.addTestData();
        var expectedProjectName = "новый проект Wrocław/Żielona Góra";

        testData.getProject().setName(expectedProjectName);

        checkedWithSuperUser.getUserRequest().create(testData.getUser());

        var project = new CheckedProject(Specifications.getSpec()
                .authSpec(testData.getUser()))
                .create(testData.getProject());

        softy.assertThat(project.getName()).isEqualTo(expectedProjectName);
    }

    @Test
    public void projectCreatedForSpecialSymbolsInNameTest() {
        var testData = testDataStorage.addTestData();
        var expectedProjectName = "!@#$%^&*()~:,.;'|/?";

        testData.getProject().setName(expectedProjectName);

        checkedWithSuperUser.getUserRequest().create(testData.getUser());

        var project = new CheckedProject(Specifications.getSpec()
                .authSpec(testData.getUser()))
                .create(testData.getProject());

        softy.assertThat(project.getName()).isEqualTo(expectedProjectName);
    }
}