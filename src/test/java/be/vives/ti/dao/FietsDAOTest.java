package be.vives.ti.dao;

import be.vives.ti.databag.Fiets;
import be.vives.ti.datatype.Standplaats;
import be.vives.ti.datatype.Status;
import be.vives.ti.exception.DBException;
import be.vives.ti.extra.Removals;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class FietsDAOTest {

    private Fiets fiets;
    private FietsDAO fietsDAO = new FietsDAO();

    private Fiets maakFiets(Status status, Standplaats standplaats, String opmerking) {
        Fiets fiets = new Fiets();
        fiets.setStatus(status);
        fiets.setStandplaats(standplaats);
        fiets.setOpmerking(opmerking);
        return fiets;
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void toevoegenFiets() throws Exception {
        Fiets fiets1 = maakFiets(Status.actief, Standplaats.Kortrijk, "Eerste fiets toegevoegd");
        try {
            fiets1.setRegistratienummer(fietsDAO.toevoegenFiets(fiets1));
            Fiets ophaalFiets = fietsDAO.zoekFiets(fiets1.getRegistratienummer());

            assertThat(ophaalFiets.getRegistratienummer()).isEqualTo(fiets1.getRegistratienummer());
            assertThat(ophaalFiets.getStandplaats()).isEqualTo(Standplaats.Kortrijk);
            assertThat(ophaalFiets.getStatus()).isEqualTo(Status.actief);
            assertThat(ophaalFiets.getOpmerking()).isEqualTo("Eerste fiets toegevoegd");

        } catch (DBException dbEx) {
            System.out.println("test = " + dbEx);
        } finally {
            Removals.removeFiets(fiets1.getRegistratienummer());
        }

    }

    @Test
    public void wijzigenToestandFiets() throws Exception {
        Fiets fiets1 = maakFiets(Status.actief, Standplaats.Kortrijk, "Eerste fiets toegevoegd");
        try {
            fiets1.setRegistratienummer(fietsDAO.toevoegenFiets(fiets1));
            fietsDAO.wijzigenToestandFiets(fiets1.getRegistratienummer(), Status.herstel);

            Fiets ophaalFiets = fietsDAO.zoekFiets(fiets1.getRegistratienummer());
            assertThat(ophaalFiets.getRegistratienummer()).isEqualTo(fiets1.getRegistratienummer());
            assertThat(ophaalFiets.getStandplaats()).isEqualTo(Standplaats.Kortrijk);
            assertThat(ophaalFiets.getStatus()).isEqualTo(Status.herstel);
            assertThat(ophaalFiets.getOpmerking()).isEqualTo("Eerste fiets toegevoegd");
        } catch (DBException dbEx) {
            System.out.println("test = " + dbEx);
        } finally {
            Removals.removeFiets(fiets1.getRegistratienummer());
        }
    }

    @Test
    public void wijzigenOpmerkingFiets() throws Exception {
        Fiets fiets1 = maakFiets(Status.actief, Standplaats.Kortrijk, "Eerste fiets toegevoegd");

        try {
            fiets1.setRegistratienummer(fietsDAO.toevoegenFiets(fiets1));
            fietsDAO.wijzigenOpmerkingFiets(fiets1.getRegistratienummer(), "Deze opmerking is gewijzigd.");

            Fiets ophaalFiets = fietsDAO.zoekFiets(fiets1.getRegistratienummer());
            assertThat(ophaalFiets.getRegistratienummer()).isEqualTo(fiets1.getRegistratienummer());
            assertThat(ophaalFiets.getStandplaats()).isEqualTo(Standplaats.Kortrijk);
            assertThat(ophaalFiets.getStatus()).isEqualTo(Status.actief);
            assertThat(ophaalFiets.getOpmerking()).isEqualTo("Deze opmerking is gewijzigd.");
        } catch (DBException dbEx) {
            System.out.println("test = " + dbEx);
        } finally {
            Removals.removeFiets(fiets1.getRegistratienummer());
        }
    }

    @Test
    public void zoekFiets() throws Exception {
        Fiets fiets1 = maakFiets(Status.actief, Standplaats.Kortrijk, "Eerste fiets toegevoegd");
        try {
            fiets1.setRegistratienummer(fietsDAO.toevoegenFiets(fiets1));

            Fiets ontvangenFiets = fietsDAO.zoekFiets(fiets1.getRegistratienummer());
            assertThat(ontvangenFiets.getRegistratienummer()).isEqualTo(fiets1.getRegistratienummer());
            assertThat(ontvangenFiets.getOpmerking()).isEqualTo("Eerste fiets toegevoegd");
            assertThat(ontvangenFiets.getStatus()).isEqualTo(Status.actief);
            assertThat(ontvangenFiets.getStandplaats()).isEqualTo(Standplaats.Kortrijk);

        } finally {
            Removals.removeFiets(fiets1.getRegistratienummer());
        }

    }

    @Test
    public void zoekAlleBeschikbareFietsen() throws Exception {
        Fiets fiets1 = maakFiets(Status.herstel, Standplaats.Kortrijk, "Eerste fiets toegevoegd");
        Fiets fiets2 = maakFiets(Status.actief, Standplaats.Kortrijk, "Tweede fiets toegevoegd");
        Fiets fiets3 = maakFiets(Status.uit_omloop, Standplaats.Kortrijk, "Derde fiets toegevoegd");
        try {
            List<Fiets> beschikbareFietsenVoorToevoeging = fietsDAO.zoekAlleBeschikbareFietsen();
            fiets1.setRegistratienummer(fietsDAO.toevoegenFiets(fiets1));
            fiets2.setRegistratienummer(fietsDAO.toevoegenFiets(fiets2));
            fiets3.setRegistratienummer(fietsDAO.toevoegenFiets(fiets3));

            List<Fiets> beschikbareFietsenNaToevoeging = fietsDAO.zoekAlleBeschikbareFietsen();

            assertThat(beschikbareFietsenNaToevoeging.size()).isEqualTo(beschikbareFietsenVoorToevoeging.size() + 3);

        } finally {
            Removals.removeFiets(fiets1.getRegistratienummer());
            Removals.removeFiets(fiets2.getRegistratienummer());
            Removals.removeFiets(fiets3.getRegistratienummer());
        }
    }

    @Test
    public void wijzigenStatusNaarHerstel() throws Exception {
        Fiets fiets1 = maakFiets(Status.actief, Standplaats.Kortrijk, "Eerste fiets toegevoegd");

        try {
            fiets1.setRegistratienummer(fietsDAO.toevoegenFiets(fiets1));
            fietsDAO.wijzigenToestandFiets(fiets1.getRegistratienummer(), Status.herstel);

            Fiets ophaalFiets = fietsDAO.zoekFiets(fiets1.getRegistratienummer());
            assertThat(ophaalFiets.getRegistratienummer()).isEqualTo(fiets1.getRegistratienummer());
            assertThat(ophaalFiets.getStandplaats()).isEqualTo(Standplaats.Kortrijk);
            assertThat(ophaalFiets.getStatus()).isEqualTo(Status.herstel);
            assertThat(ophaalFiets.getOpmerking()).isEqualTo("Eerste fiets toegevoegd");
        } catch (DBException dbEx) {
            System.out.println("test = " + dbEx);
        } finally {
            Removals.removeFiets(fiets1.getRegistratienummer());
        }
    }

    @Test
    public void wijzigenStatusNaarActief() throws Exception {
        Fiets fiets1 = maakFiets(Status.herstel, Standplaats.Kortrijk, "Eerste fiets toegevoegd");

        try {
            fiets1.setRegistratienummer(fietsDAO.toevoegenFiets(fiets1));
            fietsDAO.wijzigenToestandFiets(fiets1.getRegistratienummer(), Status.actief);

            Fiets ophaalFiets = fietsDAO.zoekFiets(fiets1.getRegistratienummer());
            assertThat(ophaalFiets.getRegistratienummer()).isEqualTo(fiets1.getRegistratienummer());
            assertThat(ophaalFiets.getStandplaats()).isEqualTo(Standplaats.Kortrijk);
            assertThat(ophaalFiets.getStatus()).isEqualTo(Status.actief);
            assertThat(ophaalFiets.getOpmerking()).isEqualTo("Eerste fiets toegevoegd");
        } catch (DBException dbEx) {
            System.out.println("test = " + dbEx);
        } finally {
            Removals.removeFiets(fiets1.getRegistratienummer());
        }
    }

    @Test
    public void wijzigenStatusNaarUitOmloop() throws Exception {
        Fiets fiets1 = maakFiets(Status.actief, Standplaats.Kortrijk, "Eerste fiets toegevoegd");
        try {
            fiets1.setRegistratienummer(fietsDAO.toevoegenFiets(fiets1));
            fietsDAO.wijzigenToestandFiets(fiets1.getRegistratienummer(), Status.uit_omloop);

            Fiets ophaalFiets = fietsDAO.zoekFiets(fiets1.getRegistratienummer());
            assertThat(ophaalFiets.getRegistratienummer()).isEqualTo(fiets1.getRegistratienummer());
            assertThat(ophaalFiets.getStandplaats()).isEqualTo(Standplaats.Kortrijk);
            assertThat(ophaalFiets.getStatus()).isEqualTo(Status.uit_omloop);
            assertThat(ophaalFiets.getOpmerking()).isEqualTo("Eerste fiets toegevoegd");
        } catch (DBException dbEx) {
            System.out.println("test = " + dbEx);
        } finally {
            Removals.removeFiets(fiets1.getRegistratienummer());
        }
    }

    @Test
    public void zoekAlleFietsen() throws Exception {
        try {

            List<Fiets> beschikbareFietsen = fietsDAO.zoekAlleFietsen();

            assertThat(beschikbareFietsen.size()).isEqualTo(beschikbareFietsen.size());
        } catch (DBException dbEx) {
            System.out.println("test = " + dbEx);
        }
    }
}