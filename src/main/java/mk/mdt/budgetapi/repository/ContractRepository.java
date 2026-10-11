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

    default Contract create(Contract contract) {
        return save(contract);
    }

    default Contract getById(Long id) {
        return findById(id).orElseThrow(() -> new RuntimeException("Contract not found"));
    }

    default List<Contract> getAll() {
        return findAll();
    }

    default Contract update(Contract contract) {
        return save(contract);
    }

    default void delete(Long id) {
        deleteById(id);
    }

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Contract> findWithLockById(Long id);
}