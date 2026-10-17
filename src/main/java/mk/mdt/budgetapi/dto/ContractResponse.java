package mk.mdt.budgetapi.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ContractResponse(
        Long id,
        String contractName,
        BigDecimal totalAmount,
        BigDecimal remainingAmount,
        Integer durationYears,
        Integer year,
        LocalDate startDate,
        LocalDate endDate
) {}