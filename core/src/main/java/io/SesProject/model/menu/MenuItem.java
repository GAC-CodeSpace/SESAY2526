package io.SesProject.model.menu;


import io.SesProject.controller.command.Command;

import java.util.Collections;
import java.util.List;

/**
 * LEAF (Foglia) del Composite Pattern.
 * Rappresenta una singola opzione cliccabile che esegue un comando.
 */
public class MenuItem implements MenuComponent {

    private String name;
    private Command command;

    public MenuItem(String name, Command command) {
        this.name = name;
        this.command = command;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void select() {
        // Pattern Command: Invoca l'esecuzione
        if (command != null) {
            command.execute();
        }
    }

    @Override
    public List<MenuComponent> getChildren() {
        // Una foglia non ha figli, restituisce lista vuota per evitare NullPointer nella View
        return Collections.emptyList();
    }

    @Override
    public void add(MenuComponent component) {
        // Violazione del principio: una foglia non può avere figli.
        // Possiamo lanciare eccezione o semplicemente ignorare.
        throw new UnsupportedOperationException("Non puoi aggiungere figli a un MenuItem (Foglia)");
    }
}
