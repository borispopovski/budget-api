package mk.mdt.budgetapi.service.domain.impl;

import jakarta.transaction.Transactional;
import mk.mdt.budgetapi.entity.Contract;
import mk.mdt.budgetapi.exception.ContractHasInvoicesException;
import mk.mdt.budgetapi.exception.InsufficientContractBalanceException;
import mk.mdt.budgetapi.repository.ContractRepository;
import mk.mdt.budgetapi.repository.InvoiceRepository;
import mk.mdt.budgetapi.service.domain.ContractService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ContractServiceImpl implements ContractService {
    private final ContractRepository contractRepository;
    private final InvoiceRepository invoiceRepository;

    public ContractServiceImpl(ContractRepository contractRepository, InvoiceRepository invoiceRepository) {
        this.contractRepository = contractRepository;
        this.invoiceRepository = invoiceRepository;
    }

    @Override
    public Optional<Contract> findById(Long id) {
        return contractRepository.findById(id);
    }

    @Override
    public Optional<Contract> findWithLockById(Long id) {
        return contractRepository.findWithLockById(id);
    }

    @Override
    public List<Contract> findAll() {
        return contractRepository.findAll();
    }

    @Override
    @Transactional
    public Contract create(Contract contract) {
        contract.setRemainingAmount(contract.getTotalAmount());
        return contractRepository.save(contract);
    }

    @Override
    @Transactional
    public Optional<Contract> update(Long id, Contract contract) {
        return contractRepository
                .findWithLockById(id)
                .map(existingContract -> {
                    BigDecimal invoicedAmount =
                            invoiceRepository.sumAmountByContractId(id);

                    if (contract.getTotalAmount().compareTo(invoicedAmount) < 0) {
                        throw new InsufficientContractBalanceException(id);
                    }

                    existingContract.setContractName(contract.getContractName());
                    existingContract.setTotalAmount(contract.getTotalAmount());
                    existingContract.setDurationYears(contract.getDurationYears());
                    existingContract.setYear(contract.getYear());
                    existingContract.setStartDate(contract.getStartDate());
                    existingContract.setEndDate(contract.getEndDate());

                    existingContract.setRemainingAmount(
                            contract.getTotalAmount().subtract(invoicedAmount)
                    );

                    return contractRepository.save(existingContract);
                });
    }

    @Override
    @Transactional
    public Optional<Contract> deleteById(Long id) {
        return contractRepository
                .findWithLockById(id)
                .map(contract -> {

                    if (invoiceRepository.existsByContractId(id)) {
                        throw new ContractHasInvoicesException(id);
                    }

                    contractRepository.delete(contract);

                    return contract;
                });
    }
}
