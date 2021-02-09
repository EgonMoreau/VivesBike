
package be.vives.ti.databag;


import be.vives.ti.datatype.Rijksregisternummer;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Rit {

    private Integer id;
    private LocalDateTime starttijd;
    private LocalDateTime eindtijd;
    private BigDecimal prijs;
    private Rijksregisternummer lidRijksregisternummer;
    private int fietsRegistratienummer;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getStarttijd() {
        return starttijd;
    }

    public void setStarttijd(LocalDateTime starttijd) {
        this.starttijd = starttijd;
    }

    public LocalDateTime getEindtijd() {
        return eindtijd;
    }

    public void setEindtijd(LocalDateTime eindtijd) {
        this.eindtijd = eindtijd;
    }

    public BigDecimal getPrijs() {
        return prijs;
    }

    public void setPrijs(BigDecimal prijs) {
        this.prijs = prijs;
    }

    public String getLidRijksregisternummer() {
        if (lidRijksregisternummer == null) {
            return null;
        } else {
            return lidRijksregisternummer.getRijksregisternummer();
        }
    }

    public void setLidRijksregisternummer(Rijksregisternummer lidRijksregisternummer) {
        this.lidRijksregisternummer = lidRijksregisternummer;
    }

    public int getFietsRegistratienummer() {
        return fietsRegistratienummer;
    }

    public void setFietsRegistratienummer(int fietsRegistratienummer) {
        this.fietsRegistratienummer = fietsRegistratienummer;
    }

    public String toString(){
        return "ID: "+id+", starttijd: "+starttijd+", rijksregisternummer: "+lidRijksregisternummer+", fietregistratienummer: "+fietsRegistratienummer;
    }

}
