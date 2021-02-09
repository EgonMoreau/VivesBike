package be.vives.ti.service;

import be.vives.ti.dao.LidDAO;
import be.vives.ti.databag.Lid;
import be.vives.ti.databag.Rit;
import be.vives.ti.exception.ApplicationException;
import be.vives.ti.exception.ApplicationExceptionType;
import be.vives.ti.exception.DBException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.time.LocalDate;
import java.time.chrono.ChronoLocalDateTime;
import java.util.List;


/**
 * Bevat alle functionaliteit van een lid, met de nodige checks.
 * - toevoegen van een lid
 * - checken als alle velden ingevuld zijn
 * - wijzigen van een lid
 * - wijzigen startdatum van lid.
 * - Uitschrijven van een lid.
 * - zoeken achter een lid
 * - zoeken achter all leden.
 */

public class LidService {

    private LidDAO lidDAO;
    private RitService ritService;

    public LidService(LidDAO lidDAO, RitService ritService) {
        this.lidDAO = lidDAO;
        this.ritService = ritService;
    }

    /**
     * Het toevoegen van een lid.
     *
     * @param l Het lid object dat moet worden toegevoegd.
     * @throws ApplicationException wanneer de lid parameter null is.
     * @throws ApplicationException Wanneer een lid al ingeschreven is.
     * @throws ApplicationException Wanneer een lid al bestaat.
     * @throws DBException duidt op fouten vanuit de be.vives.DAO.
     */
    public boolean toevoegenLid(Lid l) throws ApplicationException, DBException {
        // parameter ingevuld?
        if (l == null) {
            throw new ApplicationException(ApplicationExceptionType.LID_NULL.getMessage());
        }
        // alle gegevens ingevuld?
        checkAlleVeldenIngevuld(l);

        // start_lidmaatschap mag niet ingevuld zijn
        if (l.getStart_lidmaatschap() != null) {
            throw new ApplicationException(ApplicationExceptionType.LID_MOET_INGESCHREVEN_ZIJN.getMessage());
        }

        // bestaat lid?
        if (zoekLid(l.getRijksregisternummer()) != null) {
            throw new ApplicationException(ApplicationExceptionType.LID_BESTAAT_AL.getMessage());
        }

        l.setStart_lidmaatschap(LocalDate.now());

        //toevoegen
        return lidDAO.toevoegenLid(l);
    }

    private void checkAlleVeldenIngevuld(Lid lid) throws ApplicationException {

        if (StringUtils.isBlank(lid.getNaam())) {
            throw new ApplicationException(ApplicationExceptionType.LID_NAAM_LEEG.getMessage());
        }
        if (StringUtils.isBlank(lid.getVoornaam())) {
            throw new ApplicationException(ApplicationExceptionType.LID_VOORNAAM_LEEG.getMessage());
        }
        if (StringUtils.isBlank(lid.getRijksregisternummer())) {
            throw new ApplicationException(ApplicationExceptionType.LID_RIJSKREGISTERNUMMER_LEEG.getMessage());
        }
        if (StringUtils.isBlank(lid.getEmailadres())) {
            throw new ApplicationException(ApplicationExceptionType.LID_EMAIL_LEEG.getMessage());
        }
        if(!isValid(lid.getEmailadres())){
            throw new ApplicationException(ApplicationExceptionType.LID_EMAIL_ONGELDIG.getMessage());
        }
    }

    static boolean isValid(String email) {
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(regex);
    }

    /**
     * Wijzigen van een lid.
     *
     * @param teWijzigenLid Het rijksregister van het lid waar de aanpassingen op moeten gebeuren.
     * @throws ApplicationException Wanneer lid NULL is.
     * @throws ApplicationException Wanneer lid uitgeschreven is.
     * @throws ApplicationException Wanneer het lid niet gevonden wordt / niet bestaat.
     * @throws DBException          Duidt op fouten vanuit de be.vives.DAO.
     */
    public void wijzigenLid(Lid teWijzigenLid) throws ApplicationException, DBException {
        // parameter ingevuld?
        if (teWijzigenLid == null) {
            throw new ApplicationException(ApplicationExceptionType.LID_NULL.getMessage());
        }

        if(teWijzigenLid.getEinde_lidmaatschap() != null){
            throw new ApplicationException(ApplicationExceptionType.LID_IS_AL_UITGESCHREVEN.getMessage());
        }
        // alle gegevens ingevuld?
        checkAlleVeldenIngevuld(teWijzigenLid);
        

        if (zoekLid(teWijzigenLid.getRijksregisternummer()) == null) {
            throw new ApplicationException(ApplicationExceptionType.LID_BESTAAT_NIET.getMessage());
        }

        lidDAO.wijzigenLid(teWijzigenLid);
    }

