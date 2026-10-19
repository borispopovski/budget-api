package mk.mdt.budgetapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ContractRequest(
        @NotBlank String contractName,
        @NotNull @Positive BigDecimal totalAmount,
        @NotNull @Positive Integer durationYears,
        @NotNull Integer year,
        @NotNull LocalDate startDate,
        @NotNull LocalDate endDate
) {}