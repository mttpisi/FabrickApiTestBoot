package it.fabrick.fabricktestboot.model;

import it.fabrick.fabricktestboot.model.common.BaseEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Creditor extends BaseEntity {

    String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creditorAccountId")
    CreditorAccount account;
}
