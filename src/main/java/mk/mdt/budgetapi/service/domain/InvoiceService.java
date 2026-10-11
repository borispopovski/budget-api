package mk.mdt.budgetapi.service.domain;


import mk.mdt.budgetapi.entity.Invoice;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface InvoiceService {
    Optional<Invoice> findById(Long id);

    List<Invoice> findAll();

    Invoice create(Invoice invoice);

    Optional<Invoice> update(Long id, Invoice invoice);

    Optional<Invoice> deleteById(Long id);

}
