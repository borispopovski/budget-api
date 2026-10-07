package mk.mdt.budgetapi.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record InvoiceRequest(
        @NotNull @Positive BigDecimal amount,
        @NotNull LocalDate date,
        @NotNull Long contractId
) {}