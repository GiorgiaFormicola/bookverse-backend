package giorgiaformicola.capstone.exceptions;

public class GoogleBooksSearchException extends RuntimeException {
    public GoogleBooksSearchException() {
        super("Oops, some errors occurred. Try again later!");
    }
}
