package com.example.teamcity.api.generators;

import com.codeborne.selenide.Configuration;
import com.example.teamcity.api.config.Config;
import com.example.teamcity.api.models.BuildType;
import com.example.teamcity.api.models.NewProjectDescription;
import com.example.teamcity.api.models.User;
import com.example.teamcity.api.requests.unchecked.UncheckedBuildConfig;
import com.example.teamcity.api.requests.unchecked.UncheckedProject;
import com.example.teamcity.api.requests.unchecked.UncheckedUser;
import com.example.teamcity.api.spec.Specifications;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TestData {
    private User user;
    private NewProjectDescription project;
    private BuildType buildType;

    public void delete() {
        var spec = Specifications.getSpec().superUserSpec();

        if (Configuration.baseUrl.contains(Config.getProperty("host"))) {
            // delete build config / project in UI tests: id created by Teamcity
            new UncheckedBuildConfig(spec).delete(buildType.getTeamCityBuildId());
            new UncheckedProject(spec).delete(project.getTeamCityProjectId());

        } else {
            // build config / delete project in API tests
            new UncheckedBuildConfig(spec).delete(buildType.getId());
            new UncheckedProject(spec).delete(project.getId());
        }

        new UncheckedUser(spec).delete(user.getUsername());
    }

}
