package io.SesProject.controller;

import io.SesProject.RpgGame;
import io.SesProject.controller.command.InventoryCommand.EquipItemCommand;
import io.SesProject.controller.command.InventoryCommand.UnequipItemCommand;
import io.SesProject.model.GameSession;
import io.SesProject.model.PlayerCharacter;
import io.SesProject.model.game.item.factory.ArmorFactory;
import io.SesProject.model.game.item.factory.Item;
import io.SesProject.model.game.item.factory.SkillItemFactory;
import io.SesProject.model.game.item.factory.WeaponFactory;
import io.SesProject.model.menu.MenuComponent;
import io.SesProject.model.menu.MenuComposite;
import io.SesProject.model.menu.MenuItem;
import io.SesProject.service.AuthService;
import io.SesProject.view.BaseMenuScreen;
import io.SesProject.view.InventoryScreen;

public class InventoryController extends BaseController {

    public InventoryController(RpgGame game, AuthService authService) {
        super(game, authService);
        // Popola per testare (se vuoto)
        populateTestItems();
    }

    private void populateTestItems() {
        GameSession session = game.getCurrentSession();
        // Aggiungiamo oggetti se l'inventario è vuoto per vedere la UI
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

    public MenuComponent getInventoryTree(PlayerCharacter pc) {
        MenuComposite root = new MenuComposite("ROOT");

        // --- SEZIONE 1: EQUIPAGGIAMENTO ATTUALE (Composite) ---
        MenuComposite equipSection = new MenuComposite("EQUIPAGGIATO");

        // Arma
        Item wpn = pc.getEquippedWeapon();
        String wpnText = "[Arma] " + (wpn != null ? wpn.getName() : "Vuoto");
        // Se c'è un oggetto, il comando è Unequip. Se è vuoto, comando null (non cliccabile).
        equipSection.add(new MenuItem(wpnText, wpn != null ? new UnequipItemCommand(pc, wpn) : null));

        // Armatura
        Item arm = pc.getEquippedArmor();
        String armText = "[Armor] " + (arm != null ? arm.getName() : "Vuoto");
        equipSection.add(new MenuItem(armText, arm != null ? new UnequipItemCommand(pc, arm) : null));

        // Skill Equipaggiate
        for (Item skill : pc.getEquippedSkills()) {
            equipSection.add(new MenuItem("[Skill] " + skill.getName(), new UnequipItemCommand(pc, skill)));
        }

        root.add(equipSection);

        // --- SEZIONE 2: ZAINO (Composite) ---
        MenuComposite backpackSection = new MenuComposite("ZAINO");

        if (pc.getInventory().isEmpty()) {
            backpackSection.add(new MenuItem("- Vuoto -", null));
        } else {
            for (Item item : pc.getInventory()) {
                // Formattiamo il testo qui nel controller
                String txt = item.getName() + " (" + item.getStatDescription() + ")";
                // Qui il comando è Equip
                backpackSection.add(new MenuItem(txt, new EquipItemCommand(pc, item)));
            }
        }

        root.add(backpackSection);

        return root;
    }

    @Override
    protected BaseMenuScreen createView() {
        return new InventoryScreen(this);
    }

    public void backToPause() {
        game.changeController(new PauseMenuController(game, authService));
    }

    public PlayerCharacter getP1() { return game.getCurrentSession().getP1(); }
    public PlayerCharacter getP2() { return game.getCurrentSession().getP2(); }
}
