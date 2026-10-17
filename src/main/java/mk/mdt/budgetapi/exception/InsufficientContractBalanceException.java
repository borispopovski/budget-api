package mk.mdt.budgetapi.exception;

public class InsufficientContractBalanceException extends RuntimeException {
    public InsufficientContractBalanceException(Long id) {
        super("Insufficient remaining balance for contract: " + id);
    }
}
