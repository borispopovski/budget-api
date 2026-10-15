package mk.mdt.budgetapi.service.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import mk.mdt.budgetapi.dto.ContractRequest;
import mk.mdt.budgetapi.dto.ContractResponse;
import mk.mdt.budgetapi.entity.Contract;
import mk.mdt.budgetapi.exception.ContractHasInvoicesException;
import mk.mdt.budgetapi.exception.InsufficientContractBalanceException;
import mk.mdt.budgetapi.mapper.ContractMapper;
import mk.mdt.budgetapi.repository.ContractRepository;
import mk.mdt.budgetapi.repository.InvoiceRepository;
import mk.mdt.budgetapi.service.ContractService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ContractServiceImpl implements ContractService {
    private final ContractRepository contractRepository;
    private final InvoiceRepository invoiceRepository;
    private final ContractMapper contractMapper;

    @Override
    public Optional<ContractResponse> findById(Long id) {
        return contractRepository.findById(id)
                .map(contractMapper::toContractResponse);
    }

    @Override
    public List<ContractResponse> findAll() {
        return contractRepository.findAll().stream()
                .map(contractMapper::toContractResponse)
                .toList();
    }

    @Override
    @Transactional
    public ContractResponse create(ContractRequest contractRequest) {
        Contract contract = contractMapper.toContract(contractRequest);

        contract.setRemainingAmount(contract.getTotalAmount());

        return contractMapper.toContractResponse(contractRepository.save(contract));
    }

    @Override
    @Transactional
    public Optional<ContractResponse> update(Long id, ContractRequest contractRequest) {
        return contractRepository
                .findWithLockById(id)
                .map(existingContract -> {
                    BigDecimal invoicedAmount =
                            invoiceRepository.sumAmountByContractId(id);

                    if (contractRequest.totalAmount().compareTo(invoicedAmount) < 0) {
                        throw new InsufficientContractBalanceException(id);
                    }

                    existingContract.setContractName(contractRequest.contractName());
                    existingContract.setTotalAmount(contractRequest.totalAmount());
                    existingContract.setDurationYears(contractRequest.durationYears());
                    existingContract.setYear(contractRequest.year());
                    existingContract.setStartDate(contractRequest.startDate());
                    existingContract.setEndDate(contractRequest.endDate());

                    existingContract.setRemainingAmount(
                            contractRequest.totalAmount().subtract(invoicedAmount)
                    );

                    return contractMapper.toContractResponse(contractRepository.save(existingContract));
                });
    }

    @Override
    @Transactional
    public Optional<ContractResponse> deleteById(Long id) {
        return contractRepository
                .findWithLockById(id)
                .map(contract -> {

                    if (invoiceRepository.existsByContractId(id)) {
                        throw new ContractHasInvoicesException(id);
                    }

                    contractRepository.delete(contract);

                    return contractMapper.toContractResponse(contract);
                });
    }
}
