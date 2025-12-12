package io.SesProject.service;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import io.SesProject.model.Memento;
import io.SesProject.model.UserMemento;


public class SaveService {
    private Json json;
    private static final String SAVE_DIR = "saves/";

    public SaveService() {
        this.json = new Json();
        json.addClassTag("memento", UserMemento.class);
    }

    // Helper per ottenere il nome file corretto in base all'utente
    private String getFileName(String username) {
        return SAVE_DIR + username + ".json";
    }

    // Salva lo stato dell'utente corrente
    public void saveGame(Memento memento, String username) {
        if (username == null || username.isEmpty()) return;

        String data = json.toJson(memento, UserMemento.class);
        FileHandle file = Gdx.files.local(getFileName(username));
        file.writeString(data, false);
        System.out.println("Salvato progresso per: " + username);
    }

    // Carica lo stato
    public Memento loadGame(String username) {
        FileHandle file = Gdx.files.local(getFileName(username));
        if (file.exists()) {
            try {
                return json.fromJson(UserMemento.class, file.readString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public boolean hasSaveFile(String username) {
        if (username == null) return false;
        return Gdx.files.local(getFileName(username)).exists();
    }
}
