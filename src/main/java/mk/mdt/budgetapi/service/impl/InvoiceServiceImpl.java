package mk.mdt.budgetapi.service.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
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
        Contract contract = getContractWithLock(invoiceRequest.contractId());

        validateContractBalance(contract, invoiceRequest.amount());

        Invoice invoice = invoiceMapper.toInvoice(invoiceRequest, contract);

        Invoice saved = invoiceRepository.save(invoice);

        updateRemainingAmount(contract);

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
            throw new InsufficientContractBalanceException(contract.getId());
        }
    }

    private Contract getContractWithLock(Long contractId) {
        return contractRepository.findWithLockById(contractId)
                .orElseThrow(() -> new ContractNotFoundException(contractId));
    }
}
