package mk.mdt.budgetapi.service.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mk.mdt.budgetapi.dto.InvoiceRequest;
import mk.mdt.budgetapi.dto.InvoiceResponse;
import mk.mdt.budgetapi.entity.Contract;
import mk.mdt.budgetapi.entity.Invoice;
import mk.mdt.budgetapi.exception.ContractNotFoundException;
import mk.mdt.budgetapi.exception.InsufficientContractBalanceException;
import mk.mdt.budgetapi.mapper.InvoiceMapper;
import mk.mdt.budgetapi.repository.ContractRepository;
import mk.mdt.budgetapi.repository.InvoiceRepository;
import mk.mdt.budgetapi.service.InvoiceService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final InvoiceMapper invoiceMapper;
    private final ContractRepository contractRepository;

    @Override
    public Optional<InvoiceResponse> findById(Long id) {
        return invoiceRepository.findById(id)
                .map(invoiceMapper::toInvoiceResponse);
    }

    @Override
    public List<InvoiceResponse> findAll() {
        return invoiceRepository.findAll().stream()
                .map(invoiceMapper::toInvoiceResponse)
                .toList();
    }

    @Override
    @Transactional
    public InvoiceResponse create(InvoiceRequest invoiceRequest) {
        log.debug("Creating invoice for contract {}, amount={}", invoiceRequest.contractId(), invoiceRequest.amount());
        Contract contract = getContractWithLock(invoiceRequest.contractId());

        validateContractBalance(contract, invoiceRequest.amount());

        Invoice invoice = invoiceMapper.toInvoice(invoiceRequest, contract);

        Invoice saved = invoiceRepository.save(invoice);

        updateRemainingAmount(contract);

        log.info("Invoice created: id={}, contractId={}, amount={}", saved.getId(), contract.getId(), saved.getAmount());
        return invoiceMapper.toInvoiceResponse(saved);
    }

    @Override
    @Transactional
    public Optional<InvoiceResponse> update(Long id, InvoiceRequest invoiceRequest) {
        return invoiceRepository.findById(id)
                .map(existingInvoice -> {

                    Contract contract = getContractWithLock(existingInvoice.getContract().getId());

                    BigDecimal difference = invoiceRequest.amount()
                            .subtract(existingInvoice.getAmount());

                    validateContractBalance(contract, difference);

                    existingInvoice.setAmount(invoiceRequest.amount());
                    existingInvoice.setDate(invoiceRequest.date());

                    Invoice updated = invoiceRepository.save(existingInvoice);

                    updateRemainingAmount(contract);

                    log.info("Invoice updated: id={}, contractId={}", id, contract.getId());
                    return invoiceMapper.toInvoiceResponse(updated);
                });
    }

    @Override
    @Transactional
    public Optional<InvoiceResponse> deleteById(Long id) {
        return invoiceRepository.findById(id)
                .map(invoice -> {
                    Contract contract = getContractWithLock(invoice.getContract().getId());

                    invoiceRepository.delete(invoice);

                    updateRemainingAmount(contract);

                    log.info("Invoice deleted: id={}, contractId={}", id, contract.getId());
                    return invoiceMapper.toInvoiceResponse(invoice);
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
            log.warn("Insufficient balance on contract {}: invoiced={}, requested change={}, total={}", contract.getId(), invoicedAmount, amountDifference, contract.getTotalAmount());
            throw new InsufficientContractBalanceException(contract.getId());
        }
    }

    private Contract getContractWithLock(Long contractId) {
        return contractRepository.findWithLockById(contractId)
                .orElseThrow(() -> {
                    log.warn("Contract not found: id={}", contractId);
                    return new ContractNotFoundException(contractId);
                });
    }
}
