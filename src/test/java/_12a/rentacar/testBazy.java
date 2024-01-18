package _12a.rentacar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class testBazy {

    private static final Logger logger = LogManager.getLogger(testBazy.class);
    static Properties props = new Properties();

    static {
        try (InputStream input = new FileInputStream("src/main/resources/application.properties")) {
            props.load(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private static final String DB_URL = props.getProperty("spring.datasource.url");
    private static final String DB_USER = props.getProperty("spring.datasource.username");
    private static final String DB_PASSWORD = props.getProperty("spring.datasource.password");
    private static final Connection connection;

    static {
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            logger.info("Polączono do bazy");
        } catch (SQLException e) {
            logger.error("Nie udało się połączyć z bazą");
            throw new RuntimeException(e);
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void testTableSamochody() {
        String insertQuery = "INSERT INTO Samochody (Marka, Model, RokProdukcji, CenaDzienna, Dostepnosc) VALUES ('Test', 'Test', 1970, 250, 'Nie')";
        try (PreparedStatement insertion = connection.prepareStatement(insertQuery)) {
            insertion.executeUpdate();
        } catch (SQLException e) {
            logger.error("Nie udało się dodać rekordu do tabeli");
            throw new RuntimeException(e);
        }
    }

    public boolean checkIfRecordExistsKlienci() {
        String selectQuery = "SELECT * FROM Klienci WHERE pesel = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
            preparedStatement.setString(1, "01234567890");

            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            logger.error("Nie udało się wykonać kwerendy");
            throw new RuntimeException(e);
        }
    }

    public boolean checkIfRecordExistsWypozyczenia() {
        String selectQuery = "SELECT * FROM Wypozyczenia WHERE pesel = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
            preparedStatement.setString(1, "01234567890");

            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            logger.error("Nie udało się wykonać kwerendy");
            throw new RuntimeException(e);
        }
    }

    public boolean checkIfRecordExistsSamochody() {
        String selectQuery = "SELECT * FROM Samochody WHERE Marka = ? AND RokProdukcji = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
            preparedStatement.setString(1, "Test");
            preparedStatement.setInt(2, 1970);

            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            logger.error("Nie udało się wykonać kwerendy");
            throw new RuntimeException(e);
        }
    }

    public static void deleteRecordIfExists() {
        String deleteQuery = "DELETE FROM Klienci WHERE pesel = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
            preparedStatement.setString(1, "01234567890");
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Nie udało się usunąć rekordu");
            throw new RuntimeException(e);
        }

        deleteQuery = "DELETE FROM Wypozyczenia WHERE pesel = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
            preparedStatement.setString(1, "01234567890");
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Nie udało się usunąć rekordu");
            throw new RuntimeException(e);
        }

        deleteQuery = "DELETE FROM Samochody WHERE Marka = ? AND RokProdukcji = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
            preparedStatement.setString(1, "Test");
            preparedStatement.setInt(2, 1970);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Nie udało się usunąć rekordu");
            throw new RuntimeException(e);
        }

    }
}
