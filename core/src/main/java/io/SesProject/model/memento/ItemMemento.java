package io.SesProject.model.memento;

/*memento del singolo item dell'inventario (sará incapsulato in PlayerCharacterMemento)*/
public class ItemMemento {
    public String name;
    public String description;
    public String type; // Salviamo l'Enum come String (WEAPON, ARMOR, SKILL)
    public int value;

    // Costruttore vuoto per JSON
    public ItemMemento() {}

    public ItemMemento(String name, String description, String type, int value) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.value = value;
    }
}
