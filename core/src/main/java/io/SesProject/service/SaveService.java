package io.SesProject.service;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import io.SesProject.model.SaveMetadata;
import io.SesProject.model.memento.Memento;
import io.SesProject.model.memento.GameSessionMemento;
import io.SesProject.model.memento.PlayerCharacterMemento;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class SaveService {

    private Json json;
    private static final String ROOT_DIR = "saves/";

    public SaveService() {
        this.json = new Json();

        // Configurazione per un JSON più pulito e leggibile
        json.setOutputType(JsonWriter.OutputType.json);
        json.setUsePrototypes(false);

        // --- MAPPING DELLE CLASSI ---
        // Quando il JSON vede il tag "session", sa che deve creare un GameSessionMemento
        json.addClassTag("session", GameSessionMemento.class);
        // Quando vede "character", crea un PlayerCharacterMemento
        json.addClassTag("character", PlayerCharacterMemento.class);

        // Assicuriamoci che la cartella root esista (sicurezza)
        if (!Gdx.files.local(ROOT_DIR).exists()) {
            Gdx.files.local(ROOT_DIR).mkdirs();
        }
    }

    /**
     * Crea un nuovo file di salvataggio.
     * Ritorna L'ID dello slot creato, oppure -1 in caso di errore.
     */
    public int createNewSaveSlot(Memento memento, String username) {
        try {
            int slotId = 1;
            while (Gdx.files.local(generateFileName(username, slotId)).exists()) {
                slotId++;
            }

            writeToDisk(memento, username, slotId);
            return slotId; // Successo

        } catch (Exception e) {
            e.printStackTrace(); // Logga l'errore per lo sviluppatore
            return -1; // Segnala fallimento al Controller
        }
    }

    /**
     * Sovrascrive un salvataggio esistente (o specifico).
     * Usato in futuro dal comando "Salva Partita" nel menu di pausa.
     */
    public void saveGame(Memento memento, String username, int slotId) {
        writeToDisk(memento, username, slotId);
    }

    /**
     * Metodo helper privato per la scrittura fisica.
     */
    private void writeToDisk(Memento memento, String username, int slotId) {
        if (username == null || username.isEmpty()) return;

        String fileName = generateFileName(username, slotId);
        FileHandle file = Gdx.files.local(fileName);

        // Serializza l'oggetto Memento in stringa JSON
        // Specifichiamo la classe per assicurarci che usi i tag corretti
        String data = json.toJson(memento, GameSessionMemento.class);

        file.writeString(data, false);

        System.out.println("[SAVE SERVICE] File scritto: " + fileName);
    }

    /**
     * Carica un salvataggio specifico.
     * Usato dal futuro bottone "Load Game".
     */
    public Memento loadGame(String username, int slotId) {
        String fileName = generateFileName(username, slotId);
        FileHandle file = Gdx.files.local(fileName);

        if (file.exists()) {
            try {
                System.out.println("[SAVE SERVICE] Caricamento file: " + fileName);
                return json.fromJson(GameSessionMemento.class, file.readString());
            } catch (Exception e) {
                System.err.println("[SAVE SERVICE] Errore caricamento file: " + fileName);
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Helper per generare il percorso standard del file.
     * Esempio: "saves/Mario/save_1.json"
     */
    private String generateFileName(String username, int slotId) {
        return ROOT_DIR + username + "/save_" + slotId + ".json";
    }

    /**
     * Recupera la lista di tutti i salvataggi disponibili per un utente.
     * Legge parzialmente i JSON per estrarre la data.
     */
    public List<SaveMetadata> getSaveSlots(String username) {
        List<SaveMetadata> slots = new ArrayList<>();
        FileHandle dir = Gdx.files.local(ROOT_DIR + username);

        if (!dir.exists() || !dir.isDirectory()) {
            return slots; // Nessun salvataggio o utente errato
        }

        // Elenca i file nella directory
        FileHandle[] files = dir.list(".json");

        for (FileHandle file : files) {
            try {
                // Parsing "leggero" o parziale
                // Nota: In un gioco enorme, leggeremmo solo l'header.
                // Qui carichiamo il memento perché i file sono piccoli.
                GameSessionMemento m = json.fromJson(GameSessionMemento.class, file.readString());

                // Estraiamo l'ID dal filename (es. "save_3.json")
                String fname = file.name(); // save_3.json
                String idPart = fname.replace("save_", "").replace(".json", "");
                int slotId = Integer.parseInt(idPart);

                // Creiamo info extra (es. Livello P1)
                String info = "Lvl " + m.player1.level;

                slots.add(new SaveMetadata(slotId, m.creationDate, info));
            } catch (Exception e) {
                System.err.println("Errore lettura file salvataggio: " + file.name());
            }
        }

        // Ordiniamo per Slot ID
        slots.sort(Comparator.comparingInt(SaveMetadata::getSlotId));

        return slots;
    }
}
