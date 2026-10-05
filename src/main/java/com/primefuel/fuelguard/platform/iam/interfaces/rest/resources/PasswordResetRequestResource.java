package com.primefuel.fuelguard.platform.iam.interfaces.rest.resources;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record PasswordResetRequestResource(@NotBlank @Email String email) {
}
