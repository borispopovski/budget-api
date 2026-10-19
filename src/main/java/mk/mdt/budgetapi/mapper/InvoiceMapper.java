package mk.mdt.budgetapi.mapper;

import mk.mdt.budgetapi.dto.InvoiceRequest;
import mk.mdt.budgetapi.dto.InvoiceResponse;
import mk.mdt.budgetapi.entity.Contract;
import mk.mdt.budgetapi.entity.Invoice;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InvoiceMapper {

    @Mapping(target = "contractId", source = "contract.id")
    @Mapping(target = "contractName", source = "contract.contractName")
    InvoiceResponse toInvoiceResponse(Invoice invoice);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "contract", ignore = true)
    Invoice toInvoice(InvoiceRequest invoiceRequest, Contract contract);
}
