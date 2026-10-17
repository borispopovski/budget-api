package mk.mdt.budgetapi.exception;

public class ContractHasInvoicesException extends RuntimeException {
    public ContractHasInvoicesException(Long contractId) {
        super("Cannot delete contract " + contractId +
                " because it has associated invoices.");
    }
}
