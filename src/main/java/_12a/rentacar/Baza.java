package _12a.rentacar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


import java.sql.*;

public class Baza {
    private static final Logger logger = LogManager.getLogger(Baza.class);
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

    static class Klienci {
        public static boolean dodajKlienta(
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

        public static boolean usunKlienta(String pesel) {
            String deleteQuery = "DELETE FROM Klienci WHERE pesel = ?";

            try(PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
                preparedStatement.setString(0, pesel);

                int rowsAffected = preparedStatement.executeUpdate();

                return rowsAffected > 0;
            } catch (SQLException e) {
                logger.error("Nie udało się usunąć rekordu z bazy");
                throw new RuntimeException(e);
            }
        }
    }

    static class Wypozyczenia {
        public static boolean dodajWypozyczenie (
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
        
        public static boolean usunWypozyczenie(String id) {
            String deleteQuery = "DELETE FROM Wypozyczenia WHERE WypozyczenieID = ?";

            try(PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
                preparedStatement.setString(0, id);

                int rowsAffected = preparedStatement.executeUpdate();

                return rowsAffected > 0;
            } catch (SQLException e) {
                logger.error("Nie udało się usunąć rekordu z bazy");
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
                logger.error("Nie udało się pobrać rekordu z bazy");
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
                logger.error("Nie udało się pobrać rekordu z bazy");
                throw new RuntimeException(e);
            }
        }
        
        public static boolean dodajSamochod(
            String Marka,
            String Model,
            int RokProdukcji,
            int CenaDzienna,
            String Dostepnosc
        ) {
            String query = "INSERT INTO Samochody (SamochodID, Marka, Model, RokProdukcji, CenaDzienna, Dostepnosc) VALUES (NULL, '?', '?', '?', '?', '?')";

            try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(0, Marka);
                preparedStatement.setString(1, Model);
                preparedStatement.setInt(2, RokProdukcji);
                preparedStatement.setInt(3, CenaDzienna);
                preparedStatement.setString(4, Dostepnosc);

                int rowsAffected = preparedStatement.executeUpdate();

                return rowsAffected > 0;
            } catch (SQLException e) {
                logger.error("Nie udało się wstawić rekordu do bazy");
                throw new RuntimeException(e);
            }
        }

        public static boolean usunSamochod(String id) {
            String deleteQuery = "DELETE FROM Samochody WHERE SamochodID = ?";

            try(PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
                preparedStatement.setString(0, id);

                int rowsAffected = preparedStatement.executeUpdate();

                return rowsAffected > 0;
            } catch (SQLException e) {
                logger.error("Nie udało się usunąć rekordu z bazy");
                throw new RuntimeException(e);
            }
        }
    }
}
