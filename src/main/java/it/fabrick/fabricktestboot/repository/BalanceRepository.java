package it.fabrick.fabricktestboot.repository;

import it.fabrick.fabricktestboot.model.Balance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BalanceRepository extends JpaRepository<Balance, Long> {

    Optional<Balance> findByAccountId(String accountId);
}
