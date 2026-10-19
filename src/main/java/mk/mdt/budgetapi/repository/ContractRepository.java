package mk.mdt.budgetapi.repository;

import jakarta.persistence.LockModeType;
import mk.mdt.budgetapi.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Contract> findWithLockById(Long id);
}