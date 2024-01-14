package _12a.rentacar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;

public class Baza {
    private static final Logger logger = LogManager.getLogger(Baza.class);
    private static final String DB_URL = "jdbc:mariadb://192.168.0.130:3306/wypozyczalnia";
    private static final String DB_USER = "wypozyczalnia_user";
    private static final String DB_PASSWORD = "password";
    public static Connection connection;

    static {
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            logger.info("Polączono do bazy");
        } catch (SQLException e) {
            logger.error("Nie udało się połączyć z bazą");
            throw new RuntimeException(e);
        }
    }

    public static Connection getConnection() {
        return connection;
    }

    static class Klienci {
        public static boolean insertRecord(
                String pesel,
                String imie,
                String nazwisko,
                String adres,
                String numer_telefonu,
                String email) {

            String insertQuery = "INSERT INTO Klienci (pesel, imie, nazwisko, adres, numer_telefonu, email) VALUES (?, ?, ?, ?, ?, ?)";

            try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                preparedStatement.setString(1, pesel);
                preparedStatement.setString(2, imie);
                preparedStatement.setString(3, nazwisko);
                preparedStatement.setString(4, adres);
                preparedStatement.setString(5, numer_telefonu);
                preparedStatement.setString(6, email);

                int rowsAffected = preparedStatement.executeUpdate();

                return rowsAffected > 0;
            } catch (SQLException e) {
                logger.error("Nie udało się wstawić rekordu do bazy");
                throw new RuntimeException(e);
            }
        }
    }

    static class Wypozyczenia {
        public static boolean insertRecord (
                String selectedCar,
                String pesel,
                String pickup_date,
                String return_date,
                int koszta
        ) {

            String insertQuery = "INSERT INTO Wypozyczenia (SamochodID, pesel, DataWypozyczenia, DataZwrotu, KosztWypozyczenia) VALUES (?, ?, ?, ?, ?)";

            try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                preparedStatement.setString(1, selectedCar);
                preparedStatement.setString(2, pesel);
                preparedStatement.setString(3, pickup_date);
                preparedStatement.setString(4, return_date);
                preparedStatement.setInt(5, koszta);

                int rowsAffected = preparedStatement.executeUpdate();

                return rowsAffected > 0;
            } catch (SQLException e) {
                logger.error("Nie udało się wstawić rekordu do bazy");
                throw new RuntimeException(e);
            }
        }
    }

    static class Samochody {
        public static int pobierzKosztSamochodu(String selectedCar) {
            String cenaSamochoduQuery = "SELECT CenaDzienna FROM Samochody WHERE SamochodID = ?";
            int cenaDzienna = -1;

            try (PreparedStatement query = connection.prepareStatement(cenaSamochoduQuery)) {
                query.setString(1, selectedCar);
                try (ResultSet resultSet = query.executeQuery()) {
                    if (resultSet.next()) {
                        cenaDzienna = resultSet.getInt("CenaDzienna");
                    }
                }

                return cenaDzienna;
            } catch (SQLException e) {
                logger.error("Nie udało się wstawić rekordu do bazy");
                throw new RuntimeException(e);
            }
        }

        public static String pobierzModelMarke(String selectedCar) {
            String query = "SELECT Marka, Model FROM Samochody WHERE SamochodID = ?";
            String marka = null, model = null;
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, selectedCar);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        marka = resultSet.getString("Marka");
                        model = resultSet.getString("Model");
                    }
                    return marka+" "+model;
                }
            } catch (SQLException e) {
                logger.error("Nie udało się wstawić rekordu do bazy");
                throw new RuntimeException(e);
            }
        }
    }
}
