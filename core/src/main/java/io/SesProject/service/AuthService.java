package io.SesProject.service;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import io.SesProject.model.User;

public class AuthService {

    private static final String SAVE_DIR = "saves/";

    public boolean login(String username) {
        // Verifica se esiste il file saves/username.json
        FileHandle file = Gdx.files.local(SAVE_DIR + username + ".json");
        return file.exists();
    }

    public boolean register(String username) {
        if (username.trim().isEmpty()) return false;

        FileHandle file = Gdx.files.local(SAVE_DIR + username + ".json");

        // Se il file esiste già, non puoi registrare di nuovo lo stesso nome
        if (file.exists()) {
            return false;
        }

        // Se non esiste, crea un utente di default e salva il file iniziale
        try {
            // Creiamo un utente base
            User newUser = new User(username);

            // Usiamo il SaveService (o un metodo locale rapido) per creare il file fisico
            // Per pulizia, qui scriviamo direttamente un JSON base o usiamo SaveService se accessibile.
            // Opzione semplice usando LibGDX Json diretto qui per inizializzare:
            com.badlogic.gdx.utils.Json json = new com.badlogic.gdx.utils.Json();
            file.writeString(json.toJson(newUser), false);

            System.out.println("Nuovo profilo creato: " + file.path());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
