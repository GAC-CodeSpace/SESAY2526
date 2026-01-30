package io.SesProject.model;

import io.SesProject.model.game.item.ItemType;
import io.SesProject.model.game.item.factory.Item;
import io.SesProject.model.game.item.factory.ItemFactory;
import io.SesProject.model.game.item.observer.PlayerStatsObserver;
import io.SesProject.model.memento.ItemMemento;
import io.SesProject.model.memento.PlayerCharacterMemento;

import java.util.ArrayList;
import java.util.List;

//SUBJECT Observer pattern
public class PlayerCharacter {

    private String name;
    private String archetype;
    private int level;
    private int hp;
    private int maxHp;
    private int baseMaxHp = 100;
    private int baseDamage = 5;

    // Posizione (per esplorazione)
    private float x, y;

    // --- INVENTARIO ---
    private List<Item> inventory;

    // --- SLOT EQUIPAGGIAMENTO ---
    private Item equippedWeapon;
    private Item equippedArmor;
    private List<Item> equippedSkills; // Lista per le skill attive
    private final int MAX_SKILL_SLOTS = 4;

    // --- OBSERVER PATTERN IMPLEMENTATION ---
    // Lista degli osservatori
    private List<PlayerStatsObserver> observers = new ArrayList<>();

    public PlayerCharacter() {
        this.inventory = new ArrayList<>();
        this.equippedSkills = new ArrayList<>();
    }

    public PlayerCharacter(String name, String archetype) {
        this.name = name;
        this.archetype = archetype;
        // Inizializzazioni fondamentali
        this.inventory = new ArrayList<>();
        this.equippedSkills = new ArrayList<>();
        this.observers = new ArrayList<>();
        this.maxHp = baseMaxHp;
        this.hp = maxHp;
    }

    public void addObserver(PlayerStatsObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    // Metodo per rimuovere un osservatore (Detach)
    public void removeObserver(PlayerStatsObserver observer) {
        observers.remove(observer);
    }

    // Metodo di notifica (Notify)
    private void notifyObservers() {
        for (PlayerStatsObserver observer : observers) {
            observer.onStatsChanged(this); // Passa se stesso come riferimento
        }
    }

    // --- MEMENTO: SAVE ---
    public PlayerCharacterMemento save() {
        PlayerCharacterMemento m = new PlayerCharacterMemento();
        m.name = this.name;
        m.archetype = this.archetype;
        m.level = this.level;
        m.hp = this.hp;
        m.maxHp = this.maxHp;
        m.x = this.x;
        m.y = this.y;

        // Salvataggio Inventario
        for (Item item : this.inventory) {
            m.inventory.add(item.save());
        }

        // Salvataggio Skill Equipaggiate
        for (Item skill : this.equippedSkills) {
            m.equippedSkills.add(skill.save());
        }

        // Salvataggio Equip Singolo
        if (this.equippedWeapon != null) {
            m.equippedWeapon = this.equippedWeapon.save();
        }
        if (this.equippedArmor != null) {
            m.equippedArmor = this.equippedArmor.save();
        }

        return m;
    }

    // --- MEMENTO: RESTORE ---
    public void restore(PlayerCharacterMemento m) {
        if (m == null) return;

        this.name = m.name;
        this.archetype = m.archetype;
        this.level = m.level;
        this.hp = m.hp;
        this.maxHp = m.maxHp;
        this.x = m.x;
        this.y = m.y;

        // Ripristino Inventario
        this.inventory.clear();
        for (ItemMemento im : m.inventory) {
            Item item = ItemFactory.createFromMemento(im);
            if (item != null) this.inventory.add(item);
        }

        // Ripristino Skill Equipaggiate
        this.equippedSkills.clear();
        for (ItemMemento im : m.equippedSkills) {
            Item item = ItemFactory.createFromMemento(im);
            if (item != null) this.equippedSkills.add(item);
        }

        // Ripristino Equip Singolo
        this.equippedWeapon = ItemFactory.createFromMemento(m.equippedWeapon);
        this.equippedArmor = ItemFactory.createFromMemento(m.equippedArmor);

        // Importante: Ricalcola le stats finali dopo aver caricato l'equip
        // (es. ricalcola maxHp in base all'armatura caricata)
        recalculateStats();
    }

    // --- LOGICA EQUIP/UNEQUIP ---

    public void equipItem(Item item) {
        if (!inventory.contains(item)) return;

        // Gestione slot specifici
        if (item.getType() == ItemType.WEAPON) {
            if (equippedWeapon != null) unequipItem(equippedWeapon);
            equippedWeapon = item;
        }
        else if (item.getType() == ItemType.ARMOR) {
            if (equippedArmor != null) unequipItem(equippedArmor);
            equippedArmor = item;
        }
        else if (item.getType() == ItemType.SKILL) {
            // Se ho spazio aggiungo, altrimenti scambio col primo
            if (equippedSkills.size() < MAX_SKILL_SLOTS) {
                equippedSkills.add(item);
            } else {
                unequipItem(equippedSkills.get(0));
                equippedSkills.add(item);
            }
        }

        // Rimuove dall'inventario (sposta in equip)
        inventory.remove(item);
        recalculateStats();
    }

    public void unequipItem(Item item) {
        boolean removed = false;

        if (item == equippedWeapon) {
            equippedWeapon = null;
            removed = true;
        } else if (item == equippedArmor) {
            equippedArmor = null;
            removed = true;
        } else if (equippedSkills.contains(item)) {
            equippedSkills.remove(item);
            removed = true;
        }

        if (removed) {
            inventory.add(item);
            recalculateStats();
        }
    }

    private void recalculateStats() {
        int armorBonus = (equippedArmor != null) ? equippedArmor.getValue() : 0;
        this.maxHp = this.baseMaxHp + armorBonus;

        if (this.hp > this.maxHp) this.hp = this.maxHp;

        // NOTIFICA GLI OSSERVATORI DOPO IL CAMBIO DI STATO
        notifyObservers();
    }

    // --- GETTERS & SETTERS ---
    public void addItem(Item item) { inventory.add(item); }
    public List<Item> getInventory() { return inventory; }
    public Item getEquippedWeapon() { return equippedWeapon; }
    public Item getEquippedArmor() { return equippedArmor; }
    public List<Item> getEquippedSkills() { return equippedSkills; }
    public int getAttackPower() {
        int wpnBonus = (equippedWeapon != null) ? equippedWeapon.getValue() : 0;
        return baseDamage + wpnBonus;
    }


    // Metodi getter base (name, hp, etc.) e memento...
    public String getName() { return name; }
    public int getMaxHp() { return maxHp; }
    public int getHp() { return hp; } // Aggiunto getter HP
    public float getX() { return x; }
    public float getY() { return y; }
    public void setPosition(float x, float y) { this.x = x; this.y = y; }
    public String getArchetype() { return archetype; }
}
