package mk.mdt.budgetapi.service.domain;


import mk.mdt.budgetapi.entity.Contract;

import java.util.List;
import java.util.Optional;

public interface ContractService {
    Optional<Contract> findById(Long id);

    Optional<Contract> findWithLockById(Long id);

    List<Contract> findAll();

    Contract create(Contract contract);

    Optional<Contract> update(Long id, Contract contract);

    Optional<Contract> deleteById(Long id);
}
