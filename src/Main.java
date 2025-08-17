import java.sql.*;
import java.util.Scanner;

public class Main {
    // For connection
    private static final String url = "jdbc:mysql://127.0.0.1:3306/hotel_db";

    private static final String username = "root";

    private static final String password = "root123";

    public static void main(String[] args) {
        // Driver Loaded
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch(ClassNotFoundException e){
            System.out.println(e.getMessage());
        }

        // Create Connection to Database
        try{
            Connection connection = DriverManager.getConnection(url, username, password);
            Statement statement = connection.createStatement();
            while(true) {
                System.out.println();
                System.out.println("HOTEL MANAGEMENT SYSTEM");
                Scanner scanner = new Scanner(System.in);
                System.out.println("1. Reserve a room");
                System.out.println("2. View Reservations");
                System.out.println("3. Get Room Number");
                System.out.println("4. Update Reservations");
                System.out.println("5. Delete Reservations");
                System.out.println("6. Exit");
                System.out.println("Choose an option: ");
                int choice = scanner.nextInt();

                switch(choice){
                    case 1:
                        reserveRoom(connection, statement, scanner);
                        break;
                    case 2:
                        viewReservations(connection, statement);
                        break;
                    case 3:
                        getRoomNumber(connection, statement, scanner);
                        break;
                    case 4:
                        updateReservation(connection, statement, scanner);
                        break;
                    case 5:
                        deleteReservation(connection, statement, scanner);
                        break;
                    case 6:
                        exit();
                        scanner.close();
                        return;
                    default:
                        System.out.println("Invalid choice. Try again....");
                }
            }

        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }
        catch(InterruptedException e){
            throw new RuntimeException(e);
        }
    }

    private static void reserveRoom(Connection connection, Statement statement, Scanner scanner) {
        try{
            System.out.println("Enter Guest Name: ");
            String guestName = scanner.next();
            scanner.nextLine();
            System.out.println("Enter Room Number: ");
            int roomNumber = scanner.nextInt();
            System.out.println("Enter Contact Number: ");
            int contactNumber = scanner.nextInt();

            String query = String.format("INSERT INTO reservations (guest_name, room_number, contact_number) VALUES ('%s', '%d', '%d')", guestName, roomNumber, contactNumber);
            int affectedRows = statement.executeUpdate(query);

            if(affectedRows > 0){
                System.out.println("Reservation Successful!!");
            }
            else{
                System.out.println("Reservation Failed!!");
            }
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    private static void viewReservations(Connection connection, Statement statement) throws SQLException{
        String query = "SELECT reservation_id, guest_name, room_number, contact_number, reservation_date FROM reservations";
        ResultSet resultSet = statement.executeQuery(query);

        System.out.println("Current Reservations:");
        System.out.println("+--------------------+--------------------+--------------------+--------------------+--------------------");
        System.out.println("| Reservation ID     | Guest              | Room Number        | Contact Number     | Reservation Date   ");
        System.out.println("+--------------------+--------------------+--------------------+--------------------+--------------------");

        while(resultSet.next()){
            int reservationId = resultSet.getInt("reservation_id");
            String guestName = resultSet.getString("guest_name");
            int roomNumber = resultSet.getInt("room_number");
            String contactNumber = resultSet.getString("contact_number");
            String reservationDate = resultSet.getTimestamp("reservation_date").toString();

            System.out.printf("| %-14d | %-15s | %-13d | %-20s | %-19s    |\n",
                    reservationId, guestName, roomNumber, contactNumber, reservationDate);

        }

        System.out.println("+--------------------+--------------------+--------------------+--------------------+--------------------");
    }

    private static void getRoomNumber(Connection connection, Statement statement, Scanner scanner) {
        try{
            System.out.println("Enter Reservation ID:");
            int reservationID = scanner.nextInt();
            System.out.println("Enter Guest Name: ");
            String guestName = scanner.next();

            String query = "SELECT room_number FROM reservations " +
                    "WHERE reservation_id = " + reservationID +
                    " AND guest_name = '" + guestName + "'";

            ResultSet resultSet = statement.executeQuery(query);
            if(resultSet.next()){
                int roomNumber = resultSet.getInt("room_number");
                System.out.println("Room number for Reservation ID " + reservationID +
                        " and Guest " + guestName + " is: " + roomNumber);
            }
            else{
                System.out.println("Reservation not found for the given ID and guest name.");
            }
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    private static void updateReservation(Connection connection, Statement statement, Scanner scanner) {
        System.out.println("Enter reservation ID to update: ");
        int reservationId = scanner.nextInt();
        scanner.nextLine();

        if(reservationExists(connection, statement, reservationId)){
            System.out.println("Reservation not found for the given ID.");
            return;
        }

        System.out.println("Enter New Guest Name: ");
        String newGuestName = scanner.nextLine();
        scanner.nextLine();
        System.out.println("Enter New Room Number: ");
        int newRoomNumber = scanner.nextInt();
        System.out.println("Enter New Contact Number: ");
        int newContactNumber = scanner.nextInt();

        String query = "UPDATE reservations SET guest_name = '" + newGuestName + "', " +
                "room_number = '" + newContactNumber + "' " +
                "WHERE reservation_id = " + reservationId;

        try{
            int affectedRows = statement.executeUpdate(query);
            if(affectedRows > 0){
                System.out.println("Reservation Updated Successfully!!");
            }
            else{
                System.out.println("Reservation Updation Failed!!");
            }
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    private static void deleteReservation(Connection connection, Statement statement, Scanner scanner) {
        System.out.println("Enter reservation ID to delete: ");
        int reservationId = scanner.nextInt();
        scanner.nextLine();

        if(reservationExists(connection, statement, reservationId)){
            System.out.println("Reservation not found for the given ID.");
            return;
        }

        String query = "DELETE FROM reservations WHERE reservation_id = " + reservationId;

        try{
            int affectedRows = statement.executeUpdate(query);
            if(affectedRows > 0){
                System.out.println("Reservation Deleted Successfully!!");
            }
            else{
                System.out.println("Reservation Deletion Failed!!");
            }
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    private static boolean reservationExists(Connection connection, Statement statement, int reservationId) {
        try{
            String query = "SELECT reservation_id FROM reservations WHERE reservation_id =" + reservationId;

            ResultSet resultSet = statement.executeQuery(query);
            return !resultSet.next();
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
            return true;
        }
    }

    private static void exit() throws InterruptedException{
        System.out.println("Exiting System");
        int i = 5;
        while(i != 0){
            System.out.print(".");
            Thread.sleep(450);
            i--;
        }
        System.out.println();
        System.out.println("Thank You For Using Hotel Reservation System!!");
    }
}