package _12a.rentacar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Controller
public class RentacarController {
    private static final Logger logger = LogManager.getLogger(RentacarController.class);

    static {
        Configurator.initialize(null, "log4j2.xml");
    }

    @RequestMapping("/")
    public String home(Model model) {
        for(int i = 1; i<=3; i++) {
            model.addAttribute("markaModel" + i, Baza.Samochody.pobierzModelMarke(String.valueOf(i)));
            model.addAttribute("cenaSamochodu" + i, Baza.Samochody.pobierzKosztSamochodu(String.valueOf(i)) + " zł");
        }
//        model.addAttribute("markaModel1", Baza.Samochody.pobierzModelMarke("2"));
//        model.addAttribute("cenaSamochodu1", Baza.Samochody.pobierzKosztSamochodu("2") + " zł");
//        model.addAttribute("markaModel2", Baza.Samochody.pobierzModelMarke("1"));
//        model.addAttribute("cenaSamochodu2", Baza.Samochody.pobierzKosztSamochodu("1") + " zł");
//        model.addAttribute("markaModel3", Baza.Samochody.pobierzModelMarke("4"));
//        model.addAttribute("cenaSamochodu3", Baza.Samochody.pobierzKosztSamochodu("4") + " zł");

        logger.info("Loaded / mapping");
        return "index";
    }

    @RequestMapping("/rejestracja")
    public String rejestracja(Model model) {
        logger.info("Loaded /rejestracja mapping");
        return "rejestracja";
    }

    @PostMapping("/process_rejestracja")
    public String registerUser(
            @RequestParam String pesel,
            @RequestParam String imie,
            @RequestParam String nazwisko,
            @RequestParam String adres,
            @RequestParam String numer_telefonu,
            @RequestParam String email) {
        boolean wynik = Baza.Klienci.insertRecord(pesel, imie, nazwisko, adres, numer_telefonu, email);
        if (wynik) {
            logger.info("Dodano rekord do tabeli Klienci");
        } else {
            logger.error("Błąd przy dodaniu rekordu do tabeli Klienci");
        }

        return "redirect:/dzieki";
    }

    @RequestMapping("/wypozycz")
    public String wypozycz() {
        logger.info("Loaded /wypozycz mapping");
        return "wypozycz";
    }

    @RequestMapping("/dzieki")
    public String dzieki(Model model) {
        logger.info("Loaded /dzieki mapping");
        return "dzieki";
    }

    @PostMapping("/process_wypozycz")
    public String wypozycz_samochod(
            @RequestParam String pesel,
            @RequestParam("cars") String selectedCar,
            @RequestParam String pickup_date,
            @RequestParam String return_date) {

        int cenaDzienna = Baza.Samochody.pobierzKosztSamochodu(selectedCar);
        if (cenaDzienna >= 0) {
            long koszta = cenaDzienna * CountDays(pickup_date, return_date);
            boolean wynik = Baza.Wypozyczenia.insertRecord(selectedCar, pesel, pickup_date, return_date, (int) koszta);
            if (wynik) {
                logger.info("Dodano rekord do tabeli Wypozyczenia");
            } else {
                logger.error("Błąd przy dodaniu rekordu do tabeli Wypozyczenia");
            }
        } else {
            logger.error("Nie znaleziono ceny dla wybranego samochodu");
        }

        return "redirect:/wypozycz";
    }

    @RequestMapping("/dostepne")
    public String dostepne(Model model) {
        for(int i = 1; i<=6; i++) {
            model.addAttribute("markaModel" + i, Baza.Samochody.pobierzModelMarke(String.valueOf(i)));
            model.addAttribute("cenaSamochodu" + i, Baza.Samochody.pobierzKosztSamochodu(String.valueOf(i)) + " zł");
        }
        logger.info("Loaded /dostepne mapping");
        return "dostepne";
    }


    // Metody poniżej
    long CountDays(String rentDate, String returnDate) { // long dlatego, że Duration.between zwraca long
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime date1 = LocalDate.parse(rentDate, format).atStartOfDay();
        LocalDateTime date2 = LocalDate.parse(returnDate, format).atStartOfDay();
        return Duration.between(date1, date2).toDays();
    }

//    @RequestMapping("/test")
//    public String test_map() {
//        logger.info(Baza.Samochody.pobierzModelMarke("2"));
//        return "/test";
//    }
//
//    @PostMapping("/test")
//    public void test(@RequestParam("cars") String selectedCar) {
//        try {
//            findPage();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        String wynik = String.valueOf(Baza.Samochody.pobierzKosztSamochodu(selectedCar));
//        logger.info(wynik);
//    }

}
