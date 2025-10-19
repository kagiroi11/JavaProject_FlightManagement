import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

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
        
        // Flights Tab
        JPanel flightsPanel = createFlightsPanel();
        tabbedPane.addTab("Flights", flightsPanel);
        
        // Bookings Tab
        JPanel bookingsPanel = createBookingsPanel();
        tabbedPane.addTab("Bookings", bookingsPanel);
        
        add(tabbedPane);
    }
    
    private JPanel createFlightsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Table model for flights
        String[] columns = {"Flight No", "From", "To", "Departure", "Available Seats", "Total Seats"};
        flightsModel = new DefaultTableModel(columns, 0);
        flightsTable = new JTable(flightsModel);
        
        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(flightsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Buttons panel
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
        
        // Table model for bookings
        String[] columns = {"Booking ID", "Flight No", "Passenger", "Seat No", "Type"};
        bookingsModel = new DefaultTableModel(columns, 0);
        bookingsTable = new JTable(bookingsModel);
        
        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(bookingsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Buttons panel
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
        
        dialog.add(new JLabel("Flight Number:"));
        dialog.add(flightNoField);
        dialog.add(new JLabel("From:"));
        dialog.add(sourceField);
        dialog.add(new JLabel("To:"));
        dialog.add(destField);
        dialog.add(new JLabel("Departure Time (HH:MM):"));
        dialog.add(timeField);
        dialog.add(new JLabel("Total Seats:"));
        dialog.add(seatsSpinner);
        
        JButton addBtn = new JButton("Add");
        addBtn.addActionListener(e -> {
            try {
                String flightNo = flightNoField.getText().trim();
                String source = sourceField.getText().trim();
                String dest = destField.getText().trim();
                String time = timeField.getText().trim();
                int seats = (Integer) seatsSpinner.getValue();
                
                if (flightNo.isEmpty() || source.isEmpty() || dest.isEmpty() || time.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                Flight flight = new Flight(flightNo, source, dest, time, seats);
                flightManager.addFlight(flight);
                refreshFlightsTable();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Flight added successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
        
        // Flight selection
        JComboBox<String> flightCombo = new JComboBox<>();
        for (Flight f : flightManager.getFlights()) {
            flightCombo.addItem(f.getFlightNo() + " - " + f.getSource() + " to " + f.getDestination());
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
                
                int selectedIndex = flightCombo.getSelectedIndex();
                Flight selectedFlight = flightManager.getFlights().get(selectedIndex);
                String bookingType = (String) typeCombo.getSelectedItem();
                
                Booking booking = flightManager.bookTicket(
                    bookingType.toLowerCase(), 
                    selectedFlight.getFlightNo(), 
                    passengerName
                );
                
                if (booking != null) {
                    refreshBookingsTable();
                    refreshFlightsTable();
                    dialog.dispose();
                    JOptionPane.showMessageDialog(this, "Booking successful!\nBooking ID: " + booking.getBookingId() + 
                                                  "\nSeat: " + booking.getSeatNo());
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
            JOptionPane.showMessageDialog(this, "Please select a booking to cancel.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String bookingId = (String) bookingsModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to cancel booking " + bookingId + "?", 
            "Confirm Cancellation", 
            JOptionPane.YES_NO_OPTION);
            
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
            Object[] row = {
                flight.getFlightNo(),
                flight.getSource(),
                flight.getDestination(),
                flight.getDepartureTime(),
                flight.getAvailableSeats(),
                flight.getTotalSeats()
            };
            flightsModel.addRow(row);
        }
    }
    
    private void refreshBookingsTable() {
        bookingsModel.setRowCount(0);
        for (Booking booking : flightManager.getBookings()) {
            Object[] row = {
                booking.getBookingId(),
                booking.getFlightNo(),
                booking.getPassengerName(),
                booking.getSeatNo(),
                booking instanceof OnlineBooking ? "Online" : "Counter"
            };
            bookingsModel.addRow(row);
        }
    }
    
    private void loadSampleFlights() {
        // Add some sample flights
        flightManager.addFlight(new Flight("AA123", "New York", "London", "08:00", 200));
        flightManager.addFlight(new Flight("BA456", "London", "Paris", "10:30", 150));
        flightManager.addFlight(new Flight("DL789", "Los Angeles", "Tokyo", "14:45", 300));
        refreshFlightsTable();
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Set system look and feel
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            FlightBookingApp app = new FlightBookingApp();
            app.setVisible(true);
        });
    }
}
