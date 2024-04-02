package com.example.teamcity.ui;

import com.codeborne.selenide.Selenide;
import com.example.teamcity.ui.pages.StartUpPage;
import org.testng.annotations.Test;

public class SetupFirstStartTest extends BaseUiTest {
    @Test
    public void startUpTest()  {
        new StartUpPage()
                .open()
                .setupTeamCityServer();
 //               .getHeader.shouldHave(Condition.text("Create Administrator Account"));
        softy.assertThat(Selenide.title().contains("Create Administrator Account"));
    }
}
