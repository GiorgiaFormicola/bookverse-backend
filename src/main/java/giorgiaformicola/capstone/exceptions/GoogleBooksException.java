package giorgiaformicola.capstone.exceptions;

public class GoogleBooksException extends RuntimeException {
    public GoogleBooksException() {
        super("Oops, some errors occurred. Try again later!");
    }
}
