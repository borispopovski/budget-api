package mk.mdt.budgetapi.service.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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

        Contract saved = contractRepository.save(contract);
        log.info("Contract created: id={}, name={}, totalAmount={}", saved.getId(), saved.getContractName(), saved.getTotalAmount());
        return contractMapper.toContractResponse(saved);    }

    @Override
    @Transactional
    public Optional<ContractResponse> update(Long id, ContractRequest contractRequest) {
        log.debug("Updating contract id={}", id);
        return contractRepository
                .findWithLockById(id)
                .map(existingContract -> {
                    BigDecimal invoicedAmount =
                            invoiceRepository.sumAmountByContractId(id);

                    if (contractRequest.totalAmount().compareTo(invoicedAmount) < 0) {
                        log.warn("Contract {} update rejected: new total {} is below invoiced amount {}", id, contractRequest.totalAmount(), invoicedAmount);
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

                    Contract updated = contractRepository.save(existingContract);
                    log.info("Contract updated: id={}", id);
                    return contractMapper.toContractResponse(updated);                });
    }

    @Override
    @Transactional
    public Optional<ContractResponse> deleteById(Long id) {
        return contractRepository
                .findWithLockById(id)
                .map(contract -> {

                    if (invoiceRepository.existsByContractId(id)) {
                        log.warn("Contract {} cannot be deleted: it has invoices", id);
                        throw new ContractHasInvoicesException(id);
                    }

                    contractRepository.delete(contract);
                    log.info("Contract deleted: id={}", id);

                    return contractMapper.toContractResponse(contract);
                });
    }
}
