package com.flightbooking.model;

import java.util.ArrayList;

public class Flight {
    private String flightNo, source, destination, departureTime;
    private int totalSeats, availableSeats;
    private ArrayList<Integer> bookedSeats;

    public Flight(String flightNo, String source, String destination, String departureTime, int totalSeats) {
        this.flightNo = flightNo;
        this.source = source;
        this.destination = destination;
        this.departureTime = departureTime;
        this.totalSeats = totalSeats;
        this.availableSeats = totalSeats;
        this.bookedSeats = new ArrayList<>();
    }

    public String getFlightNo() { return flightNo; }
    public String getSource() { return source; }
    public String getDestination() { return destination; }
    public String getDepartureTime() { return departureTime; }
    public int getTotalSeats() { return totalSeats; }
    public int getAvailableSeats() { return availableSeats; }
    public ArrayList<Integer> getBookedSeats() { return bookedSeats; }

    public int reserveSeat() {
        if (availableSeats > 0) {
            int seatNo = totalSeats - availableSeats + 1;
            bookedSeats.add(seatNo);
            availableSeats--;
            return seatNo;
        }
        return -1;
    }

    public void freeSeat() {
        if (!bookedSeats.isEmpty()) {
            bookedSeats.remove(bookedSeats.size() - 1);
            availableSeats++;
        }
    }

    public void delayFlight(String newTime) {
        this.departureTime = newTime;
        System.out.println("Flight " + flightNo + " delayed to " + newTime);
    }

    @Override
    public String toString() {
        return flightNo + " | " + source + " -> " + destination + " | " + departureTime +
               " | Available: " + availableSeats + "/" + totalSeats;
    }
}
