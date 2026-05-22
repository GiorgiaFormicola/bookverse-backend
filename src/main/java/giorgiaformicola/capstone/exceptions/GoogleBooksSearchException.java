package giorgiaformicola.capstone.exceptions;

public class GoogleBooksSearchException extends RuntimeException {
    public GoogleBooksSearchException() {
        super("Oops, Google Books API in temporary unreachable. Try again later!");
    }

    public GoogleBooksSearchException(String message, Exception ex) {
        super(message + "(" + ex + ")");
    }

    public GoogleBooksSearchException(String message) {
        super(message);
    }
}
