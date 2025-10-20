package com.flightbooking.model;

public abstract class Booking {
    protected String bookingId;
    protected String flightNo;
    protected String passengerName;
    protected int seatNo;

    public Booking(String bookingId, String flightNo, String passengerName, int seatNo) {
        this.bookingId = bookingId;
        this.flightNo = flightNo;
        this.passengerName = passengerName;
        this.seatNo = seatNo;
    }

    public String getBookingId() { return bookingId; }
    public String getFlightNo() { return flightNo; }
    public String getPassengerName() { return passengerName; }
    public int getSeatNo() { return seatNo; }

    public abstract void processBooking();

    @Override
    public String toString() {
        return bookingId + " | Flight: " + flightNo + " | Passenger: " + passengerName + " | Seat: " + seatNo;
    }
}
