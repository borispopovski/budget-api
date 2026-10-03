package mk.mdt.budgetapi.repository;

import mk.mdt.budgetapi.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    default Invoice create(Invoice invoice) {
        return save(invoice);
    }

    default Invoice getById(Long id) {
        return findById(id).orElseThrow(() -> new RuntimeException("Invoice not found"));
    }

    default List<Invoice> getAll() {
        return findAll();
    }

    default Invoice update(Invoice invoice) {
        return save(invoice);
    }

    default void delete(Long id) {
        deleteById(id);
    }
}