package be.vives.ti.dao;

import be.vives.ti.dao.connect.ConnectionManager;
import be.vives.ti.databag.Rit;
import be.vives.ti.datatype.Rijksregisternummer;
import be.vives.ti.exception.ApplicationException;
import be.vives.ti.exception.DBException;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Bevat alle functionaliteit op de DAO-tabel Rit.
 * - toevoegen van een rit
 * - afsluiten van een rit
 * - zoeken van een rit in de database
 * - zoeken van een lijst van alle actieve ritten van een lid
 * - zoeken van een lijst van alle actieve ritten van een fiets
 * - een rit uit de database halen.
 * - een lijst van ritten uit de database halen.
 */

public class RitDAO {

    /**
     * Voegt een rit toe. Het id is het id van de rit.
     * @param rit de rit dat toegevoegd moet worden.
     * @return ritID wanneer het toevoegen van de rit gelukt is.
     * @throws DBException Exception die duidt op een verkeerde installatie van de DAO of een fout in de query.
     */
    public Integer toevoegenRit(Rit rit) throws DBException {
        //null waarde test
        if(rit != null){
            try (Connection conn = ConnectionManager.getConnection()) {
                try (PreparedStatement stmt = conn.prepareStatement(
                        "insert into rit(lid_rijksregisternummer ,"
                        + " fiets_registratienummer"
                        + " ) values(?,?)",
                        Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setString(1, rit.getLidRijksregisternummer());
                    stmt.setInt(2,rit.getFietsRegistratienummer());
                    stmt.execute();

                    ResultSet generatedKeys = stmt.getGeneratedKeys();
                    if (generatedKeys.first()) {
                        return generatedKeys.getInt(1);
                    }
                } catch (SQLException sqlEx) {
                    throw new DBException("SQL-exception in toevoegenRit "
                            + "- statement: " + sqlEx);

                }
            }catch (SQLException sqlEx) {
                throw new DBException("SQL-exception in bestaatRit - connection "+sqlEx);

            }
        }
        return null;
    }

    /**
     * beindigd de rit. returned null indien rit null is
     * @param rit rit die moet afgesloten worden
     * @throws DBException Exception die duidt op een verkeerde installatie van de DAO of een fout in de query.
     */
    public void afsluitenRit(Rit rit) throws DBException {
        if(rit!=null){
            //verbinding testen
            try(Connection conn = ConnectionManager.getConnection()){
                    try(PreparedStatement stmt = conn.
                            prepareStatement(
                                    "update rit"
                                    +" set eindtijd = ?,"
                                    +" prijs = ?"
                                    +" where id = ?"
                            )) {
                        stmt.setTimestamp(1, Timestamp.valueOf(rit.getEindtijd()));
                        stmt.setDouble(2, rit.getPrijs().doubleValue());
                        stmt.setInt(3, rit.getId());
                        stmt.execute();

                    }catch(SQLException sql){
                        throw new DBException("Afsluiten rit SQLException "+sql);
                    }
            }catch(SQLException sql){
                throw new DBException("afsluiten rit SQLException "+sql);
            }
        }

    }

    /**
     * Zoekt adhv van het ritID een rit op. Wanneer geen ritID werd meegegeven null teruggeven
     * @param ritID ritID van de rit die gezocht moet worden.
     * @return Rit die gezocht werd, null indien de rit niet werd gevonden.
     * @throws DBException Exception die duidt op een verkeerde installatie van de DAO of een fout in de query.
     */
    public Rit zoekRit(int ritID) throws DBException {
        if(ritID != 0){
            Rit returnRit = null;
            try(Connection conn = ConnectionManager.getConnection()) {
                try (PreparedStatement stmt = conn.prepareStatement(
                        "select id"
                                + " ,starttijd"
                                + " ,eindtijd"
                                + " ,prijs"
                                + " ,lid_rijksregisternummer"
                                + " ,fiets_registratienummer"
                                + " from rit"
                                + " where id=?"
                )) {
                    stmt.setInt(1, ritID);
                    stmt.execute();
                    try (ResultSet r = stmt.getResultSet()) {
                        if (r.first()) {
                            return getRitUitDatabase(r);
                        }
                    } catch (SQLException sql) {
                        throw new DBException("zoekRit SQLException " + sql);
                    }
                } catch (SQLException sql) {
                    throw new DBException("zoekRit SQLException " + sql);
                }
            }catch (SQLException sql){
                throw new DBException("zoekRit SQLException "+sql);
            }
        }
        return null;
    }

    /**
     * Zoekt adhv van het rijksregister de eerste rit van een lid. indien geen rijksregisternummer meegegeven null teruggeven
     * @param rr rijksregisternummer van het lid die gezocht moet worden.
     * @return Rit die gezocht werd, null indien de rit niet werd gevonden.
     * @throws DBException Exception die duidt op een verkeerde installatie van de DAO of een fout in de query.
     */

    public Rit zoekEersteRitVanLid(String rr) throws DBException {
        if(rr!=null){
            try(Connection conn = ConnectionManager.getConnection()) {
                try (PreparedStatement stmt = conn.prepareStatement(
                        "select *"
                        +" from rit"
                        +" where lid_rijksregisternummer=?"
                )){
                    stmt.setString(1, rr);
                    stmt.execute();
                    try (ResultSet r = stmt.getResultSet()) {
                        if (r.first()) {
                            return getRitUitDatabase(r);
                        }
                    } catch (SQLException sql) {
                        throw new DBException("zoekRit SQLException " + sql);
                    }
                } catch (SQLException sql) {
                    throw new DBException("zoekRit SQLException " + sql);
                }
            }catch (SQLException sql){
                throw new DBException("zoekRit SQLException "+sql);
            }
        }
        return null;
    }

    /**
     * Zoekt adhv van het rijksregister de actieve rit van een lid. Wanneer geen ritID gevonden wordt null teruggeven
     * @param rr rijksregisternummer van het lid die gezocht moet worden.
     * @return rittenlijst die gezocht wordt, bevat actieve rit, null indien geen actieve rit.
     * @throws DBException Exception die duidt op een verkeerde installatie van de DAO of een fout in de query.
     */
    public List<Rit> zoekActieveRittenVanLid(String rr) throws DBException {
        if(rr!=null || !rr.trim().equals("")) {
            try (Connection conn = ConnectionManager.getConnection()) {
                // preparedStatement opstellen (en automatisch sluiten)
                try (PreparedStatement stmt = conn.prepareStatement(
                        "select id"
                                + " , starttijd"
                                + " , eindtijd"
                                + " , prijs"
                                + " , lid_rijksregisternummer"
                                + " , fiets_registratienummer"
                                + " from rit "
                                + " where eindtijd is NULL AND lid_rijksregisternummer = ?"
                )) {
                    stmt.setString(1, rr);
                    stmt.execute();
                    // result opvragen (en automatisch sluiten)
                    try (ResultSet r = stmt.getResultSet()) {
                        // van alle leden uit de DAO Lid-objecten maken
                        // en in een lijst steken
                        return getRittenUitDatabase(r);
                    } catch (SQLException sqlEx) {
                        throw new DBException(
                                "SQL-exception in zoekActieveRittenVanLid - resultset" + sqlEx);
                    }
                } catch (SQLException sqlEx) {
                    throw new DBException("SQL-exception in zoekActieveRittenVanLid - statement" + sqlEx);
                }
            } catch (SQLException sqlEx) {
                throw new DBException(
                        "SQL-exception in zoekActieveRittenVanLid - connection" + sqlEx);
            }
        } else{
            return null;
        }
    }

    /**
     * Zoekt adhv van het registratienummer de actieve rit van een fiets.
     * Wanneer geen registratienummer meegegeven null teruggeven
     * @param regnr registratienummer van de fiets die gezocht moet worden.
     * @return rittenlijst die gezocht wordt, bevat actieve rit, null indien geen actieve rit.
     * @throws DBException Exception die duidt op een verkeerde installatie van de DAO of een fout in de query.
     */
    public List<Rit> zoekActieveRittenVanFiets(int regnr) throws DBException {
            try (Connection conn = ConnectionManager.getConnection()) {
                // preparedStatement opstellen (en automatisch sluiten)
                try (PreparedStatement stmt = conn.prepareStatement(
                        "select id"
                                + " , starttijd"
                                + " , eindtijd"
                                + " , prijs"
                                + " , lid_rijksregisternummer"
                                + " , fiets_registratienummer"
                                + " from rit "
                                + " where eindtijd is null AND fiets_registratienummer = ?"
                )) {
                    stmt.setInt(1, regnr);
                    stmt.execute();
                    // result opvragen (en automatisch sluiten)
                    try (ResultSet r = stmt.getResultSet()) {
                        return getRittenUitDatabase(r);
                    } catch (SQLException sqlEx) {
                        throw new DBException(
                                "SQL-exception in zoekActieveRittenVanLid - resultset" + sqlEx);
                    }
                } catch (SQLException sqlEx) {
                    throw new DBException("SQL-exception in zoekActieveRittenVanLid - statement" + sqlEx);
                }
            } catch (SQLException sqlEx) {
                throw new DBException(
                        "SQL-exception in zoekActieveRittenVanLid - connection" + sqlEx);
            }

    }

    /**
     * ophalen van alle ritten
     * @return lijst van alle ritten
     * @throws DBException Exception die duidt op een verkeerde installatie van de DAO of een fout in de query.
     */
    public List<Rit> zoekAlleRitten() throws DBException {
        try (Connection conn = ConnectionManager.getConnection()) {
            // preparedStatement opstellen (en automatisch sluiten)
            try (PreparedStatement stmt = conn.prepareStatement(
                    "select id"
                            + " , lid_rijksregisternummer"
                            + " , fiets_registratienummer"
                            + " , starttijd"
                            + " , eindtijd"
                            + " , prijs"
                            + " from rit "
                            + " order by id")) {
                stmt.execute();
                // result opvragen (en automatisch sluiten)
                try (ResultSet r = stmt.getResultSet()) {
                    // van alle ritten uit de DAO rit-objecten maken
                    // en in een lijst steken
                    return getRittenUitDatabase(r);
                } catch (SQLException sqlEx) {
                    throw new DBException(
                            "SQL-exception in zoekAlleRitten - resultset" + sqlEx);
                }
            } catch (SQLException sqlEx) {
                throw new DBException("SQL-exception in zoekAlleRitten - statement" + sqlEx);
            }
        } catch (SQLException sqlEx) {
            throw new DBException(
                    "SQL-exception in zoekAlleRitten - connection" + sqlEx);
        }
    }

    /**
     * Maakt een Arraylist adhv van de Resultset uit zoekActieveRittenVanFiets of zoekActieveRittenVanLid
     * @param r Resultset van de ritten
     * @return rittenlijst van de ritten uit getRitUitDatabase
     */
    private ArrayList<Rit> getRittenUitDatabase(ResultSet r) throws SQLException {
        ArrayList<Rit> ritten = new ArrayList<>();
        while (r.next()) {
            Rit rit = getRitUitDatabase(r);
            ritten.add(rit);
        }
        return ritten;
    }

    /**
     * Maakt een Rit object adhv de waardes gegeven uit getRittenUitDatabase
     * @param r Resultset van getRittenUitDatabase
     * @return rit object gemaakt uit ResultSet r
     * @throws ApplicationException Exception die duid op fout bij maken van Rijsregisternummer object
     */
    private Rit getRitUitDatabase(ResultSet r) throws SQLException {
        Rit rit = new Rit();
        try {
            Rijksregisternummer rijksregisternummer = new Rijksregisternummer(r.getString("lid_rijksregisternummer"));
            rit.setLidRijksregisternummer(rijksregisternummer);
        } catch (ApplicationException ex) {
            System.out.println("getRitUitDatabase rr "+ex);
        }
        rit.setId(r.getInt("id"));
        rit.setStarttijd(r.getTimestamp("starttijd").toLocalDateTime());
        if(r.getTimestamp("eindtijd")!=null) {
            rit.setEindtijd(r.getTimestamp("eindtijd").toLocalDateTime());
            rit.setPrijs(r.getBigDecimal("prijs"));
        }
        rit.setFietsRegistratienummer(r.getInt("fiets_registratienummer"));
        return rit;
    }
}
