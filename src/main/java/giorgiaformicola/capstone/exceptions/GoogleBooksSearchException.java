package giorgiaformicola.capstone.exceptions;

public class GoogleBooksSearchException extends RuntimeException {
    public GoogleBooksSearchException(String message, Exception ex) {
        super(message + " (" + ex + ") ");
    }

    public GoogleBooksSearchException(String message) {
        super(message);
    }
}
