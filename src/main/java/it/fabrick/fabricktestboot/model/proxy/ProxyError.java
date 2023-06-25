package it.fabrick.fabricktestboot.model.proxy;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProxyError {

    String code;

    String description;

    String params;

    public ProxyError(String description) {
        this.description = description;
    }
}
