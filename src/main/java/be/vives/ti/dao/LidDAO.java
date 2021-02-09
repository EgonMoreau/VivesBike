package be.vives.ti.dao;


import be.vives.ti.dao.connect.ConnectionManager;
import be.vives.ti.databag.Lid;
import be.vives.ti.datatype.Rijksregisternummer;
import be.vives.ti.exception.ApplicationException;
import be.vives.ti.exception.DBException;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Bevat alle functionaliteit op de DAO-tabel Lid.
 * - toevoegen van een lid
 * - wijzigen van een lid
 * - uitschrijven van een lid
 * - zoeken van een lid uit de database
 * - zoeken van een lijst van alle leden
 * - een lid uit de database halen.
 * - een lijst van leden uit de database halen.
 */

public class LidDAO {

    /**
     * Voegt een lid toe. Het id is het rijksregisternummer van het lid.
     * @param lid het lid dat toegevoegd moet worden.
     * @return True wanneer het toevoegen van het lid gelukt is.
     * @throws DBException Exception die duidt op een verkeerde installatie van de DAO of een fout in de query.
     */
    public boolean toevoegenLid(Lid lid) throws DBException {
        if(lid != null){
            //Connectie tot stand brengen (en automatisch sluiten)
            try (Connection conn = ConnectionManager.getConnection()) {
                //PreparedStatement opstellen (en automatisch sluiten)
                try (PreparedStatement stmt = conn.prepareStatement(
                        "insert into Lid(rijksregisternummer"
                                + " , voornaam"
                                + " , naam"
                                + " , emailadres"
                                + " , start_lidmaatschap"
                                + " , einde_lidmaatschap"
                                + " , opmerking"
                                + " ) values(?,?,?,?,?,?,?)",
                        Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setString(1, lid.getRijksregisternummer());
                    stmt.setString(2, lid.getVoornaam());
                    stmt.setString(3, lid.getNaam());
                    stmt.setString(4, lid.getEmailadres());
                    stmt.setDate(5,   Date.valueOf(lid.getStart_lidmaatschap()));
                    if(lid.getEinde_lidmaatschap() == null){
                        stmt.setNull(6, Types.DATE);
                    }else{
                        stmt.setDate(6,   Date.valueOf(lid.getEinde_lidmaatschap()));
                    }
                    stmt.setString(7, lid.getOpmerking());
                    stmt.execute();
                    ResultSet generatedKeys = stmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        System.out.println(generatedKeys.getString(1));
                    }
                } catch (SQLException sqlEx) {
                    throw new DBException("SQL-exception in toevoegenLid"
                            + "- statement" + sqlEx);
                }
            }catch (SQLException sqlEx) {
                throw new DBException("SQL-exception in bestaatLid - connection" + sqlEx);
            }
            return true;
        }else{
            return false;
        }
    }

    /**
     * Maakt het wijzigen van het lid mogelijk.
     * @param lid het lid dat gewijzigd moet worden.
     * @throws DBException Exception die duidt op een verkeerde installatie van de DAO of een fout in de query.
     */
    public void wijzigenLid(Lid lid) throws DBException {
        if (lid != null) {

            // connectie tot stand brengen (en automatisch sluiten)
            try (Connection conn = ConnectionManager.getConnection()) {
                // preparedStatement opstellen (en automatisch sluiten)
                try (PreparedStatement stmt = conn.
                        prepareStatement("update Lid "
                                + " set voornaam =?"
                                + "   , naam = ?"
                                + "   , emailadres = ?"
                                + "   , start_lidmaatschap = ? "
                                + "   , einde_lidmaatschap = ? "
                                + "   , opmerking = ? "
                                + " where rijksregisternummer = ?")) {

                    stmt.setString(1, lid.getVoornaam());
                    stmt.setString(2, lid.getNaam());
                    stmt.setString(3, lid.getEmailadres());
                    stmt.setObject(4, lid.getStart_lidmaatschap(), Types.DATE);
                    stmt.setObject(5, lid.getEinde_lidmaatschap(), Types.DATE);
                    stmt.setString(6, lid.getOpmerking());
                    stmt.setString(7, lid.getRijksregisternummer());
                    stmt.execute();
                } catch (SQLException sqlEx) {
                    throw new DBException("SQL-exception in wijzigenLid - statement" + sqlEx);
                }
            } catch (SQLException sqlEx) {
                throw new DBException(
                        "SQL-exception in wijzigenLids - connection" + sqlEx);
            }
        }
    }

    /**
     * Uitschrijven van leden aan de hand van het rijksrewgisternummer.
     * @param rr rijkregisternummer van het lid dat moet worden uitgeschreven.
     * @throws DBException Exception die duidt op een verkeerde installatie van de DAO of een fout in de query.
     */
    public void uitschrijvenLid(String rr) throws DBException {
        if(rr != null){
            Lid lidDieHuurt;
            try (Connection conn = ConnectionManager.getConnection()) {
                // preparedStatement opstellen (en automatisch sluiten)
                try (PreparedStatement stmt = conn.prepareStatement(
                        "update Lid"
                                + " set einde_lidmaatschap = ?"
                                + " where rijksregisternummer = ?")) {

                    stmt.setDate(1, Date.valueOf(LocalDate.now()));
                    stmt.setString(2, rr);
                    // execute voert het SQL-statement uit
                    stmt.execute();

                } catch (SQLException sqlEx) {
                    throw new DBException("SQL-exception in zoekLid "
                            + "- statement" + sqlEx);
                }
            } catch (SQLException sqlEx) {
                throw new DBException("SQL-exception in zoekLid "
                        + "- connection" + sqlEx);
            }
        }
    }

