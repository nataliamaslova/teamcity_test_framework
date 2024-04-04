package com.example.teamcity.ui.pages.admin;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.example.teamcity.ui.Selectors;
import com.example.teamcity.ui.pages.Page;

import java.time.Duration;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.element;

public class BuildSteps extends Page {
    private SelenideElement buildSteps = element(byText("Build Steps"));
    private SelenideElement addBuildStep = element(byText("Add build step"));
    private SelenideElement menuElementMaven = element(byText("Maven"));
    private SelenideElement menuElementCommandLine = element(byText("Command Line"));
    private SelenideElement submitButton = element(Selectors.byName("submitButton"));
    private SelenideElement messageErrorCustomScript = element(Selectors.byId("error_script.content"));

    public BuildSteps open() {
        buildSteps.click();
        addBuildStep.click();
        return this;
    }

    public BuildSteps createBuildSteps() {
        menuElementMaven.click();
        submit();
        return this;
    }

    public BuildSteps createCommandLineBuildSteps() {
        menuElementCommandLine.click();
        submit();
        return this;
    }

    public void submit() {
        submitButton.click();
        waitUntilDataIsSaved();
    }

    public void waitUntilMessageErrorCustomScriptIsLoaded() {
        waitUntilPageIsLoaded();
        messageErrorCustomScript.shouldBe(Condition.visible, Duration.ofSeconds(30));
    }
}
