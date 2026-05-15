package giorgiaformicola.capstone.exceptions;

public class SearchException extends RuntimeException {
    public SearchException() {
        super("Oops, something went wrong with you search! Wait a few minutes and try again!");
    }
}
