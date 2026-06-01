package giorgiaformicola.capstone.payloads.books;

public record BookStatsDTO(
        long saved,
        long read,
        long reading,
        long reviews
) {
}
