/* Computer Player.java
 * Zulaikha Zakiullah
 * This class deals with the AI in single player.
 */

import java.awt.*;
import java.util.*;

class ComputerPlayer {
    private Player player;   // character CP is playing as
    private Image cursor;    // cursor used to represent computer player
    private int cx, cy;      // position of CP's "cursor"
    private Player target;   // player that CP intends to attack
    private Platform targetPlatform;    // platform player is aiming to land on

    public ComputerPlayer(Image cursor) {
        this.cursor = cursor;
    }

    public Player getPlayer() {return player;}
    public void setPlayer(Player ch) {player = ch;}

    // chooseTarget - randomly choose which player to target
    public void chooseTarget(Player[] players) {
        Random rand = new Random();
        target = players[rand.nextInt(players.length)];
    }

    // decideMove - decides what move for player to do
    public void decideMove(Scene scene) {
        // try to move closer to target, if within attacking range then attack
        if (target != null) {
            // if player is injured
            if (player.getState() == Player.INJURED) {
                int dir = player.getDir() == ObjOnScreen.LEFT ? 1 : -1;
                player.setX(player.getX()+player.getVX()*dir);
            }
            // if target is to left of player, run left
            else if (target.getX() < player.getX() && !(target.getCurrent().getHitbox().intersects(player.getCurrent().getHitbox()))) {
                player.setDir(ObjOnScreen.LEFT);
                player.setState(Player.RUNNING);
                player.setX(player.getX()-player.getVX());
            }
            // if target is to right of player, run right
            else if (target.getX() > player.getX() && !(target.getCurrent().getHitbox().intersects(player.getCurrent().getHitbox()))) {
                player.setDir(ObjOnScreen.RIGHT);
                player.setState(Player.RUNNING);
                player.setX(player.getX()+player.getVX());
            }
            // if target and player are in same place (relative to centre)
            else {
                player.setDir(player.getDir());
                player.setDropping(false);
                player.setState(Player.IDLING);
            }
            // if target is above player, jump up
            if (target.getY() < player.getY() && (player.getState() != Player.JUMPING ||  player.getState() == Player.JUMPING && player.getJumpTime() < player.getMaxJumpTime())) {
                if (player.getState() != Player.JUMPING) {
                    for (Platform platform : scene.getPlatforms()) {
                        if (player.isOn(platform)) {
                            player.setLanding(platform);
                        }
                    }
                }
                player.setState(Player.JUMPING);
                player.setJumpTime(player.getJumpTime()+10);
                player.setVY(-18);
                player.setY(player.getY()+player.getVY());
            }
            // down arc of jump
            else if (player.getState() == Player.JUMPING && (player.getJumpTime() >= player.getMaxJumpTime() || target.getY() >= player.getY() && player.getJumpTime() < player.getMaxJumpTime())) {
                if (player.getVY()-scene.GRAV < 0) {
                    player.setJumpIndex(0);
                }
                player.setJumpTime(player.getJumpTime()+10);
                player.setVY((int)(player.getVY()+scene.GRAV));
                player.setY(player.getY()+player.getVY());

            }
            // if target is below player and there is a platform below
            else if (target.getY() > player.getY() && player.hasPlatformBelow(scene) && player.getState() != Player.JUMPING) {
                player.setDropping(true);
                player.setPrevLanding(player.getPlatformFrom(scene));
                player.setState(Player.JUMPING);
                player.setJumpIndex(0);
                player.setJumpTime(player.getJumpTime()+10);
                player.setVY((int)(player.getVY()+scene.GRAV));
                player.setY(player.getY()+player.getVY());
            }
            // if target and player are level
            else {
                if (player.getPrevX() != player.getX()) {
                    player.setState(Player.RUNNING);
                }
                else {
                    player.setState(Player.IDLING);
                }
            }
            // if player and target make contact, attack
            if (player.collide(target)) {
                if (new Random().nextInt() > 0.75 && !player.isAttacking()) {
                    chooseAttack();
                }
            }
        }
    }

    // chooseAttack - decide which attack to use based on what state player is in
    public void chooseAttack() {
        if (player.hasAttackWith(player.getState())) {
            player.setAttacking(true);
            Attack[] atks = player.getAttacksWith(player.getState());
            player.setAttackInUse(atks[new Random().nextInt(atks.length)]);
        }
    }

    public Platform platformTargetIsOn(Scene scene) {
        for (Platform platform : scene.getPlatforms()) {
            if (target.getY() == platform.minY() && target.getX()+target.getCurrent().getHitbox().getWidth()/2 >= platform.minX() && target.getX()-target.getCurrent().getHitbox().getWidth()/2 <= platform.maxX()) {
                return platform;
            }
        }
        return null;
    }
}
