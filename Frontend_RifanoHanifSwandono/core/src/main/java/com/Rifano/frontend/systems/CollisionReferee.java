import com.Rifano.frontend.objects.bullets.Bullet;
import com.Rifano.frontend.objects.GameObject;
import com.Rifano.frontend.objects.Player;
import com.Rifano.frontend.objects.enemies.Enemy;
import com.Rifano.frontend.objects.enemies.Fairy;
import com.Rifano.frontend.objects.enemies.Boss;
import com.Rifano.frontend.objects.items.Item;
import com.Rifano.frontend.objects.items.ItemType;
import com.Rifano.frontend.systems.EntityFactory;

import java.util.ArrayList;
import java.util.List;


public class CollisionReferee {

    public void resolveCollisions(Player player, List<GameObject> entities, BulletManager bulletManager) {
        if (player == null || entities == null || bulletManager == null) return;
        List<GameObject> newDrops = new ArrayList<>();

        for(Bullet bullet=  bulletManager.getActivePlayerBullets()){
            if(bullet.isDestroyed()) continue;

            for(GameObject entity : entities){
                if(entity instanceof Enemy enemy && !enemy.isDestroyed()){
                    if(bullet.getCoreHitbox().overlaps(enemy.getCoreHitbox())){
                        boolean defeated = enemy.takeDamage((bullet.getDamage()));
                        if (defeated){
                            player.addScore((enemy.getScoreValue()));
                            Item drop= EntityFactory.createItem(enemy.getX(), enemy.getY(), ItemType.POWER);
                            newDrops.add((drop));
                        }
                        bullet.destroy();
                    }
                }
            }
        }

        for(Bullet bullet: bulletManager.getActiveEnemyBullets){
            if(bullet.isDestroyed()) continue;
            if(!player.isDestroyed(){
                if(bullet.getCoreHitbox().overlaps(player.getCoreHitbox())){
                    player.takeDamage((bullet.getDamage()));
                    bullet.destroy();

            }

            else if (!bullet.hasBeenGrazed() && bullet.getCoreHitbox().overlaps(player.getGrazeHitbox())){
                player.addScore(50);
                }

        }
// 2. Evaluate Enemy Bullets vs Player (Core Hit vs Graze Hit)
// TODO Code for Question 4:
//     - PRIORITY 2: Graze Hit (Bullet Grazes Player)
//       - Check if the bullet has NOT been grazed yet
//         AND the bullet's Core Hitbox touches the player's Graze Hitbox.
//       - If YES:
//         * Mark the bullet as grazed.
//         * Give the graze score bonus to the player: player.addScore(50)

// 3. Evaluate Player vs Items and Physical Enemy Contact
// TODO Code for Question 5:
// - Loop through each entity in entities:
//   - If the entity is already destroyed, skip it (continue)
//
//   - CASE A: Item Collection
//     - Check whether entity is an instance of Item item
//     - Check whether the item's Core Hitbox touches the player's Core Hitbox OR Graze Hitbox.
//     - If YES: call player.collectItem(item)
//
//   - CASE B: Physical Contact with Enemy
//     - Check whether entity is an instance of Enemy enemy
//     - If the player's Core Hitbox collides with the enemy's Core Hitbox.
//     - If YES: deal 10 body collision damage to the player: player.takeDamage(10)


        entities.addAll(newDrops);
    }

}
