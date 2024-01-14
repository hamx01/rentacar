package _12a.rentacar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testng.annotations.BeforeClass;

import java.sql.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // must be here to MockMvc work
@AutoConfigureMockMvc
class RentacarApplicationTests {
    private testBazy baza = new testBazy();
//    private Baza baza_prod = new Baza();
    private static final Logger logger = LogManager.getLogger(RentacarApplicationTests.class);

    @BeforeClass
    public static void setupLogger() {
        Configurator.initialize(null, "log4j2.xml");
    }

    @Test
    void testDaysCount() {
        logger.info("Expected output: 29 Dni");
        RentacarController controller = new RentacarController();  //yyyy-MM-dd
        long wynik = controller.CountDays("2023-12-01", "2023-12-30");
        logger.info("Wynik metody: " + wynik + " Dni");
        assertEquals(29, wynik);
    }

    @Test
    public void testConToDB() {
        try (Connection connection = DriverManager.getConnection(
                "jdbc:mysql://192.168.0.130:3306/wypozyczalnia",
                "wypozyczalnia_user",
                "password")) {
            if (connection != null) {
                logger.info("Połączono z bazą!");
            } else {
                logger.error("Problem połączenia z bazą!");
            }
            assertNotNull(connection);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testWypozyczMapping() throws Exception {
        MvcResult wypozycz = mockMvc.perform(MockMvcRequestBuilders.get("/wypozycz"))
                .andExpect(status().isOk())
                .andReturn();
        logger.info("HTTP status code: " + wypozycz.getResponse().getStatus());
    }

    @Test
    public void testMainPageMapping() throws Exception {
        MvcResult mainpage = mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(status().isOk())
                .andReturn();
        logger.info("HTTP status code: " + mainpage.getResponse().getStatus());
    }

    @Test
    public void testDziekiMapping() throws Exception {
        MvcResult dzieki = mockMvc.perform(MockMvcRequestBuilders.get("/dzieki"))
                .andExpect(status().isOk())
                .andReturn();
        logger.info("HTTP status code: " + dzieki.getResponse().getStatus());
    }

    @Test
    public void testDostepneMapping() throws Exception {
        MvcResult dostepne = mockMvc.perform(MockMvcRequestBuilders.get("/dostepne"))
                .andExpect(status().isOk())
                .andReturn();
        logger.info("HTTP status code: " + dostepne.getResponse().getStatus());
    }

    @Test
    public void testRejestracjaMapping() throws Exception {
        MvcResult rejestracja = mockMvc.perform(MockMvcRequestBuilders.get("/rejestracja"))
                .andExpect(status().isOk())
                .andReturn();
        logger.info("HTTP status code: " + rejestracja.getResponse().getStatus());
    }

    @AfterAll
    public static void cleanup() {
        testBazy.deleteRecordIfExists();
    }


    @Test
    public void testInsertForSamochody() {
//        baza.testTableKlienci();
//        baza.testTableWypozyczenia();
        baza.testTableSamochody();

        assertTrue(baza.checkIfRecordExistsSamochody());
        if(baza.checkIfRecordExistsSamochody()) {
            logger.info("Rekord testowy został dodany do tabeli Samochody!");
        } else {
            logger.error("Rekord nie mógł być dodany do tabeli Samochody!");
        }

//        assertTrue(baza.checkIfRecordExistsKlienci(), "Rekord nie został dodany do bazy danych.");
//        if(baza.checkIfRecordExistsKlienci()) {
//            System.out.println("Rekord testowy został dodany do tabeli Klienci!");
//        }

//        assertTrue(baza.checkIfRecordExistsWypozyczenia(), "Rekord nie został dodany do bazy danych.");
//        if(baza.checkIfRecordExistsWypozyczenia()) {
//            System.out.println("Rekord testowy został dodany do tabeli Wypozyczenia!");
//        }

    }

    @Test
    public void testInsertForKlienci() {
        boolean wynik = Baza.Klienci.insertRecord("01234567890", "test", "test", "test", "000000000", "test@example.com");
        if(wynik) {
            logger.info("Rekord został dodany do bazy!");
        } else {
            logger.error("Nie udało się dodać rekordu testowego do bazy");
        }
        assertTrue(wynik);
    }

    @Test
    public void testInsertForWypozyczenia() {
        boolean wynik = Baza.Wypozyczenia.insertRecord("2","01234567890", "1999-01-01", "1999-01-02", Integer.parseInt("100"));
        if(wynik) {
            logger.info("Rekord został dodany do bazy!");
        } else {
            logger.error("Nie udało się dodać rekordu testowego do bazy");
        }
        assertTrue(wynik);
    }
}
