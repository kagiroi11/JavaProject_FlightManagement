public class CounterBooking extends Booking {
    public CounterBooking(String bookingId, String flightNo, String passengerName, int seatNumber) {
        super(bookingId, flightNo, passengerName, seatNumber);
    }

    @Override
    public void processBooking() {
        System.out.println("[Counter] Booking confirmed -> " + toString());
    }
}
