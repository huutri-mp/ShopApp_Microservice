package com.example.profileservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddressUpdateRequest {
    @NotBlank
    String contactName;
    @Pattern(regexp = "^[0-9]{10,15}$", message = "Số điện thoại không hợp lệ")
    String contactPhone;
    @NotBlank
    String addressLine;
    @NotBlank
    String wards;
    @NotBlank
    String province;

    Boolean isDefault;
}
