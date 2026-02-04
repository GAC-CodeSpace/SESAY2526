package io.SesProject.service.npc;

import io.SesProject.model.game.npc.EnemyTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/*Database di nemici*/
public class Bestiary {

    private static final Map<Integer, List<EnemyTemplate>> dungeonMinions = new HashMap<>();
    private static final Map<Integer, EnemyTemplate> dungeonBosses = new HashMap<>();
    private static final Random random = new Random();

    static {
        // --- LIVELLO 1 ---
        // Minions: Solo Scheletri (magari deboli)
        dungeonMinions.put(1, Arrays.asList(
            new EnemyTemplate("Scheletro", "skeleton", 50 , 5)
        ));
        // Boss: Scheletro Gigante (versione più debole)
        dungeonBosses.put(1, new EnemyTemplate("Scheletro Gigante", "giant_skeleton", 200 , 20));

        // --- LIVELLO 2 ---
        // Minions: Scheletri più forti
        dungeonMinions.put(2, Arrays.asList(
            new EnemyTemplate("Scheletro Guerriero", "skeleton_warrior", 80 , 5)
        ));
        // Boss: Scheletro Gigante (versione più forte)
        dungeonBosses.put(2, new EnemyTemplate("Scheletro Gigante Antico", "giant_skeleton", 400 , 10));
    }

    public static EnemyTemplate getMinionForLevel(int level) {
        List<EnemyTemplate> list = dungeonMinions.get(level);
        if (list == null || list.isEmpty()) return new EnemyTemplate("Scheletro Base", "skeleton", 30 , 5);
        return list.get(random.nextInt(list.size()));
    }

    public static EnemyTemplate getBossForLevel(int level) {
        EnemyTemplate boss = dungeonBosses.get(level);
        if (boss == null) return new EnemyTemplate("Scheletro Gigante", "giant_skeleton", 150 , 5);
        return boss;
    }
}
