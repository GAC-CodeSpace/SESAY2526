package io.SesProject.model;



public class UserMemento implements Memento {

    // Dati da salvare
    public String username;
    public int level;
    public int currentHp;
    public int maxHp;
    public int xp;

    // Coordinate sulla mappa (utile per quando sarà implementato il gioco)
    public float x;
    public float y;
    public String currentMapName;

    // --- Costruttore Vuoto ---
    // OBBLIGATORIO per la serializzazione JSON di LibGDX
    public UserMemento() {
    }

    // Costruttore di comodo per la creazione rapida
    public UserMemento(String username, int level, int hp, int maxHp, int xp, float x, float y, String map) {
        this.username = username;
        this.level = level;
        this.currentHp = hp;
        this.maxHp = maxHp;
        this.xp = xp;
        this.x = x;
        this.y = y;
        this.currentMapName = map;
    }
}
