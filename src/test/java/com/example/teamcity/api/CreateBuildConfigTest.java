package com.example.teamcity.api;

import com.example.teamcity.api.enums.Role;
import com.example.teamcity.api.generators.TestDataGenerator;
import com.example.teamcity.api.requests.checked.CheckedBuildConfig;
import com.example.teamcity.api.requests.checked.CheckedProject;
import com.example.teamcity.api.requests.checked.CheckedUser;
import com.example.teamcity.api.requests.unchecked.UncheckedBuildConfig;
import com.example.teamcity.api.spec.Specifications;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

public class CreateBuildConfigTest extends BaseApiTest {

    @Test
    public void buildConfigurationCreatedForStandardPositiveCaseTest() {
        var testData = testDataStorage.addTestData();

        new CheckedUser(Specifications.getSpec().superUserSpec())
                .create(testData.getUser());

        new CheckedProject(Specifications.getSpec()
                .authSpec(testData.getUser()))
                .create(testData.getProject());

        var buildConfig = new CheckedBuildConfig(Specifications.getSpec()
                .authSpec(testData.getUser()))
                .create(testData.getBuildType());

        softy.assertThat(buildConfig.getId()).isEqualTo(testData.getBuildType().getId());
    }

    @Test
    public void buildConfigurationCreatedForProjectAdminRoleTest() {
        var testData = testDataStorage.addTestData();

        checkedWithSuperUser.getProjectRequest()
                .create(testData.getProject());

        testData.getUser().setRoles(TestDataGenerator.generateRoles(Role.PROJECT_ADMIN, "p:" + testData.getProject().getId()));

        checkedWithSuperUser.getUserRequest()
                .create(testData.getUser());

        var buildConfig = new CheckedBuildConfig(Specifications.getSpec().authSpec(testData.getUser()))
                .create(testData.getBuildType());

        softy.assertThat(buildConfig.getId()).isEqualTo(testData.getBuildType().getId());
    }

    @Test
    public void buildConfigIsNotCreatedForIncorrectRoleTest() {
        var testData = testDataStorage.addTestData();

        uncheckedWithSuperUser.getProjectRequest()
                .create(testData.getProject());

        testData.getUser().setRoles(TestDataGenerator.generateRoles(Role.PROJECT_VIEWER, "p:" + testData.getProject().getId()));

        uncheckedWithSuperUser.getUserRequest().create(testData.getUser());

        new UncheckedBuildConfig(Specifications.getSpec().authSpec(testData.getUser()))
                .create(testData.getBuildType()).then().statusCode(HttpStatus.SC_FORBIDDEN)
                .body(Matchers.containsString("Access denied. " +
                        "Check the user has enough permissions to perform the operation"));
    }

    @Test
    public void buildConfigurationIsNotCreatedForEmptyNameTest() {
        var testData = testDataStorage.addTestData();

        testData.getBuildType().setName("");

        new CheckedUser(Specifications.getSpec().superUserSpec())
                .create(testData.getUser());

        new CheckedProject(Specifications.getSpec()
                .authSpec(testData.getUser()))
                .create(testData.getProject());

        new UncheckedBuildConfig(Specifications.getSpec()
                .authSpec(testData.getUser()))
                .create(testData.getBuildType())
                .then().assertThat().statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.containsString("When creating a build type, " +
                        "non empty name should be provided"));
    }

    @Test
    public void buildConfigurationIsNotCreatedForEmptyIdTest() {
        var testData = testDataStorage.addTestData();

        testData.getBuildType().setId("");

        new CheckedUser(Specifications.getSpec().superUserSpec())
                .create(testData.getUser());

        new CheckedProject(Specifications.getSpec()
                .authSpec(testData.getUser()))
                .create(testData.getProject());

        new UncheckedBuildConfig(Specifications.getSpec()
                .authSpec(testData.getUser()))
                .create(testData.getBuildType())
                .then().assertThat().statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                .body(Matchers.containsString("Build configuration or template ID must not be empty"));
    }

    @Test
    public void buildConfigurationCreatedForUkrainianPolandEncodingInNameTest() {
        var testData = testDataStorage.addTestData();
        var expectedProjectName = "дослідження білд-конфігурацїї/Kręta Góra";

        testData.getBuildType().setName(expectedProjectName);

        new CheckedUser(Specifications.getSpec().superUserSpec())
                .create(testData.getUser());

        new CheckedProject(Specifications.getSpec()
                .authSpec(testData.getUser()))
                .create(testData.getProject());

        var buildConfig = new CheckedBuildConfig(Specifications.getSpec()
                .authSpec(testData.getUser()))
                .create(testData.getBuildType());

        softy.assertThat(buildConfig.getName()).isEqualTo(expectedProjectName);
    }

    @Test
    public void buildConfigurationCreatedForSpecialSymbolsInNameTest() {
        var testData = testDataStorage.addTestData();
        var expectedProjectName = "!@#$%^&*()~:,.;'|/?";

        testData.getBuildType().setName(expectedProjectName);

        new CheckedUser(Specifications.getSpec().superUserSpec())
                .create(testData.getUser());

        new CheckedProject(Specifications.getSpec()
                .authSpec(testData.getUser()))
                .create(testData.getProject());

        var buildConfig = new CheckedBuildConfig(Specifications.getSpec()
                .authSpec(testData.getUser()))
                .create(testData.getBuildType());

        softy.assertThat(buildConfig.getName()).isEqualTo(expectedProjectName);
    }

    @Test
    public void buildConfigurationCreatedFor4000SymbolsInNameTest() {
        var testData = testDataStorage.addTestData();
        var expectedProjectName = RandomStringUtils.randomAlphabetic(4000);

        testData.getBuildType().setName(expectedProjectName);

        new CheckedUser(Specifications.getSpec().superUserSpec())
                .create(testData.getUser());

        new CheckedProject(Specifications.getSpec()
                .authSpec(testData.getUser()))
                .create(testData.getProject());

        var buildConfig = new CheckedBuildConfig(Specifications.getSpec()
                .authSpec(testData.getUser()))
                .create(testData.getBuildType());

        softy.assertThat(buildConfig.getName()).isEqualTo(expectedProjectName);
    }

}
