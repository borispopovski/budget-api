package mk.mdt.budgetapi.exception;

public class InvoiceNotFoundException extends RuntimeException {
    public InvoiceNotFoundException(Long id) {
        super("An invoice with id %d does not exist.".formatted(id));
    }
}