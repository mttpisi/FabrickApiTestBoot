package it.fabrick.fabricktestboot.model;

import it.fabrick.fabricktestboot.model.common.BaseEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Builder
public class Transaction extends BaseEntity {

    String transactionId;

    String operationId;

    String accountingDate;

    String valueDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "typeId")
    TransactionType type;

    BigDecimal amount;

    String currency;

    String description;

}
