package _12a.rentacar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // must be here to MockMvc work
@AutoConfigureMockMvc
class RentacarApplicationTests {
    private final testBazy baza = new testBazy();
    private static final Logger logger = LogManager.getLogger(RentacarApplicationTests.class);

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
        if (baza.getConnection() != null) {
            logger.info("Połączono z bazą!");
        } else {
            logger.error("Problem połączenia z bazą!");
        }
        assertNotNull(baza.getConnection());
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

    @Test
    public void testInsertForSamochody() {
        baza.testTableSamochody();

        if(baza.checkIfRecordExistsSamochody()) {
            logger.info("Rekord testowy został dodany do tabeli Samochody!");
        } else {
            logger.error("Rekord nie mógł być dodany do tabeli Samochody!");
        }
        assertTrue(baza.checkIfRecordExistsSamochody());
    }

    @Test
    public void testInsertForKlienci() {
        boolean wynik = Baza.Klienci.dodajKlienta("01234567890", "test", "test", "test", "000000000", "test@example.com");
        if(wynik) {
            logger.info("Rekord został dodany do bazy!");
        } else {
            logger.error("Nie udało się dodać rekordu testowego do bazy");
        }
        assertTrue(wynik);
    }

    @Test
    public void testInsertForWypozyczenia() {
        boolean wynik = Baza.Wypozyczenia.dodajWypozyczenie("2","01234567890", "1999-01-01", "1999-01-02", Integer.parseInt("100"));
        if(wynik) {
            logger.info("Rekord został dodany do bazy!");
        } else {
            logger.error("Nie udało się dodać rekordu testowego do bazy");
        }
        assertTrue(wynik);
    }

    @AfterAll
    public static void cleanup() {
        testBazy.deleteRecordIfExists();
    }
}
