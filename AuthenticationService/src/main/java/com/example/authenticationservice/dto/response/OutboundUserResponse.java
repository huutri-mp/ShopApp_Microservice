package com.example.authenticationservice.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OutboundUserResponse {

    String id;
    String email;
    boolean verifiedEmail;
    String name;

    String picture;

    @JsonProperty("picture")
    private void unpackPicture(Object pictureObject) {
        try {
            if (pictureObject instanceof String) {
                this.picture = (String) pictureObject;
                return;
            }

            Map<String, Object> pictureMap = (Map<String, Object>) pictureObject;
            Map<String, Object> data = (Map<String, Object>) pictureMap.get("data");

            if (data != null && data.containsKey("url")) {
                this.picture = data.get("url").toString();
            }
        } catch (Exception e) {
            this.picture = null;
        }
    }

    public String getAvatarUrl() {
        return picture;
    }
}
