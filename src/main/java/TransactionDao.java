import java.sql.*;
import java.util.Scanner;

public class TransactionDao {
    private Connection connection;
    private Scanner scanner;

    public void run() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Sterownik do bazy nie został znaleziony" + e.getMessage());
            return;
        }
        try {
            String url = "jdbc:mysql://localhost:3306/householdbudget?serverTimezone=UTC&characterEncoding=utf8";
            connection = DriverManager.getConnection(url, "root", "admin");
        } catch (SQLException e) {
            System.out.println("Błąd podczas nawiązywania połączenia: " + e.getMessage());
            return;
        }
        while (true) {
            System.out.println("Co chcesz zrobić?");
            System.out.println("1 - dodać transakcje");
            System.out.println("2 - modyfikować transakcje");
            System.out.println("3 - usunąć transakcje");
            System.out.println("4 - wyświetlić wszystkie przychody");
            System.out.println("5 - wyświetlić wszystkie wydatki");
            System.out.println("0 - Koniec");
            scanner = new Scanner(System.in);
            String option = scanner.nextLine();
            switch (option) {
                case "1":
                    addTransaction();
                    break;
                case "2":
                    modifyTransaction();
                    break;
                case "3":
                    deleteTransaction();
                    break;
                case "4":
                    showTransactions('P');
                    break;
                case "5":
                    showTransactions('W');
                    break;
                case "0":
                    close();
                    return;
                default:
                    System.out.println("Nieznana opcja!");
            }
        }
    }

    private void addTransaction() {
        System.out.println("Podaj typ transakcji" +
                "\nP - przychód" +
                "\nW - wydatek");
        char type = scanner.next().charAt(0);
        scanner.nextLine();
        System.out.println("Podaj opis transakcji");
        String description = scanner.nextLine();
        System.out.println("Podaj wysokość transakcji");
        double amount = scanner.nextDouble();
        scanner.nextLine();
        System.out.println("Podaj datę transakcji w formacie XXXX-XX-XX");
        String date = scanner.nextLine();
        Transaction transaction = new Transaction(type, description, amount, date);
        try {
            String sql = "INSERT INTO transaction (type, description, amount, date) VALUES (?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, String.valueOf(transaction.getType()));
            preparedStatement.setString(2, transaction.getDescription());
            preparedStatement.setDouble(3, transaction.getAmount());
            preparedStatement.setString(4, transaction.getDate());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Niepowodzenie podczas zapisu do bazy" + " " + e.getMessage());
        }
    }

    private void modifyTransaction() {
        System.out.println("Podaj id transakcji, którą chcesz zaktualizować");
        int id = scanner.nextInt();
        scanner.nextLine();
        System.out.println("Podaj typ transakcji" +
                "\nP - przychód" +
                "\nW - wydatek");
        char type = scanner.next().charAt(0);
        scanner.nextLine();
        System.out.println("Podaj opis transakcji");
        String description = scanner.nextLine();
        System.out.println("Podaj wysokość transakcji");
        double amount = scanner.nextDouble();
        scanner.nextLine();
        System.out.println("Podaj datę transakcji w formacie XXXX-XX-XX");
        String date = scanner.nextLine();
        Transaction transaction = new Transaction(id, type, description, amount, date);
        try {
            String sql = "UPDATE transaction SET type = ?, description = ?, amount = ?, date = ? WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, String.valueOf(transaction.getType()));
            preparedStatement.setString(2, transaction.getDescription());
            preparedStatement.setDouble(3, transaction.getAmount());
            preparedStatement.setString(4, transaction.getDate());
            preparedStatement.setInt(5, transaction.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Niepowodzenie podczas zapisu do bazy" + " " + e.getMessage());
        }
    }

    private void showTransactions(char type) {
        try {
            String sql = "SELECT * FROM transaction WHERE type = ?";
            PreparedStatement queryStatement = connection.prepareStatement(sql);
            String transactionType = String.valueOf(type);
            queryStatement.setString(1, transactionType);
            ResultSet resultSet = queryStatement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                char type1 = resultSet.getString("type").charAt(0);
                String description = resultSet.getString("description");
                double amount = resultSet.getInt("amount");
                String date = resultSet.getString("date");
                System.out.println("id: " + id + "\nTyp: " + type1 + "\nOpis: " + description + "\nKwota: " + amount + "\nData: " + date + "\n");
            }
        } catch (SQLException e) {
            System.out.println("Niepowodzenie podczas zapisu do bazy" + " " + e.getMessage());
        }
    }

    private void deleteTransaction() {
        System.out.println("Podaj id transakcji, którą chcesz usunąć");
        int id = scanner.nextInt();
        scanner.nextLine();
        try {
            String sql = "DELETE FROM transaction WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Niepowodzenie podczas zapisu do bazy" + " " + e.getMessage());
        }
        System.out.println("Transakcja usunięta");
    }

    private void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            System.out.println("Błąd podczas zamykania połączenia: " + e.getMessage());
        }
    }
}
