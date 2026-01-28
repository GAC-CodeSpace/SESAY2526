package io.SesProject.service.npc;

import io.SesProject.model.game.npc.EnemyTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/*Database di nemici*/
public class Bestiary {

    // -- Lista possibili nemici --
    private static final Map<Integer, List<EnemyTemplate>> dungeonMinions = new HashMap<>();

    //-- lista di Boss di fine livello --
    private static final Map<Integer, EnemyTemplate> dungeonBosses = new HashMap<>();

    private static final Random random = new Random();

    static {
        // --- LIVELLO 1: Fogne della Città ---
        dungeonMinions.put(1, Arrays.asList(
            new EnemyTemplate("Ratto Gigante", "rat_sprite", 200),
            new EnemyTemplate("Slime di Scarto", "slime_green_sprite", 30)
        ));
        dungeonBosses.put(1, new EnemyTemplate("Re dei Ratti", "rat_king_sprite", 500));

        // --- LIVELLO 2: Cripta Antica ---
        dungeonMinions.put(2, Arrays.asList(
            new EnemyTemplate("Scheletro", "skeleton_sprite", 200),
            new EnemyTemplate("Zombie", "zombie_sprite", 60)
        ));
        dungeonBosses.put(2, new EnemyTemplate("Negromante", "necromancer_sprite", 600));

        // --- LIVELLO 3: Vulcano ---
        dungeonMinions.put(3, Arrays.asList(
            new EnemyTemplate("Diavoletto", "imp_sprite", 200),
            new EnemyTemplate("Golem di Magma", "magma_golem_sprite", 300)
        ));
        dungeonBosses.put(3, new EnemyTemplate("Signore del Fuoco", "fire_lord_sprite", 600));
    }

    /**
     * Restituisce un nemico comune casuale per il dungeon del livello specificato.
     */
    public static EnemyTemplate getMinionForLevel(int level) {
        List<EnemyTemplate> list = dungeonMinions.get(level);
        if (list == null || list.isEmpty()) return new EnemyTemplate("Blob", "blob", 10);
        return list.get(random.nextInt(list.size()));
    }

    /**
     * Restituisce il Boss specifico per il livello.
     */
    public static EnemyTemplate getBossForLevel(int level) {
        EnemyTemplate boss = dungeonBosses.get(level);
        if (boss == null) return new EnemyTemplate("Boss Generico", "boss_placeholder", 100);
        return boss;
    }
}
