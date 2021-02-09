package be.vives.ti.exception;

public enum ApplicationExceptionType {

    // ongeldige eigenschappen lid
    LID_NULL("Er werd geen LID opgegeven."),
    LID_NAAM_LEEG("Er werd geen naam opgegeven."),
    LID_VOORNAAM_LEEG("Er werd geen voornaam opgegeven."),
    LID_RIJSKREGISTERNUMMER_LEEG("Er werd geen rijksregisternummer opgegeven."),
    LID_EMAIL_LEEG("Er werd geen emailadres opgegeven."),
    LID_EMAIL_ONGELDIG("Email adres is ongeldig"),
    LID_MOET_INGESCHREVEN_ZIJN("Het lid wordt direct automatisch INGESCHREVEN."),
    LID_IS_AL_UITGESCHREVEN("Lid is uitgeschreven en kan niet meer worden aangepast."),
    LID_WERD_AL_UITGESCHREVEN("Het opgegeven lid werd al uitgeschreven."),
    LID_BESTAAT_AL("Het opgegeven lid bestaat al."),
    LID_BESTAAT_NIET("Het opgegeven lid bestaat niet."),
    GEEN_STARTDATUM_OPGEGEVEN("Er werd geen startdatum opgegeven."),
    LID_STARTDATUM_TE_LAAT("De opgegeven startdatum komt na de startdatum van de eerste rit."),
    LID_HEEFT_ACTIEVE_RITTEN("Er zijn nog actieve ritten gevonden."),

    LID_RIJKSREGISTERNUMMER_ONGELDIG("Het opgegeven rijksregisternummer is ongeldig"),

    //ongeldige eigenschappen fiets
    FIETS_NULL("Er werd geen FIETS opgegeven."),
    FIETS_BESTAAT_NIET("De opgegeven fiets bestaat niet."),
    FIETS_REGISTRATIE_LEEG("Er werd geen fiets registratienummer opgegeven."),
    FIETS_STANDPLAATS_LEEG("Er werd geen standplaats opgegeven."),
    FIETS_STATUS_LEEG("Er werd geen status opgegeven."),
    FIETS_OPMERKING_LEEG("Er werd geen opmerking opgegeven."),
    FIETS_REGISTRATIE_BESTAAT("Er werd een registratienummer ingegeven dat al bestaat."),
    FIETS_STANDPLAATS_ONBEKEND("De standplaats van de fiets in niet gekend."),
    FIETS_STATUS_NIET_ACTIEF("De status van de fiets moet actief zijn."),

    //ongeldige eigenschappen rit
    RIT_NULL("Er werd geen RIT opgegeven."),
    RIT_ONBEKEND("Het opgegeven rit bestaat niet."),
    RIT_FIETS_IN_GEBRUIK("Fiets wordt al gehuurt."),
    RIT_LID_HUURT("Lid huurt al een fiets."),
    RIT_STARTTIJD_AUTOMATISCH("De starttijd wordt automatisch ingevuld"),
    RIT_ID_LEEG("Er werd geen RIT ID opgegeven."),
    RIT_GEEN_RITTEN("Lid heeft geen ritten."),
    RIT_GEEN_ACTIEVE_RIT_LID("Lid heeft geen actieve rit."),
    RIT_GEEN_ACTIEVE_RIT_FIETS("Fiets heeft geen actieve rit.");

    private final String message;

    ApplicationExceptionType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
