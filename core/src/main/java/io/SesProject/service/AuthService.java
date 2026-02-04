package io.SesProject.service;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class AuthService {

    private static final String ROOT_DIR = "saves/";

    /**
     * Verifica se esiste il PROFILO (cioè la cartella dell'utente).
     * NON controlla se ci sono salvataggi dentro.
     */
    public boolean login(String username) {
        if (username == null || username.trim().isEmpty()) return false;

        // Controlla se esiste la directory
        FileHandle userDir = Gdx.files.local(ROOT_DIR + username);
        return userDir.exists() && userDir.isDirectory();
    }

    /**
     * Crea un nuovo PROFILO (cioè una cartella vuota).
     */
    public boolean register(String username) {
        if (username == null || username.trim().isEmpty()) return false;

        FileHandle userDir = Gdx.files.local(ROOT_DIR + username);

        // Se la cartella esiste già, non possiamo ricrearla
        if (userDir.exists()) {
            return false;
        }

        // Crea la cartella fisica
        userDir.mkdirs();
        System.out.println("[AUTH] Creata cartella profilo: " + userDir.path());
        return true;
    }
}
