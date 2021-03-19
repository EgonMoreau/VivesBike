# VIVESBike
> Project waarbij per 2 een Java applicatie wordt geschreven volgens het gelaagde model. Hierbij wordt een UI in JavaFX geschreven en wordt de data opgehaald uit en weggeschreven  in een MySql database. Zowel de DAO laag als de Service laag worden getest via JUnit. In de Servicelaag wordt de DAO-laag uitgemockt. Mogelijke fouten worden correct opgevangen door Exceptions

## Technologiestack
- Java 8
- Maven 3
- Intellij 2019
- git
- GitHub en GitHub Classroom
- MySql database
- JUnit
- Mockito
- assertJ
- SQL

## Opgave
Vives wil naar analogie van de blue-bike :bike:, ook fietsen verhuren (standplaats station) bij de verschillende vestigingen van VIVES.

Om een fiets te kunnen huren moet een persoon lid zijn en kan dan om het even wanneer één fiets
huren (één persoon kan nooit meerdere fietsen tegelijk huren). Een persoon blijft lid totdat hij zich
uitschrijft. We noteren dan z’n uitschrijfdatum maar verwijderen de persoon niet. Naast de
standaardgegevens, houden we van een lid opmerkingen bij. Hier kan dan bijvoorbeeld bijgehouden
worden of er dikwijls problemen zijn met deze persoon (betalingen, beschadigingen, diefstal, …). 

De gegevens van een lid kunnen gewijzigd worden zolang deze ingeschreven is. Het gaat dan om
naam, voornaam, e-mail en opmerking. De startdatum kan ook gewijzigd worden, maar moet altijd
voor de start van de eerste rit blijven, anders zou de persoon een fiets gehuurd hebben zonder lid te
zijn. De einddatum wordt ingevuld wanneer het lid zich uitschrijft, en kan niet meer gewijzigd
worden. Een lid kan zich maar uitschrijven wanneer hij/zij geen fiets aan het huren is.

De fietsen die gehuurd kunnen worden staan steeds aan het station. Wie een fiets afhaalt aan het
station betaalt 1€ en mag die 24 uur gebruiken. Wie de fiets langer wil gebruiken, mag dat, maar die
betaalt voor elke 24 uur opnieuw 1€. Via de smartphone kan het slot van de fiets open gemaakt
worden en meteen vertrekken. Op dat moment start zijn huurtijd. De fiets moet telkens
teruggebracht worden naar hetzelfde station. Op het moment dat de huurder de fiets terug
binnenbrengt, eindigt de huurtijd. De prijs wordt automatisch berekend en bijgehouden. Uiteraard
kan een fiets niet aan twee leden tegelijk verhuurd worden.

Een fiets kan nooit van standplaats wijzigen. Er kan ten allen tijde een opmerking toegevoegd
worden aan een fiets. Een fiets kan zich in 3 verschillende toestanden bevinden: actief, in herstel of
uit omloop. 

* ACTIEF: Een fiets kan maar gehuurd worden wanneer die zich in de toestand actief bevindt.
          Wanneer een fiets wordt toegevoegd is hij per definitie actief.
* IN HERSTEL: Een fiets kan na een rit beschadigd zijn. De huurder kan dit na de rit melden.
              Wanneer zo’n melding wordt gemaakt, wordt de fiets in toestand ‘herstel’ gebracht. Er kan aanvullend een opmerking toegevoegd worden aan het dossier van de huurder of aan de 
              gehuurde fiets. Wanneer de fiets hersteld is, wordt die weer actief gemaakt.
* UIT OMLOOP: Er kan beslist worden dat een fiets niet meer gebruikt zal worden en dus uit
              omloop wordt gehaald. Dergelijke fiets wordt nooit meer in omloop gebracht.
              
Gevraagd wordt om een applicatie te schrijven met volgende functionaliteiten:
* Toevoegen lid op basis van rijksregisternummer, naam, voornaam en e-mailadres. Er kan een opmerking meegegeven worden. Een lid is meteen ingeschreven.
* Wijzigen lid (naam, voornaam, e-mail en opmerking)
* Wijzigen van de startdatum van het lid.
* Uitschrijven lid
* Toevoegen fiets op basis van een standplaats.
* Wijzigen fiets (status en opmerking)
* Het starten van een rit op basis van het rijksregisternummer van een lid en registratienummer van de fiets.
* Afsluiten rit

### GUI
Werk een grafische userinterface :computer: uit in JavaFX waarbij het mogelijk is om in het hoofdscherm een lid
te selecteren uit een lijst van alle mogelijke leden, waarna je de details te zien krijgt van dit lid.

De gebruiker krijgt de mogelijkheid om een lid toe te voegen (moet niet uitgewerkt worden), te
wijzigen (naam, voornaam, email, opmerking), de startdatum van een lid aan te passen of een lid uit
te schrijven. 

Wanneer er gekozen wordt om de startdatum van een lid te wijzigen worden de betreffende velden
en de knoppen OK en Annuleren enabled. Het wijzigen verloopt analoog.

Bij het uitschrijven van een lid wordt geen nieuw scherm geopend. De systeemdatum wordt ingevuld
en de checkbox uitgeschreven wordt aangevinkt.
