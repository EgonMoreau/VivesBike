
package be.vives.ti.databag;

import be.vives.ti.datatype.Rijksregisternummer;

import java.time.LocalDate;

public class Lid {

    private Rijksregisternummer rijksregisternummer;
    private String naam;
    private String voornaam;
    private String emailadres;
    private LocalDate start_lidmaatschap;
    private LocalDate einde_lidmaatschap;
    private String opmerking;

    public String getRijksregisternummer() {
        if (rijksregisternummer == null) {
            return null;
        } else {
            return rijksregisternummer.getRijksregisternummer();
        }
    }

    public void setRijksregisternummer(Rijksregisternummer rijksregisternummer) {
        this.rijksregisternummer = rijksregisternummer;
    }

    public String getNaam() {
        return naam;
    }

    public void setNaam(String naam) {
        this.naam = naam;
    }

    public String getVoornaam() {
        return voornaam;
    }

    public void setVoornaam(String voornaam) {
        this.voornaam = voornaam;
    }

    public String getEmailadres() {
        return emailadres;
    }

    public void setEmailadres(String email) {
        this.emailadres = email;
    }

    public LocalDate getStart_lidmaatschap() {
        return start_lidmaatschap;
    }

    public void setStart_lidmaatschap(LocalDate start_lidmaatschap) {
        this.start_lidmaatschap = start_lidmaatschap;
    }

    public LocalDate getEinde_lidmaatschap() {
        return einde_lidmaatschap;
    }

    public void setEinde_lidmaatschap(LocalDate einde_lidmaatschap) {
        this.einde_lidmaatschap = einde_lidmaatschap;
    }

    public String getOpmerking() {
        return opmerking;
    }

    public void setOpmerking(String opmerking) {
        this.opmerking = opmerking;
    }

    @Override
    public String toString() {
        return "Lid{" + "rijksregisternummer=" + rijksregisternummer + ", naam=" + naam + ", voornaam=" + voornaam  + ", email=" + emailadres + ", start_lidmaatschap=" + start_lidmaatschap + ", einde_lidmaatschap=" + einde_lidmaatschap + ", opmerkingen=" + opmerking + '}';
    }

}
