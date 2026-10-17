package mk.mdt.budgetapi.exception;

public class ContractNotFoundException extends RuntimeException {
    public ContractNotFoundException(Long id) {
        super("A contract with id %d does not exist.".formatted(id));
    }
}
