package io.SesProject.model;

public class User {

    private String username;
    private int level;

    // Nuovi campi di esempio per dare senso al salvataggio
    private int currentHp = 100;
    private int maxHp = 100;
    private int xp = 0;
    private float x = 0, y = 0; // Posizione nel mondo
    private String currentMap = "start_village";

    // Costruttore vuoto per Json LibGDX
    public User() {}

    public User(String username) {
        this.username = username;
        this.level = 1;
    }

    // --- MEMENTO PATTERN: SAVE ---
    // Crea un'istantanea dello stato corrente
    public Memento save() {
        return new UserMemento(
            this.username,
            this.level,
            this.currentHp,
            this.maxHp,
            this.xp,
            this.x,
            this.y,
            this.currentMap
        );
    }

    // --- MEMENTO PATTERN: RESTORE ---
    // Ripristina lo stato da un memento
    public void restore(Memento m) {
        // Controllo di sicurezza
        if (!(m instanceof UserMemento)) {
            throw new IllegalArgumentException("Memento non valido per User");
        }

        UserMemento state = (UserMemento) m;

        this.username = state.username;
        this.level = state.level;
        this.currentHp = state.currentHp;
        this.maxHp = state.maxHp;
        this.xp = state.xp;
        this.x = state.x;
        this.y = state.y;
        this.currentMap = state.currentMapName;

        System.out.println("Stato utente ripristinato: Livello " + level + " - Mappa " + currentMap);
    }

    // Getters e Setters standard...
    public String getUsername() { return username; }
    public void setPosition(float x, float y) { this.x = x; this.y = y; }
    // ... altri getter
}
