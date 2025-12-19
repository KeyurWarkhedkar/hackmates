package com.keyur.hackmates.DTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserSignupDto {
    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).*$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character (@#$%^&+=!)"
    )
    private String password;

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 30, message = "First name must be between 2 and 30 characters")
    @Pattern(
            regexp = "^[A-Za-z\\s.'-]+$",
            message = "First name can only contain letters, spaces, dots, apostrophes and hyphens"
    )
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 30, message = "Last name must be between 2 and 30 characters")
    @Pattern(
            regexp = "^[A-Za-z\\s.'-]+$",
            message = "Last name can only contain letters, spaces, dots, apostrophes and hyphens"
    )
    private String lastName;

//    @Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters")
//    @Pattern(
//            regexp = "^[A-Za-z0-9._-]+$",
//            message = "Username can only contain letters, numbers, dots, underscores, and hyphens"
//    )
//    private String username;
//
//    // Profile photo - no validation here, handle separately in controller
//    private MultipartFile profilePhoto;
//
//    @Size(max = 500, message = "Bio cannot exceed 500 characters")
//    private String bio;
//
//    // Validation for skills list
//    //@Size(max = 10, message = "Cannot have more than 10 skills")
//    private List<@NotBlank(message = "Skill cannot be blank")
//    @Size(min = 2, max = 50, message = "Each skill must be between 2 and 50 characters")
//    @Pattern(regexp = "^[A-Za-z0-9\\s+#&.-]+$",
//            message = "Skill can only contain letters, numbers, spaces, and special characters (#&.+-)")
//            String> skills;
//
//    // Validation for interests list
//    //@Size(max = 10, message = "Cannot have more than 10 interests")
//    private List<@NotBlank(message = "Interest cannot be blank")
//    @Size(min = 2, max = 50, message = "Each interest must be between 2 and 50 characters")
//    @Pattern(regexp = "^[A-Za-z0-9\\s+#&.-]+$",
//            message = "Interest can only contain letters, numbers, spaces, and special characters (#&.+-)")
//            String> interests;
}
