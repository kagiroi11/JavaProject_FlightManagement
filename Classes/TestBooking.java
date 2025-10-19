public class TestBooking {
    public static void main(String[] args) {
        // Create flight
        Flight flight = new Flight("AI101", "Delhi", "Bangalore", "2025-09-10 09:30", 2);

        // Reserve seats from flight
        int seat1 = flight.reserveSeat();
        int seat2 = flight.reserveSeat();

        // Create bookings
        Booking b1 = new OnlineBooking("BKG1001", flight.getFlightNo(), "Rahul", seat1);
        Booking b2 = new CounterBooking("BKG1002", flight.getFlightNo(), "Anita", seat2);

        // Process bookings
        b1.processBooking();
        b2.processBooking();

        // Show flight status after bookings
        System.out.println(flight);

        // Cancel one booking (freeing seat)
        flight.freeSeat();
        System.out.println("After cancellation:");
        System.out.println(flight);
    }
}
