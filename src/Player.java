/* Player.java
 * Zulaikha Zakiullah
 * This class deals with all the players and their interactions with the environment (platforms) and movement.
 */

class Player extends ObjOnScreen {
    public static int IDLING = 10, RUNNING = 11, JUMPING = 12, CROUCHING = 13, INJURED = 14;
    private String origin;		// origin - video game series character is from
    /* x - x position of player relative to center; y - y position relative to bottom
     * prevX, prevY - previous position of player
     * vx, vy - velocity of character in x and y direction
     * initVY - initial velocity in y direction (when first jumping); maxJumpTime - max time character can travel up (when jumping)
     * jumpIndex - index of frame in jump gif; dist - distance player will travel jumping up OR minimum distance between player and a platform
     * atkIndex - index of frame in attack gif; injuredIndex - index of frame in attack gif
     * stock - number of lives; damage - amount of damage as a % (though this can range from 0 to 999)
     * prevState - previous state of character; state - state character is in (idling, running, etc.)
     */
    private int prevX, prevY, vx, vy, initVY, jumpTime, maxJumpTime, jumpIndex, atkIndex, injuredIndex, dist, stock, damage, prevState, state;
    // attacking - character is attacking; defending - character is defending; dropping - dropping from a platform
    private boolean attacking, defending, dropping;
    // all sprite sequences
    private Gif[] idle = new Gif[2];
    private Gif[] run = new Gif[2];
    private Gif[][] jump = new Gif[2][2];
    private Gif[] crouch = new Gif[2];
    private Gif[] injured = new Gif[2];

    private Frame current;

    // array of attacks held as objects of the Attack class
    private Attack[] attacks;
    private Attack attackInUse; // attack being used; null if not attacking

    private Platform landing, prevLanding;   // landing - platform player will land on if jumping; prevLanding - previous landing

    public Player(String name, String origin, int x, int y, int vx, int initVY, int dir, Gif[] idle, Gif[] run, Gif[][] jump, Gif[] crouch, Gif[] injured, Attack[] attacks, int maxJumpTime) {
        super(name, x, y, dir);
        this.origin = origin;
        for (int i=0; i<2; i++) {
            this.idle[i] = idle[i];
            this.run[i] = run[i];
            this.crouch[i] = crouch[i];
            this.injured[i] = injured[i];
            for (int j=0; j<2; j++) {
                this.jump[i][j] = jump[i][j];
            }
        }
        this.attacks = new Attack[attacks.length];
        for (int i=0; i<attacks.length; i++) {
            this.attacks[i] = attacks[i];
        }
        this.prevX = x;
        this.prevY = y;
        this.vx = vx;
        this.initVY = initVY;
        this.maxJumpTime = maxJumpTime;
        stock = 6;
        vy = jumpTime = jumpIndex = atkIndex = damage = 0;
        attacking = defending = dropping = false;
        prevState = state = IDLING;
        current = idle[dir].getFrameAt(0);
    }

    // reset - resets character
    public void reset() {
        prevX = getX();
        prevY = getY();
        stock = 6;
        vy = jumpTime = jumpIndex = atkIndex = damage = 0;
        attacking = defending = false;
        prevState = state = IDLING;
        current = idle[getDir()].getFrameAt(0);
    }

    // ------------ all getter methods ------------ //
    public String getOrigin() {return origin;}
    public Attack[] getAttacks() {return attacks;}
    public int getPrevX() {return prevX;}
    public int getPrevY() {return prevY;}
    public int getVX() {return vx;}
    public int getVY() {return vy;}
    public int getJumpTime() {return jumpTime;}
    public int getMaxJumpTime() {return maxJumpTime;}
    public int getStock() {return stock;}
    public int getDamage() {return damage;}
    public boolean isAttacking() {return attacking;}
    public boolean isDefending() {return defending;}
    public boolean isDropping() {return dropping;}
    public int getState() {return state;}
    public Frame getCurrent() {return current;}
    public Gif[][] getJump() {return jump;}
    public Gif[] getInjured() {return injured;}
    public int getJumpIndex() {return jumpIndex;}
    public Attack getAttackInUse() {return attackInUse;}
    public int getAtkIndex() {return atkIndex;}
    public int getInjuredIndex() {return injuredIndex;}
    public Platform getPrevLanding() {return prevLanding;}
    public Platform getLanding() {return landing;}

