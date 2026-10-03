package mk.mdt.budgetapi.repository;

import mk.mdt.budgetapi.model.Contract;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

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
}