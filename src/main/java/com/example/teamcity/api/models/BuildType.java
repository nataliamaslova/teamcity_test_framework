package com.example.teamcity.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BuildType {
    private String id;
    private NewProjectDescription project;
    private String name;

    // Teamcity constructs build id on the base of build name and project id:
    // test_McXDlyulmK + test_ulQPurWwvQ -> TestUlQPurWwvQ_TestMcXDlyulmK
    public String getTeamCityBuildId() {
        if (name.isEmpty()) {
            return name;
        }
        String id = name.replace("_", "");
        id = Character.toUpperCase(id.charAt(0)) + id.substring(1,4) + Character.toUpperCase(id.charAt(4)) + id.substring(5);
        id = project.getTeamCityProjectId() + "_" + id;
        return id;
    }
}
