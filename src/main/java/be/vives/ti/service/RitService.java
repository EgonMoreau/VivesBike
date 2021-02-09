package be.vives.ti.service;

import be.vives.ti.dao.LidDAO;
import be.vives.ti.dao.RitDAO;
import be.vives.ti.databag.Fiets;
import be.vives.ti.databag.Rit;
import be.vives.ti.datatype.Status;
import be.vives.ti.exception.ApplicationException;
import be.vives.ti.exception.ApplicationExceptionType;
import be.vives.ti.exception.DBException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Bevat alle functionaliteit van een rit, met de nodige checks.
 * - toevoegen van een rit.
 * - afsluiten van een rit.
 * - zoeken van een rit.
 * - zoeken naar eerste rit van een lid.
 * - zoeken naar actieve rit van een lid.
 * - zoeken naar actieve rit van een fiets.
 * - berekenen van prijs van een rit.
 */

public class RitService {

    private RitDAO ritDAO;
    private FietsService fietsService;
    private LidDAO lidDAO;
    private LidService lidService;
    private static final double PRIJS_PER_DAG = 1.00;

    public RitService(RitDAO ritDAO, FietsService fietsService, LidDAO lidDAO) {
        this.ritDAO = ritDAO;
        this.fietsService = fietsService;
        this.lidDAO = lidDAO;
        lidService = new LidService(lidDAO, this);
    }
    /**
     * Het toevoegen van een rit.
     *
     * @param rit Het rit object dat moet worden toegevoegd.
     * @throws ApplicationException wanneer de rit parameter null is.
     * @throws ApplicationException Wanneer een rit al toegevoegd is.
     * @throws ApplicationException Wanneer een fiets al in gebruik is.
     * @throws ApplicationException Wanneer een fiets niet bestaat.
     * @throws DBException duidt op fouten vanuit de be.vives.DAO.
     */
    public Integer toevoegenRit(Rit rit) throws ApplicationException, DBException {
        if(rit==null) {
            throw new ApplicationException(ApplicationExceptionType.RIT_NULL.getMessage());
        }
        if(rit.getStarttijd() !=null){
            throw new ApplicationException(ApplicationExceptionType.RIT_STARTTIJD_AUTOMATISCH.getMessage());
        }
        if(!bestaatFiets(rit.getFietsRegistratienummer())){
            throw new ApplicationException(ApplicationExceptionType.FIETS_BESTAAT_NIET.getMessage());
        }
        if(isFietsVerhuurd(rit.getFietsRegistratienummer())){
            throw new ApplicationException(ApplicationExceptionType.RIT_FIETS_IN_GEBRUIK.getMessage());
        }
        if(!isFietsVerhuurbaar(rit.getFietsRegistratienummer())){
            throw new ApplicationException(ApplicationExceptionType.FIETS_STATUS_NIET_ACTIEF.getMessage());
        }
        if(!bestaatLid(rit.getLidRijksregisternummer())){
            throw new ApplicationException(ApplicationExceptionType.LID_BESTAAT_NIET.getMessage());
        }
        if(isLidAanHetHuren(rit.getLidRijksregisternummer())){
            throw new ApplicationException(ApplicationExceptionType.RIT_LID_HUURT.getMessage());
        }

        return ritDAO.toevoegenRit(rit);
    }

    /**
     * Beindigd ritten.
     * @param id ritID van de rit dat moet worden afgesloten.
     * @throws ApplicationException Wanneer het opgegeven id null is.
     * @throws ApplicationException Wanneer een rit niet bestaat.
     */
    public void afsluitenRit(Integer id) throws ApplicationException, DBException {
        if(id==null) {
            throw new ApplicationException(ApplicationExceptionType.RIT_ID_LEEG.getMessage());
        }
        Rit afTeSluitenRit = ritDAO.zoekRit(id);
        if(afTeSluitenRit == null){
            throw new ApplicationException(ApplicationExceptionType.RIT_ONBEKEND.getMessage());
        }
        afTeSluitenRit.setEindtijd(LocalDateTime.now());
        double prijs = prijsBerekenen(afTeSluitenRit);
        afTeSluitenRit.setPrijs(BigDecimal.valueOf(prijs));
        ritDAO.afsluitenRit(afTeSluitenRit);
    }

    /**
     * Zoekt een rit.
     * @param ritID Ritid nummer van de rit die moet gezocht worden.
     * @throws ApplicationException Wanneer de ritID null is.
     */
    public Rit zoekRit(Integer ritID) throws ApplicationException, DBException {
        if(ritID == null){
            throw new ApplicationException(ApplicationExceptionType.RIT_ID_LEEG.getMessage());
        }
        return ritDAO.zoekRit(ritID);
    }

    /**
     * Zoekt de eerste rit van een lid
     * @param rr rijksregisternummer van het lid dat de eerste rit van gezocht wordt.
     * @throws ApplicationException Wanneer het opgegeven rijksregisternummer null is.
     */
    public Rit zoekEersteRitVanLid(String rr) throws ApplicationException, DBException {
        if(rr == null){
            throw new ApplicationException(ApplicationExceptionType.LID_RIJSKREGISTERNUMMER_LEEG.getMessage());
        }
        if(!bestaatLid(rr)) {
            throw new ApplicationException(ApplicationExceptionType.LID_BESTAAT_NIET.getMessage());
        }
        return ritDAO.zoekEersteRitVanLid(rr);
    }

