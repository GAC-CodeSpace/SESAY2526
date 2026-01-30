package io.SesProject.model;



/**
 * Data Transfer Object (DTO) semplice.
 * Contiene solo le info necessarie per disegnare il bottone di caricamento.
 */
public class SaveMetadata {
    private int slotId;
    private String date;
    private String details; // Es. "Livello 5"

    public SaveMetadata(int slotId, String date, String details) {
        this.slotId = slotId;
        this.date = date;
        this.details = details;
    }

    public int getSlotId() { return slotId; }
    public String getDate() { return date; }
    public String getDetails() { return details; }

    @Override
    public String toString() {
        return "Slot " + slotId + " - " + date + " (" + details + ")";
    }
}
