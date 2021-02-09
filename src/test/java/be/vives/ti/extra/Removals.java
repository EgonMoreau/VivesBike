package be.vives.ti.extra;

import be.vives.ti.dao.connect.ConnectionManager;
import be.vives.ti.exception.DBException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Removals {
    /**
     * Verwijdert de opgegeven klant uit de DAO, zonder enige controle
     *
     * @param id id van de klant die verwijderd moet worden
     * @throws DBException Exception die duidt op een verkeerde
     *                     installatie van de DAO of een fout in de query.
     */
    public static void removeLid(String rijksregisternummer) throws DBException {

        // connectie tot stand brengen (en automatisch sluiten)
        try (Connection conn = ConnectionManager.getConnection()) {
            // preparedStatement opstellen (en automtisch sluiten)
            try (PreparedStatement stmt = conn.prepareStatement(
                    "delete from Lid where rijksregisternummer = ?")) {
                stmt.setString(1, rijksregisternummer);
                // execute voert elke sql-statement uit, executeQuery enkel de select
                stmt.execute();
            } catch (SQLException sqlEx) {
                throw new DBException("SQL-exception in removeLid - statement" + sqlEx);
            }
        } catch (SQLException sqlEx) {
            throw new DBException(
                    "SQL-exception in removeLid - connection" + sqlEx);
        }
    }

    public static void removeFiets(Integer regnr) throws DBException {
        try (Connection conn = ConnectionManager.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(
                    "delete from fiets where registratienummer = ?")) {
                stmt.setInt(1, regnr);
                stmt.execute();
            } catch (SQLException sqlEx) {
                throw new DBException("SQL-exception in removeFiets - statement" + sqlEx);
            }
        } catch (SQLException sqlEx) {
            throw new DBException(
                    "SQL-exception in removeFiets - connection" + sqlEx);
        }
    }

    public static void removeRit(Integer ritID) throws DBException {
        if(ritID != 999999) {
            try (Connection conn = ConnectionManager.getConnection()) {
                try (PreparedStatement stmt = conn.prepareStatement(
                        "delete from rit where id = ?")) {
                    stmt.setInt(1, ritID);
                    stmt.execute();
                } catch (SQLException sqlEx) {
                    throw new DBException("SQL-exception in removeRit - statement" + sqlEx);
                }
            } catch (SQLException sqlEx) {
                throw new DBException(
                        "SQL-exception in removeRit - connection" + sqlEx);
            }
        }
    }
}
