package io.SesProject.model.menu;
import java.util.List;

/**
 * Interfaccia comune per il Composite Pattern.
 * Rappresenta sia una singola voce di menu (MenuItem)
 * sia un contenitore di voci (MenuComposite).
 */
public interface MenuComponent {

    /**
     * Restituisce il testo da visualizzare sul bottone/etichetta.
     */
    String getName();

    /**
     * Esegue l'azione associata al componente.
     */
    void select();

    /**
     * Restituisce la lista dei figli (se presenti).
     */
    List<MenuComponent> getChildren();

    /**
     * Metodo opzionale per aggiungere figli.
     * Le foglie (MenuItem) potrebbero lanciare eccezione o ignorarlo.
     */
    void add(MenuComponent component);
}