    // ------------ all setter methods ------------ //
    public void setPrevPos(int preX, int preY) {prevX = preX; prevY = preY;}
    public void setVX(int v) {vx = v;}
    public void setVY(int v) {vy = v;}
    public void setJumpTime(int j) {jumpTime = j;}
    public void setStock(int s) {stock = s;}
    public void setDamage(int d) {
        damage = (d >= 0 && d <= 999) ? d : damage;
    }
    public void setAttacking(boolean a) {attacking = a;}
    public void setDefending(boolean d) {defending = d;}
    public void setDropping(boolean d) {dropping = d;}
    public void setState(int s) {state = (s == IDLING || s == RUNNING || s == JUMPING  || s == CROUCHING || s == INJURED) ? s : state;}
    public void setJumpIndex(int j) {jumpIndex = j;}
    public void setLanding(Platform land) {landing = land;}
    public void setAttackInUse(Attack atk) {attackInUse = atk;}
    public void setAtkIndex(int a) {atkIndex = a;}
    public void setInjuredIndex(int i) {injuredIndex = i;}
    public void setPrevLanding(Platform p) {prevLanding = p;}
    public void setCurrent(int elapsedTime) {
        if (state == IDLING && attacking == false) {
            current = idle[getDir()].getFrame(elapsedTime);
        }
        else if (state == RUNNING && attacking == false) {
            current = run[getDir()].getFrame(elapsedTime);
        }
        else if (state == CROUCHING && attacking == false) {
            current = crouch[getDir()].getFrame(elapsedTime);
        }
        else if (attacking) {
            current = attackInUse.getRegAtk()[getDir()].getFrame(elapsedTime);
        }
    }
    public void setCurrentAtIndex(int ind) {
        if (state == IDLING && attacking == false) {
            current = idle[getDir()].getFrameAt(ind);
        }
        else if (state == IDLING && attacking) {

        }
        else if (state == RUNNING && attacking == false) {
            current = run[getDir()].getFrameAt(ind);
        }
        else if (state == RUNNING && attacking) {

        }
        //else if (state == JUMPING && attacking == false) {return jump[getDir()].getFrame(time);}
        else if (state == JUMPING && attacking) {

        }
        else if (state == CROUCHING && attacking == false) {
            current = crouch[getDir()].getFrameAt(ind);
        }
        else if (state == CROUCHING && attacking) {

        }
    }

    public void setCurrent(Frame frame) {current = frame != null ? frame : current;}

    // updateJumpIndex - updates index of jump frame to be displayed
    public void updateJumpIndex(Scene scene) {
        if (vy < 0) {   // jumping up
            if (jumpTime == 10) {
                // distance player will travel before reaching vy of zero
                dist = (int)Math.abs((0-Math.pow(vy, 2))/(2*scene.GRAV) + vy*maxJumpTime);
            }
            int distPerFrame = dist/jump[0][0].size();      // distance travelled per frame
            jumpIndex = distPerFrame != 0 ? (landing.minY()-getY())/distPerFrame : jump[0][0].size()-1; // index of jump frame
            if (jumpIndex >= jump[0][0].size()) {
                jumpIndex = jump[0][0].size()-1;
            }
        }
        else {          // falling down
            if (vy-scene.GRAV < 0) {
                // find platform player is directly above
                landing = scene.getPlatforms()[0];
                dist = scene.getPlatforms()[0].minY()-getY();
                for (Platform platform : scene.getPlatforms()) {
                    if (platform.minY()
                            -getY() < dist && getX() >= platform.minX() && getX() <= platform.maxX()) {
                        landing = platform;
                        dist = platform.minY()-getY();
                    }
                }
            }
            int distPerFrame = Math.abs(dist/jump[0][1].size());   // distance travelled per frame
            // dist-(landing.minY()-getY()) - distance player has travelled downward in air
            if (distPerFrame != 0) {
                jumpIndex = (dist-(landing.minY()-getY()) > 0 ? dist-(landing.minY()-getY()) : 0)/distPerFrame;   // index of jump frame
                if (jumpIndex >= jump[1][0].size()) {
                    jumpIndex = jump[1][0].size()-1;
                }
            }
            else {
                jumpIndex = 0;
            }
        }
    }

