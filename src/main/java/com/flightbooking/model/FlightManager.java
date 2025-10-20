package com.flightbooking.model;

import com.flightbooking.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FlightManager {

    // Add flight to DB
    public boolean addFlight(Flight flight) {
        String sql = "INSERT INTO Flight (FlightNo, Source, Destination, DepartureTime, TotalSeats, AvailableSeats) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, flight.getFlightNo());
            stmt.setString(2, flight.getSource());
            stmt.setString(3, flight.getDestination());
            stmt.setString(4, flight.getDepartureTime());
            stmt.setInt(5, flight.getTotalSeats());
            stmt.setInt(6, flight.getAvailableSeats());
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get all flights
    public List<Flight> getFlights() {
        List<Flight> flights = new ArrayList<>();
        String sql = "SELECT * FROM Flight";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                flights.add(new Flight(
                        rs.getString("FlightNo"),
                        rs.getString("Source"),
                        rs.getString("Destination"),
                        rs.getString("DepartureTime"),
                        rs.getInt("TotalSeats"),
                        rs.getInt("AvailableSeats")
                ));
            }

        } catch (SQLException e) { e.printStackTrace(); }
        return flights;
    }

    // Find flight
    public Flight findFlight(String flightNo) {
        String sql = "SELECT * FROM Flight WHERE FlightNo=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, flightNo);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Flight(
                        rs.getString("FlightNo"),
                        rs.getString("Source"),
                        rs.getString("Destination"),
                        rs.getString("DepartureTime"),
                        rs.getInt("TotalSeats"),
                        rs.getInt("AvailableSeats")
                );
            }

        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    // Update available seats
    public boolean updateSeats(String flightNo, int newAvailableSeats) {
        String sql = "UPDATE Flight SET AvailableSeats=? WHERE FlightNo=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, newAvailableSeats);
            stmt.setString(2, flightNo);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // Book ticket
    public Booking bookTicket(String type, String flightNo, String passengerName) {
        try (Connection conn = DBConnection.getConnection()) {
            Flight flight = findFlight(flightNo);
            if (flight == null) { System.out.println("Flight not found!"); return null; }

            if (flight.getAvailableSeats() <= 0) { System.out.println("No seats available!"); return null; }

            int seatNo = flight.getTotalSeats() - flight.getAvailableSeats() + 1;
            String bookingId = "BKG" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

            String sql = "INSERT INTO Booking (BookingID, FlightNo, PassengerName, SeatNo, BookingType) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, bookingId);
                stmt.setString(2, flightNo);
                stmt.setString(3, passengerName);
                stmt.setInt(4, seatNo);
                stmt.setString(5, type.equalsIgnoreCase("online") ? "Online" : "Counter");
                stmt.executeUpdate();
            }

            updateSeats(flightNo, flight.getAvailableSeats() - 1);

            Booking booking = type.equalsIgnoreCase("online")
                    ? new OnlineBooking(bookingId, flightNo, passengerName, seatNo)
                    : new CounterBooking(bookingId, flightNo, passengerName, seatNo);

            booking.processBooking();
            return booking;

        } catch (SQLException e) { e.printStackTrace(); return null; }
    }

    // Cancel ticket
    public boolean cancelTicket(String bookingId) {
        try (Connection conn = DBConnection.getConnection()) {

            String sql = "SELECT FlightNo FROM Booking WHERE BookingID=?";
            String flightNo;
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, bookingId);
                ResultSet rs = stmt.executeQuery();
                if (!rs.next()) { System.out.println("Booking not found!"); return false; }
                flightNo = rs.getString("FlightNo");
            }

            sql = "DELETE FROM Booking WHERE BookingID=?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, bookingId);
                stmt.executeUpdate();
            }

            Flight flight = findFlight(flightNo);
            updateSeats(flightNo, flight.getAvailableSeats() + 1);

            System.out.println("Booking " + bookingId + " cancelled successfully.");
            return true;

        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // -----------------------------
    // Get all bookings (fix for UI)
    public List<Booking> getBookings() {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM Booking";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String bookingId = rs.getString("BookingID");
                String flightNo = rs.getString("FlightNo");
                String passengerName = rs.getString("PassengerName");
                int seatNo = rs.getInt("SeatNo");
                String type = rs.getString("BookingType");

                Booking booking;
                if ("Online".equalsIgnoreCase(type)) {
                    booking = new OnlineBooking(bookingId, flightNo, passengerName, seatNo);
                } else {
                    booking = new CounterBooking(bookingId, flightNo, passengerName, seatNo);
                }

                bookings.add(booking);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return bookings;
    }
}
