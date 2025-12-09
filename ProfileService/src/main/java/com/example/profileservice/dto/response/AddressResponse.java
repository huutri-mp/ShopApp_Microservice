package com.example.profileservice.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;


@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddressResponse {
    Integer addressId;
    String contactName;
    String contactPhone;
    String addressLine;
    String wards;
    String province;
    String country;
    Boolean isDefault;
}
