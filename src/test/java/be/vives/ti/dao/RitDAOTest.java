package be.vives.ti.dao;

import be.vives.ti.databag.Fiets;
import be.vives.ti.databag.Lid;
import be.vives.ti.databag.Rit;
import be.vives.ti.datatype.Rijksregisternummer;
import be.vives.ti.datatype.Standplaats;
import be.vives.ti.datatype.Status;
import be.vives.ti.exception.DBException;
import be.vives.ti.extra.Removals;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class RitDAOTest {
    private Rit rit;
    private RitDAO ritDAO = new RitDAO();
    private FietsDAO fietsDAO = new FietsDAO();
    private LidDAO lidDAO = new LidDAO();

    Fiets fiets;
    Fiets fiets2;
    Fiets fiets3;
    Fiets fiets4;
    Fiets fiets5;
    Lid lid;


    private Rit maakRit(Rijksregisternummer rijksregisternr, int fietregistratienr) {
        rit = new Rit();
        rit.setLidRijksregisternummer(rijksregisternr);
        rit.setFietsRegistratienummer(fietregistratienr);
        return rit;
    }

    private Lid maakLid(Rijksregisternummer rijksregisternummer, String voornaam, String naam, String emailadres, LocalDate start_lidmaatschap, String opmerking) {
        Lid lid = new Lid();
        lid.setRijksregisternummer(rijksregisternummer);
        lid.setVoornaam(voornaam);
        lid.setNaam(naam);
        lid.setEmailadres(emailadres);
        lid.setStart_lidmaatschap(start_lidmaatschap);
        lid.setOpmerking(opmerking);
        return lid;
    }

    private Fiets maakFiets(Status status, Standplaats standplaats, String opmerking) {
        Fiets f = new Fiets();
        f.setStatus(status);
        f.setStandplaats(standplaats);
        f.setOpmerking(opmerking);
        return f;
    }

    @Before
    public void setUp() throws Exception {
        LocalDate huidigTijdstip = LocalDate.now();
        fiets = maakFiets(Status.actief, Standplaats.Kortrijk, "Eerste fiets toegevoegd testtoevoegenRit");
        fiets2 = maakFiets(Status.actief, Standplaats.Brugge, "Tweede fiets toegevoegd testzoekEersteRitVanLid");
        fiets3 = maakFiets(Status.actief, Standplaats.Oostende, "Derde fiets toegevoegd testzoekEersteRitVanLid");
        fiets4 = maakFiets(Status.herstel, Standplaats.Kortrijk, "Vierde fiets toegevoegd zoekActieveRittenVanLid");
        fiets5 = maakFiets(Status.uit_omloop, Standplaats.Oostende, "Vijfde fiets toegevoegd zoekActieveRittenVanLid");
        lid = maakLid(new Rijksregisternummer("64101612335"), "Andres", "Sabbe", "sabbeandres@outlook.com", huidigTijdstip, "dit is een opmerking");
        lidDAO.toevoegenLid(lid);
        fiets.setRegistratienummer(fietsDAO.toevoegenFiets(fiets));
        fiets2.setRegistratienummer(fietsDAO.toevoegenFiets(fiets2));
        fiets3.setRegistratienummer(fietsDAO.toevoegenFiets(fiets3));
        fiets4.setRegistratienummer(fietsDAO.toevoegenFiets(fiets4));
        fiets5.setRegistratienummer(fietsDAO.toevoegenFiets(fiets5));
    }

    @After
    public void tearDown() throws Exception {
        Removals.removeLid(lid.getRijksregisternummer());
        Removals.removeFiets(fiets.getRegistratienummer());
        Removals.removeFiets(fiets2.getRegistratienummer());
        Removals.removeFiets(fiets3.getRegistratienummer());
        Removals.removeFiets(fiets4.getRegistratienummer());
        Removals.removeFiets(fiets5.getRegistratienummer());
    }

    @Test
    public void toevoegenRit() throws Exception {
        Rit toevoegenRit = maakRit(new Rijksregisternummer(lid.getRijksregisternummer()), fiets.getRegistratienummer());
        try {
            toevoegenRit.setId(ritDAO.toevoegenRit(toevoegenRit));
            Rit ophaalRit = ritDAO.zoekRit(toevoegenRit.getId());

            assertThat(ophaalRit.getId()).isEqualTo(toevoegenRit.getId());
            assertThat(ophaalRit.getLidRijksregisternummer()).isEqualTo(toevoegenRit.getLidRijksregisternummer());
            assertThat(ophaalRit.getFietsRegistratienummer()).isEqualTo(toevoegenRit.getFietsRegistratienummer());
            assertThat(ophaalRit.getStarttijd()).isNotNull();
        }finally {
            Removals.removeRit(toevoegenRit.getId());
        }
    }

    @Test
    public void toevoegenRitOnbestaandLid() throws Exception {
        Rit toevoegenRit = maakRit(new Rijksregisternummer("01031200101"), fiets.getRegistratienummer());
        assertThatThrownBy(() -> ritDAO.toevoegenRit(toevoegenRit)).isInstanceOf(DBException.class);
    }

    @Test
    public void toevoegenRitOnbestaandeFiets() throws Exception {
        Rit toevoegenRit = maakRit(new Rijksregisternummer(lid.getRijksregisternummer()), 999999);
        assertThatThrownBy(() -> ritDAO.toevoegenRit(toevoegenRit)).isInstanceOf(DBException.class);
    }

    @Test
    public void afsluitenRit() throws Exception {
        Rit toevoegenRit = maakRit(new Rijksregisternummer(lid.getRijksregisternummer()), fiets.getRegistratienummer());
        try {
            toevoegenRit.setId(ritDAO.toevoegenRit(toevoegenRit));
            toevoegenRit.setPrijs(BigDecimal.valueOf(20));
            toevoegenRit.setEindtijd(LocalDateTime.now());

            Rit toegevoegdeRit = ritDAO.zoekRit(toevoegenRit.getId());
            assertThat(toegevoegdeRit.getStarttijd()).isNotNull();
            assertThat(toegevoegdeRit.getEindtijd()).isNull();

            ritDAO.afsluitenRit(toevoegenRit);

            Rit ophaalRit = ritDAO.zoekRit(toevoegenRit.getId());

            assertThat(ophaalRit.getLidRijksregisternummer()).isEqualTo(toevoegenRit.getLidRijksregisternummer());
            assertThat(ophaalRit.getFietsRegistratienummer()).isEqualTo(toevoegenRit.getFietsRegistratienummer());
            assertThat(ophaalRit.getStarttijd()).isNotNull();
            assertThat(ophaalRit.getEindtijd()).isNotNull();
            assertThat(ophaalRit.getPrijs()).isEqualTo(toevoegenRit.getPrijs());
        }finally {
            Removals.removeRit(toevoegenRit.getId());
        }

    }

    @Test
    public void zoekRit() throws Exception {
        Rit toevoegenRit = maakRit(new Rijksregisternummer(lid.getRijksregisternummer()), fiets.getRegistratienummer());
        try {
            toevoegenRit.setId(ritDAO.toevoegenRit(toevoegenRit));

            Rit ophaalRit = ritDAO.zoekRit(toevoegenRit.getId());

            assertThat(ophaalRit.getId()).isEqualTo(toevoegenRit.getId());
            assertThat(ophaalRit.getLidRijksregisternummer()).isEqualTo(toevoegenRit.getLidRijksregisternummer());
            assertThat(ophaalRit.getFietsRegistratienummer()).isEqualTo(toevoegenRit.getFietsRegistratienummer());
            assertThat(ophaalRit.getEindtijd()).isNull();
            assertThat(ophaalRit.getPrijs()).isNull();
            assertThat(ophaalRit.getStarttijd()).isNotNull();
        }finally {
            Removals.removeRit(toevoegenRit.getId());
        }
    }

    @Test
    public void zoekRitNull() throws Exception {
        assertThat(ritDAO.zoekRit(0)).isNull();
    }

    @Test
    public void zoekRitOnbestaande() throws Exception {
        assertThat(ritDAO.zoekRit(99999)).isNull();
    }

    @Test
    public void zoekEersteRitVanLid() throws Exception {
        Rit rit1 = maakRit(new Rijksregisternummer(lid.getRijksregisternummer()), fiets.getRegistratienummer());
        Rit rit2 = maakRit(new Rijksregisternummer(lid.getRijksregisternummer()), fiets2.getRegistratienummer());
        Rit rit3 = maakRit(new Rijksregisternummer(lid.getRijksregisternummer()), fiets3.getRegistratienummer());
        try {
            //Ritten
            rit2.setId(ritDAO.toevoegenRit(rit2));
            rit1.setId(ritDAO.toevoegenRit(rit1));
            rit3.setId(ritDAO.toevoegenRit(rit3));

            Rit eersteRit = ritDAO.zoekEersteRitVanLid(lid.getRijksregisternummer());

            assertThat(eersteRit.getLidRijksregisternummer()).isEqualTo(rit2.getLidRijksregisternummer());
            assertThat(eersteRit.getFietsRegistratienummer()).isEqualTo(rit2.getFietsRegistratienummer());
            assertThat(eersteRit.getFietsRegistratienummer()).isNotEqualTo(rit1.getFietsRegistratienummer());
            assertThat(eersteRit.getFietsRegistratienummer()).isNotEqualTo(rit3.getFietsRegistratienummer());
            assertThat(eersteRit.getStarttijd()).isNotNull();
        }finally {
            Removals.removeRit(rit1.getId());
            Removals.removeRit(rit2.getId());
            Removals.removeRit(rit3.getId());
        }
    }

    @Test
    public void zoekEersteRitVanLid_NulResultaten() throws Exception {
        Rit IDEersteRitVanLid = ritDAO.zoekEersteRitVanLid(lid.getRijksregisternummer());
        assertThat(IDEersteRitVanLid).isEqualTo(null);
    }

    @Test
    public void zoekActieveRittenVanLid() throws Exception {
        Rit rit1 = maakRit(new Rijksregisternummer(lid.getRijksregisternummer()), fiets.getRegistratienummer());
        Rit rit2 = maakRit(new Rijksregisternummer(lid.getRijksregisternummer()), fiets2.getRegistratienummer());
        Rit rit3 = maakRit(new Rijksregisternummer(lid.getRijksregisternummer()), fiets3.getRegistratienummer());

        try {

            rit2.setId(ritDAO.toevoegenRit(rit2));
            rit1.setId(ritDAO.toevoegenRit(rit1));
            rit3.setId(ritDAO.toevoegenRit(rit3));

            rit1.setEindtijd(LocalDateTime.now());
            rit3.setEindtijd(LocalDateTime.now());

            rit1.setPrijs(BigDecimal.valueOf(20));
            rit3.setPrijs(BigDecimal.valueOf(20));

            ritDAO.afsluitenRit(rit1);
            ritDAO.afsluitenRit(rit3);

            List<Rit> ophaalRitten = ritDAO.zoekActieveRittenVanLid(lid.getRijksregisternummer());
            assertThat(ophaalRitten.size()).isEqualTo(1);

            Rit ritControle = ophaalRitten.get(0);

            //rit 1 is de enige die juist is (alleen deze is actief op dit moment)
            assertThat(ritControle.getLidRijksregisternummer()).isEqualTo(rit2.getLidRijksregisternummer());
            assertThat(ritControle.getFietsRegistratienummer()).isEqualTo(rit2.getFietsRegistratienummer());
            assertThat(ritControle.getStarttijd()).isNotNull();
            assertThat(ritControle.getEindtijd()).isNull();

        }finally{
            Removals.removeRit(rit1.getId());
            Removals.removeRit(rit2.getId());
            Removals.removeRit(rit3.getId());
        }
    }

    @Test
    public void zoekActieveRittenVanLid_GeenResultaten(){
        try {
            List<Rit> ophaalRitten = ritDAO.zoekActieveRittenVanLid(lid.getRijksregisternummer());
            assertThat(ophaalRitten.size()).isEqualTo(0);
        } catch (DBException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void zoekActieveRittenVanFiets() throws Exception {
        Rit rit1 = maakRit(new Rijksregisternummer(lid.getRijksregisternummer()), fiets.getRegistratienummer());
        Rit rit2 = maakRit(new Rijksregisternummer(lid.getRijksregisternummer()), fiets2.getRegistratienummer());
        Rit rit3 = maakRit(new Rijksregisternummer(lid.getRijksregisternummer()), fiets3.getRegistratienummer());

        try {

            rit2.setId(ritDAO.toevoegenRit(rit2));
            rit1.setId(ritDAO.toevoegenRit(rit1));
            rit3.setId(ritDAO.toevoegenRit(rit3));

            rit1.setEindtijd(LocalDateTime.now());
            rit2.setEindtijd(LocalDateTime.now());

            rit1.setPrijs(BigDecimal.valueOf(20));
            rit2.setPrijs(BigDecimal.valueOf(20));

            ritDAO.afsluitenRit(rit1);
            ritDAO.afsluitenRit(rit2);

            List<Rit> ophaalRitten = ritDAO.zoekActieveRittenVanFiets(fiets3.getRegistratienummer());
            assertThat(ophaalRitten.size()).isEqualTo(1);

            Rit ritControle = ophaalRitten.get(0);

            //rit 1 is de enige die juist is (alleen deze is actief op dit moment)
            assertThat(ritControle.getLidRijksregisternummer()).isEqualTo(rit3.getLidRijksregisternummer());
            assertThat(ritControle.getFietsRegistratienummer()).isEqualTo(rit3.getFietsRegistratienummer());
            assertThat(ritControle.getStarttijd()).isNotNull();
            assertThat(ritControle.getEindtijd()).isNull();

        }finally{
            Removals.removeRit(rit1.getId());
            Removals.removeRit(rit2.getId());
            Removals.removeRit(rit3.getId());
        }
    }
}