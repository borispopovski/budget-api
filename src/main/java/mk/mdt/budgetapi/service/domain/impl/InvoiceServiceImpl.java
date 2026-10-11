package mk.mdt.budgetapi.service.domain.impl;

import jakarta.transaction.Transactional;
import mk.mdt.budgetapi.entity.Contract;
import mk.mdt.budgetapi.entity.Invoice;
import mk.mdt.budgetapi.exception.ContractNotFoundException;
import mk.mdt.budgetapi.exception.InsufficientContractBalanceException;
import mk.mdt.budgetapi.repository.InvoiceRepository;
import mk.mdt.budgetapi.service.domain.ContractService;
import mk.mdt.budgetapi.service.domain.InvoiceService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class InvoiceServiceImpl implements InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final ContractService contractService;

    public InvoiceServiceImpl(InvoiceRepository invoiceRepository, ContractService contractService) {
        this.invoiceRepository = invoiceRepository;
        this.contractService = contractService;
    }

    @Override
    public Optional<Invoice> findById(Long id) {
        return this.invoiceRepository.findById(id);
    }

    @Override
    public List<Invoice> findAll() {
        return this.invoiceRepository.findAll();
    }

    @Override
    @Transactional
    public Invoice create(Invoice invoice) {
        Contract contract = getContractWithLock( invoice.getContract().getId());

        invoice.setContract(contract);

        validateContractBalance(contract, invoice.getAmount());

        Invoice savedInvoice = invoiceRepository.save(invoice);

        updateRemainingAmount(contract);

        return savedInvoice;
    }

    @Override
    @Transactional
    public Optional<Invoice> update(Long id, Invoice invoice) {
        return invoiceRepository.findById(id)
                .map(existingInvoice -> {

                    Contract contract = getContractWithLock(existingInvoice.getContract().getId());

                    BigDecimal difference =
                            invoice.getAmount().subtract(existingInvoice.getAmount());

                    validateContractBalance(contract, difference);

                    existingInvoice.setAmount(invoice.getAmount());
                    existingInvoice.setDate(invoice.getDate());

                    Invoice updated = invoiceRepository.save(existingInvoice);
                    updateRemainingAmount(contract);

                    return updated;
                });
    }

    @Override
    @Transactional
    public Optional<Invoice> deleteById(Long id) {
        return invoiceRepository.findById(id)
                .map(invoice -> {
                    Contract contract = getContractWithLock(invoice.getContract().getId());

                    invoiceRepository.delete(invoice);

                    updateRemainingAmount(contract);

                    return invoice;
                });
    }


    private void updateRemainingAmount(Contract contract) {
        BigDecimal invoicedAmount =
                invoiceRepository.sumAmountByContractId(contract.getId());

        contract.setRemainingAmount(
                contract.getTotalAmount().subtract(invoicedAmount)
        );

    }

    private void validateContractBalance(Contract contract, BigDecimal amountDifference) {
        BigDecimal invoicedAmount =
                invoiceRepository.sumAmountByContractId(contract.getId());

        if (invoicedAmount.add(amountDifference)
                .compareTo(contract.getTotalAmount()) > 0) {
            throw new InsufficientContractBalanceException(contract.getId());
        }
    }

    private Contract getContractWithLock(Long contractId) {
        return contractService.findWithLockById(contractId)
                .orElseThrow(() -> new ContractNotFoundException(contractId));
    }
}
