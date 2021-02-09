package be.vives.ti.dao;

import be.vives.ti.databag.Lid;
import be.vives.ti.datatype.Rijksregisternummer;
import be.vives.ti.exception.DBException;
import be.vives.ti.extra.Removals;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class LidDAOTest {
    private LidDAO lidDAO = new LidDAO();
    private Lid lid;

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

    @Before
    public void setUp() throws Exception { }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void toevoegenLid() throws Exception {
        try{
            LocalDate huidigTijdstip = LocalDate.now();
            Lid lid = maakLid(new Rijksregisternummer("64101612335"), "Andres", "Sabbe", "sabbeandres@outlook.com", huidigTijdstip, "dit is een opmerking");
            assertThat(lidDAO.toevoegenLid(lid)).isTrue();

            Lid ophaalLid = lidDAO.zoekLid(lid.getRijksregisternummer());
            assertThat(ophaalLid.getRijksregisternummer()).isEqualTo("64101612335");
            assertThat(ophaalLid.getVoornaam()).isEqualTo("Andres");
            assertThat(ophaalLid.getNaam()).isEqualTo("Sabbe");
            assertThat(ophaalLid.getEmailadres()).isEqualTo("sabbeandres@outlook.com");
            assertThat(ophaalLid.getStart_lidmaatschap()).isEqualTo(huidigTijdstip);
            assertThat(ophaalLid.getEinde_lidmaatschap()).isNull();
            assertThat(ophaalLid.getOpmerking()).isEqualTo("dit is een opmerking");

        }finally {
            Removals.removeLid("64101612335");
        }
    }

    @Test
    public void toevoegenLidZonderOpmerking() throws Exception {
        try{
            LocalDate huidigTijdstip = LocalDate.now();
            Lid lid = maakLid(new Rijksregisternummer("64101612335"), "Andres", "Sabbe", "sabbeandres@outlook.com", huidigTijdstip, null);
            assertThat(lidDAO.toevoegenLid(lid)).isTrue();

            Lid ophaalLid = lidDAO.zoekLid(lid.getRijksregisternummer());
            assertThat(ophaalLid.getRijksregisternummer()).isEqualTo("64101612335");
            assertThat(ophaalLid.getVoornaam()).isEqualTo("Andres");
            assertThat(ophaalLid.getNaam()).isEqualTo("Sabbe");
            assertThat(ophaalLid.getEmailadres()).isEqualTo("sabbeandres@outlook.com");
            assertThat(ophaalLid.getStart_lidmaatschap()).isEqualTo(huidigTijdstip);
            assertThat(ophaalLid.getEinde_lidmaatschap()).isNull();
            assertThat(ophaalLid.getOpmerking()).isNull();

        }finally {
            Removals.removeLid("64101612335");
        }
    }

    @Test
    public void toevoegenLidNull() throws Exception {
        assertThat(lidDAO.toevoegenLid(null)).isFalse();
    }

    @Test
    public void toevoegenLidZonderRijksregisternummer() throws Exception {
        LocalDate huidigTijdstip = LocalDate.now();
        Lid lid = maakLid(null, "Andres", "Sabbe", "sabbeandres@outlook.com", huidigTijdstip, "");
        assertThatThrownBy(() -> {
            lidDAO.toevoegenLid(lid);
        }).isInstanceOf(DBException.class);
    }

    @Test
    public void toevoegenLidZonderVoornaam() throws Exception {
        LocalDate huidigTijdstip = LocalDate.now();
        Lid lid = maakLid(new Rijksregisternummer("64101612335"), null, "Sabbe", "sabbeandres@outlook.com", huidigTijdstip, "");
        assertThatThrownBy(() -> {
            lidDAO.toevoegenLid(lid);
        }).isInstanceOf(DBException.class);
    }

    @Test
    public void toevoegenLidZonderNaam() throws Exception {
        LocalDate huidigTijdstip = LocalDate.now();
        Lid lid = maakLid(new Rijksregisternummer("64101612335"), "Andres", null, "sabbeandres@outlook.com", huidigTijdstip, "");
        assertThatThrownBy(() -> {
            lidDAO.toevoegenLid(lid);
        }).isInstanceOf(DBException.class);
    }

    @Test
    public void toevoegenLidZonderEmail() throws Exception {
        LocalDate huidigTijdstip = LocalDate.now();
        Lid lid = maakLid(new Rijksregisternummer("64101612335"), "Andres", "Sabbe", null, huidigTijdstip, "");
        assertThatThrownBy(() -> {
            lidDAO.toevoegenLid(lid);
        }).isInstanceOf(DBException.class);
    }

    @Test
    public void toevoegenLidZonderStartLidmaatschap() throws Exception {
        Lid lid = maakLid(new Rijksregisternummer("64101612335"), "Andres", "Sabbe", "sabbeandresoutlook.com", null, "");
        assertThatThrownBy(() -> {
            lidDAO.toevoegenLid(lid);
        }).isInstanceOf(NullPointerException.class);
    }


    @Test
    public void wijzigenLidVoornaam() throws Exception{
        try{
            LocalDate huidigTijdstip = LocalDate.now();
            Lid lid = maakLid(new Rijksregisternummer("64101612335"), "Andres", "Sabbe", "sabbeandres@outlook.com", huidigTijdstip, "dit is een opmerking");
            assertThat(lidDAO.toevoegenLid(lid)).isTrue();

            lid.setVoornaam("Pieter");
            lidDAO.wijzigenLid(lid);
            Lid ophaalLid = lidDAO.zoekLid(lid.getRijksregisternummer());
            assertThat(ophaalLid.getRijksregisternummer()).isEqualTo("64101612335");
            assertThat(ophaalLid.getVoornaam()).isEqualTo("Pieter");
            assertThat(ophaalLid.getNaam()).isEqualTo("Sabbe");
            assertThat(ophaalLid.getEmailadres()).isEqualTo("sabbeandres@outlook.com");
            assertThat(ophaalLid.getStart_lidmaatschap()).isEqualTo(huidigTijdstip);
            assertThat(ophaalLid.getEinde_lidmaatschap()).isNull();
            assertThat(ophaalLid.getOpmerking()).isEqualTo("dit is een opmerking");

        }finally {
            Removals.removeLid("64101612335");
        }
    }

    @Test
    public void wijzigenLidNaam() throws Exception{
        try{
            LocalDate huidigTijdstip = LocalDate.now();
            Lid lid = maakLid(new Rijksregisternummer("64101612335"), "Andres", "Sabbe", "sabbeandres@outlook.com", huidigTijdstip, "dit is een opmerking");
            assertThat(lidDAO.toevoegenLid(lid)).isTrue();

            lid.setNaam("Deman");
            lidDAO.wijzigenLid(lid);
            Lid ophaalLid = lidDAO.zoekLid(lid.getRijksregisternummer());
            assertThat(ophaalLid.getRijksregisternummer()).isEqualTo("64101612335");
            assertThat(ophaalLid.getVoornaam()).isEqualTo("Andres");
            assertThat(ophaalLid.getNaam()).isEqualTo("Deman");
            assertThat(ophaalLid.getEmailadres()).isEqualTo("sabbeandres@outlook.com");
            assertThat(ophaalLid.getStart_lidmaatschap()).isEqualTo(huidigTijdstip);
            assertThat(ophaalLid.getEinde_lidmaatschap()).isNull();
            assertThat(ophaalLid.getOpmerking()).isEqualTo("dit is een opmerking");

        }finally {
            Removals.removeLid("64101612335");
        }
    }

    @Test
    public void wijzigenLidEmail() throws Exception{
        try{
            LocalDate huidigTijdstip = LocalDate.now();
            Lid lid = maakLid(new Rijksregisternummer("64101612335"), "Andres", "Sabbe", "sabbeandres@outlook.com", huidigTijdstip, "dit is een opmerking");
            assertThat(lidDAO.toevoegenLid(lid)).isTrue();

            lid.setEmailadres("sabbeandres@gmail.be");
            lidDAO.wijzigenLid(lid);
            Lid ophaalLid = lidDAO.zoekLid(lid.getRijksregisternummer());
            assertThat(ophaalLid.getRijksregisternummer()).isEqualTo("64101612335");
            assertThat(ophaalLid.getVoornaam()).isEqualTo("Andres");
            assertThat(ophaalLid.getNaam()).isEqualTo("Sabbe");
            assertThat(ophaalLid.getEmailadres()).isEqualTo("sabbeandres@gmail.be");
            assertThat(ophaalLid.getStart_lidmaatschap()).isEqualTo(huidigTijdstip);
            assertThat(ophaalLid.getEinde_lidmaatschap()).isNull();
            assertThat(ophaalLid.getOpmerking()).isEqualTo("dit is een opmerking");

        }finally {
            Removals.removeLid("64101612335");
        }
    }

    @Test
    public void wijzigenLidOpmerking() throws Exception{
        try{
            LocalDate huidigTijdstip = LocalDate.now();
            Lid lid = maakLid(new Rijksregisternummer("64101612335"), "Andres", "Sabbe", "sabbeandres@outlook.com", huidigTijdstip, "dit is een opmerking");
            assertThat(lidDAO.toevoegenLid(lid)).isTrue();

            lid.setOpmerking("opmerking 2");
            lidDAO.wijzigenLid(lid);
            Lid ophaalLid = lidDAO.zoekLid(lid.getRijksregisternummer());
            assertThat(ophaalLid.getRijksregisternummer()).isEqualTo("64101612335");
            assertThat(ophaalLid.getVoornaam()).isEqualTo("Andres");
            assertThat(ophaalLid.getNaam()).isEqualTo("Sabbe");
            assertThat(ophaalLid.getEmailadres()).isEqualTo("sabbeandres@outlook.com");
            assertThat(ophaalLid.getStart_lidmaatschap()).isEqualTo(huidigTijdstip);
            assertThat(ophaalLid.getEinde_lidmaatschap()).isNull();
            assertThat(ophaalLid.getOpmerking()).isEqualTo("opmerking 2");
        }finally {
            Removals.removeLid("64101612335");
        }
    }

    @Test
    public void wijzigenLidStartlidmaatschap() throws Exception{
        try{
            LocalDate huidigTijdstip = LocalDate.now();
            Lid lid = maakLid(new Rijksregisternummer("64101612335"), "Andres", "Sabbe", "sabbeandres@outlook.com", huidigTijdstip, "dit is een opmerking");
            assertThat(lidDAO.toevoegenLid(lid)).isTrue();

            LocalDate nieuwTijdstip = LocalDate.now().plusDays(2);
            lid.setStart_lidmaatschap(nieuwTijdstip);

            lidDAO.wijzigenLid(lid);
            Lid ophaalLid = lidDAO.zoekLid(lid.getRijksregisternummer());

            assertThat(ophaalLid.getRijksregisternummer()).isEqualTo("64101612335");
            assertThat(ophaalLid.getVoornaam()).isEqualTo("Andres");
            assertThat(ophaalLid.getNaam()).isEqualTo("Sabbe");
            assertThat(ophaalLid.getEmailadres()).isEqualTo("sabbeandres@outlook.com");
            assertThat(ophaalLid.getStart_lidmaatschap()).isEqualTo(nieuwTijdstip);
            assertThat(ophaalLid.getEinde_lidmaatschap()).isNull();
            assertThat(ophaalLid.getOpmerking()).isEqualTo("dit is een opmerking");

        }finally {
            Removals.removeLid("64101612335");
        }
    }


    @Test
    public void uitschrijvenLid() throws Exception {
        try{
            LocalDate huidigTijdstip = LocalDate.now();
            Lid lid = maakLid(new Rijksregisternummer("64101612335"), "Andres", "Sabbe", "sabbeandres@outlook.com", huidigTijdstip, "dit is een opmerking");
            assertThat(lidDAO.toevoegenLid(lid)).isTrue();

            lidDAO.uitschrijvenLid(lid.getRijksregisternummer());

            Lid ophaalLid = lidDAO.zoekLid(lid.getRijksregisternummer());

            assertThat(ophaalLid.getRijksregisternummer()).isEqualTo("64101612335");
            assertThat(ophaalLid.getVoornaam()).isEqualTo("Andres");
            assertThat(ophaalLid.getNaam()).isEqualTo("Sabbe");
            assertThat(ophaalLid.getEmailadres()).isEqualTo("sabbeandres@outlook.com");
            assertThat(ophaalLid.getStart_lidmaatschap()).isEqualTo(huidigTijdstip);
            assertThat(ophaalLid.getEinde_lidmaatschap()).isNotNull();
            assertThat(ophaalLid.getOpmerking()).isEqualTo("dit is een opmerking");

        }finally {
            Removals.removeLid("64101612335");
        }
    }

    @Test
    public void uitschrijvenLidZonderRijksregisterNummer(){
        assertThatCode(() -> lidDAO.uitschrijvenLid("")).doesNotThrowAnyException();
        assertThatCode(() -> lidDAO.uitschrijvenLid(null)).doesNotThrowAnyException();
    }

    @Test
    public void zoekLid() throws Exception{
        try{
            LocalDate huidigTijdstip = LocalDate.now();
            Lid lid = maakLid(new Rijksregisternummer("64101612335"), "Andres", "Sabbe", "sabbeandres@outlook.com", huidigTijdstip, "dit is een opmerking");
            assertThat(lidDAO.toevoegenLid(lid)).isTrue();

            Lid opgezochteLid = lidDAO.zoekLid(lid.getRijksregisternummer());

            assertThat(opgezochteLid.getRijksregisternummer()).isEqualTo("64101612335");
            assertThat(opgezochteLid.getVoornaam()).isEqualTo("Andres");
            assertThat(opgezochteLid.getNaam()).isEqualTo("Sabbe");
            assertThat(opgezochteLid.getEmailadres()).isEqualTo("sabbeandres@outlook.com");
            assertThat(opgezochteLid.getStart_lidmaatschap()).isEqualTo(huidigTijdstip);
            assertThat(opgezochteLid.getEinde_lidmaatschap()).isNull();
            assertThat(opgezochteLid.getOpmerking()).isEqualTo("dit is een opmerking");

        }finally {
            Removals.removeLid("64101612335");
        }
    }

    @Test
    public void zoekLidNull() throws Exception{
        assertThat(lidDAO.zoekLid(null)).isNull();
    }

    @Test
    public void zoekLidOnbestaande() throws Exception{
        assertThat(lidDAO.zoekLid("000000000000")).isNull();
    }

    @Test
    public void zoekAlleLeden() throws Exception{
        try{
            LocalDate huidigTijdstip = LocalDate.now();
            Lid lid1 = maakLid(new Rijksregisternummer("64101612335"), "Andres", "Sabbe", "sabbeandres@outlook.com", huidigTijdstip, "dit is een opmerking");
            Lid lid2 = maakLid(new Rijksregisternummer("93051822361"), "Pieter", "Post", "pieterpost@outlook.com", huidigTijdstip, "dit is een opmerking");
            Lid lid3 = maakLid(new Rijksregisternummer("75120513714"), "Franky", "Testman", "frankytestman@outlook.com", huidigTijdstip, "dit is een opmerking");

            List<Lid> opgezochteLedenVoor = lidDAO.zoekAlleLeden();

            assertThat(lidDAO.toevoegenLid(lid1)).isTrue();
            assertThat(lidDAO.toevoegenLid(lid2)).isTrue();
            assertThat(lidDAO.toevoegenLid(lid3)).isTrue();

            List<Lid> opgezochteLedenNa = lidDAO.zoekAlleLeden();

            assertThat(opgezochteLedenNa.size()).isEqualTo(opgezochteLedenVoor.size()+3);
        }finally {
            Removals.removeLid("64101612335");
            Removals.removeLid("93051822361");
            Removals.removeLid("75120513714");
        }
    }
}
