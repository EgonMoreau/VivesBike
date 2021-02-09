package be.vives.ti.service;

import be.vives.ti.dao.FietsDAO;
import be.vives.ti.databag.Fiets;
import be.vives.ti.datatype.Standplaats;
import be.vives.ti.datatype.Status;
import be.vives.ti.exception.ApplicationException;
import be.vives.ti.exception.ApplicationExceptionType;
import be.vives.ti.exception.DBException;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class FietsServiceTest {
    private FietsService fietsService;
    private FietsDAO fietsDAO;

    public FietsServiceTest() {
        this.fietsDAO = mock(FietsDAO.class);
        this.fietsService = new FietsService(fietsDAO);
    }

    private Fiets maakFiets(Status status, Standplaats standplaats, String opmerking) {
        Fiets fiets = new Fiets();
        fiets.setStatus(status);
        fiets.setStandplaats(standplaats);
        fiets.setOpmerking(opmerking);
        return fiets;
    }

    @Test
    public void toevoegenFiets_null() throws Exception {
        assertThatThrownBy(() -> {
            fietsService.toevoegenFiets(null);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.FIETS_NULL.getMessage());

        verify(fietsDAO, never()).toevoegenFiets(null);
    }

    @Test
    public void toevoegenFiets_standplaats_null() throws Exception {
        Fiets fiets = maakFiets(Status.actief, null, "testing");
        assertThatThrownBy(() -> {
            fietsService.toevoegenFiets(fiets);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.FIETS_STANDPLAATS_ONBEKEND.getMessage());

        verify(fietsDAO, never()).toevoegenFiets(fiets);
    }

    @Test
    public void toevoegenFiets_status_null() throws Exception {
        Fiets fiets = maakFiets(null, Standplaats.Kortrijk, "testing");
        assertThatThrownBy(() -> {
            fietsService.toevoegenFiets(fiets);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.FIETS_STATUS_LEEG.getMessage());

        verify(fietsDAO, never()).toevoegenFiets(fiets);
    }

    //positieve test
    @Test
    public void toevoegenFiets_succesvol() throws Exception {
        Fiets fiets = maakFiets(Status.actief, Standplaats.Kortrijk, "testen");
        fiets.setRegistratienummer(fietsDAO.toevoegenFiets(fiets));

        when(fietsDAO.toevoegenFiets(fiets)).thenReturn(fiets.getRegistratienummer());

        Integer returnedFietsId = fietsService.toevoegenFiets(fiets);

        assertThat(returnedFietsId).isEqualTo(fiets.getRegistratienummer());

    }

    @Test
    public void wijzigenStatusNaarHerstel() throws Exception {
        int fietsID = 123;
        Fiets fiets = maakFiets(Status.actief, Standplaats.Kortrijk, "testen");
        fiets.setRegistratienummer(fietsID);

        when(fietsDAO.zoekFiets(fiets.getRegistratienummer())).thenReturn(fiets);

        Fiets teZoekenFiets = fietsService.zoekFiets(fiets.getRegistratienummer());
        assertThat(teZoekenFiets.getStatus()).isEqualTo(Status.actief);

        fiets.setStatus(Status.herstel);
        fietsService.wijzigenStatusNaarHerstel(fiets.getRegistratienummer());

        teZoekenFiets = fietsService.zoekFiets(fiets.getRegistratienummer());
        assertThat(teZoekenFiets.getStatus()).isEqualTo(Status.herstel);
    }

    @Test
    public void wijzigenStatusNaarUitOmloop() throws Exception {
        int fietsID = 123;
        Fiets fiets = maakFiets(Status.actief, Standplaats.Kortrijk, "testen");
        fiets.setRegistratienummer(fietsID);

        when(fietsDAO.zoekFiets(fiets.getRegistratienummer())).thenReturn(fiets);

        Fiets teZoekenFiets = fietsService.zoekFiets(fiets.getRegistratienummer());
        assertThat(teZoekenFiets.getStatus()).isEqualTo(Status.actief);

        fiets.setStatus(Status.uit_omloop);
        fietsService.wijzigenStatusNaarHerstel(fiets.getRegistratienummer());

        teZoekenFiets = fietsService.zoekFiets(fiets.getRegistratienummer());
        assertThat(teZoekenFiets.getStatus()).isEqualTo(Status.uit_omloop);
    }

    @Test
    public void wijzigenStatusNaarActief() throws Exception {
        int fietsID = 123;
        Fiets fiets = maakFiets(Status.herstel, Standplaats.Kortrijk, "testen");
        fiets.setRegistratienummer(fietsID);

        when(fietsDAO.zoekFiets(fiets.getRegistratienummer())).thenReturn(fiets);

        Fiets teZoekenFiets = fietsService.zoekFiets(fiets.getRegistratienummer());
        assertThat(teZoekenFiets.getStatus()).isEqualTo(Status.herstel);

        fiets.setStatus(Status.actief);
        fietsService.wijzigenStatusNaarHerstel(fiets.getRegistratienummer());

        teZoekenFiets = fietsService.zoekFiets(fiets.getRegistratienummer());
        assertThat(teZoekenFiets.getStatus()).isEqualTo(Status.actief);
    }

    @Test
    public void wijzigenOpmerkingFiets() throws Exception {
        int fietsID = 123;
        Fiets fiets = maakFiets(Status.actief, Standplaats.Kortrijk, "testen");
        fiets.setRegistratienummer(fietsID);

        when(fietsDAO.zoekFiets(fiets.getRegistratienummer())).thenReturn(fiets);

        Fiets teZoekenFiets = fietsService.zoekFiets(fiets.getRegistratienummer());
        assertThat(teZoekenFiets.getOpmerking()).isEqualTo("testen");

        fiets.setOpmerking("Bewerkt");
        fietsService.wijzigenStatusNaarHerstel(fiets.getRegistratienummer());

        teZoekenFiets = fietsService.zoekFiets(fiets.getRegistratienummer());
        assertThat(teZoekenFiets.getOpmerking()).isEqualTo("Bewerkt");
    }

    @Test
    public void zoekFiets() throws Exception {
        int fietsID = 123;
        Fiets fiets = maakFiets(Status.actief, Standplaats.Kortrijk, "testen");
        fiets.setRegistratienummer(fietsID);

        when(fietsDAO.zoekFiets(fietsID)).thenReturn(fiets);

        assertThat(fietsService.zoekFiets(fietsID)).isEqualTo(fiets);
    }


}
