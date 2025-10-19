import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        FlightManager manager = new FlightManager();

        while (true) {
            System.out.println("\n=== Login ===");
            System.out.println("1. Admin");
            System.out.println("2. Customer");
            System.out.println("3. Exit");
            System.out.print("Enter your role: ");
            int role = sc.nextInt();
            sc.nextLine();

            if (role == 3) {
                System.out.println("Exiting system...");
                break;
            }

            if (role == 1) {
                while (true) {
                    System.out.println("\n--- Admin Menu ---");
                    System.out.println("1. Add Flight");
                    System.out.println("2. View Flights");
                    System.out.println("3. Book Ticket");
                    System.out.println("4. Cancel Ticket");
                    System.out.println("5. Delay Flight");
                    System.out.println("6. View All Bookings");
                    System.out.println("7. Logout");
                    System.out.print("Enter your choice: ");
                    int choice = sc.nextInt();
                    sc.nextLine();

                    if (choice == 7) break;

                    switch (choice) {
                        case 1:
                            System.out.print("Enter Flight No: ");
                            String flightNo = sc.nextLine();
                            System.out.print("Enter Source: ");
                            String source = sc.nextLine();
                            System.out.print("Enter Destination: ");
                            String dest = sc.nextLine();
                            System.out.print("Enter Departure Time: ");
                            String time = sc.nextLine();
                            System.out.print("Enter Total Seats: ");
                            int seats = sc.nextInt(); sc.nextLine();
                            manager.addFlight(new Flight(flightNo, source, dest, time, seats));
                            System.out.println("✅ Flight added successfully!");
                            break;

                        case 2:
                            manager.viewFlights();
                            break;

                        case 3:
                            System.out.print("Enter Flight No: ");
                            String fNo = sc.nextLine();
                            System.out.print("Enter Passenger Name: ");
                            String pname = sc.nextLine();
                            System.out.print("Booking Type (online/counter): ");
                            String type = sc.nextLine();

                            Booking booking = manager.bookTicket(type, fNo, pname);
                            if (booking != null) {
                                System.out.println("✅ Booking confirmed!");
                                System.out.println("   Booking ID: " + booking.getBookingId());
                                System.out.println("   Passenger: " + booking.getPassengerName());
                                System.out.println("   Flight No: " + booking.getFlightNo());
                                System.out.println("   Seat No.:  " + booking.getSeatNo());
                            }
                            break;

                        case 4:
                            System.out.print("Enter Booking ID to cancel: ");
                            String cancelId = sc.nextLine().trim(); // trim spaces
                            manager.cancelTicket(cancelId);
                            break;

                        case 5:
                            System.out.print("Enter Flight No: ");
                            String dfNo = sc.nextLine();
                            System.out.print("Enter New Departure Time: ");
                            String newTime = sc.nextLine();
                            manager.delayFlight(dfNo, newTime);
                            break;

                        case 6:
                            manager.viewAllBookings();
                            break;

                        default:
                            System.out.println("Invalid choice, try again.");
                    }
                }
            }

            if (role == 2) {
                System.out.print("Enter your name: ");
                String customerName = sc.nextLine();

                while (true) {
                    System.out.println("\n--- Customer Menu ---");
                    System.out.println("1. View Flights");
                    System.out.println("2. Book Ticket");
                    System.out.println("3. Cancel Ticket");
                    System.out.println("4. View My Bookings");
                    System.out.println("5. Logout");
                    System.out.print("Enter your choice: ");
                    int choice = sc.nextInt();
                    sc.nextLine();

                    if (choice == 5) break;

                    switch (choice) {
                        case 1:
                            manager.viewFlights();
                            break;

                        case 2:
                            System.out.print("Enter Flight No: ");
                            String fNo = sc.nextLine();
                            System.out.print("Booking Type (online/counter): ");
                            String type = sc.nextLine();

                            Booking booking = manager.bookTicket(type, fNo, customerName);
                            if (booking != null) {
                                System.out.println("✅ Booking confirmed!");
                                System.out.println("   Booking ID: " + booking.getBookingId());
                                System.out.println("   Passenger: " + booking.getPassengerName());
                                System.out.println("   Flight No: " + booking.getFlightNo());
                                System.out.println("   Seat No.:  " + booking.getSeatNo());
                            }
                            break;

                        case 3:
                            System.out.print("Enter Booking ID to cancel: ");
                            String cancelId = sc.nextLine().trim(); // trim spaces
                            manager.cancelTicket(cancelId);
                            break;

                        case 4:
                            manager.viewBookingsByPassenger(customerName);
                            break;

                        default:
                            System.out.println("Invalid choice, try again.");
                    }
                }
            }
        }

        sc.close();
    }
}
