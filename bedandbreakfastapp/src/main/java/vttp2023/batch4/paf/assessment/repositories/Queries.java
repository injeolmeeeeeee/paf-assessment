package vttp2023.batch4.paf.assessment.repositories;

public class Queries {

    public static final String SQL_COUNT_USER = """
            select count(*) from users
            where email = ?
            """;
    public static final String SQL_SAVE_USER = """
            insert into users(email, name)
                value (?, ?)
            """;
    public static final String SQL_SAVE_BOOKING = """
            insert into bookings(booking_id, listing_id, duration, email)
                value (?, ?, ?, ?)
            """;
    
}
