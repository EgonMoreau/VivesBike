package be.vives.ti.service;

import be.vives.ti.dao.LidDAO;
import be.vives.ti.dao.RitDAO;
import be.vives.ti.databag.Fiets;
import be.vives.ti.databag.Lid;
import be.vives.ti.databag.Rit;
import be.vives.ti.datatype.Rijksregisternummer;
import be.vives.ti.datatype.Standplaats;
import be.vives.ti.datatype.Status;
import be.vives.ti.exception.ApplicationException;
import be.vives.ti.exception.ApplicationExceptionType;
import org.junit.Test;
import org.mockito.Mockito;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RitServiceTest {
    private RitService ritService;
    private RitDAO ritDAO;
    private LidService lidService;
    private FietsService fietsService;
    private LidDAO lidDAO;


    public RitServiceTest(){
        this.ritDAO = mock(RitDAO.class);
        this.fietsService = mock(FietsService.class);
        this.lidDAO=mock(LidDAO.class);
        this.ritService = new RitService(ritDAO, fietsService, lidDAO);
        this.lidService = new LidService(lidDAO, ritService);
    }


    private Rit maakRit(Rijksregisternummer rijksregisternr, int fietregistratienr) {
        Rit rit = new Rit();
        rit.setLidRijksregisternummer(rijksregisternr);
        rit.setFietsRegistratienummer(fietregistratienr);
        return rit;
    }

    private Lid maakLid(Rijksregisternummer rijksregisternummer, String voornaam, String naam, String emailadres, LocalDate start_lidmaatschap) {
        Lid lid = new Lid();
        lid.setRijksregisternummer(rijksregisternummer);
        lid.setVoornaam(voornaam);
        lid.setNaam(naam);
        lid.setEmailadres(emailadres);
        lid.setStart_lidmaatschap(start_lidmaatschap);
        return lid;
    }

    private Fiets maakFiets(Status status, Standplaats standplaats, String opmerking) {
        Fiets fiets = new Fiets();
        fiets.setStatus(status);
        fiets.setStandplaats(standplaats);
        fiets.setOpmerking(opmerking);
        return fiets;
    }

    @Test
    public void toevoegenRit_StartTijdIngevuld() throws Exception{
        int fietsId = 8;
        Fiets fiets = maakFiets(Status.actief, Standplaats.Kortrijk, "Eerste fiets toegevoegd");
        fiets.setRegistratienummer(fietsId);
        Rit rit = maakRit(new Rijksregisternummer("00031300147"), fietsId);
        rit.setStarttijd(LocalDateTime.now());

        assertThatThrownBy(() -> {
            ritService.toevoegenRit(rit);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.RIT_STARTTIJD_AUTOMATISCH.getMessage());

        verify(ritDAO, never()).toevoegenRit(rit);
    }

    @Test
    public void toevoegenRit_LidNull() throws Exception{
        int fietsId = 8;
        Fiets fiets = maakFiets(Status.actief, Standplaats.Kortrijk, "Eerste fiets toegevoegd");
        fiets.setRegistratienummer(fietsId);
        Rit rit = maakRit(null, fietsId);

        when(fietsService.zoekFiets(fietsId)).thenReturn(fiets);

        assertThatThrownBy(() -> {
            ritService.toevoegenRit(rit);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.LID_RIJSKREGISTERNUMMER_LEEG.getMessage());

        verify(ritDAO, never()).toevoegenRit(rit);
    }

    @Test
    public void toevoegenRit_LidBestaatNiet() throws Exception{
        int fietsId = 8;
        String rrn = "00031300147";
        Fiets fiets = maakFiets(Status.actief, Standplaats.Kortrijk, "Eerste fiets toegevoegd");
        fiets.setRegistratienummer(fietsId);
        Rit rit = maakRit(new Rijksregisternummer(rrn), fietsId);

        when(lidService.zoekLid(rrn)).thenReturn(null);
        when(fietsService.zoekFiets(fietsId)).thenReturn(fiets);

        assertThatThrownBy(() -> {
            ritService.toevoegenRit(rit);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.LID_BESTAAT_NIET.getMessage());

        verify(ritDAO, never()).toevoegenRit(rit);
        verify(lidDAO).zoekLid(rrn);
    }

    @Test
    public void toevoegenRit_LidHuurtAl() throws Exception{
        int fietsId = 8;
        Fiets fiets = maakFiets(Status.actief, Standplaats.Kortrijk, "Eerste fiets toegevoegd");
        fiets.setRegistratienummer(fietsId);
        Lid lid = maakLid(new Rijksregisternummer("00031300147"), "Andres", "Sabbe", "sabbeandres@gmail.com", LocalDate.now());
        Rit rit = maakRit(new Rijksregisternummer(lid.getRijksregisternummer()), fietsId);

        when(lidService.zoekLid(lid.getRijksregisternummer())).thenReturn(lid);
        when(fietsService.zoekFiets(fietsId)).thenReturn(fiets);
        ArrayList<Rit> ritten = new ArrayList<>();
        Rit r = new Rit();
        ritten.add(r);
        when(ritDAO.zoekActieveRittenVanLid(lid.getRijksregisternummer())).thenReturn(ritten);

        assertThatThrownBy(() -> {
            ritService.toevoegenRit(rit);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.RIT_LID_HUURT.getMessage());

        verify(ritDAO, never()).toevoegenRit(rit);
        verify(lidDAO, Mockito.times(2)).zoekLid(lid.getRijksregisternummer());
    }

    @Test
    public void toevoegenRit_FietsAlInGebruik() throws Exception{
        int fietsId = 8;
        Fiets fiets = maakFiets(Status.actief, Standplaats.Kortrijk, "Eerste fiets toegevoegd");
        fiets.setRegistratienummer(fietsId);
        Rit rit = maakRit(new Rijksregisternummer("00031300147"), fietsId);

        when(fietsService.zoekFiets(fietsId)).thenReturn(fiets);
        ArrayList<Rit> ritten = new ArrayList<>();
        Rit r = new Rit();
        ritten.add(r);
        when(ritDAO.zoekActieveRittenVanFiets(fietsId)).thenReturn(ritten);

        assertThatThrownBy(() -> {
            ritService.toevoegenRit(rit);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.RIT_FIETS_IN_GEBRUIK.getMessage());

        verify(ritDAO, never()).toevoegenRit(rit);
    }

    @Test
    public void toevoegenRit_FietsBestaatNiet() throws Exception{
        int fietsId = 8;
        Fiets fiets = maakFiets(Status.actief, Standplaats.Kortrijk, "Eerste fiets toegevoegd");
        fiets.setRegistratienummer(fietsId);
        Rit rit = maakRit(new Rijksregisternummer("00031300147"), fietsId);

        when(fietsService.zoekFiets(fietsId)).thenReturn(null);

        assertThatThrownBy(() -> {
            ritService.toevoegenRit(rit);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.FIETS_BESTAAT_NIET.getMessage());

        verify(ritDAO, never()).toevoegenRit(rit);
    }

    @Test
    public void toevoegenRit_succesvol() throws Exception {
        int fietsId = 8;
        Fiets fiets = maakFiets(Status.actief, Standplaats.Kortrijk, "Eerste fiets toegevoegd");
        fiets.setRegistratienummer(fietsId);
        Lid lid = maakLid(new Rijksregisternummer("00031300147"), "Andres", "Sabbe", "sabbeandres@gmail.com", LocalDate.now());
        Rit rit = maakRit(new Rijksregisternummer(lid.getRijksregisternummer()), fietsId);

        when(lidService.zoekLid(lid.getRijksregisternummer())).thenReturn(lid);
        when(fietsService.zoekFiets(fietsId)).thenReturn(fiets);
        when(ritDAO.zoekActieveRittenVanFiets(fietsId)).thenReturn(Collections.emptyList());
        when(ritDAO.zoekActieveRittenVanLid(lid.getRijksregisternummer())).thenReturn(Collections.emptyList());
        when(ritDAO.toevoegenRit(rit)).thenReturn(2);

        assertThat(ritService.toevoegenRit(rit)).isEqualTo(2);
        verify(ritDAO, atLeastOnce()).toevoegenRit(rit);
        verify(lidDAO, Mockito.times(2)).zoekLid(lid.getRijksregisternummer());
    }

    @Test
    public void afsluitenRit_zonderId() throws Exception {
        assertThatThrownBy(() -> {
            ritService.afsluitenRit(null);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.RIT_ID_LEEG.getMessage());

        verify(ritDAO, never()).afsluitenRit(null);
    }

    @Test
    public void afsluitenRit_onbestaandeRit() throws Exception {
        Integer ritId = 2000;

        int fietsId = 8;
        Fiets fiets = maakFiets(Status.actief, Standplaats.Kortrijk, "Eerste fiets toegevoegd");
        fiets.setRegistratienummer(fietsId);
        Rit rit = maakRit(new Rijksregisternummer("00031300147"), fietsId);
        rit.setId(ritId);

        when(ritDAO.zoekRit(rit.getId())).thenReturn(null);

        assertThatThrownBy(() -> {
            ritService.afsluitenRit(rit.getId());
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.RIT_ONBEKEND.getMessage());

        verify(ritDAO, times(1)).zoekRit(rit.getId());
        verify(ritDAO, never()).afsluitenRit(rit);
    }

    @Test
    public void prijsBerekenen_1uur() throws Exception {
        Rit rit = maakRit(new Rijksregisternummer("00031300147"), 2);
        rit.setStarttijd(LocalDateTime.now());
        rit.setEindtijd(LocalDateTime.now().plusHours(1));
        assertThat(ritService.prijsBerekenen(rit)).isEqualTo(1);
    }

    @Test
    public void prijsBerekenen_24uur() throws Exception {
        Rit rit = maakRit(new Rijksregisternummer("00031300147"), 2);
        rit.setStarttijd(LocalDateTime.now());
        rit.setEindtijd(LocalDateTime.now().plusHours(24));
        assertThat(ritService.prijsBerekenen(rit)).isEqualTo(1);
    }

    @Test
    public void prijsBerekenen_25uur() throws Exception {
        Rit rit = maakRit(new Rijksregisternummer("00031300147"), 2);
        rit.setStarttijd(LocalDateTime.now());
        rit.setEindtijd(LocalDateTime.now().plusHours(25));
        assertThat(ritService.prijsBerekenen(rit)).isEqualTo(2);
    }

    @Test
    public void prijsBerekenen_49uur() throws Exception {
        Rit rit = maakRit(new Rijksregisternummer("00031300147"), 2);
        rit.setStarttijd(LocalDateTime.now());
        rit.setEindtijd(LocalDateTime.now().plusHours(49));
        assertThat(ritService.prijsBerekenen(rit)).isEqualTo(3);
    }

    @Test
    public void afsluitenRit_succesvol() throws Exception {
        Integer ritId = 2000;

        int fietsId = 8;
        Fiets fiets = maakFiets(Status.actief, Standplaats.Kortrijk, "Eerste fiets toegevoegd");
        fiets.setRegistratienummer(fietsId);
        Rit rit = maakRit(new Rijksregisternummer("00031300147"), fietsId);
        rit.setStarttijd(LocalDateTime.now());
        rit.setId(ritId);

        when(ritDAO.zoekRit(rit.getId())).thenReturn(rit);

        assertThatCode(() -> {
            ritService.afsluitenRit(rit.getId());
        }).doesNotThrowAnyException();

        verify(ritDAO).afsluitenRit(rit);
        verify(ritDAO).zoekRit(rit.getId());

    }

    @Test
    public void zoekRit_zonderId(){
        assertThatThrownBy(() -> {
            ritService.afsluitenRit(null);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.RIT_ID_LEEG.getMessage());
    }

    @Test
    public void zoekRit_Onbestaand() throws Exception{
        int ritId = 3;
        when(ritDAO.zoekRit(ritId)).thenReturn(null);

        assertThatCode(() -> {
            ritService.zoekRit(ritId);
        }).doesNotThrowAnyException();

        assertThat(ritService.zoekRit(ritId)).isNull();
    }

    @Test
    public void zoekRit_succesvol() throws Exception {
        Integer ritId = 2000;

        int fietsId = 8;
        Fiets fiets = maakFiets(Status.actief, Standplaats.Kortrijk, "Eerste fiets toegevoegd");
        fiets.setRegistratienummer(fietsId);
        Rit rit = maakRit(new Rijksregisternummer("00031300147"), fietsId);
        rit.setStarttijd(LocalDateTime.now());
        rit.setId(ritId);

        when(ritDAO.zoekRit(ritId)).thenReturn(rit);

        assertThat(ritService.zoekRit(ritId)).isEqualTo(rit);

    }

    @Test
    public void zoekEersteRitVanLid_LeegRijksregisternummer() throws Exception {
        assertThatThrownBy(() -> {
            ritService.zoekEersteRitVanLid(null);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.LID_RIJSKREGISTERNUMMER_LEEG.getMessage());

        verify(ritDAO, never()).zoekEersteRitVanLid(null);
    }

    @Test
    public void zoekEersteRitVanLid_GeenRit() throws Exception {
        Lid lid = maakLid(new Rijksregisternummer("00031300147"), "Andres", "Sabbe", "sabbeandres@gmail.com", LocalDate.now());

        when(lidService.zoekLid(lid.getRijksregisternummer())).thenReturn(lid);
        when(ritDAO.zoekEersteRitVanLid(lid.getRijksregisternummer())).thenReturn(null);

        assertThat(ritService.zoekEersteRitVanLid(lid.getRijksregisternummer())).isEqualTo(null);

        verify(ritDAO).zoekEersteRitVanLid(lid.getRijksregisternummer());
        verify(lidDAO).zoekLid(lid.getRijksregisternummer());
    }

    @Test
    public void zoekEersteRitVanLid_succesvol() throws Exception {
        Lid lid = maakLid(new Rijksregisternummer("00031300147"), "Andres", "Sabbe", "sabbeandres@gmail.com", LocalDate.now());
        Rit rit = maakRit(new Rijksregisternummer(lid.getRijksregisternummer()), 3);

        when(lidService.zoekLid(lid.getRijksregisternummer())).thenReturn(lid);
        when(ritDAO.zoekEersteRitVanLid("00031300147")).thenReturn(rit);

        assertThat(ritService.zoekEersteRitVanLid(lid.getRijksregisternummer())).isEqualTo(rit);
        verify(lidDAO).zoekLid(lid.getRijksregisternummer());
    }

    @Test
    public void zoekActieveRittenVanLid_LeegRijksregisternummer() throws Exception {
        assertThatThrownBy(() -> {
            ritService.zoekActieveRittenVanLid(null);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.LID_RIJSKREGISTERNUMMER_LEEG.getMessage());

        verify(ritDAO, never()).zoekActieveRittenVanLid(null);
    }

    @Test
    public void zoekActieveRittenVanLid_GeenRitten() throws Exception {
        Lid lid = maakLid(new Rijksregisternummer("00031300147"), "Andres", "Sabbe", "sabbeandres@gmail.com", LocalDate.now());

        when(lidService.zoekLid(lid.getRijksregisternummer())).thenReturn(lid);
        when(ritDAO.zoekActieveRittenVanLid(lid.getRijksregisternummer())).thenReturn(Collections.emptyList());

        assertThat(ritService.zoekActieveRittenVanLid(lid.getRijksregisternummer())).isEqualTo(Collections.emptyList());

        verify(ritDAO).zoekActieveRittenVanLid(lid.getRijksregisternummer());
        verify(lidDAO).zoekLid(lid.getRijksregisternummer());
    }

    @Test
    public void zoekActieveRittenVanLid_OnbekendLid() throws Exception {
        String rrn = "00031300146";
        when(lidService.zoekLid(rrn)).thenReturn(null);

        assertThatThrownBy(() -> {
            ritService.zoekActieveRittenVanLid(rrn);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.LID_BESTAAT_NIET.getMessage());

        verify(ritDAO, never()).zoekActieveRittenVanLid(rrn);
    }

    @Test
    public void zoekActieveRittenVanLid_Succesvol() throws Exception {
        Lid lid = maakLid(new Rijksregisternummer("00031300147"), "Andres", "Sabbe", "sabbeandres@gmail.com", LocalDate.now());
        Rit rit = maakRit(new Rijksregisternummer(lid.getRijksregisternummer()), 1);
        List<Rit> ritten = new ArrayList<>();
        ritten.add(rit);

        when(lidService.zoekLid(lid.getRijksregisternummer())).thenReturn(lid);
        when(ritDAO.zoekActieveRittenVanLid(lid.getRijksregisternummer())).thenReturn(ritten);

        assertThat(ritService.zoekActieveRittenVanLid(lid.getRijksregisternummer())).containsExactly(rit);

        verify(ritDAO).zoekActieveRittenVanLid(lid.getRijksregisternummer());
        verify(lidDAO).zoekLid(lid.getRijksregisternummer());
    }

    @Test
    public void zoekActieveRittenVanFiets_LeegRegistratienummer(){
        assertThatThrownBy(()-> {
            ritService.zoekActieveRittenVanFiets(null);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.FIETS_REGISTRATIE_LEEG.getMessage());
    }

    @Test
    public void zoekActieveRittenVanFiets_GeenRitten() throws Exception {
        Fiets fiets = maakFiets(Status.actief, Standplaats.Kortrijk, "");
        fiets.setRegistratienummer(1);

        when(fietsService.zoekFiets(fiets.getRegistratienummer())).thenReturn(fiets);
        when(ritDAO.zoekActieveRittenVanFiets(fiets.getRegistratienummer())).thenReturn(Collections.emptyList());

        assertThat(ritService.zoekActieveRittenVanFiets(fiets.getRegistratienummer())).isEqualTo(Collections.emptyList());

        verify(ritDAO).zoekActieveRittenVanFiets(fiets.getRegistratienummer());
        verify(fietsService).zoekFiets(fiets.getRegistratienummer());
    }

    @Test
    public void zoekActieveRittenVanFiets_OnbekendeFiets() throws Exception {
        when(fietsService.zoekFiets(1)).thenReturn(null);

        assertThatThrownBy(() -> {
            ritService.zoekActieveRittenVanFiets(1);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.FIETS_BESTAAT_NIET.getMessage());

        verify(ritDAO, never()).zoekActieveRittenVanFiets(1);
    }

    @Test
    public void zoekActieveRittenVanFiets_Succesvol() throws Exception {
        Lid lid = maakLid(new Rijksregisternummer("00031300147"), "Andres", "Sabbe", "sabbeandres@gmail.com", LocalDate.now());
        Fiets fiets = maakFiets(Status.actief, Standplaats.Kortrijk, "");
        fiets.setRegistratienummer(1);

        Rit rit = maakRit(new Rijksregisternummer(lid.getRijksregisternummer()), fiets.getRegistratienummer());
        List<Rit> ritten = new ArrayList<>();
        ritten.add(rit);

        when(fietsService.zoekFiets(fiets.getRegistratienummer())).thenReturn(fiets);
        when(ritDAO.zoekActieveRittenVanFiets(fiets.getRegistratienummer())).thenReturn(ritten);

        assertThat(ritService.zoekActieveRittenVanFiets(fiets.getRegistratienummer())).containsExactly(rit);

        verify(ritDAO).zoekActieveRittenVanFiets(fiets.getRegistratienummer());
        verify(fietsService).zoekFiets(fiets.getRegistratienummer());
    }
}