package io.SesProject.service;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import io.SesProject.model.User;
import io.SesProject.model.memento.Memento;
import io.SesProject.model.memento.UserMemento;


public class AuthService {

    private static final String SAVE_DIR = "saves/";

    public boolean login(String username) {
        if (username == null || username.trim().isEmpty()) return false;
        FileHandle file = Gdx.files.local(SAVE_DIR + username + ".json");
        return file.exists();
    }

    public boolean register(String username) {
        if (username == null || username.trim().isEmpty()) return false;

        FileHandle file = Gdx.files.local(SAVE_DIR + username + ".json");

        // Se il file esiste già, errore
        if (file.exists()) {
            return false;
        }

        try {
            // 1. Creiamo l'utente base (che genera P1 e P2 di default)
            User newUser = new User(username);

            // 2. IMPORTANTE: Creiamo il Memento!
            // Non salviamo 'newUser' direttamente, ma il suo snapshot.
            // Questo garantisce che i campi si chiamino "player1" e non "p1".
            Memento memento = newUser.save();

            // 3. Scriviamo il JSON usando il Memento
            Json json = new Json();
            // Opzionale: usiamo la stessa formattazione del SaveService per pulizia
            json.setUsePrototypes(false);

            // Salviamo specificando che è un UserMemento
            file.writeString(json.toJson(memento, UserMemento.class), false);

            System.out.println("Nuovo profilo creato: " + file.path());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
