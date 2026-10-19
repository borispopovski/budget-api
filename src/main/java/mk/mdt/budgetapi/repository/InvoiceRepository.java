package mk.mdt.budgetapi.repository;

import mk.mdt.budgetapi.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {


    @Query("""
    SELECT COALESCE(SUM(i.amount), 0)
    FROM Invoice i
    WHERE i.contract.id = :contractId
    """)
    BigDecimal sumAmountByContractId(@Param("contractId") Long contractId);

    boolean existsByContractId(Long contractId);
}