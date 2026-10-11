package mk.mdt.budgetapi.service.application.Impl;

import mk.mdt.budgetapi.dto.ContractRequest;
import mk.mdt.budgetapi.dto.ContractResponse;
import mk.mdt.budgetapi.entity.Contract;
import mk.mdt.budgetapi.mapper.ContractMapper;
import mk.mdt.budgetapi.service.application.ContractApplicationService;
import mk.mdt.budgetapi.service.domain.ContractService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ContractApplicationServiceImpl implements ContractApplicationService {
    private final ContractService contractService;
    private final ContractMapper contractMapper;

    public ContractApplicationServiceImpl(ContractService contractService, ContractMapper contractMapper) {
        this.contractService = contractService;
        this.contractMapper = contractMapper;
    }

    @Override
    public Optional<ContractResponse> findById(Long id) {
        return contractService.findById(id)
                .map(contractMapper::toContractResponse);
    }

    @Override
    public List<ContractResponse> findAll() {
        return contractService.findAll()
                .stream()
                .map(contractMapper::toContractResponse)
                .toList();
    }

    @Override
    public ContractResponse create(ContractRequest contractRequest) {

        return contractMapper
                .toContractResponse(contractService.create(contractMapper.toContract(contractRequest)));
    }

    @Override
    public Optional<ContractResponse> update(Long id, ContractRequest contractRequest) {
        return contractService
                .update(id,contractMapper.toContract(contractRequest))
                .map(contractMapper::toContractResponse);
    }

    @Override
    public Optional<ContractResponse> deleteById(Long id) {
        return contractService
                .deleteById(id)
                .map(contractMapper::toContractResponse);
    }
}
