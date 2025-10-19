# Flight Booking System

A Java Swing application for managing flight bookings.

## Project Structure

```
src/
├── main/
│   └── java/
│       └── com/
│           └── flightbooking/
│               ├── model/         # Domain models
│               │   ├── Booking.java
│               │   ├── CounterBooking.java
│               │   ├── Flight.java
│               │   ├── FlightManager.java
│               │   └── OnlineBooking.java
│               └── ui/            # User interface
│                   ├── FlightBookingApp.java
│                   └── Main.java
└── test/
    └── java/
        └── com/
            └── flightbooking/
                └── TestFlight.java
```

## How to Run

### Prerequisites
- Java 11 or higher
- Maven

### Running with Maven

1. Build the project:
   ```bash
   mvn clean package
   ```

2. Run the application:
   ```bash
   mvn exec:java -Dexec.mainClass="com.flightbooking.ui.Main"
   ```

### Running with Java

1. Compile the project:
   ```bash
   javac -d target/classes -sourcepath src/main/java src/main/java/com/flightbooking/ui/Main.java
   ```

2. Run the application:
   ```bash
   java -cp target/classes com.flightbooking.ui.Main
   ```

## Features

- View available flights
- Add new flights
- Book flights (Online/Counter)
- Cancel bookings
- View all bookings
- Real-time seat availability

## License

This project is licensed under the MIT License.