    public List<Rit> zoekAlleRitten() throws DBException{
        return ritDAO.zoekAlleRitten();
    }

    /**
     * Zoekt de actieve rit van een lid
     * @param rr rijksregisternummer van het lid dat de rit van gezocht wordt.
     * @throws ApplicationException Wanneer het opgegeven rijksregisternummer null is.
     */
    public List<Rit> zoekActieveRittenVanLid(String rr) throws DBException, ApplicationException {
        if(rr == null){
            throw new ApplicationException(ApplicationExceptionType.LID_RIJSKREGISTERNUMMER_LEEG.getMessage());
        }
        if(!bestaatLid(rr)) {
            throw new ApplicationException(ApplicationExceptionType.LID_BESTAAT_NIET.getMessage());
        }
        return ritDAO.zoekActieveRittenVanLid(rr);
    }

    /**
     * Zoekt de actieve rit van een fiets
     * @param regnr fiets registratienummer van de fiets dat de rit van gezocht wordt.
     * @throws ApplicationException Wanneer het opgegeven fiets registratienummer null is.
     */
    public List<Rit> zoekActieveRittenVanFiets(Integer regnr) throws DBException, ApplicationException {
        if(regnr == null){
            throw new ApplicationException(ApplicationExceptionType.FIETS_REGISTRATIE_LEEG.getMessage());
        }
        if(!bestaatFiets(regnr)){
            throw new ApplicationException(ApplicationExceptionType.FIETS_BESTAAT_NIET.getMessage());
        }
        return ritDAO.zoekActieveRittenVanFiets(regnr);
    }

    /**
     * De prijs berekenen van een afgesloten rit.
     * @param rit de rit waarvan de prijs berekend wordt.
     * @return de berekende prijs
     */
    public double prijsBerekenen(Rit rit){
        long hours = ChronoUnit.SECONDS.between(rit.getStarttijd(), rit.getEindtijd()); // tijd in seconden tussen starttijd en eindtijd
        return Math.ceil(hours/86400.00)* PRIJS_PER_DAG;// 86400 seconden = 24 uur, dus verlopen seconden/24uur = aantal dagen * 1 euro per dag
    }

    /**
     * Hier wordt gecontroleerd of het lid bestaat
     * @param rr het rijksregisternummer van het lid dat moet gecontroleerd worden.
     * @return false als het lid niet bestaat, true als het lid bestaat.
     * @throws DBException duidt op fouten vanuit de be.vives.DAO.
     */
    private Boolean bestaatLid(String rr) throws DBException, ApplicationException {
        if(lidService.zoekLid(rr)==null){
            return false;
        }
        return true;
    }

    /**
     *
     * @param rrn rijksregisternummer van het lid dat gecontroleerd moet worden.
     * @return false als het lid al een fiets huurt. true als het lid nog geen fiets huurt.
     * @throws DBException duidt op fouten vanuit de be.vives.DAO.
     * @throws ApplicationException Als het lid niet bestaat.
     */
    private Boolean isLidAanHetHuren(String rrn) throws DBException, ApplicationException {
        List<Rit> lijstLid = zoekActieveRittenVanLid(rrn); // 1 lid
        if(lijstLid.size() == 0){
            return false;
        }
        return true;
    }

    /**
     * Hier wordt getest of de fiets bestaat.
     * @param rn fietsregistratienummer van de fiets die moet gecontroleerd worden
     * @return false als de fiets niet bestaat. true als de fiets bestaat
     * @throws DBException duidt op fouten vanuit de be.vives.DAO.
     */
    private Boolean bestaatFiets(int rn) throws DBException, ApplicationException {
        if(fietsService.zoekFiets(rn)==null){
            return false;
        }
        return true;
    }

    /**
     * Hier wordt getest of de fiets verhuurd wordt of niet.
     * @param rn het fietsregistratienummer van de fiets die moet gecontroleerd worden.
     * @return false als de fiets al verhuurd wordt, true als de fiets niet verhuurd wordt
     * @throws DBException duidt op fouten vanuit de be.vives.DAO.
     * @throws ApplicationException Als de fiets niet bestaat.
     */
    private Boolean isFietsVerhuurd(int rn) throws DBException, ApplicationException {
        List<Rit> lijstFiets = zoekActieveRittenVanFiets(rn); // 1 fiets
        if(lijstFiets.size() == 0){
            return false;
        }
        return true;
    }

    /**
     * Hier wordt getest of de fiets kan verhuurd worden. Ofdat de fiets op actief staat.
     * @param rn Het fietsregistratienummer
     * @return False als de fiets niet actief is voor verhuur, true als de fiets actief is.
     * @throws DBException  duidt op fouten vanuit de be.vives.DAO.
     * @throws ApplicationException Als de fiets niet bestaat
     */
    private Boolean isFietsVerhuurbaar(int rn) throws DBException, ApplicationException {
        Fiets fiets = fietsService.zoekFiets(rn);
        if(!fiets.getStatus().equals(Status.actief)){
            return false;
        }
        return true;
    }



}