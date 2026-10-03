package mk.mdt.budgetapi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal totalAmount;
    private BigDecimal remainingAmount;
    private Integer durationYears;
    private Integer year;
    private LocalDate startDate;
    private LocalDate endDate;
    private String contractName;

    @OneToMany(mappedBy = "contract")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Invoice> invoices = new ArrayList<>();
}