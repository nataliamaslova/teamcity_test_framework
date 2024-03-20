package com.example.teamcity.ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.example.teamcity.ui.Selectors;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.element;

public abstract class Page {
    private SelenideElement submitButton = element(Selectors.byType("submit"));
    private SelenideElement savingWaitingMarker = element(Selectors.byId("saving"));
    private SelenideElement pageWaitingMarker = element(Selectors.byDataTest("ring-loader"));

    public void submit() {
        submitButton.click();
        waitUntilDataIsSaved();
    }

    public void waitUntilPageIsLoaded() {
        pageWaitingMarker.shouldNotBe(Condition.visible, Duration.ofMinutes(1));
    }

    public void waitUntilDataIsSaved() {
        savingWaitingMarker.shouldNotBe(Condition.visible, Duration.ofSeconds(30));
    }

}
