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
public class NewProjectDescription {
    private Project parentProject;
    private String name;
    private String id;
    private Boolean copyAllAssociatedSettings;

    // Teamcity builds project id on the base of project name: test_qIRqFnQwqc -> TestQIRqFnQwqc
    public String getTeamCityProjectId() {
        if (name.isEmpty()) {
            return name;
        }
        String id = name.replace("_", "");
        id = Character.toUpperCase(id.charAt(0)) + id.substring(1,4) + Character.toUpperCase(id.charAt(4)) + id.substring(5);
        return id;
    }
}
