package com.flightbooking.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Manages flights and bookings in the flight booking system.
 */

public class FlightManager {
    private List<Flight> flights = new ArrayList<>();
    private List<Booking> bookings = new ArrayList<>();

    public void addFlight(Flight flight) {
        flights.add(flight);
    }

    public List<Flight> getFlights() {
        return new ArrayList<>(flights);
    }

    public List<Booking> getBookings() {
        return new ArrayList<>(bookings);
    }

    public void viewFlights() {
        if (flights.isEmpty()) {
            System.out.println("No flights available.");
        } else {
            for (Flight f : flights) {
                System.out.println(f);
            }
        }
    }

    public Flight findFlight(String flightNo) {
        for (Flight f : flights) {
            if (f.getFlightNo().equalsIgnoreCase(flightNo)) {
                return f;
            }
        }
        return null;
    }

    // Generate unique booking ID
    private String generateBookingId() {
        return "BKG" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    // Book ticket (auto booking ID generation)
    public Booking bookTicket(String type, String flightNo, String passengerName) {
        Flight flight = findFlight(flightNo);
        if (flight == null) {
            System.out.println("Flight not found!");
            return null;
        }
        int seat = flight.reserveSeat();
        if (seat == -1) {
            System.out.println("No seats available on flight " + flightNo);
            return null;
        }

        String bookingId = generateBookingId();  // auto-generate

        Booking booking;
        if (type.equalsIgnoreCase("online")) {
            booking = new OnlineBooking(bookingId, flightNo, passengerName, seat);
        } else {
            booking = new CounterBooking(bookingId, flightNo, passengerName, seat);
        }

        bookings.add(booking);
        booking.processBooking();
        return booking;
    }

    public void cancelTicket(String bookingId) {
        if (bookingId == null) {
            return;
        }
        
        Booking toCancel = null;
        for (Booking b : bookings) {
            if (b != null && bookingId.equalsIgnoreCase(b.getBookingId())) {
                toCancel = b;
                break;
            }
        }

        if (toCancel != null) {
            Flight flight = findFlight(toCancel.getFlightNo());
            if (flight != null) {
                flight.freeSeat();
            }
            bookings.remove(toCancel);
            System.out.println("Booking " + bookingId + " cancelled successfully.");
        } else {
            System.out.println("No booking found with ID " + bookingId);
        }
    }

    public void delayFlight(String flightNo, String newTime) {
        Flight flight = findFlight(flightNo);
        if (flight != null) {
            flight.delayFlight(newTime);
        } else {
            System.out.println("Flight not found!");
        }
    }

    public void viewAllBookings() {
        if (bookings.isEmpty()) {
            System.out.println("No bookings found.");
        } else {
            for (Booking b : bookings) {
                if (b != null) {
                    System.out.println(b);
                }
            }
        }
    }
}
