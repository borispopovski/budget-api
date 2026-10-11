package mk.mdt.budgetapi.service.application.Impl;

import mk.mdt.budgetapi.dto.InvoiceRequest;
import mk.mdt.budgetapi.dto.InvoiceResponse;
import mk.mdt.budgetapi.entity.Contract;
import mk.mdt.budgetapi.entity.Invoice;
import mk.mdt.budgetapi.exception.ContractNotFoundException;
import mk.mdt.budgetapi.mapper.InvoiceMapper;
import mk.mdt.budgetapi.service.application.InvoiceApplicationService;
import mk.mdt.budgetapi.service.domain.ContractService;
import mk.mdt.budgetapi.service.domain.InvoiceService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InvoiceApplicationServiceImpl implements InvoiceApplicationService {
    private final InvoiceService invoiceService;
    private final ContractService contractService;
    private final InvoiceMapper invoiceMapper;

    public InvoiceApplicationServiceImpl(InvoiceService invoiceService, ContractService contractService, InvoiceMapper invoiceMapper) {
        this.invoiceService = invoiceService;
        this.contractService = contractService;
        this.invoiceMapper = invoiceMapper;
    }

    @Override
    public Optional<InvoiceResponse> findById(Long id) {
        return invoiceService.findById(id)
                .map(invoiceMapper::toInvoiceResponse);
    }

    @Override
    public List<InvoiceResponse> findAll() {
        return invoiceService.findAll()
                .stream()
                .map(invoiceMapper::toInvoiceResponse)
                .toList();
    }

    @Override
    public InvoiceResponse create(InvoiceRequest invoiceRequest) {
        Contract contract = contractService.findById(invoiceRequest.contractId())
                .orElseThrow(() -> new ContractNotFoundException(invoiceRequest.contractId()));

        return invoiceMapper.toInvoiceResponse(
                invoiceService.create(invoiceMapper.toInvoice(invoiceRequest, contract)));
    }

    @Override
    public Optional<InvoiceResponse> update(Long id, InvoiceRequest invoiceRequest) {
        Contract contract = contractService.findById(invoiceRequest.contractId())
                .orElseThrow(() -> new ContractNotFoundException(invoiceRequest.contractId()));

        return invoiceService.update(id, invoiceMapper.toInvoice(invoiceRequest, contract))
                .map(invoiceMapper::toInvoiceResponse);
    }

    @Override
    public Optional<InvoiceResponse> deleteById(Long id) {
        return invoiceService
                .deleteById(id)
                .map(invoiceMapper::toInvoiceResponse);
    }
}
