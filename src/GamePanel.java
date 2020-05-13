/* GamePanel.java
 * Zulaikha Zakiullah
 * This class deals with the game itself.
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;

class GamePanel extends JPanel {
    private boolean[] keys;	        // array of booleans indicating which keys are being pressed
    private Scene scene;            // scene where players battle
    private Gif countdown;          // gif representing the countdown when first starting a game
    private Image[] arrows;         // player arrows that hover over each character during the battle
    private int time, timeLeft, initTime;     // time - total time elapsed; timeLeft - time remaining in seconds; initTime - initial time left
    private Player player;          // player - user's player
    private ComputerPlayer[] cpus;  // computer players
    private boolean starting;       // if round has just started
    private double volume;          // volume of sound/music

    // faces - array to display stats; timeNums - numbers used to display time left; damageNums - numbers sed to display player damages
    private Image[] faces, timeNums, damageNums;

    public GamePanel() {
        keys = new boolean[KeyEvent.KEY_LAST+1];
        time = 0;
        starting = false;
    }

    // ------------ all getter methods ------------ //
    public int getTimeLeft() {return timeLeft;}
    public Player getPlayer() {return player;}
    public ComputerPlayer[] getCPUs() {return cpus;}
    public Scene getScene() {return scene;}

    // ------------ all setter methods ------------ //
    public void setKey(int i, boolean b) {	// set which keys are being pressed
        keys[i] = b;
    }
    public void setArrows(Image[] arrows) {
        this.arrows = new Image[arrows.length];
        for (int i=0; i<arrows.length; i++) {
            this.arrows[i] = arrows[i];
        }
    }
    public void setScene(Scene s) {scene = s;}
    public void setCountdown(Gif c) {countdown = c;}
    public void setTimeLeft(int t) {timeLeft = initTime = t;}
    public void setPlayer(Player ch) {
        player = ch;
        player.setState(Player.IDLING);
    }
    public void setCPUs(ComputerPlayer[] cpus) {
        this.cpus = new ComputerPlayer[cpus.length];
        for (int i=0; i<cpus.length; i++) {
            this.cpus[i] = cpus[i];
            this.cpus[i].getPlayer().setState(Player.IDLING);
        }
    }
    public void setStarting(boolean s) {starting = s;}
    public void setTimeNumbers(Image[] numbers) {
        this.timeNums = new Image[numbers.length];
        for (int i=0; i<numbers.length; i++) {
            this.timeNums[i] = numbers[i];
        }
    }
    public void setDamageNumbers(Image[] numbers) {
        this.damageNums = new Image[numbers.length];
        for (int i=0; i<numbers.length; i++) {
            this.damageNums[i] = numbers[i];
        }
    }
    public void setStatFaces(Image[] faces) {
        this.faces = new Image[faces.length];
        for (int i=0; i<faces.length; i++) {
            this.faces[i] = faces[i];
        }
    }
    public void setVolume(double v) {volume = v;}

    // reset - resets game
    public void reset() {
        starting = true;
        scene.getTrackPlaying().stop();
        time = 0;
        timeLeft = initTime;
        player.reset();
        for (ComputerPlayer cpu : cpus) {
            cpu.getPlayer().reset();
        }
    }

    public void checkKeys() {
        player.setPrevPos(player.getX(), player.getY());
        // --------------------- player movements --------------------- //
        if (keys[KeyEvent.VK_RIGHT]) {
            player.setDir(ObjOnScreen.RIGHT);
            if (keys[KeyEvent.VK_DOWN] == false) {
                player.setX(player.getX()+player.getVX());
                if (player.getState() != Player.JUMPING) {
                    player.setState(Player.RUNNING);
                }
            }
        }
        else if (keys[KeyEvent.VK_LEFT]) {
            player.setDir(ObjOnScreen.LEFT);
            if (keys[KeyEvent.VK_DOWN] == false) {
                player.setX(player.getX()-player.getVX());
                if (player.getState() != Player.JUMPING) {
                    player.setState(Player.RUNNING);
                }
            }
        }
        // crouching
        if (keys[KeyEvent.VK_DOWN] && player.getState() != Player.JUMPING) {
            player.setState(Player.CROUCHING);
        }
        // jumping from ground or holding "UP" key while in air
        if (keys[KeyEvent.VK_UP] && (player.getState() != Player.JUMPING ||  player.getState() == Player.JUMPING && player.getJumpTime() < player.getMaxJumpTime())) {
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
        // falling without holding "DOWN" key
        else if (player.getState() == Player.JUMPING && (player.getJumpTime() >= player.getMaxJumpTime() || keys[KeyEvent.VK_UP] == false && player.getJumpTime() < player.getMaxJumpTime())) {
            if (player.getVY()-scene.GRAV < 0) {
                player.setJumpIndex(0);
            }
            player.setJumpTime(player.getJumpTime()+10);
            player.setVY((int)(player.getVY()+scene.GRAV));
            player.setY(player.getY()+player.getVY());
        }
        // falling while holding "DOWN" key
        if (keys[KeyEvent.VK_DOWN] && player.getState() == Player.JUMPING) {
            player.setJumpTime(player.getMaxJumpTime());
            player.setVY(player.getVY()+5);
        }
        // idling
        if (keys[KeyEvent.VK_LEFT] == false && keys[KeyEvent.VK_RIGHT] == false && keys[KeyEvent.VK_UP] == false && keys[KeyEvent.VK_DOWN] == false && player.getState() != Player.JUMPING) {
            player.setState(Player.IDLING);
        }

        // --------------------- player attacking --------------------- //
        if (player.isAttacking() == false && (keys[KeyEvent.VK_F] && player.hasAttackWithKey(KeyEvent.VK_F) && player.getAttackWithKey(KeyEvent.VK_F).getState() == player.getState() || keys[KeyEvent.VK_D] && player.hasAttackWithKey(KeyEvent.VK_D) && player.getAttackWithKey(KeyEvent.VK_D).getState() == player.getState() || keys[KeyEvent.VK_S] && player.hasAttackWithKey(KeyEvent.VK_S) && player.getAttackWithKey(KeyEvent.VK_S).getState() == player.getState() || keys[KeyEvent.VK_A] && player.hasAttackWithKey(KeyEvent.VK_A) && player.getAttackWithKey(KeyEvent.VK_A).getState() == player.getState())) {
            int key = keys[KeyEvent.VK_F] ? KeyEvent.VK_F : keys[KeyEvent.VK_D] ? KeyEvent.VK_D : keys[KeyEvent.VK_S] ? KeyEvent.VK_S : KeyEvent.VK_A;
            player.setAttackInUse(player.getAttackWithKey(key));
            player.setAttacking(true);
        }

        // ------------ player pressing SPACEBAR to get off platform ------------ //
        if (!player.isDropping() && player.getState() != Player.JUMPING && keys[KeyEvent.VK_SPACE]) {
            player.setDropping(true);
            player.setPrevLanding(player.getPlatformFrom(scene));
            player.setState(Player.JUMPING);
            player.setJumpIndex(0);
            player.setJumpTime(player.getMaxJumpTime());
            player.setVY((int)scene.GRAV);
            player.setY(player.getY()+player.getVY());
        }
    }

    // checkCollisions - checks Player collisions with Platform objects
    public void checkCollisions() {
        // collisions between player and platform
        for (Platform platform : scene.getPlatforms()) {
            if (platform.isRectangle()) {
                if ((!player.isDropping() || player.isDropping() && platform != player.getPrevLanding()) && player.getPrevY() < player.getY() && player.getY() >= platform.minY() && player.getY() <= platform.maxY() && player.getX()+player.getCurrent().getHitbox().getWidth()/2 >= platform.minX() && player.getX()-player.getCurrent().getHitbox().getWidth()/2 <= platform.maxX() && player.getState() == Player.JUMPING) {
                    player.setDropping(false);
                    player.setY(platform.minY());
                    player.setVY(0);
                    player.setJumpTime(0);
                    player.setJumpIndex(0);
                    player.setState(Player.IDLING);
                }
                else if (player.getState() != Player.JUMPING && player.getPrevX()+player.getCurrent().getHitbox().getWidth()/2 >= platform.minX() && player.getPrevX()-player.getCurrent().getHitbox().getWidth()/2 <= platform.maxX() && (player.getX()+player.getCurrent().getHitbox().getWidth()/2 <= platform.minX() || player.getX()-player.getCurrent().getHitbox().getWidth()/2 >= platform.maxX())) {
                    player.setState(Player.JUMPING);
                    player.setJumpIndex(0);
                    player.setJumpTime(player.getMaxJumpTime());
                    player.setVY(0);
                }
                for (ComputerPlayer cp : cpus) {
                    if ((!cp.getPlayer().isDropping() || cp.getPlayer().isDropping() && platform != cp.getPlayer().getPrevLanding()) && cp.getPlayer().getPrevY() < cp.getPlayer().getPrevY() && cp.getPlayer().getY() >= platform.minY() && cp.getPlayer().getY() <= platform.maxY() && cp.getPlayer().getX()+cp.getPlayer().getCurrent().getHitbox().getWidth()/2 >= platform.minX() && cp.getPlayer().getX()-cp.getPlayer().getCurrent().getHitbox().getWidth()/2 <= platform.maxX() && cp.getPlayer().getState() == Player.JUMPING) {
                        cp.getPlayer().setDropping(false);
                        cp.getPlayer().setY(platform.minY());
                        cp.getPlayer().setVY(0);
                        cp.getPlayer().setJumpTime(0);
                        cp.getPlayer().setJumpIndex(0);
                        cp.getPlayer().setState(Player.IDLING);
                    }
                    else if (!cp.getPlayer().isDropping() && cp.getPlayer().getPrevX()+cp.getPlayer().getCurrent().getHitbox().getWidth()/2 >= platform.minX() && cp.getPlayer().getPrevX()-cp.getPlayer().getCurrent().getHitbox().getWidth()/2 <= platform.maxX() && (cp.getPlayer().getX()-cp.getPlayer().getCurrent().getHitbox().getWidth()/2 <= platform.minX() || cp.getPlayer().getX()+cp.getPlayer().getCurrent().getHitbox().getWidth()/2 >= platform.maxX())) {
                        cp.getPlayer().setState(Player.JUMPING);
                        cp.getPlayer().setJumpIndex(0);
                        cp.getPlayer().setJumpTime(player.getMaxJumpTime());
                        cp.getPlayer().setVY(0);
                    }
                }
            }
            else {

            }
        }
        // collisions between player and player
        if (player.isAttacking()) {
            for (ComputerPlayer cp : cpus) {
                // if player and target hitboxes collide
                if (player.collide(cp.getPlayer()) && cp.getPlayer().getState() != Player.INJURED) {
                    // calculate damage
                    //int d = cp.getPlayer().getDamage()+player.getAttackInUse().getDamage() > 999 ? 999 : cp.getPlayer().getDamage()+player.getAttackInUse().getDamage();
                    int d = cp.getPlayer().getDamage()+new Random().nextInt(10)+1;
                    cp.getPlayer().setDamage(d);
                    if (!cp.getPlayer().isAttacking() && cp.getPlayer().getState() != Player.JUMPING) {
                        cp.getPlayer().setState(Player.INJURED);
                    }
                }
            }
        }
        for (ComputerPlayer cp : cpus) {
            // computer player attacking real player
            if (cp.getPlayer().collide(player) && cp.getPlayer().isAttacking() && player.getState() != Player.INJURED) {
                //int d = player.getDamage()+cp.getPlayer().getAttackInUse().getDamage() > 999 ? 999 : player.getDamage()+cp.getPlayer().getAttackInUse().getDamage();
                int d = player.getDamage()+new Random().nextInt(10)+1;
                player.setDamage(d);
                if (!player.isAttacking() && player.getState() != Player.JUMPING) {
                    player.setState(Player.INJURED);
                }
            }
            // computer player attacking another computer player
            for (ComputerPlayer c : cpus) {
                if (cp != c && cp.getPlayer().collide(c.getPlayer()) && cp.getPlayer().isAttacking() && c.getPlayer().getState() != Player.INJURED) {
                    //int d = c.getPlayer().getDamage()+cp.getPlayer().getAttackInUse().getDamage() > 999 ? 999 : c.getPlayer().getDamage()+cp.getPlayer().getAttackInUse().getDamage();
                    int d = c.getPlayer().getDamage()+new Random().nextInt(10)+1;
                    c.getPlayer().setDamage(d);
                    if (!c.getPlayer().isAttacking() && c.getPlayer().getState() != Player.JUMPING) {
                        c.getPlayer().setState(Player.INJURED);
                    }
                }
            }
        }
    }

    // checkFallen - checks any characters that have fallen offscreen
    public void checkFallen() {
        // if player has fallen offscreen
        if (player.getY()-player.getJump()[1][0].getFrameAt(0).getIcon().getIconHeight() > scene.getImg().getHeight(this)) {
            player.setDropping(false);
            player.setStock(player.getStock()-1 < 0 ? 0 : player.getStock()-1); // reduce stock by one
            player.setX((int)scene.getSpawningPoint().getX());  // set player position to scene's spawning point
            player.setY((int)scene.getSpawningPoint().getY());
            player.setVY(0);
        }
        // if computer player has fallen offscreen
        for (ComputerPlayer cp : cpus) {
            if (cp.getPlayer().getY()-cp.getPlayer().getJump()[1][0].getFrameAt(0).getIcon().getIconHeight() > scene.getImg().getHeight(this)) {
                cp.getPlayer().setDropping(false);
                cp.getPlayer().setStock(cp.getPlayer().getStock()-1 < 0 ? 0 : cp.getPlayer().getStock()-1); // reduce stock by one
                cp.getPlayer().setX((int)scene.getSpawningPoint().getX());  // set player position to scene's spawning point
                cp.getPlayer().setY((int)scene.getSpawningPoint().getY());
                cp.getPlayer().setVY(0);
            }
        }
    }

    public void update(int time) {	// updates the state of the game
        repaint();
        this.time = time;
        if (starting == false) {
            checkKeys();
            for (ComputerPlayer cp : cpus) {
                cp.getPlayer().setPrevPos(cp.getPlayer().getX(), cp.getPlayer().getY());
                cp.decideMove(scene);
            }
            checkCollisions();
            checkFallen();
        }

        // timeLeft lowers by 1 second
        if (starting == false && this.time % 1000 == 0 && timeLeft > 0) {
            timeLeft--;
        }
        if (timeLeft > 0) {
            // updating current sprite showing for player
            if (player.getState() != Player.JUMPING && player.getState() != Player.INJURED && !player.isAttacking()) {
                player.setCurrent(time);
            }
            else if (player.getState() == Player.INJURED) {
                if (time%1000 == 0) {
                    if (player.getCurrent().getIcon() == player.getInjured()[player.getDir()].getLast()) {
                        player.setState(Player.IDLING);
                    }
                    else {
                        player.setCurrent(player.getCurrent().getNext());
                    }
                }
            }
            // if player is jumping
            else if (player.getState() == Player.JUMPING) {
                player.updateJumpIndex(scene);
                // jumping up
                if (player.getVY() < 0) {
                    if (player.isAttacking() && player.getAttackInUse().getState() == player.getState()) {
                        player.setCurrent(player.getAttackInUse().getJumpAtk()[player.getDir()][0].getFrameAt(player.getJumpIndex()));
                    }
                    else {
                        player.setCurrent(player.getJump()[player.getDir()][0].getFrameAt(player.getJumpIndex()));
                    }
                }
                // jumping down
                else {
                    if (player.isAttacking() && player.getAttackInUse().getState() == player.getState()) {
                        player.setCurrent(player.getAttackInUse().getJumpAtk()[player.getDir()][1].getFrameAt(player.getJumpIndex()));
                    }
                    else {
                        player.setCurrent(player.getJump()[player.getDir()][1].getFrameAt(player.getJumpIndex()));
                    }
                }
            }
            // player is attacking
            else if (player.getState() != Player.JUMPING && player.isAttacking() && player.getAttackInUse().getState() != Player.JUMPING) {
                if (time % player.getAttackInUse().getRegAtk()[0].getPeriod() == 0) {
                    player.setAtkIndex(player.getAtkIndex()+1);
                }
                if (player.getAtkIndex() != player.getAttackInUse().getRegAtk()[0].size()) {
                    player.setCurrent(player.getAttackInUse().getRegAtk()[player.getDir()].getFrameAt(player.getAtkIndex()));
                }
                else {
                    player.setAtkIndex(0);
                    player.setAttacking(false);
                }
            }
            else {

            }
            player.updateHitbox();  // update hitbox
            for (ComputerPlayer cp : cpus) {

                Player player = cp.getPlayer();

                if (player.getState() != Player.JUMPING && player.getState() != Player.INJURED && !player.isAttacking()) {
                    player.setCurrent(time);
                }
                else if (player.getState() == Player.INJURED) {
                    if (time%1000 == 0) {
                        if (player.getCurrent().getIcon() == player.getInjured()[player.getDir()].getLast()) {
                            player.setState(Player.IDLING);
                        }
                        else {
                            player.setCurrent(player.getCurrent().getNext());
                        }
                    }
                }
                else if (player.getState() == Player.JUMPING) {
                    player.updateJumpIndex(scene);
                    if (player.getVY() < 0) {
                        if (player.isAttacking() && player.getAttackInUse().getState() == player.getState()) {
                            player.setCurrent(player.getAttackInUse().getJumpAtk()[player.getDir()][0].getFrameAt(player.getJumpIndex()));
                        }
                        else {
                            player.setCurrent(player.getJump()[player.getDir()][0].getFrameAt(player.getJumpIndex()));
                        }
                    }
                    else {
                        if (player.isAttacking() && player.getAttackInUse().getState() == player.getState()) {
                            player.setCurrent(player.getAttackInUse().getJumpAtk()[player.getDir()][1].getFrameAt(player.getJumpIndex()));
                        }
                        else {
                            player.setCurrent(player.getJump()[player.getDir()][1].getFrameAt(player.getJumpIndex()));
                        }
                    }
                }
                // CPU is attacking
                else if (player.getState() != Player.JUMPING && player.isAttacking() && player.getAttackInUse().getState() != Player.JUMPING) {
                    if (time % player.getAttackInUse().getRegAtk()[0].getPeriod() == 0) {
                        player.setAtkIndex(player.getAtkIndex()+1);
                    }
                    if (player.getAtkIndex() != player.getAttackInUse().getRegAtk()[0].size()) {
                        player.setCurrent(player.getAttackInUse().getRegAtk()[player.getDir()].getFrameAt(player.getAtkIndex()));
                    }
                    else {
                        player.setAtkIndex(0);
                        player.setAttacking(false);
                    }
                }
                else {

                }
                player.updateHitbox();


            }
        }
        if (starting) {
            for (ComputerPlayer cp : cpus) {
                Player[] players = new Player[cpus.length];
                players[0] = player;
                int ind = 1;
                for (int i=0; i<cpus.length; i++) {
                    if (cpus[i] != cp) {
                        players[ind] = cpus[i].getPlayer();
                        ind++;
                    }
                }
                cp.chooseTarget(players);
            }
        }
    }

    // showTime - displays time left in match
    public void showTime(Graphics g) {
        int minutes = timeLeft/60, seconds = timeLeft%60, x = 1030, y = 16, gap = timeNums[0].getWidth(this)-4;
        if (minutes >= 10) {
            g.drawImage(timeNums[minutes/10], x, y, this);
        }
        g.drawImage(timeNums[minutes%10], x+gap, y, this);
        g.drawImage(timeNums[10], x+2*gap, y, this);
        g.drawImage(timeNums[seconds/10], x+3*gap, y, this);
        g.drawImage(timeNums[seconds%10], x+4*gap, y, this);
    }

    // showStats - shows all player damage and stocks
    public void showStats(Graphics g) {
        int gap = getWidth()/(faces.length+1), y = 550, width = 150;
        g.setFont(new Font("Arial Black", Font.PLAIN, 16));
        for (int i=0; i<faces.length; i++) {
            // display face for stats
            g.setColor(i == 0 ? new Color(227, 46, 46) : new Color(140,142, 138));
            g.fillRect(gap*(i+1)-width/2, y, faces[i].getWidth(this), faces[i].getHeight(this));
            int offset = 6, txtHeight = 30;
            g.fillPolygon(new int[] {gap*(i+1)-width/2, gap*(i+1)-width/2+offset-2, gap*(i+1)+width/2}, new int[] {y+faces[i].getHeight(this)+offset+8, y+faces[i].getHeight(this)+offset+txtHeight, y+faces[i].getHeight(this)+offset}, 3);
            g.setColor(Color.WHITE);
            g.drawString(i == 0 ? player.getName().toUpperCase() : cpus[i-1].getPlayer().getName().toUpperCase(), gap*(i+1)-width/2+6, y+faces[i].getHeight(this)+offset+16);
            g.drawImage(faces[i], gap*(i+1)-width/2, y, this);
            // display damage percents, not displayed if player has zero stock
            Player p = i == 0 ? player : cpus[i-1].getPlayer();
            if (p.getStock() > 0 && p.getDamage() < 999) {
                int damage = i == 0 ? player.getDamage() : cpus[i-1].getPlayer().getDamage(), initOffset = -2, numGap = 40, numOffset = 10;
                if (damage < 10) {
                    g.drawImage(damageNums[damage], gap*(i+1)-width/2+faces[i].getWidth(this)+initOffset, y+numOffset, this);
                    g.drawImage(damageNums[10], gap*(i+1)-width/2+faces[i].getWidth(this)+initOffset+numGap, y+numOffset, this);
                }
                else if (damage < 100) {
                    g.drawImage(damageNums[damage/10], gap*(i+1)-width/2+faces[i].getWidth(this)+initOffset, y+numOffset, this);
                    g.drawImage(damageNums[damage%10], gap*(i+1)-width/2+faces[i].getWidth(this)+initOffset+numGap, y+numOffset, this);
                    g.drawImage(damageNums[10], gap*(i+1)-width/2+faces[i].getWidth(this)+initOffset+numGap*2, y+numOffset, this);
                }
                else {
                    g.drawImage(damageNums[damage/100], gap*(i+1)-width/2+faces[i].getWidth(this)+initOffset, y+numOffset, this);
                    g.drawImage(damageNums[(damage/10)%10], gap*(i+1)-width/2+faces[i].getWidth(this)+initOffset+numGap, y+numOffset, this);
                    g.drawImage(damageNums[damage%10], gap*(i+1)-width/2+faces[i].getWidth(this)+initOffset+numGap*2, y+numOffset, this);
                    g.drawImage(damageNums[10], gap*(i+1)-width/2+faces[i].getWidth(this)+initOffset+numGap*3, y+numOffset, this);
                }
            }
            // display player stock (how many lives remaining)
            g.setColor(i == 0 ? new Color(227, 46, 46) : new Color(140,142, 138));
            int stock = i == 0 ? player.getStock() : cpus[i-1].getPlayer().getStock(), d = 16;
            for (int j=0; j<stock; j++) {
                g.fillOval(gap*(i+1)-width/2+faces[i].getWidth(this)+6+j*(d+2), y, d, d);
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        g.drawImage(scene.getImg(), 0, 0, this);
        if (starting) {     // displays countdown sequence, signalling beginning of battle
            if (time == 0) {
                SoundFX count = new SoundFX(getClass().getResource("/sounds/announcer/three.wav").getFile(), volume/100.0);
                count.play(0);
            }
            else if (time == countdown.getPeriod()) {
                SoundFX count = new SoundFX(getClass().getResource("/sounds/announcer/two.wav").getFile(), volume/100.0);
                count.play(0);
            }
            else if (time == 2*countdown.getPeriod()) {
                SoundFX count = new SoundFX(getClass().getResource("/sounds/announcer/one.wav").getFile(), volume/100.0);
                count.play(0);
            }
            else if (time == 3*countdown.getPeriod()) {
                SoundFX count = new SoundFX(getClass().getResource("/sounds/announcer/go.wav").getFile(), volume/100.0);
                count.play(0);
            }
            else if (time == 4*countdown.getPeriod()) {
                scene.getTrackPlaying().setVolume(volume/100.0);
                scene.getTrackPlaying().play(SoundFX.LOOP_CONTINUOUSLY);
                starting = false;
                for (ComputerPlayer cp : cpus) {
                    Player[] opps = new Player[cpus.length];
                    opps[0] = player;
                    int i = 1;
                    for (ComputerPlayer cp2 : cpus) {
                        if (cp2 != cp) {
                            opps[i] = cp2.getPlayer();
                            i++;
                        }
                    }
                    cp.chooseTarget(opps);
                }
            }
            player.setX((int)(scene.getStartingPoints()[0].getX()));
            player.setY((int)(scene.getStartingPoints()[0].getY()));
            player.setDir(scene.getStartingPoints()[0].getX() < getWidth()/2 ? ObjOnScreen.RIGHT : ObjOnScreen.LEFT);
            for (ComputerPlayer cpu : cpus) {
                int ind = Arrays.asList(cpus).indexOf(cpu);
                cpu.getPlayer().setX((int)(scene.getStartingPoints()[ind+1].getX()));
                cpu.getPlayer().setY((int)(scene.getStartingPoints()[ind+1].getY()));
                cpu.getPlayer().setDir(scene.getStartingPoints()[ind+1].getX() < getWidth()/2 ? ObjOnScreen.RIGHT : ObjOnScreen.LEFT);
            }
        }
        if (player != null && player.getStock() > 0) {
            g.drawImage(arrows[0], player.getX()-arrows[0].getWidth(this)/2, player.getY()-player.getCurrent().getIcon().getIconHeight()-arrows[0].getHeight(this)-10, this);
            g.drawImage(player.getCurrent().getIcon().getImage(), player.getX()-player.getCurrent().getIcon().getIconWidth()/2, player.getY()-player.getCurrent().getIcon().getIconHeight(), this);
        }
        if (cpus != null) {
            for (ComputerPlayer cpu : cpus) {
                if (cpu.getPlayer().getStock() > 0) {
                    g.drawImage(arrows[4], cpu.getPlayer().getX()-arrows[4].getWidth(this)/2, cpu.getPlayer().getY()-cpu.getPlayer().getCurrent().getIcon().getIconHeight()-arrows[4].getHeight(this)-10, this);
                    g.drawImage(cpu.getPlayer().getCurrent().getIcon().getImage(), cpu.getPlayer().getX()-cpu.getPlayer().getCurrent().getIcon().getIconWidth()/2, cpu.getPlayer().getY()-cpu.getPlayer().getCurrent().getIcon().getIconHeight(), this);
                }
            }
        }
        if (starting) {
            g.drawImage(countdown.getFrameAt(time/1000).getIcon().getImage(), getWidth()/2-countdown.getCurrent().getIcon().getIconWidth()/2, getHeight()/2-countdown.getCurrent().getIcon().getIconHeight()/2, this);
        }
        showTime(g);
        showStats(g);
    }
}
