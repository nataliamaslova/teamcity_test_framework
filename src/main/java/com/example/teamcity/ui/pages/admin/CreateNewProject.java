package com.example.teamcity.ui.pages.admin;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.example.teamcity.ui.Selectors;
import com.example.teamcity.ui.pages.Page;

import java.time.Duration;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.element;

public class CreateNewProject extends Page {

    private SelenideElement headerCreateProject = element(byText("Create Project"));
    private SelenideElement headerCreateProjectFromUrl = element(byText("Create Project From URL"));
    private SelenideElement urlInput = element(Selectors.byId("url"));
    private SelenideElement projectNameInput = element((Selectors.byId("projectName")));
    private SelenideElement buildTypeNameInput = element((Selectors.byId("buildTypeName")));

    public void waitUntilCreateNewProjectPageIsLoaded() {
        waitUntilPageIsLoaded();
        headerCreateProject.shouldBe(Condition.visible, Duration.ofSeconds(30));
        }
    public void waitUntilCreateProjectFromUrlPageIsLoaded() {
        waitUntilPageIsLoaded();
        headerCreateProjectFromUrl.shouldBe(Condition.visible, Duration.ofSeconds(30));
    }


    public CreateNewProject open(String parentProjectId) {
        Selenide.open("/admin/createObjectMenu.html?projectId=" + parentProjectId + "&showMode=createProjectMenu");
        waitUntilCreateNewProjectPageIsLoaded();
        return this;
    }

    public CreateNewProject createProjectByUrl(String url) {
        urlInput.sendKeys(url);
        submit();
        return this;
    }

    public void setupProject(String projectName, String buildTypeName) {
        waitUntilCreateProjectFromUrlPageIsLoaded();
        projectNameInput.clear();
        projectNameInput.sendKeys(projectName);
        buildTypeNameInput.clear();
        buildTypeNameInput.sendKeys(buildTypeName);
        submit();
    }
}
