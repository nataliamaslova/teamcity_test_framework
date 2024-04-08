package com.example.teamcity.ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.example.teamcity.ui.Selectors;
import lombok.Getter;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.element;

@Getter
public class StartUpPage extends Page {
    private static final String STARTUP_PAGE_URL = "/mnt";
    private SelenideElement header = element(Selectors.byId("header"));
    private SelenideElement proceedButton = element(Selectors.byId("proceedButton"));
    private SelenideElement acceptLicense = element(Selectors.byId("agreementPage"));
    private SelenideElement acceptLicenseCheck = element(Selectors.byId("accept"));

    public StartUpPage open() {
        Selenide.open(STARTUP_PAGE_URL);
        return this;
    }

    public StartUpPage setupTeamCityServer() {
        waitUntilPageIsLoaded();
        proceedButton.click();
        waitUntilPageIsLoaded();
        proceedButton.click();
        waitUntilPageIsLoaded();
        acceptLicense.shouldBe(Condition.enabled, Duration.ofMinutes(5));
        acceptLicense.scrollTo();
        acceptLicenseCheck.click();
        submit();
        return this;
    }


}