    // updateHitbox - updates hitbox based on player's position
    public void updateHitbox() {
        int w = current.getIcon().getIconWidth(), h = current.getIcon().getIconHeight();
        current.getHitbox().setBounds(getX()-w/2, getY()-h, w, h);
    }

    // hasAttackWith - checks if character has an attack with the condition specified, e.g. has attack with the condition that character is running
    public boolean hasAttackWith(int condition) {
        for (Attack atk : attacks) {
            if (atk.getState() == condition) {
                return true;
            }
        }
        return false;
    }
    // getAttackWith - gets attack with the specified condition, e.g. running
    public Attack[] getAttacksWith(int condition) {
        Attack[] atks;
        int n = 0;
        for (Attack atk : attacks) {
            if (atk.getState() == condition) {
                n++;
            }
        }
        atks = new Attack[n];
        n = 0;
        for (Attack atk : attacks) {
            if (atk.getState() == condition) {
                atks[n] = atk;
                n++;
            }
        }
        return atks;
    }

    // getAttackWithKey - returns whether player has attack that is activated by pressing specified key passed in
    public boolean hasAttackWithKey(int key) {
        for (Attack atk : attacks) {
            if (atk.getKey() == key) {
                return true;
            }
        }
        return false;
    }
    // getAttackWithKey - returns attack that is activated by pressing specified key passed in
    public Attack getAttackWithKey(int key) {
        for (Attack atk : attacks) {
            if (atk.getKey() == key) {
                return atk;
            }
        }
        return null;
    }

    // isOnPlatform - checks if player is on platform
    public boolean isOnPlatform(Scene scene) {
        for (Platform platform : scene.getPlatforms()) {
            if (platform.isRectangle()) {

            }
            else {

            }
        }
        return false;
    }

    // getPlatformFrom - returns platform that player is standing on
    public Platform getPlatformFrom(Scene scene) {
        for (Platform platform : scene.getPlatforms()) {
            if (isOn(platform)) {
                return platform;
            }
        }
        return null;
    }

    // isOn - checks if player is on specified platform
    public boolean isOn(Platform platform) {
        return getY() == platform.minY() && getX()+current.getHitbox().getWidth()/2 >= platform.minX() && getX()-current.getHitbox().getWidth()/2 <= platform.maxX();
    }

    // hasPlatformBelow - checks if there is platform directly below player
    public boolean hasPlatformBelow(Scene scene) {
        for (Platform platform : scene.getPlatforms()) {
            if (platform.minY() > getY() && platform.minX() <= getX()+current.getHitbox().getWidth()/2 && platform.maxX() >= getX()-current.getHitbox().getWidth()/2) {
                return true;
            }
        }
        return false;
    }

    // collide - checks if player has hit another player object
    public boolean collide(Player player) {
        return current.getHitbox().intersects(player.getCurrent().getHitbox());
    }

    // returns a copy of the Player object
    public Player clone() {
        return new Player(getName(), origin, getX(), getY(), vx, initVY, getDir(), idle, run, jump, crouch, injured, attacks, maxJumpTime);
    }
}
