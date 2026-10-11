package mk.mdt.budgetapi.service.application;

import mk.mdt.budgetapi.dto.ContractRequest;
import mk.mdt.budgetapi.dto.ContractResponse;

import java.util.List;
import java.util.Optional;

public interface ContractApplicationService {
    Optional<ContractResponse> findById(Long id);

    List<ContractResponse> findAll();

    ContractResponse create(ContractRequest contractRequest);

    Optional<ContractResponse> update(Long id, ContractRequest contractRequest);

    Optional<ContractResponse> deleteById(Long id);
}
