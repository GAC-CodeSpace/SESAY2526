package io.SesProject.controller;


import io.SesProject.RpgGame;
import io.SesProject.model.GameSession;
import io.SesProject.model.memento.Memento;
import io.SesProject.model.menu.MenuComponent;
import io.SesProject.model.menu.MenuComposite;
import io.SesProject.model.menu.MenuItem;
import io.SesProject.service.AuthService;
import io.SesProject.service.SystemFacade;
import io.SesProject.view.BaseMenuScreen;
import io.SesProject.view.RoleSelectionScreen;

public class RoleSelectionController extends BaseController {
    private boolean isP1Tank = true;

    public RoleSelectionController(RpgGame game, AuthService authService) {
        super(game, authService);
    }

    @Override
    protected BaseMenuScreen createView() {
        return new RoleSelectionScreen(this, buildMenuTree());
    }

    private MenuComponent buildMenuTree() {
        MenuComposite root = new MenuComposite("SELEZIONE RUOLI");

        // Usiamo le lambda per definire l'azione direttamente qui
        root.add(new MenuItem("SCAMBIA RUOLI", () -> {
            this.isP1Tank = !this.isP1Tank;
            System.out.println("[LOG] Switch ruoli: P1 Tank = " + isP1Tank);
        }));

        root.add(new MenuItem("CONFERMA E CREA", () -> {
            confirmAndSave();
        }));

        root.add(new MenuItem("INDIETRO", () -> {
            game.changeController(new MainMenuController(game, authService));
        }));

        return root;
    }

    private void confirmAndSave() {
        System.out.println("[CMD] Creazione sessione con ruoli selezionati...");
        SystemFacade facade = game.getSystemFacade();
        String currentProfile = game.getCurrentUser().getUsername();

        // Passiamo isP1Tank al costruttore della sessione
        GameSession initialSession = new GameSession(this.isP1Tank);

        // Il Memento salverà i PlayerCharacter già configurati con la classe corretta
        Memento snapshot = initialSession.save();

        int newSlotId = facade.getSaveService().createNewSaveSlot(snapshot, currentProfile);

        if (newSlotId != -1) {
            game.changeController(new MainMenuController(game, authService));
        }
    }

    public boolean isP1Tank() { return isP1Tank; }
}
