package io.SesProject.controller;

import com.badlogic.gdx.audio.Sound;
import io.SesProject.RpgGame;
import io.SesProject.controller.command.InventoryCommand.EquipItemCommand;
import io.SesProject.controller.command.InventoryCommand.UnequipItemCommand;
import io.SesProject.controller.command.InventoryCommand.UsePowerUpCommand;
import io.SesProject.model.GameSession;
import io.SesProject.model.PlayerCharacter;
import io.SesProject.model.game.item.factory.*;
import io.SesProject.model.menu.MenuComponent;
import io.SesProject.model.menu.MenuComposite;
import io.SesProject.model.menu.MenuItem;
import io.SesProject.service.AuthService;
import io.SesProject.view.BaseMenuScreen;
import io.SesProject.view.InventoryScreen;

import java.util.ArrayList;
import java.util.List;


public class InventoryController extends BaseController {

    public InventoryController(RpgGame game, AuthService authService) {
        super(game, authService);

        populateTestItems();
    }

    private void populateTestItems() {
        GameSession session = game.getCurrentSession();
        if (session.getP1().getInventory().isEmpty()) {
            session.getP1().addItem(new WeaponFactory("Sword").createItem());
            session.getP1().addItem(new ArmorFactory("Plate").createItem());
            session.getP1().addItem(new SkillItemFactory("Colpo Pesante").createItem());
        }
        if (session.getP2().getInventory().isEmpty()) {
            session.getP2().addItem(new SkillItemFactory("Palla di Fuoco").createItem());
            session.getP2().addItem(new SkillItemFactory("Cura").createItem());
        }
    }

    /**
     * COSTRUZIONE MENU (Command Pattern)
     * Ora passiamo 'this' (il controller) ai comandi, così possono chiamare
     * i metodi performEquip/Unequip che contengono l'audio.
     */
    public MenuComponent getInventoryTree(PlayerCharacter pc) {
        MenuComposite root = new MenuComposite("ROOT");

        // --- SEZIONE 1: EQUIPAGGIAMENTO ATTUALE ---
        MenuComposite equipSection = new MenuComposite("EQUIPAGGIATO");

        // Arma
        Item wpn = pc.getEquippedWeapon();
        String wpnText = "[Arma] " + (wpn != null ? wpn.getName() : "Vuoto");
        equipSection.add(new MenuItem(wpnText,
            wpn != null ? new UnequipItemCommand(pc, wpn, this) : null));

        // Armatura
        Item arm = pc.getEquippedArmor();
        String armText = "[Armor] " + (arm != null ? arm.getName() : "Vuoto");
        equipSection.add(new MenuItem(armText,
            arm != null ? new UnequipItemCommand(pc, arm, this) : null));

        // Skill Equipaggiate
        for (Item skill : pc.getEquippedSkills()) {
            equipSection.add(new MenuItem("[Skill] " + skill.getName(),
                new UnequipItemCommand(pc, skill, this)));
        }

        root.add(equipSection);

        // --- SEZIONE 2: ZAINO ---
        MenuComposite backpackSection = new MenuComposite("ZAINO");

        if (pc.getInventory().isEmpty()) {
            backpackSection.add(new MenuItem("- Vuoto -", null));
        } else {
            for (Item item : pc.getInventory()) {
                String txt = item.getName() + " (" + item.getStatDescription() + ")";

                // LOGICA MODIFICATA:
                // Controlliamo se l'oggetto è un PowerUp consumabile
                if (item instanceof PowerUpItem) {

                    backpackSection.add(new MenuItem(txt, new UsePowerUpCommand(this, pc, item)));
                } else {
                    backpackSection.add(new MenuItem(txt, new EquipItemCommand(this, pc, item)));
                }
            }
        }

        root.add(backpackSection);

        return root;
    }

    // --- RECEIVER METHODS (Logica + Audio) ---

    public void performEquip(PlayerCharacter pc, Item item) {
        // 1. Riproduci Suono
        game.getSystemFacade().getAudioManager().playSound("music/sfx/menu/070_Equip_10.wav" , game.getSystemFacade().getAssetManager());

        // 2. Modifica Model
        pc.equipItem(item);

        System.out.println("[INV] Oggetto equipaggiato: " + item.getName());
    }

    public void performUnequip(PlayerCharacter pc, Item item) {
        // 1. Riproduci Suono
        game.getSystemFacade().getAudioManager().playSound("music/sfx/menu/071_Unequip_01.wav" , game.getSystemFacade().getAssetManager());

        // 2. Modifica Model
        pc.unequipItem(item);

        System.out.println("[INV] Oggetto rimosso: " + item.getName());
    }

    public void usePowerUp(PlayerCharacter pc, Item powerUp) {
        // Creiamo una lista che contiene TUTTO ciò che il player indossa
        List<Item> equipaggiamentoPotenziabile = new ArrayList<>();

        if (pc.getEquippedWeapon() != null) equipaggiamentoPotenziabile.add(pc.getEquippedWeapon());
        if (pc.getEquippedArmor() != null) equipaggiamentoPotenziabile.add(pc.getEquippedArmor());
        equipaggiamentoPotenziabile.addAll(pc.getEquippedSkills());

        if (equipaggiamentoPotenziabile.isEmpty()) {
            System.out.println("Non hai nulla di equipaggiato da potenziare!");
            return;
        }

        // Ora il menu mostrerà sia la Spada che l'Armatura che le Skill
        MenuComposite choiceMenu = new MenuComposite("QUALE OGGETTO POTENZIARE?");

        for (Item oggetto : equipaggiamentoPotenziabile) {
            choiceMenu.add(new MenuItem(oggetto.getName() + " (Attuale: " + oggetto.getValue() + ")", () -> {

                // Applichiamo il bonus (funziona sia per l'attacco dell'arma che per la difesa dell'armatura)
                oggetto.setValue(oggetto.getValue() + powerUp.getValue());

                // Rimuoviamo il consumabile
                pc.getInventory().remove(powerUp);

                // IMPORTANTE: Ricalcoliamo le statistiche (se hai potenziato l'armatura, gli HP devono salire!)
                pc.recalculateStats();

                System.out.println("Potenziato " + oggetto.getName() + " a " + oggetto.getValue());
            }));
        }

        // Visualizza il menu di scelta nel pannello del giocatore corretto
        if (game.getScreen() instanceof InventoryScreen) {
            ((InventoryScreen) game.getScreen()).updateMenuRoot(choiceMenu, pc);
        }

    }

    // --- VIEW & NAVIGAZIONE ---

    @Override
    protected BaseMenuScreen createView() {
        return new InventoryScreen(this);
    }

    public void backToPause() {
        game.getSystemFacade().getAudioManager().playSound("music/sfx/menu/001_Hover_01.wav", game.getSystemFacade().getAssetManager());
        game.changeController(new PauseMenuController(game, authService));
    }

    public PlayerCharacter getP1() { return game.getCurrentSession().getP1(); }
    public PlayerCharacter getP2() { return game.getCurrentSession().getP2(); }
}
