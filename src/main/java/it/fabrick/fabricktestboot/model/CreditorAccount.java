package it.fabrick.fabricktestboot.model;

import it.fabrick.fabricktestboot.model.common.BaseEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.Entity;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class CreditorAccount extends BaseEntity {

    String accountCode;

}
