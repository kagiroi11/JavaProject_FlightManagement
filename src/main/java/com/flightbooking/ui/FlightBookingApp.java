package com.flightbooking.ui;

import com.flightbooking.model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class FlightBookingApp extends JFrame {
    private FlightManager flightManager;
    private JTabbedPane tabbedPane;
    private JTable flightsTable, bookingsTable;
    private DefaultTableModel flightsModel, bookingsModel;

    public FlightBookingApp() {
        flightManager = new FlightManager();
        initializeUI();
        loadSampleFlights();
    }

    private void initializeUI() {
        setTitle("Flight Booking System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Flights", createFlightsPanel());
        tabbedPane.addTab("Bookings", createBookingsPanel());

        add(tabbedPane);
    }

    private JPanel createFlightsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columns = {"Flight No", "From", "To", "Departure", "Available Seats", "Total Seats"};
        flightsModel = new DefaultTableModel(columns, 0);
        flightsTable = new JTable(flightsModel);
        panel.add(new JScrollPane(flightsTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addFlightBtn = new JButton("Add Flight");
        addFlightBtn.addActionListener(e -> showAddFlightDialog());
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refreshFlightsTable());
        buttonPanel.add(addFlightBtn);
        buttonPanel.add(refreshBtn);

        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createBookingsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columns = {"Booking ID", "Flight No", "Passenger", "Seat No", "Type"};
        bookingsModel = new DefaultTableModel(columns, 0);
        bookingsTable = new JTable(bookingsModel);
        panel.add(new JScrollPane(bookingsTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton bookFlightBtn = new JButton("New Booking");
        bookFlightBtn.addActionListener(e -> showBookingDialog());
        JButton cancelBookingBtn = new JButton("Cancel Booking");
        cancelBookingBtn.addActionListener(e -> cancelSelectedBooking());
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refreshBookingsTable());

        buttonPanel.add(bookFlightBtn);
        buttonPanel.add(cancelBookingBtn);
        buttonPanel.add(refreshBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void showAddFlightDialog() {
        JDialog dialog = new JDialog(this, "Add New Flight", true);
        dialog.setLayout(new GridLayout(0, 2, 5, 5));
        dialog.setSize(400, 300);

        JTextField flightNoField = new JTextField();
        JTextField sourceField = new JTextField();
        JTextField destField = new JTextField();
        JTextField timeField = new JTextField();
        JSpinner seatsSpinner = new JSpinner(new SpinnerNumberModel(100, 1, 500, 1));

        dialog.add(new JLabel("Flight Number:")); dialog.add(flightNoField);
        dialog.add(new JLabel("From:")); dialog.add(sourceField);
        dialog.add(new JLabel("To:")); dialog.add(destField);
        dialog.add(new JLabel("Departure Time (HH:MM):")); dialog.add(timeField);
        dialog.add(new JLabel("Total Seats:")); dialog.add(seatsSpinner);

        JButton addBtn = new JButton("Add");
        addBtn.addActionListener(e -> {
            String flightNo = flightNoField.getText().trim();
            String source = sourceField.getText().trim();
            String dest = destField.getText().trim();
            String time = timeField.getText().trim();
            int seats = (Integer) seatsSpinner.getValue();

            if (flightNo.isEmpty() || source.isEmpty() || dest.isEmpty() || time.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Flight flight = new Flight(flightNo, source, dest, time, seats, seats);
            if(flightManager.addFlight(flight)) {
                refreshFlightsTable();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Flight added successfully!");
            } else {
                JOptionPane.showMessageDialog(dialog, "Error adding flight.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.add(addBtn);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showBookingDialog() {
        if (flightManager.getFlights().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No flights available for booking.", "No Flights", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(this, "New Booking", true);
        dialog.setLayout(new GridLayout(0, 2, 5, 5));

        JComboBox<String> flightCombo = new JComboBox<>();
        for (Flight f : flightManager.getFlights()) {
            flightCombo.addItem(f.getFlightNo() + " - " + f.getSource() + " to " + f.getDestination());
        }

        JTextField nameField = new JTextField();
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"Online", "Counter"});

        dialog.add(new JLabel("Select Flight:")); dialog.add(flightCombo);
        dialog.add(new JLabel("Passenger Name:")); dialog.add(nameField);
        dialog.add(new JLabel("Booking Type:")); dialog.add(typeCombo);

        JButton bookBtn = new JButton("Book");
        bookBtn.addActionListener(e -> {
            String passengerName = nameField.getText().trim();
            if (passengerName.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Enter passenger name.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int index = flightCombo.getSelectedIndex();
            Flight selectedFlight = flightManager.getFlights().get(index);
            String bookingType = (String) typeCombo.getSelectedItem();

            Booking booking = flightManager.bookTicket(bookingType.toLowerCase(), selectedFlight.getFlightNo(), passengerName);
            if (booking != null) {
                refreshBookingsTable();
                refreshFlightsTable();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Booking successful!\nID: " + booking.getBookingId() + "\nSeat: " + booking.getSeatNo());
            }
        });

        dialog.add(bookBtn);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void cancelSelectedBooking() {
        int selectedRow = bookingsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a booking to cancel.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String bookingId = (String) bookingsModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Cancel booking " + bookingId + "?", "Confirm", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            flightManager.cancelTicket(bookingId);
            refreshBookingsTable();
            refreshFlightsTable();
            JOptionPane.showMessageDialog(this, "Booking cancelled successfully.");
        }
    }

    private void refreshFlightsTable() {
        flightsModel.setRowCount(0);
        for (Flight flight : flightManager.getFlights()) {
            flightsModel.addRow(new Object[]{
                    flight.getFlightNo(),
                    flight.getSource(),
                    flight.getDestination(),
                    flight.getDepartureTime(),
                    flight.getAvailableSeats(),
                    flight.getTotalSeats()
            });
        }
    }

    private void refreshBookingsTable() {
        bookingsModel.setRowCount(0);
        for (Booking booking : flightManager.getBookings()) {
            bookingsModel.addRow(new Object[]{
                    booking.getBookingId(),
                    booking.getFlightNo(),
                    booking.getPassengerName(),
                    booking.getSeatNo(),
                    booking instanceof OnlineBooking ? "Online" : "Counter"
            });
        }
    }

    private void loadSampleFlights() {
        flightManager.addFlight(new Flight("AA123", "New York", "London", "08:00", 200, 200));
        flightManager.addFlight(new Flight("BA456", "London", "Paris", "10:30", 150, 150));
        flightManager.addFlight(new Flight("DL789", "Los Angeles", "Tokyo", "14:45", 300, 300));
        refreshFlightsTable();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FlightBookingApp().setVisible(true));
    }
}
