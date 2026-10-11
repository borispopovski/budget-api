package mk.mdt.budgetapi.service.application;

import mk.mdt.budgetapi.dto.InvoiceRequest;
import mk.mdt.budgetapi.dto.InvoiceResponse;

import java.util.List;
import java.util.Optional;

public interface InvoiceApplicationService {
    Optional<InvoiceResponse> findById(Long id);

    List<InvoiceResponse> findAll();

    InvoiceResponse create(InvoiceRequest invoiceRequest);

    Optional<InvoiceResponse> update(Long id, InvoiceRequest invoiceRequest);

    Optional<InvoiceResponse> deleteById(Long id);
}
