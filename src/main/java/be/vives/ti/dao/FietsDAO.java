package be.vives.ti.dao;

import be.vives.ti.dao.connect.ConnectionManager;
import be.vives.ti.databag.Fiets;
import be.vives.ti.datatype.Standplaats;
import be.vives.ti.datatype.Status;
import be.vives.ti.exception.DBException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FietsDAO {


    /**
     * Bevat alle functionaliteit op de DAO-tabel Fiets.
     * - toevoegen van een fiets
     * - wijzigen van de opmerking van een fiets
     * - wijzigen van de toestand waarin de fiets zich bevind
     * - zoeken van een fiets in de database
     * - zoeken van een lijst van alle fietsen
     * - zoeken van alle beschikbare fietsen
     * - een fiets uit de database halen.
     * - een lijst van fietsen uit de database halen.
     */

    /**
     * Voegt een fiets toe. Het id wordt automatisch gegenereerd door de DAO
     * @param fiets de fiets die toegevoegd moet worden
     * @return gegenereerd id van de fiets die net werd toegevoegd of null indien geen fiets werd opgegeven.
     * @throws DBException Exception die duidt op een verkeerde installatie van de DAO of een fout in de query.
     */
    public Integer toevoegenFiets(Fiets fiets) throws DBException {
        if (fiets != null) {
            Integer primaryKey = null;
            //Connectie tot stand brengen (en automatisch sluiten)
            try (Connection conn = ConnectionManager.getConnection()) {
                //PreparedStatement opstellen (en automatisch sluiten)
                try (PreparedStatement stmt = conn.prepareStatement(
                        "insert into Fiets(status, standplaats, opmerkingen) values (?,?,?)",
                        Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setObject(1, Status.actief.toString());
                    stmt.setObject(2, fiets.getStandplaats().toString());
                    stmt.setString(3, fiets.getOpmerking());
                    stmt.execute();

                    ResultSet generatedKeys = stmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        primaryKey = generatedKeys.getInt(1);
                    }
                } catch (SQLException sqlEx) {
                    throw new DBException("SQL-exception in toevoegenFiets "
                            + "- statement" + sqlEx);
                }
            } catch (SQLException sqlEx) {
                throw new DBException("SQL-exception in toevoegenFiets "
                        + "- connection" + sqlEx);
            }
            return primaryKey;
        } else {
            return null;
        }
    }

    /**
     * Wijzigt de opmerking van een fiets.
     * @param regnr registratienummer van fiets waarvan opmerking gewijzigd moet worden.
     * @param opmerking     Nieuwe opmerking voor de fiets.
     * @throws DBException Exception die duidt op een verkeerde installatie van de DAO of een fout in de query.
     */
    public void wijzigenOpmerkingFiets(int regnr, String opmerking) throws DBException {
        if (regnr != 0 & opmerking != null) {

            // connectie tot stand brengen (en automatisch sluiten)
            try (Connection conn = ConnectionManager.getConnection()) {
                // preparedStatement opstellen (en automatisch sluiten)
                try (PreparedStatement stmt = conn.
                        prepareStatement("update fiets "
                                + " set opmerkingen = ?"
                                + " where registratienummer = ?")) {

                    stmt.setString(1, opmerking);
                    stmt.setString(2, String.valueOf(regnr));
                    stmt.execute();
                } catch (SQLException sqlEx) {
                    throw new DBException("SQL-exception in wijzigenOpmerkingFiets - statement" + sqlEx);
                }
            } catch (SQLException sqlEx) {
                throw new DBException(
                        "SQL-exception in wijzigenOpmerkingFiets - connection" + sqlEx);
            }
        }
    }

    /**
     * Wijzigt de status van een fiets.
     * @param regnr registratienummer van fiets waarvan opmerking gewijzigd moet worden.
     * @param status     Nieuwe status voor de fiets.
     * @throws DBException Exception die duidt op een verkeerde installatie van de DAO of een fout in de query.
     */
    public void wijzigenToestandFiets(int regnr, Status status) throws DBException {
        if (regnr != 0) {
            // connectie tot stand brengen (en automatisch sluiten)
            try (Connection conn = ConnectionManager.getConnection()) {
                // preparedStatement opstellen (en automatisch sluiten)
                try (PreparedStatement stmt = conn.
                        prepareStatement("update fiets "
                                + " set status = ? " +
                                " where registratienummer = ?")) {
                    stmt.setObject(1, status.toString());
                    stmt.setString(2, String.valueOf(regnr));
                    stmt.execute();
                } catch (SQLException sqlEx) {
                    throw new DBException("SQL-exception in wijzigenStatus - statement" + sqlEx);
                }
            } catch (SQLException sqlEx) {
                throw new DBException(
                        "SQL-exception in wijzigenStatus - connection" + sqlEx);
            }
        }
    }

    /**
     * Zoekt adhv van het registratienummer een fiets op. Wanneer geen fietswerd gevonden wordt null teruggeven
     *
     * @param regnr registratienummer van de rekening die gezocht moet worden.
     * @return Fiets die gezocht wordt, null indien de fiets niet werd gevonden.
     * @throws DBException Exception die duidt op een verkeerde installatie van de DAO of een fout in de query.
     */
    public Fiets zoekFiets(Integer regnr) throws DBException {
        if (regnr != 0) {
            Fiets returnFiets = null;
            // connectie tot stand brengen (en automatisch sluiten)
            try (Connection conn = ConnectionManager.getConnection()) {
                // preparedStatement opstellen (en automatisch sluiten)
                try (PreparedStatement stmt = conn.prepareStatement(
                        "select registratienummer, "
                                + "status, "
                                + "standplaats,"
                                + " opmerkingen "
                                + "from fiets "
                                + "where registratienummer = ?")) {

                    // parameters invullen in query
                    stmt.setString(1, regnr.toString());

                    // execute voert het SQL-statement uit
                    stmt.execute();

                    // result opvragen (en automatisch sluiten)
                    try (ResultSet r = stmt.getResultSet()) {
                        if (r.next()) {
                            if(r == null){
                                return null;
                            }
                            returnFiets = getFietsUitDatabase(r);
                        }
                        return returnFiets;
                    } catch (SQLException sqlEx) {
                        throw new DBException("SQL-exception in zoekFiets - resultset " + sqlEx);
                    }
                } catch (SQLException sqlEx) {
                    throw new DBException("SQL-exception in zoekFiets " + "- statement " + sqlEx);
                }
            } catch (SQLException sqlEx) {
                throw new DBException("SQL-exception in zoekFiets " + "- connection" + sqlEx);
            }
        }
        return null;
    }

    /**
     * Zoekt achter alle fietsen in de database.
     *
     * @return lijst van fietsen die gezocht wordt.
     * @throws DBException Exception die duidt op een verkeerde installatie van de DAO of een fout in de query.
     */
    public List<Fiets> zoekAlleFietsen() throws DBException{
        // connectie tot stand brengen (en automatisch sluiten)
        try (Connection conn = ConnectionManager.getConnection()) {
            // preparedStatement opstellen (en automatisch sluiten)
             try (PreparedStatement stmt = conn.prepareStatement(
                    "select registratienummer"
                            + " , status"
                            + " , standplaats"
                            + " , opmerkingen"
                            + " from fiets"
                            + " order by registratienummer")) {
                stmt.execute();
                try (ResultSet r = stmt.getResultSet()) {
                    return getFietsenUitDatabase(r);
                } catch (SQLException sqlEx) {
                    throw new DBException(
                            "SQL-exception in zoekAlleFietsen - resultset " + sqlEx);
                }
            } catch (SQLException sqlEx) {
                throw new DBException("SQL-exception in zoekAlleFietsen - statement " + sqlEx);
            }
        } catch (SQLException sqlEx) {
            throw new DBException(
                    "SQL-exception in zoekAlleFietsen - connection " + sqlEx);
        }
    }

    /**
     * Geeft een lijst terug van alle fietsen met de status ACTIEF en die momenteel geen openstaande rit hebben.
     *
     * @return een lijst van alle beschikbare fietsen.
     * @throws DBException Exception die duidt op een verkeerde installatie van de DAO of een fout in de query.
     */
    public List<Fiets> zoekAlleBeschikbareFietsen() throws DBException {
        // connectie tot stand brengen (en automatisch sluiten)
        try (Connection conn = ConnectionManager.getConnection()) {
            // preparedStatement opstellen (en automatisch sluiten)
            try (PreparedStatement stmt = conn.prepareStatement(
                    "select registratienummer"
                            + " , status"
                            + " , standplaats"
                            + " , opmerkingen"
                            + " from fiets f "
                            + " LEFT JOIN rit r on f.registratienummer = r.fiets_registratienummer"
                            + " where f.status = 'actief' AND r.eindtijd > r.starttijd or (r.starttijd is null and r.eindtijd is null)"
                            + " order by registratienummer")) {
                stmt.execute();
                // result opvragen (en automatisch sluiten)
                try (ResultSet r = stmt.getResultSet()) {
                    // van alle klanten uit de DAO Klant-objecten maken
                    // en in een ljst steken
                    return getFietsenUitDatabase(r);
                } catch (SQLException sqlEx) {
                    throw new DBException(
                            "SQL-exception in zoekAlleFietsen - resultset" + sqlEx);
                }
            } catch (SQLException sqlEx) {
                throw new DBException("SQL-exception in zoekAlleFietsen - statement" + sqlEx);
            }
        } catch (SQLException sqlEx) {
            throw new DBException(
                    "SQL-exception in zoekAlleFietsen - connection" + sqlEx);
        }
    }

    private Fiets getFietsUitDatabase(ResultSet r) throws SQLException {
        Fiets fiets = new Fiets();
        fiets.setRegistratienummer(r.getInt("registratienummer"));
        fiets.setStatus(Status.valueOf(r.getString("status")));
        fiets.setStandplaats(Standplaats.valueOf(r.getString("standplaats")));
        fiets.setOpmerking(r.getString("opmerkingen"));
        return fiets;
    }

    private ArrayList<Fiets> getFietsenUitDatabase(ResultSet r) throws SQLException {
        ArrayList<Fiets> fietsen = new ArrayList<>();
        while (r.next()) {
            Fiets lid = getFietsUitDatabase(r);
            fietsen.add(lid);
        }
        return fietsen;
    }

}
