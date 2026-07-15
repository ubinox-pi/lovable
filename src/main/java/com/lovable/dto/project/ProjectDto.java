package com.lovable.dto.project;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lovable.dto.auth.UserDto;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectDto implements Serializable {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank(message = "Name cannot be blank")
    private String name;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UserDto owner;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Boolean isPublic;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdAt;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime updatedAt;
}
