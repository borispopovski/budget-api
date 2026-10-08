package mk.mdt.budgetapi.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record InvoiceResponse(
        Long id,
        BigDecimal amount,
        LocalDate date,
        Long contractId,
        String contractName
){}