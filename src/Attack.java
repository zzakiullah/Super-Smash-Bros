/* Attack.java
 * Zulaikha Zakiullah
 * This class deals with characters' attacks.
 */

import java.util.*;

class Attack {
    public static final int STRIKE = 0, SHOOT = 1, RECOVER = 2;
    /* type - type of attack (can be STRIKE or SHOOT, or RECOVER)
     * shootType - type of attack involving shooting something (can be NONE, STRAIGHT, or TARGET)
     */
    private final int type, damage;
    private String name;            // name - name of attack
    /* state - state character must be in to use attack (e.g. must be jumping, crouching)
     * key - key pressed to initiate attack
     */
    private int state, key;
    private Projectile proj;	    // projs - projectiles launched (if applicable, if type is SHOOT, otherwise this is set to null)
    private Gif[] regAtk;           // attack used when not jumping
    private Gif[][] jumpAtk;        // attack used when jumping

    // ----------------- all constructors ----------------- //
    // attack is not used when jumping and projectiles are not needed
    public Attack(String name, int type, int state, Gif[] regAtk, int key, int damage) {
        this.name = name;
        this.type = type;
        this.state = state;
        this.key = key;
        this.damage = damage;
        this.regAtk = new Gif[regAtk.length];
        for (int i=0; i<regAtk.length; i++) {
            //System.out.println(regAtk[i]);
            this.regAtk[i] = regAtk[i];
        }
    }
    // attack is used when jumping and projectiles are not used
    public Attack(String name, int type, int state, Gif[][] jumpAtk, int key, int damage) {
        this.name = name;
        this.type = type;
        this.state = state;
        this.key = key;
        this.damage = damage;
        this.jumpAtk = new Gif[jumpAtk.length][jumpAtk[0].length];
        for (int i=0; i<jumpAtk.length; i++) {
            for (int j=0; j<jumpAtk[i].length; j++) {
                this.jumpAtk[i][j] = jumpAtk[i][j];
            }
        }
    }
    // attack is not used when jumping and projectiles are used
    public Attack(String name, int type, int state, Gif[] regAtk, Projectile proj, int key, int damage) {
        this.name = name;
        this.type = type;
        this.state = state;
        this.key = key;
        this.damage = damage;
        this.regAtk = new Gif[regAtk.length];
        for (int i=0; i<regAtk.length; i++) {
            this.regAtk[i] = regAtk[i];
        }
        this.proj = proj;
    }
    // attack is used when jumping and projectiles are used
    public Attack(String name, int type, int state, Gif[][] jumpAtk, Projectile proj, int key, int damage) {
        this.name = name;
        this.type = type;
        this.state = state;
        this.key = key;
        this.damage = damage;
        this.jumpAtk = new Gif[jumpAtk.length][jumpAtk[0].length];
        for (int i=0; i<jumpAtk.length; i++) {
            for (int j=0; j<jumpAtk[i].length; j++) {
                this.jumpAtk[i][j] = jumpAtk[i][j];
            }
        }
        this.proj = proj;
    }
    // ---------------------------------------------------- //

    // ------------ all getter methods ------------ //
    public int getType() {return type;}
    public int getDamage() {return new Random().nextInt(20)+1;}
    public int getState() {return state;}
    public int getKey() {return key;}

    public Gif[] getRegAtk() {return regAtk;}
    public Gif[][] getJumpAtk() {return jumpAtk;}
}
