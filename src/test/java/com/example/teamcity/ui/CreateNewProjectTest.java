package com.example.teamcity.ui;

import com.codeborne.selenide.Condition;
import com.example.teamcity.ui.pages.ProjectsPage;
import com.example.teamcity.ui.pages.admin.CreateNewProject;
import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

public class CreateNewProjectTest extends BaseUiTest {
    @Test
    public void authorizedUserShouldBeAbleCreateNewProject() {
        var testData = testDataStorage.addTestData();
        var url = "https://github.com/AlexPshe/spring-core-for-qa";

        loginAsUser(testData.getUser());

        new CreateNewProject()
                .open(testData.getProject().getParentProject().getLocator())
                .createProjectByUrl(url)
                .setupProject(testData.getProject().getName(), testData.getBuildType().getName());

        new ProjectsPage().open()
                .getSubprojects()
                .stream().reduce((first, second) -> second).get()
                .getHeader().shouldHave(Condition.text(testData.getProject().getName()));

        uncheckedWithSuperUser.getProjectRequest()
                .get(testData.getProject().getTeamCityProjectId())
                .then().assertThat().statusCode(HttpStatus.SC_OK);
    }

    @Test
    public void projectIsNotCreatedForInvalidUrl() {
        var testData = testDataStorage.addTestData();
        var url = "abc";

        loginAsUser(testData.getUser());

        new CreateNewProject()
                .open(testData.getProject().getParentProject().getLocator())
                .createProjectByUrl(url)
                .waitUntilMessageErrorUrlIsLoaded();

        new ProjectsPage().open()
                .getSubprojects()
                .stream().reduce((first, second) -> second).get()
                .getHeader().shouldNotHave(Condition.text(testData.getProject().getName()));

        uncheckedWithSuperUser.getProjectRequest()
                .get(testData.getProject().getTeamCityProjectId())
                .then().assertThat().statusCode(HttpStatus.SC_NOT_FOUND);
     }
}
