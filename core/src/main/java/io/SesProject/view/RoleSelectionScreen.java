package io.SesProject.view;


import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import io.SesProject.controller.RoleSelectionController;
import io.SesProject.model.menu.MenuComponent;

    public class RoleSelectionScreen extends BaseMenuScreen {
        private RoleSelectionController controller;
        private MenuComponent menuRoot;
        private Label statusLabel;

        public RoleSelectionScreen(RoleSelectionController controller, MenuComponent menuRoot) {
            super();
            this.controller = controller;
            this.menuRoot = menuRoot;
            buildUI();
        }

        @Override
        protected void buildUI() {
            if (menuRoot == null) return;
            rootTable.clear();

            Label titleLabel = new Label("CONFIGURAZIONE PARTITA", skin);
            rootTable.add(titleLabel).padBottom(30).row();

            // Label dinamica che mostra chi fa cosa
            statusLabel = new Label("", skin);
            updateStatusText();
            rootTable.add(statusLabel).padBottom(40).row();

            for (MenuComponent child : menuRoot.getChildren()) {
                TextButton btn = new TextButton(child.getName(), skin);
                btn.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        child.select();
                        updateStatusText(); // Aggiorna il testo se i ruoli cambiano
                    }
                });
                rootTable.add(btn).width(300).height(60).padBottom(15).row();
            }
        }

        private void updateStatusText() {
            if (controller.isP1Tank()) {
                statusLabel.setText("P1 (WASD): TANK | P2 (FRECCE): MAGO");
            } else {
                statusLabel.setText("P1 (WASD): MAGO | P2 (FRECCE): TANK");
            }
        }
    }

