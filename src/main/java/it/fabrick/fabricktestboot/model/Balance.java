package it.fabrick.fabricktestboot.model;

import it.fabrick.fabricktestboot.model.common.BaseEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.Entity;
import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Builder
public class Balance extends BaseEntity {

    String date;

    BigDecimal balance;

    BigDecimal availableBalance;

    String currency;

    String accountId;

}
