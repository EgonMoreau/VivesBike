
package be.vives.ti.databag;

import be.vives.ti.datatype.Standplaats;
import be.vives.ti.datatype.Status;

public class Fiets {
    
    private Integer registratienummer;
    private Standplaats standplaats;
    private Status status;
    private String opmerking;
            
    public Standplaats getStandplaats() {
        return standplaats;
    }

    public void setStandplaats(Standplaats standplaats) {
        this.standplaats = standplaats;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status Status) {
        this.status = Status;
    }

    public Integer getRegistratienummer() {
        return registratienummer;
    }

    public void setRegistratienummer(Integer registratienummer) {
        this.registratienummer = registratienummer;
    }

    public String getOpmerking() {
        return opmerking;
    }

    public void setOpmerking(String opmerking) {
        this.opmerking = opmerking;
    }

    @Override
    public String toString() {
        return "registratienummer=" + registratienummer + ", standplaats=" + standplaats + ", status=" + status + ", opmerking=" + opmerking ;
    }
    
    
    
}
