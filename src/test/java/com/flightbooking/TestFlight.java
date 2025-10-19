package com.flightbooking;

import com.flightbooking.model.Flight;

public class TestFlight {
    public static void main(String[] args) {
        Flight flight = new Flight("BA123", "London", "New York", "10:00", 200);
        System.out.println(flight);
        int seat = flight.reserveSeat();
        System.out.println("Booked seat: " + seat);
        System.out.println(flight);
    }
}
