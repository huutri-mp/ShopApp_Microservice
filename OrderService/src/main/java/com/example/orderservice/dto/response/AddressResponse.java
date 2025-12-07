package com.example.orderservice.dto.response;

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
    String contactName;
    String contactPhone;
    String addressLine1;
    String addressLine2;
    String city;
    String province;
    String postalCode;
    String country;
    Boolean isDefault;
}