    /**
     * Zoekt een lid aan de hand van het rijksregisternummer.
     * @param rijksregisternummer het lid dat moet worden gevonden.
     * @return het lid dat wordt opgevraagd.
     * @throws DBException Exception die duidt op een verkeerde installatie van de DAO of een fout in de query.
     */
    public Lid zoekLid(String rijksregisternummer) throws DBException, ApplicationException {
        if (rijksregisternummer != null) {
            Lid returnLid = null;
            // connectie tot stand brengen (en automatisch sluiten)
            try (Connection conn = ConnectionManager.getConnection()) {
                // preparedStatement opstellen (en automatisch sluiten)
                try (PreparedStatement stmt = conn.prepareStatement(
                        "select rijksregisternummer"
                                + " , voornaam"
                                + " , naam"
                                + " , emailadres"
                                + " , start_lidmaatschap"
                                + " , einde_lidmaatschap"
                                + " , opmerking "
                                + " from Lid "
                                + " where rijksregisternummer = ?")) {

                    // parameters invullen in query
                    stmt.setString(1, rijksregisternummer);

                    // execute voert het SQL-statement uit
                    stmt.execute();
                    // result opvragen (en automatisch sluiten)
                    try (ResultSet r = stmt.getResultSet()) {
                        // van de klant uit de DAO een Klant-object maken
                        // er werd een klant gevonden

                        if (r.next()) {
                            if(r == null){
                                return null;
                            }
                            returnLid = getLidUitDatabase(r);
                        }
                        return returnLid;
                    } catch (SQLException sqlEx) {
                        throw new DBException("SQL-exception in zoekLid "
                                + "- resultset" + sqlEx);
                    }
                } catch (SQLException sqlEx) {
                    throw new DBException("SQL-exception in zoekLid "
                            + "- statement" + sqlEx);
                }
            } catch (SQLException sqlEx) {
                throw new DBException("SQL-exception in zoekLid "
                        + "- connection" + sqlEx);
            }
        }
        return null;
    }

    /**
     * Ophalen van alle leden.
     * @return Lijst van alle leden.
     * @throws DBException Exception die duidt op een verkeerde installatie van de DAO of een fout in de query.
     */
    public List<Lid> zoekAlleLeden() throws DBException {
        try (Connection conn = ConnectionManager.getConnection()) {
            // preparedStatement opstellen (en automatisch sluiten)
            try (PreparedStatement stmt = conn.prepareStatement(
                    "select rijksregisternummer"
                            + " , voornaam"
                            + " , naam"
                            + " , emailadres"
                            + " , start_lidmaatschap"
                            + " , einde_lidmaatschap"
                            + " , opmerking "
                            + " from Lid "
                            + " order by naam"
                            + "        , voornaam")) {
                stmt.execute();
                // result opvragen (en automatisch sluiten)
                try (ResultSet r = stmt.getResultSet()) {
                    // van alle leden uit de DAO Lid-objecten maken
                    // en in een ljst steken
                    return getLedenUitDatabase(r);
                } catch (SQLException sqlEx) {
                    throw new DBException(
                            "SQL-exception in zoekAlleLeden - resultset" + sqlEx);
                }
            } catch (SQLException sqlEx) {
                throw new DBException("SQL-exception in zoekAlleLeden - statement" + sqlEx);
            }
        } catch (SQLException sqlEx) {
            throw new DBException(
                    "SQL-exception in zoekAlleLeden - connection" + sqlEx);
        }
    }

    private ArrayList<Lid> getLedenUitDatabase(ResultSet r) throws SQLException {
        ArrayList<Lid> Leden = new ArrayList<>();
        while (r.next()) {
            Lid lid = getLidUitDatabase(r);
            Leden.add(lid);
        }
        return Leden;
    }

    private Lid getLidUitDatabase(ResultSet r) throws SQLException {
        Lid lid = new Lid();
        try{
            Rijksregisternummer rijksregisternummer = new Rijksregisternummer(r.getString("rijksregisternummer"));
            lid.setRijksregisternummer(rijksregisternummer);
        }catch(ApplicationException ex){
            System.out.println(ex);
        }
        lid.setVoornaam(r.getString("voornaam"));
        lid.setNaam(r.getString("naam"));
        lid.setEmailadres(r.getString("emailadres"));
        lid.setStart_lidmaatschap(r.getDate("start_lidmaatschap").toLocalDate());
        if(r.getDate("einde_lidmaatschap") != null)
            lid.setEinde_lidmaatschap(r.getDate("einde_lidmaatschap").toLocalDate());
        lid.setOpmerking(r.getString("opmerking"));
        return lid;
    }


}
