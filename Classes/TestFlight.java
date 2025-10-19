public class TestFlight {
    public static void main(String[] args) {
        Flight f = new Flight("AI101", "Delhi", "Bangalore", "2025-09-10 09:30", 3);

        System.out.println(f); // print flight details

        int seat1 = f.reserveSeat();
        int seat2 = f.reserveSeat();
        System.out.println("Booked seats: " + seat1 + ", " + seat2);
        System.out.println(f);

        f.freeSeat();
        System.out.println("After cancelling one booking:");
        System.out.println(f);
    }
}
