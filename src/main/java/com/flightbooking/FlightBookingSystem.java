package com.flightbooking;

import com.flightbooking.model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.UUID;
import javax.swing.SpinnerNumberModel;

public class FlightBookingSystem extends JFrame {
    private final FlightManager flightManager = new FlightManager();
    private final JTabbedPane tabbedPane = new JTabbedPane();
    private JTable flightsTable;
    private JTable bookingsTable;
    private DefaultTableModel flightsModel;
    private DefaultTableModel bookingsModel;

    public FlightBookingSystem() {
        // Initialize UI
        setTitle("Flight Booking System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);
        
        // Add tabs
        tabbedPane.addTab("Flights", createFlightsPanel());
        tabbedPane.addTab("Bookings", createBookingsPanel());
        
        add(tabbedPane);
        
        // Load sample data
        loadSampleData();
    }
    
    private JPanel createFlightsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Create table model
        String[] columnNames = {"Flight No.", "From", "To", "Departure", "Available Seats"};
        flightsModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Create table
        flightsTable = new JTable(flightsModel);
        JScrollPane scrollPane = new JScrollPane(flightsTable);
        
        // Add components to panel
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Add buttons
        JButton addFlightBtn = new JButton("Add Flight");
        addFlightBtn.addActionListener(e -> showAddFlightDialog());
        
        JButton bookFlightBtn = new JButton("Book Flight");
        bookFlightBtn.addActionListener(e -> showBookingDialog());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(addFlightBtn);
        buttonPanel.add(bookFlightBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createBookingsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Create table model
        String[] columnNames = {"Booking ID", "Flight", "Passenger", "Seat"};
        bookingsModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Create table
        bookingsTable = new JTable(bookingsModel);
        JScrollPane scrollPane = new JScrollPane(bookingsTable);
        
        // Add components to panel
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Add cancel booking button
        JButton cancelBtn = new JButton("Cancel Booking");
        cancelBtn.addActionListener(e -> cancelBooking());
        
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refreshBookings());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(cancelBtn);
        buttonPanel.add(refreshBtn);
        
        // Add components to panel
        panel.add(new JScrollPane(bookingsTable), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void showAddFlightDialog() {
        JDialog dialog = new JDialog(this, "Add New Flight", true);
        dialog.setLayout(new BorderLayout(10, 10));
        
        // Create panel for form fields
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        
        JTextField flightNoField = new JTextField(15);
        JTextField fromField = new JTextField(15);
        JTextField toField = new JTextField(15);
        JTextField timeField = new JTextField(15);
        JSpinner seatsSpinner = new JSpinner(new SpinnerNumberModel(100, 1, 500, 1));
        
        // Add components to form panel
        formPanel.add(new JLabel("Flight Number*:"));
        formPanel.add(flightNoField);
        formPanel.add(new JLabel("From*:"));
        formPanel.add(fromField);
        formPanel.add(new JLabel("To*:"));
        formPanel.add(toField);
        formPanel.add(new JLabel("Departure Time (HH:MM)*:"));
        formPanel.add(timeField);
        formPanel.add(new JLabel("Total Seats*:"));
        formPanel.add(seatsSpinner);
        
        // Create panel for buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        JButton addBtn = new JButton("Add Flight");
        addBtn.addActionListener(e -> {
            try {
                String flightNo = flightNoField.getText().trim();
                String from = fromField.getText().trim();
                String to = toField.getText().trim();
                String time = timeField.getText().trim();
                int seats = (Integer) seatsSpinner.getValue();
                
                // Input validation
                if (flightNo.isEmpty() || from.isEmpty() || to.isEmpty() || time.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please fill in all required fields.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Validate time format (simple check)
                if (!time.matches("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$")) {
                    JOptionPane.showMessageDialog(dialog, "Please enter a valid time in HH:MM format.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                Flight flight = new Flight(flightNo, from, to, time, seats);
                flightManager.addFlight(flight);
                refreshFlights();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Flight added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        // Add buttons to button panel
        buttonPanel.add(cancelBtn);
        buttonPanel.add(addBtn);
        
        // Add components to dialog
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        // Set dialog properties
        dialog.setSize(400, 250);
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void showBookingDialog() {
        if (flightManager.getFlights().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No flights available for booking.", "No Flights", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JDialog dialog = new JDialog(this, "New Booking", true);
        dialog.setLayout(new GridLayout(4, 2, 5, 5));
        
        // Flight selection
        JComboBox<Flight> flightCombo = new JComboBox<>();
        for (Flight f : flightManager.getFlights()) {
            if (f.getAvailableSeats() > 0) {
                flightCombo.addItem(f);
            }
        }
        
        if (flightCombo.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this, "No flights with available seats.", "No Seats", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JTextField nameField = new JTextField();
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"Online", "Counter"});
        
        dialog.add(new JLabel("Select Flight:"));
        dialog.add(flightCombo);
        dialog.add(new JLabel("Passenger Name:"));
        dialog.add(nameField);
        dialog.add(new JLabel("Booking Type:"));
        dialog.add(typeCombo);
        
        JButton bookBtn = new JButton("Book");
        bookBtn.addActionListener(e -> {
            try {
                String passengerName = nameField.getText().trim();
                if (passengerName.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please enter passenger name.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                Flight selectedFlight = (Flight) flightCombo.getSelectedItem();
                String bookingType = (String) typeCombo.getSelectedItem();
                
                if (selectedFlight == null) {
                    JOptionPane.showMessageDialog(this, "Please select a valid flight.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                String bookingId = generateBookingId();
                int seat = selectedFlight.reserveSeat();
                if (seat == -1) {
                    JOptionPane.showMessageDialog(this, "No seats available on this flight.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                try {
                    // Use FlightManager to handle the booking
                    Booking booking = flightManager.bookTicket(
                        bookingType.toLowerCase(), 
                        selectedFlight.getFlightNo(), 
                        passengerName
                    );
                    
                    if (booking != null) {
                        // Refresh the UI
                        refreshBookings();
                        refreshFlights();
                        
                        // Show success message
                        JOptionPane.showMessageDialog(this, 
                            "Booking successful!\n" +
                            "Booking ID: " + booking.getBookingId() + "\n" +
                            "Flight: " + booking.getFlightNo() + "\n" +
                            "Seat: " + booking.getSeatNo(),
                            "Booking Confirmation",
                            JOptionPane.INFORMATION_MESSAGE
                        );
                        
                        // Close the dialog
                        dialog.dispose();
                    } else {
                        JOptionPane.showMessageDialog(dialog, 
                            "Failed to create booking. Please try again.", 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE
                        );
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Error creating booking: " + ex.getMessage(), 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        dialog.add(new JLabel()); // Empty cell for layout
        dialog.add(bookBtn);
        
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void cancelBooking() {
        int selectedRow = bookingsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a booking to cancel.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String bookingId = (String) bookingsModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(
            this, 
            "Are you sure you want to cancel booking " + bookingId + "?",
            "Confirm Cancellation",
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            // Manually handle ticket cancellation
            Booking toRemove = null;
            for (Booking b : flightManager.getBookings()) {
                if (b.getBookingId().equals(bookingId)) {
                    toRemove = b;
                    // Find the flight and free the seat
                    for (Flight f : flightManager.getFlights()) {
                        if (f.getFlightNo().equals(b.getFlightNo())) {
                            f.freeSeat();
                            break;
                        }
                    }
                    break;
                }
            }
            if (toRemove != null) {
                flightManager.getBookings().remove(toRemove);
            }
            refreshBookings();
            refreshFlights();
            JOptionPane.showMessageDialog(this, "Booking cancelled successfully.");
        }
    }
    
    private void refreshFlights() {
        flightsModel.setRowCount(0);
        for (Flight flight : flightManager.getFlights()) {
            flightsModel.addRow(new Object[]{
                flight.getFlightNo(),
                flight.getSource(),
                flight.getDestination(),
                flight.getDepartureTime(),
                flight.getAvailableSeats() + "/" + flight.getTotalSeats()
            });
        }
    }
    
    // Helper method to generate booking ID
    private String generateBookingId() {
        return "BKG" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    private void refreshBookings() {
        if (bookingsModel == null) return;
        
        // Store the current selection
        int selectedRow = bookingsTable.getSelectedRow();
        
        // Update the model
        bookingsModel.setRowCount(0);
        for (Booking booking : flightManager.getBookings()) {
            bookingsModel.addRow(new Object[]{
                booking.getBookingId(),
                booking.getFlightNo(),
                booking.getPassengerName(),
                "Seat " + booking.getSeatNo()
            });
        }
        
        // Restore selection if possible
        if (selectedRow >= 0 && selectedRow < bookingsModel.getRowCount()) {
            bookingsTable.setRowSelectionInterval(selectedRow, selectedRow);
        }
    }
    
    private void loadSampleData() {
        // Add some sample flights
        flightManager.addFlight(new Flight("AA123", "New York", "London", "08:00", 200));
        flightManager.addFlight(new Flight("BA456", "London", "Paris", "10:30", 150));
        flightManager.addFlight(new Flight("DL789", "Los Angeles", "Tokyo", "14:45", 300));
        
        // Add a sample booking
        Flight sampleFlight = flightManager.getFlights().get(0);
        int seat = sampleFlight.reserveSeat();
        if (seat != -1) {
            Booking sampleBooking = new OnlineBooking("SAMPLE001", sampleFlight.getFlightNo(), "John Doe", seat);
            flightManager.getBookings().add(sampleBooking);
        }
        
        // Refresh tables
        refreshFlights();
        refreshBookings();
    }
    
    public static void main(String[] args) {
        try {
            // Set system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            FlightBookingSystem app = new FlightBookingSystem();
            app.setVisible(true);
        });
    }
}
