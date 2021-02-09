package be.vives.ti.service;

import be.vives.ti.dao.LidDAO;
import be.vives.ti.databag.Lid;
import be.vives.ti.datatype.Rijksregisternummer;
import be.vives.ti.exception.ApplicationException;
import be.vives.ti.exception.ApplicationExceptionType;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;

import static org.mockito.Mockito.mock;

public class LidServiceTest {
    private RitService ritService;
    private LidService lidService;
    private LidDAO lidDAO;

    public LidServiceTest(){
        this.lidDAO = mock(LidDAO.class);
        this.ritService = mock(RitService.class);
        this.lidService = new LidService(lidDAO, ritService);
    }

    private Lid maakLid(Rijksregisternummer rijksregisternummer, String voornaam, String naam, String emailadres, String opmerking) {
        Lid lid = new Lid();
        lid.setRijksregisternummer(rijksregisternummer);
        lid.setVoornaam(voornaam);
        lid.setNaam(naam);
        lid.setEmailadres(emailadres);
        lid.setOpmerking(opmerking);
        return lid;
    }


    @Test
    public void toevoegenLidZonderEmail() throws Exception{
        Lid lid = maakLid(new Rijksregisternummer("00031300147"), "Pieter", "Post", "", "");
        assertThatThrownBy(()->{
            lidService.toevoegenLid(lid);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.LID_EMAIL_LEEG.getMessage());

        verify(lidDAO, never()).toevoegenLid(lid);

    }

    @Test
    public void toevoegenLidZonderOpmerking() throws Exception{
        Lid lid = maakLid(new Rijksregisternummer("00031300147"), "Pieter", "Post", "pieterpost@gmail.com", "");
        when(lidDAO.toevoegenLid(lid)).thenReturn(true);
        assertThat(lidService.toevoegenLid(lid)).isTrue();

    }

    @Test
    public void toevoegenLidZonderVoornaam() throws Exception{
        Lid lid = maakLid(new Rijksregisternummer("00031300147"), "", "Post", "pieterpost@gmail.com", "");
        assertThatThrownBy(()->{
            lidService.toevoegenLid(lid);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.LID_VOORNAAM_LEEG.getMessage());
    }

    @Test
    public void wijzigenLidNaam() throws Exception{
        Lid lid = maakLid(new Rijksregisternummer("00031300147"), "Pieter", "Post", "pieterpost@gmail.com", "");

        when(lidDAO.zoekLid(lid.getRijksregisternummer())).thenReturn(lid);

        Lid teZoekenLid = lidService.zoekLid(lid.getRijksregisternummer());
        assertThat(teZoekenLid.getNaam()).isEqualTo(lid.getNaam());

        lid.setNaam("Dutry");

        lidService.wijzigenLid(lid);

        when(lidDAO.zoekLid(lid.getRijksregisternummer())).thenReturn(lid);

        teZoekenLid = lidService.zoekLid(lid.getRijksregisternummer());
        assertThat(teZoekenLid.getNaam()).isEqualTo(lid.getNaam());

        verify(lidDAO, Mockito.times(3)).zoekLid(lid.getRijksregisternummer());
        verify(lidDAO).wijzigenLid(lid);
    }

    @Test
    public void wijzigenLidStartlidmaatschap() throws Exception{

    }

    @Test
    public void zoekLid() throws Exception{
        Lid lid = maakLid(new Rijksregisternummer("00031300147"), "Pieter", "Post", "pieterpost@gmail.com", "");

        when(lidDAO.zoekLid(lid.getRijksregisternummer())).thenReturn(lid);
        Lid teZoekenLid = lidService.zoekLid(lid.getRijksregisternummer());

        assertThat(teZoekenLid.getRijksregisternummer()).isEqualTo(lid.getRijksregisternummer());
        assertThat(teZoekenLid.getEmailadres()).isEqualTo(lid.getEmailadres());
        assertThat(teZoekenLid.getNaam()).isEqualTo(lid.getNaam());
        assertThat(teZoekenLid.getVoornaam()).isEqualTo(lid.getVoornaam());

        verify(lidDAO).zoekLid(lid.getRijksregisternummer());
    }

    @Test
    public void toevoegenLidNull() throws Exception{
        assertThatThrownBy(()->{
            lidService.toevoegenLid(null);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.LID_NULL.getMessage());

        verify(lidDAO, never()).toevoegenLid(null);
    }

    @Test
    public void wijzigenLidVoornaam() throws Exception{
        Lid lid = maakLid(new Rijksregisternummer("00031300147"), "Pieter", "Post", "pieterpost@gmail.com", "");

        when(lidDAO.zoekLid(lid.getRijksregisternummer())).thenReturn(lid);

        Lid teZoekenLid = lidService.zoekLid(lid.getRijksregisternummer());
        assertThat(teZoekenLid.getVoornaam()).isEqualTo(lid.getVoornaam());

        lid.setVoornaam("Peter");

        lidService.wijzigenLid(lid);

        when(lidDAO.zoekLid(lid.getRijksregisternummer())).thenReturn(lid);

        teZoekenLid = lidService.zoekLid(lid.getRijksregisternummer());
        assertThat(teZoekenLid.getVoornaam()).isEqualTo(lid.getVoornaam());

        verify(lidDAO, Mockito.times(3)).zoekLid(lid.getRijksregisternummer());
        verify(lidDAO).wijzigenLid(lid);

    }

    @Test
    public void toevoegenLidZonderNaam() throws Exception{
        Lid lid = maakLid(new Rijksregisternummer("00031300147"), "", "Post", "pieterpost@gmail.com", "");
        assertThatThrownBy(()->{
            lidService.toevoegenLid(lid);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.LID_VOORNAAM_LEEG.getMessage());
        verify(lidDAO, never()).toevoegenLid(lid);

    }

    @Test
    public void uitschrijvenLidZonderRijksregisternummer() throws Exception{
        when(lidDAO.zoekLid("")).thenReturn(null);
        assertThatThrownBy(()->{
            lidService.uitschrijvenLid("");
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.LID_RIJSKREGISTERNUMMER_LEEG.getMessage());

        verify(lidDAO, never()).zoekLid("");
        verify(lidDAO, never()).uitschrijvenLid("");

    }

    @Test
    public void zoekLidNull() throws Exception{
        assertThatThrownBy(()->{
            lidService.zoekLid(null);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.LID_RIJSKREGISTERNUMMER_LEEG.getMessage());
    }

    @Test
    public void wijzigenLidEmail() throws Exception{
        Lid lid = maakLid(new Rijksregisternummer("00031300147"), "Pieter", "Post", "pieterpost@gmail.com", "");

        when(lidDAO.zoekLid(lid.getRijksregisternummer())).thenReturn(lid);

        Lid teZoekenLid = lidService.zoekLid(lid.getRijksregisternummer());
        assertThat(teZoekenLid.getEmailadres()).isEqualTo(lid.getEmailadres());

        lid.setEmailadres("123@123.com");

        lidService.wijzigenLid(lid);

        when(lidDAO.zoekLid(lid.getRijksregisternummer())).thenReturn(lid);

        teZoekenLid = lidService.zoekLid(lid.getRijksregisternummer());
        assertThat(teZoekenLid.getEmailadres()).isEqualTo(lid.getEmailadres());

        verify(lidDAO, Mockito.times(3)).zoekLid(lid.getRijksregisternummer());
        verify(lidDAO).wijzigenLid(lid);
    }

    @Test
    public void zoekLidOnbestaande() throws Exception{
        assertThat(
            lidService.zoekLid("00031300147")).isNull();
    }

    @Test
    public void zoekAlleLeden() throws Exception{

    }

    @Test
    public void wijzigenLidOpmerking() throws Exception{
        Lid lid = maakLid(new Rijksregisternummer("00031300147"), "Pieter", "Post", "pieterpost@gmail.com", "");

        when(lidDAO.zoekLid(lid.getRijksregisternummer())).thenReturn(lid);

        Lid teZoekenLid = lidService.zoekLid(lid.getRijksregisternummer());
        assertThat(teZoekenLid.getOpmerking()).isEqualTo(lid.getOpmerking());

        lid.setOpmerking("Dit is een opmerking");

        lidService.wijzigenLid(lid);

        when(lidDAO.zoekLid(lid.getRijksregisternummer())).thenReturn(lid);

        teZoekenLid = lidService.zoekLid(lid.getRijksregisternummer());
        assertThat(teZoekenLid.getOpmerking()).isEqualTo(lid.getOpmerking());

        verify(lidDAO, Mockito.times(3)).zoekLid(lid.getRijksregisternummer());
        verify(lidDAO).wijzigenLid(lid);

    }

    @Test
    public void toevoegenLid() throws Exception{
        Lid lid = maakLid(new Rijksregisternummer("00031300147"), "Pieter", "Post", "pieterpost@gmail.com", "");

        when(lidDAO.toevoegenLid(lid)).thenReturn(true);
        when(lidDAO.zoekLid(lid.getRijksregisternummer())).thenReturn(null);

        assertThat(lidService.toevoegenLid(lid)).isTrue();

        verify(lidDAO).toevoegenLid(lid);
    }

    @Test
    public void uitschrijvenLid() throws Exception{
        Lid lid = maakLid(new Rijksregisternummer("00031300147"), "Pieter", "Post", "pieterpost@gmail.com", "");

        when(lidDAO.zoekLid(lid.getRijksregisternummer())).thenReturn(lid);
        assertThatCode(()->{
            lidService.uitschrijvenLid(lid.getRijksregisternummer());
        }).doesNotThrowAnyException();

        verify(lidDAO).zoekLid(lid.getRijksregisternummer());
        verify(lidDAO).uitschrijvenLid(lid.getRijksregisternummer());


    }

    @Ignore
    public void toevoegenLidZonderRijksregisternummer() throws Exception{

    }

    @Test
    public void toevoegenLidZonderStartLidmaatschap() throws Exception{

    }


}
