package com.example.teamcity.ui;

import com.codeborne.selenide.Selenide;
import com.example.teamcity.ui.pages.admin.BuildSteps;
import com.example.teamcity.ui.pages.admin.CreateNewProject;
import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

public class CreateBuildConfigTest extends BaseUiTest {
    @Test
    public void authorizedUserShouldBeAbleCreateBuildConfig() {
        var testData = testDataStorage.addTestData();
        var url = "https://github.com/AlexPshe/spring-core-for-qa";

        loginAsUser(testData.getUser());

        new CreateNewProject()
                .open(testData.getProject().getParentProject().getLocator())
                .createProjectByUrl(url)
                .setupProject(testData.getProject().getName(), testData.getBuildType().getName());

        new BuildSteps()
                .open()
                .createBuildSteps();

        uncheckedWithSuperUser.getBuildConfigRequest()
                .get(testData.getBuildType().getTeamCityBuildId())
                .then().assertThat().statusCode(HttpStatus.SC_OK);

        String pageTitle = Selenide.title();
        softy.assertThat(pageTitle.contains(testData.getBuildType().getName()));
    }

    @Test
    public void buildConfigurationIsNotCreatedForInvalidBuildTypeName() {
        var testData = testDataStorage.addTestData();
        var url = "https://github.com/AlexPshe/spring-core-for-qa";

        loginAsUser(testData.getUser());

        new CreateNewProject()
                .open(testData.getProject().getParentProject().getLocator())
                .createProjectByUrl(url)
                .setupProject(testData.getProject().getName(), "")
                .waitUntilMessageErrorBuildConfigurationIsLoaded();

        uncheckedWithSuperUser.getBuildConfigRequest()
                .get(testData.getBuildType().getTeamCityBuildId())
                .then().assertThat().statusCode(HttpStatus.SC_NOT_FOUND);
    }
}
