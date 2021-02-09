package be.vives.ti.service;

import be.vives.ti.dao.FietsDAO;
import be.vives.ti.databag.Fiets;
import be.vives.ti.datatype.Standplaats;
import be.vives.ti.datatype.Status;
import be.vives.ti.exception.ApplicationException;
import be.vives.ti.exception.ApplicationExceptionType;
import be.vives.ti.exception.DBException;
import org.apache.commons.lang3.EnumUtils;

import java.util.List;


/**
 * Bevat alle functionaliteit van een fiets, met de nodige checks.
 * - toevoegen van een fiets.
 * - wijzigen van status naar herstel van een fiets.
 * - wijzigen van status naar uitomloop van een fiets.
 * - wijzigen van status naar Actief van een fiets.
 * - wijzigen van opmering van een fiets.
 * - zoeken van een fiets
 * - zoeken van alle beschikbare fiets.
 * - zoeken alle fietsen
 * - check alle velden ingevuld.
 */
public class FietsService {
    private FietsDAO fietsDAO;

    public FietsService(FietsDAO fietsDAO) {
        this.fietsDAO = fietsDAO;
    }

    /**
     * Het toevoegen van een fiets
     *
     * @param fiets Fiets die moet worden toegevoegd.
     * @throws ApplicationException Wordt gegooid wanneer de fiets parameter null is.
     * @throws ApplicationException Wordt gegooid wanneer de standplaats van een fiets niet gekend is.
     * @throws DBException          duidt op fouten vanuit de be.vives.DAO.
     */
    public Integer toevoegenFiets(Fiets fiets) throws ApplicationException, DBException {

        if (fiets == null) {
            throw new ApplicationException(ApplicationExceptionType.FIETS_NULL.getMessage());
        }

        if (fiets.getStandplaats() == null || !EnumUtils.isValidEnum(Standplaats.class, fiets.getStandplaats().toString())) {
            throw new ApplicationException(ApplicationExceptionType.FIETS_STANDPLAATS_ONBEKEND.getMessage());
        }

        if (fiets.getStatus() == null || !EnumUtils.isValidEnum(Status.class, fiets.getStatus().toString())) {
            throw new ApplicationException(ApplicationExceptionType.FIETS_STATUS_LEEG.getMessage());
        }

        return fietsDAO.toevoegenFiets(fiets);

    }

    /**
     * veranderd de status van de fiets naar Herstel.
     *
     * @param regnr Het registratienummer van de fiets die moet veranderd worden.
     * @throws ApplicationException Wordt gegooid wanneer Fiets NULL is.
     * @throws DBException          Duidt op fouten vanuit de be.vives.DAO.
     */
    public void wijzigenStatusNaarHerstel(int regnr) throws ApplicationException, DBException {
        if (zoekFiets(regnr) == null) {
            throw new ApplicationException(ApplicationExceptionType.FIETS_NULL.getMessage());
        }
        fietsDAO.wijzigenToestandFiets(regnr, Status.herstel);
    }

    /**
     * veranderd de status van de fiets naar uit omloop.
     *
     * @param regnr Het registratienummer van de fiets die moet veranderd worden.
     * @throws ApplicationException Wordt gegooid wanneer Fiets NULL is.
     * @throws DBException          Duidt op fouten vanuit de be.vives.DAO.
     */
    public void wijzigenStatusNaarUitOmloop(int regnr) throws ApplicationException, DBException {
        if (zoekFiets(regnr) == null) {
            throw new ApplicationException(ApplicationExceptionType.FIETS_NULL.getMessage());
        }
        fietsDAO.wijzigenToestandFiets(regnr, Status.uit_omloop);
    }

    /**
     * veranderd de status van de fiets naar actief.
     *
     * @param regnr Het registratienummer van de fiets die moet veranderd worden.
     * @throws ApplicationException Wordt gegooid wanneer Fiets NULL is.
     * @throws DBException          Duidt op fouten vanuit de be.vives.DAO.
     */
    public void wijzigenStatusNaarActief(int regnr) throws ApplicationException, DBException {
        if (zoekFiets(regnr) == null) {
            throw new ApplicationException(ApplicationExceptionType.FIETS_NULL.getMessage());
        }
        fietsDAO.wijzigenToestandFiets(regnr, Status.actief);
    }

    /**
     * veranderd de opmerking van de opgegeven fiets.
     *
     * @param regnr     Het registratienummer van de fiets die moet veranderd wordt.
     * @param opmerking De opmerking die de fiets moet krijgen.
     * @throws ApplicationException Wordt gegooid wanneer Fiets NULL is.
     * @throws ApplicationException Wordt gegooid wanneer de opmerking NULL is.
     * @throws DBException          Duidt op fouten vanuit de be.vives.DAO.
     */
    public void wijzigenOpmerkingFiets(int regnr, String opmerking) throws ApplicationException, DBException {
        if (zoekFiets(regnr) == null) {
            throw new ApplicationException(ApplicationExceptionType.FIETS_NULL.getMessage());
        }
        if (opmerking == null) {
            throw new ApplicationException(ApplicationExceptionType.FIETS_OPMERKING_LEEG.getMessage());

        }
        fietsDAO.wijzigenOpmerkingFiets(regnr, opmerking);
    }

    /**
     * Zoekt een fiets aan de hand van het registratienummer.
     *
     * @param registratienummer Het registratienummer van de fiets die wordt opgevraagd.
     * @return geeft de fiets terug die overeen komt met het registratienummer.
     * @throws ApplicationException Wordt gegooid wanneer registratienummer leeg is.
     * @throws DBException          Duidt op fouten vanuit de be.vives.DAO.
     */
    public Fiets zoekFiets(int registratienummer) throws ApplicationException, DBException {
        if (registratienummer == 0) {
            throw new ApplicationException(ApplicationExceptionType.FIETS_REGISTRATIE_LEEG.getMessage());
        }
        return fietsDAO.zoekFiets(registratienummer);
    }

    /**
     * Zoekt alle beschikbare fietsen.
     *
     * @return Lijst van alle beschikbare fietsen.
     * @throws DBException Duidt op fouten vanuit de be.vives.DAO.
     */
    public List<Fiets> zoekAlleBeschikbareFietsen() throws DBException {
        return fietsDAO.zoekAlleBeschikbareFietsen();
    }

    /**
     * Zoekt alle fietsen.
     *
     * @return Lijst van alle fiets
     * @throws DBException Duidt op fouten vanuit de be.vives.DAO.
     */
    public List<Fiets> zoekAlleFietsen() throws DBException {
        return fietsDAO.zoekAlleFietsen();
    }

}
