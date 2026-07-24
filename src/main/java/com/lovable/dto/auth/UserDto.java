package com.lovable.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto implements Serializable {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @Pattern(
            regexp = "^[a-zA-Z0-9_]+$",
            message = "Username can contain only letters, numbers, and underscores"
    )
    @Size(max = 100, message = "Username cannot exceed 100 characters")
    @NotBlank(message = "Name cannot be blank")
    private String name;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email cannot be blank")
    @Size(max = 255, message = "Email cannot exceed 255 characters")
    private String email;

    @Pattern(
            regexp = "^[a-zA-Z0-9_]+$",
            message = "Username can contain only letters, numbers, and underscores"
    )
    @Size(max = 100, message = "Username cannot exceed 100 characters")
    @JsonProperty("username")
    private String uniqueUsername;

    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[a-zA-Z\\d@$!%*?&]{8,}$",
            message = "Password must be at least 8 characters and contain uppercase, lowercase, digit, and special character")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Size(max = 255, message = "Password cannot exceed 255 characters")
    @NotBlank(message = "Password cannot be blank")
    private String password;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdAt;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime updatedAt;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime deletedAt;
}
