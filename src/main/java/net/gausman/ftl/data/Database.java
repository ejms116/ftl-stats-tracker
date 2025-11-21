package net.gausman.ftl.data;

import net.blerf.ftl.parser.DataManager;
import net.blerf.ftl.parser.SavedGameParser;
import net.blerf.ftl.xml.ShipBlueprint;
import net.gausman.ftl.model.SaveMetadata;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.*;
import java.util.*;

public class Database {
    private final String dbPath;
    private final String savesPath;
    private Connection conn;

    public Database(Path dojoDir) throws SQLException {
        this.dbPath = String.format("%s/metadata.db", dojoDir);
        this.savesPath = String.format("%s/saves", dojoDir);
        connect();
        initializeSchema();
        syncWithFilesystem();
    }

    private void connect(){
        try {
            this.conn = DriverManager.getConnection("jdbc:sqlite:" + this.dbPath);
        } catch (SQLException e){
            throw new RuntimeException("Could not connect to SQLite database", e);
        }
    }

    private void initializeSchema() throws SQLException {
        String sqlSaves = """
                CREATE TABLE IF NOT EXISTS saves (
                    filename TEXT PRIMARY KEY,
                    ship TEXT NOT NULL,
                    sector INTEGER NOT NULL,
                    difficulty INTEGER NOT NULL,
                    description TEXT NOT NULL
                );
            """;

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sqlSaves);
        } catch (SQLException e){
            throw new RuntimeException("Could not create database tables", e);
        }
    }

    private void syncWithFilesystem(){
        List<SaveMetadata> existing = getAllSaves();
        Map<String, SaveMetadata> map = new HashMap<>();
        for (SaveMetadata meta : existing){
            map.put(meta.getFilename(), meta);
        }

        File folder = new File(savesPath);
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".sav"));

        if (files == null) return;

        SavedGameParser parser = new SavedGameParser();

        Map<String, SavedGameParser.SavedGameState> missingFiles = new HashMap<>();
        for (File file : files){
            if (!map.containsKey(file.getName())){
                SavedGameParser.SavedGameState gs = null;
                try {
                    gs = parser.readSavedGame(file);
                } catch (IOException e){

                }
                missingFiles.put(file.getName(), gs);
            }
        }

        if (!missingFiles.isEmpty()){
            insertDefaultMetadataBatch(missingFiles);
        }
    }

    private void insertDefaultMetadataBatch(Map<String, SavedGameParser.SavedGameState> filenameGameStateMap){
        String sql = "INSERT INTO saves(filename, ship, sector, difficulty, description) VALUES (?, ?, ?, 0, '')";

        DataManager dm = DataManager.get();
        Map<String, ShipBlueprint> shipMap = dm.getPlayerShips();
        try (PreparedStatement stmt = conn.prepareStatement(sql)){
            conn.setAutoCommit(false);

            for (Map.Entry<String, SavedGameParser.SavedGameState> entry : filenameGameStateMap.entrySet()){
                stmt.setString(1, entry.getKey());
                if (entry.getValue() == null){
                    stmt.setString(2, "");
                    stmt.setString(3, "0");
                } else {
                    stmt.setString(2, shipMap.get(entry.getValue().getPlayerShipBlueprintId()).getName().toString());
                    stmt.setString(3, String.valueOf(entry.getValue().getSectorNumber()+1));
                }

                stmt.addBatch();
            }

            stmt.executeBatch();
            conn.commit();

        } catch (SQLException e){
            try { conn.rollback();} catch (SQLException ignored){}
            throw new RuntimeException("Batch insert failed", e);
        } finally {
            try { conn.setAutoCommit(true);} catch (SQLException ignored){}
        }
    }

    // Public API
    public List<SaveMetadata> getAllSaves(){
        String sql = "SELECT * FROM saves";
        List<SaveMetadata> list = new ArrayList<>();

        try (Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()){
                list.add(new SaveMetadata(
                        rs.getString("filename"),
                        rs.getInt("difficulty"),
                        rs.getString("ship"),
                        rs.getInt("sector"),
                        rs.getString("description")
                ));
            }
        } catch (SQLException e){
            throw new RuntimeException("Could not retrieve saves", e);
        }
        return list;
    }

    public void updateMetadata(SaveMetadata metadata) throws SQLException {
        String sql = "UPDATE saves set difficulty = ?, description = ? WHERE filename = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, metadata.getDifficulty());
            stmt.setString(2, metadata.getDescription());
            stmt.setString(3, metadata.getFilename());

            stmt.execute();
        } catch (SQLException e){
            throw e;
        }
    }

    public void renameSave(String oldFilename, SaveMetadata metadata){}

}
