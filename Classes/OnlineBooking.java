public class OnlineBooking extends Booking {
    public OnlineBooking(String bookingId, String flightNo, String passengerName, int seatNumber) {
        super(bookingId, flightNo, passengerName, seatNumber);
    }

    @Override
    public void processBooking() {
        System.out.println("[Online] Booking confirmed -> " + toString());
    }
}
