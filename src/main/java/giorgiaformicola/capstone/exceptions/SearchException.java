package giorgiaformicola.capstone.exceptions;

public class SearchException extends RuntimeException {
    public SearchException() {
        super("Oops, something went wrong with your search! Try to change your search params. If the problem persists, wait a few minutes and try again!");
    }
}
