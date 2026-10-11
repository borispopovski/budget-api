package mk.mdt.budgetapi.mapper;

import mk.mdt.budgetapi.dto.ContractRequest;
import mk.mdt.budgetapi.dto.ContractResponse;
import mk.mdt.budgetapi.entity.Contract;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ContractMapper {

    ContractResponse toContractResponse(Contract contract);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "remainingAmount", source = "totalAmount")
    @Mapping(target = "invoices", ignore = true)
    Contract toContract(ContractRequest contractRequest);

}
