package net.gausman.ftl.model;

import java.util.Set;

public class SaveMetadata {
    private String filename;
    private Integer difficulty;
    private String ship;
    private Integer sector;
    private String description;

    public SaveMetadata(String filename, Integer difficulty, String ship, Integer sector, String description) {
        this.filename = filename;
        this.difficulty = difficulty;
        this.ship = ship;
        this.sector = sector;
        this.description = description;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Integer getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Integer difficulty) {
        this.difficulty = difficulty;
    }

    public String getShip() {
        return ship;
    }

    public void setShip(String ship) {
        this.ship = ship;
    }

    public Integer getSector() {
        return sector;
    }

    public void setSector(Integer sector) {
        this.sector = sector;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

