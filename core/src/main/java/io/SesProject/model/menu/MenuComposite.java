package io.SesProject.model.menu;

import java.util.ArrayList;
import java.util.List;

/**
 * COMPOSITE del Composite Pattern.
 * È un contenitore che può avere figli (sia MenuItem che altri MenuComposite).
 */
public class MenuComposite implements MenuComponent {

    private String name;
    private List<MenuComponent> children;

    public MenuComposite(String name) {
        this.name = name;
        this.children = new ArrayList<>();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void select() {
        // Generalmente un Composite (come un titolo di sottomenu) non fa nulla di logico,
        // oppure potrebbe espandere/collassare la vista.
        System.out.println("Selezionato gruppo: " + name);
    }

    @Override
    public List<MenuComponent> getChildren() {
        return children;
    }

    @Override
    public void add(MenuComponent component) {
        this.children.add(component);
    }
}