    /**
     * Het wijzigen van de startdatum van een lid.
     *
     * @param rr Het rijksregister van het lid waar de aanpassingen op moeten gebeuren.
     * @param startDatum De startdatum waarnaar veranderd moet worden.
     * @throws ApplicationException Wanneer de parameter rr leeg is.
     * @throws ApplicationException Wanneer het lid niet bestaad.
     * @throws ApplicationException Wanneer het lid is uitgeschreven.
     * @throws ApplicationException Wanneer de parameter startDatum leeg is.
     * @throws DBException          Duidt op fouten vanuit de be.vives.DAO.
     */
    public void wijzigStartDatumVanLid(String rr, LocalDate startDatum) throws ApplicationException, DBException {
        // kijken of rijksregisternummer is opgegeven
        if(rr.equals("") || rr == null){
            throw new ApplicationException(ApplicationExceptionType.LID_RIJSKREGISTERNUMMER_LEEG.getMessage());
        }
        // kijken of het lid bestaat
        Lid lid = zoekLid(rr);
        if(lid == null) {
            throw new ApplicationException(ApplicationExceptionType.LID_BESTAAT_NIET.getMessage());
        }

        if(lid.getEinde_lidmaatschap() != null){
            throw new ApplicationException(ApplicationExceptionType.LID_IS_AL_UITGESCHREVEN.getMessage());
        }

        // kijken of een startdatum werd meegegeven
        if(startDatum == null){
            throw new ApplicationException(ApplicationExceptionType.GEEN_STARTDATUM_OPGEGEVEN.getMessage());
        }

        // startdatum moet lager zijn of startdatum van eerste rit.
        Rit eersteRit = ritService.zoekEersteRitVanLid(rr);
        if(eersteRit != null){
            if(eersteRit.getStarttijd().isAfter(ChronoLocalDateTime.from(startDatum))){
                lid.setStart_lidmaatschap(startDatum);
                wijzigenLid(lid);
            }else{
                throw new ApplicationException(ApplicationExceptionType.LID_STARTDATUM_TE_LAAT.getMessage());
            }

        }else{
            // lid heeft nog nooit een fiets gehuurd
            lid.setStart_lidmaatschap(startDatum);
            wijzigenLid(lid);
        }
    }

    /**
     * Schrijft leden uit.
     * @param rr rijksregister nummer van het lid dat moet worden uitgeschreven.
     * @throws ApplicationException Wanneer het opgegeven lid al uitgeschreven is.
     * @throws ApplicationException Wanneer een lid niet bestaat.
     * @throws ApplicationException Wanneer een lid nog actieve ritten heeft.
     * @throws DBException Duidt op fouten vanuit de be.vives.DAO.
     */

    //TODO docs, zoekLid wordt nu uit de service opgevraagd en dus ook deze exceptions moeten worden aangepast
    public void uitschrijvenLid(String rr) throws ApplicationException, DBException {
        Lid lid = this.zoekLid(rr);
        if(lid == null){
            throw new ApplicationException(ApplicationExceptionType.LID_BESTAAT_NIET.getMessage());
        }
        if(lid.getEinde_lidmaatschap() != null){
            throw new ApplicationException(ApplicationExceptionType.LID_WERD_AL_UITGESCHREVEN.getMessage());
        }
        if(ritService.zoekActieveRittenVanLid(rr).size() > 0){
            throw new ApplicationException(ApplicationExceptionType.LID_HEEFT_ACTIEVE_RITTEN.getMessage());
        }
        lidDAO.uitschrijvenLid(rr);
    }

    /**
     * Zoekt een lid aan de hand van zijn rijksregisternummer.
     * @return Het lid dat het rijksregisternummer heeft van de paranmeter.
     * @param rijksregisternummer van het lid dat moet worden opgezocht.
     * @throws ApplicationException Wanneer de parameter rijksregisternummer leeg is.
     * @throws ApplicationException Wanneer een lid niet bestaat.
     * @throws DBException Duidt op fouten vanuit de be.vives.DAO.
     */
    public Lid zoekLid(String rijksregisternummer) throws ApplicationException, DBException {
        // parameter ingevuld?
        if (StringUtils.isBlank(rijksregisternummer)) {
            throw new ApplicationException(ApplicationExceptionType.LID_RIJSKREGISTERNUMMER_LEEG.getMessage());
        }
        return lidDAO.zoekLid(rijksregisternummer);
    }

    public List<Lid> zoekAlleLeden() throws DBException {
        return lidDAO.zoekAlleLeden();
    }
}