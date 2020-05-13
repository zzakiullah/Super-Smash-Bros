/* ChoosePanel.java
 * Zulaikha Zakiullah
 * This class deals with the choosing panel, in which players choose their characters.
 */

import javax.swing.*;
import java.awt.*;

class ChoosePanel extends JPanel {
    public static final int SMASH = 0, ONLINE = 1, CHARACTER = 2, SCENE = 3, MINIMUM_TIME = 1, MAXIMUM_TIME = 99;
    // mode - SMASH mode or ONLINE mode; chooseType - choosing CHARACTER or SCENE
    // players - number of players; time - time taken for the round in minutes
    private int mode, chooseType, players, time;
    private Integer[] chosen;               // array of indices of players chosen
    private Integer chosenScene;            // scene chosen
    private Image[] charImgs, sceneImgs;    // character and scene images
    private String[] charNames, sceneNames; // character and scene names
    // charBtns - character buttons; displayCharBtns - buttons used when clicking for a computer player
    // removeCharBtns - buttons used to remove a computer player; sceneBtns - buttons used for choosing scene
    private JButton[] charBtns, displayCharBtns, removeCharBtns, sceneBtns;
    private JComboBox<String> musicBox;     // drop down list of all music tracks for scene

    public ChoosePanel(Player[] characters, Scene[] scenes) {
        super();
        setSize(1200, 700);
        setLocation(0, 0);
        setLayout(null);
        setOpaque(false);
        mode = SMASH;
        chooseType = CHARACTER;
        time = 2;
        players = 1;
        chosen = new Integer[4];
        this.charNames = new String[characters.length+1];
        for (int i=0; i<characters.length; i++) {
            this.charNames[i] = characters[i].getName();
        }
        this.charNames[charNames.length-1] = "Random";
        this.sceneNames = new String[scenes.length+1];
        for (int i=0; i<scenes.length; i++) {
            this.sceneNames[i] = scenes[i].getName();
        }
        this.sceneNames[sceneNames.length-1] = "Random";
        repaint();
        makeButtons();
        makeImgs("/images/choose/button_characters/", "/images/choose/button_scenes/");
        setVisible(true);
        requestFocus();
    }

    public void resetPlayers() {players = 1;}

    public void reset() {
        chooseType = CHARACTER;
        time = 2;
        players = 1;
        chosen = new Integer[4];
        chosenScene = null;
    }

    public int getMode() {return mode;}
    public int getChooseType() {return chooseType;}
    public int getTime() {return time;}
    public Image[] getCharImgs() {return charImgs;}
    public JButton[] getCharBtns() {return charBtns;}
    public JButton[] getDisplayCharBtns() {return displayCharBtns;}
    public JButton[] getRemoveCharBtns() {return removeCharBtns;}
    public JButton[] getSceneBtns() {return sceneBtns;}
    public Integer[] getChosen() {return chosen;}
    public Integer getChosenScene() {return chosenScene;}
    public int getPlayers() {return players;}
    public void setMode(int m) {
        mode = m;
        players = mode == SMASH ? 1 : 4;
    }
    public void setChooseType(int c) {chooseType = c;}
    public void setTime(int t) {time = t;}
    public void setChosen(int ind, int i) {chosen[i] = ind;}
    public void setMusicBox(String[] songNames) {
        musicBox = new JComboBox<String>(songNames);

    }
    public void resetChosen() {
        chosen = new Integer[4];
        players = 1;
    }
    public void setChosenScene(Integer scene) {chosenScene = scene;}
    public void addPlayer() {players++;}
    public void removePlayer() {players--;}

    public void makeButtons() {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Image clickGlove = toolkit.getImage(getClass().getResource("/images/cursors/Mouse3.png"));
        // making charBtns
        charBtns = new JButton[charNames.length];
        for (int i=0; i<charBtns.length; i++) {
            charBtns[i] = new JButton(new ImageIcon(getClass().getResource("/images/choose/select_rects/normal_player.png")));
            charBtns[i].setSize(100, 80);
            charBtns[i].setContentAreaFilled(false);
            charBtns[i].setFocusPainted(false);
            charBtns[i].setBorderPainted(false);
            charBtns[i].setCursor(toolkit.createCustomCursor(clickGlove, new Point(0, 0), ""));
            charBtns[i].setRolloverEnabled(true);
            charBtns[i].setRolloverIcon(new ImageIcon(getClass().getResource("/images/choose/select_rects/p1.png")));
            int gap = 5, inner = 9, outer = 10;
            if (i < inner) {
                charBtns[i].setLocation((1200-inner*charBtns[i].getWidth()-gap*(inner-1))/2+(charBtns[i].getWidth()+gap)*i, 95);
            }
            else if (i < inner+outer) {
                charBtns[i].setLocation((1200-outer*charBtns[i].getWidth()-gap*(outer-1))/2+(charBtns[i].getWidth()+gap)*(i-inner), charBtns[0].getY()+charBtns[0].getHeight());
            }
            else if (i < inner+2*outer) {
                charBtns[i].setLocation((1200-outer*charBtns[i].getWidth()-gap*(outer-1))/2+(charBtns[i].getWidth()+gap)*(i-inner-outer), charBtns[inner+outer].getY()+charBtns[inner+outer].getHeight());
            }
            else {
                charBtns[i].setLocation((1200-inner*charBtns[i].getWidth()-gap*(inner-1))/2+(charBtns[i].getWidth()+gap)*(i-inner-2*outer), charBtns[inner+2*outer].getY()+charBtns[inner+2*outer].getHeight());
            }
        }
        // making displayCharBtns and removeCharBtns
        if (mode == SMASH) {
            displayCharBtns = new JButton[3];
            removeCharBtns = new JButton[3];
            int gap = 20, width = 225, height = 275;
            for (int i=0; i<3; i++) {
                displayCharBtns[i] = new JButton(new ImageIcon(getClass().getResource("/images/choose/player_labels/smash/blank.png")));
                displayCharBtns[i].setSize(width, height);
                displayCharBtns[i].setContentAreaFilled(false);
                displayCharBtns[i].setFocusPainted(false);
                displayCharBtns[i].setBorderPainted(false);
                displayCharBtns[i].setLocation((1200-width*4-gap*3)/2+(gap+width)*(i+1), 700-displayCharBtns[i].getHeight());
                displayCharBtns[i].setCursor(toolkit.createCustomCursor(clickGlove, new Point(0, 0), ""));
                displayCharBtns[i].setVisible(false);
                removeCharBtns[i] = new JButton(new ImageIcon(getClass().getResource("/images/choose/player_labels/smash/x.png")));
                removeCharBtns[i].setSize(50, 50);
                removeCharBtns[i].setContentAreaFilled(false);
                removeCharBtns[i].setFocusPainted(false);
                removeCharBtns[i].setBorderPainted(false);
                removeCharBtns[i].setLocation(displayCharBtns[i].getX()+displayCharBtns[i].getWidth()-removeCharBtns[i].getWidth()-2, displayCharBtns[i].getY()+2);
                removeCharBtns[i].setCursor(toolkit.createCustomCursor(clickGlove, new Point(0, 0), ""));
                removeCharBtns[i].setVisible(false);
            }
            displayCharBtns[0].setVisible(true);
        }
        // making sceneBtns
        sceneBtns = new JButton[sceneNames.length];
        for (int i=0; i<sceneBtns.length; i++) {
            sceneBtns[i] = new JButton(new ImageIcon(getClass().getResource("/images/choose/select_rects/normal_scene.png")));
            sceneBtns[i].setSize(128, 75);
            sceneBtns[i].setContentAreaFilled(false);
            sceneBtns[i].setFocusPainted(false);
            sceneBtns[i].setBorderPainted(false);
            sceneBtns[i].setCursor(toolkit.createCustomCursor(clickGlove, new Point(0, 0), ""));
            sceneBtns[i].setRolloverEnabled(true);
            sceneBtns[i].setRolloverIcon(new ImageIcon(getClass().getResource("/images/choose/select_rects/scene_border.png")));
            int gap = 5, inner = 4, outer = 5, initX = 500;
            int x = (1200-initX-inner*sceneBtns[0].getWidth()-(inner-1)*gap)/2, x2 = (1200-initX-outer*sceneBtns[0].getWidth()-(outer-1)*gap)/2, y = 125;
            if (i < inner) {
                sceneBtns[i].setLocation(initX+x+i*(sceneBtns[0].getWidth()+gap), y);
            }
            else if (i < inner+outer) {
                sceneBtns[i].setLocation(initX+x2+(i-inner)*(sceneBtns[0].getWidth()+gap), y+sceneBtns[0].getHeight()+gap);
            }
            else if (i < 2*inner+outer) {
                sceneBtns[i].setLocation(initX+x+(i-inner-outer)*(sceneBtns[0].getWidth()+gap), y+2*(sceneBtns[0].getHeight()+gap));
            }
            else if (i < 2*inner+2*outer) {
                sceneBtns[i].setLocation(initX+x2+(i-2*inner-outer)*(sceneBtns[0].getWidth()+gap), y+3*(sceneBtns[0].getHeight()+gap));
            }
            else if (i < 3*inner+2*outer) {
                sceneBtns[i].setLocation(initX+x+(i-2*inner-2*outer)*(sceneBtns[0].getWidth()+gap), y+4*(sceneBtns[0].getHeight()+gap));
            }
            else if (i < 3*inner+3*outer) {
                sceneBtns[i].setLocation(initX+x2+(i-3*inner-2*outer)*(sceneBtns[0].getWidth()+gap), y+5*(sceneBtns[0].getHeight()+gap));
            }
        }
    }

    public void makeImgs(String dir, String dir2) {
        charImgs = new Image[charNames.length];
        try {
            for (int i = 0; i < charImgs.length; i++) {
                charImgs[i] = new ImageIcon(getClass().getResource(dir + (charNames[i].replace(" ", "_"))+".png")).getImage();
            }
        }
        catch(Exception ex) {
            System.err.println("makeImgs, charImgs: "+ex);
            ex.printStackTrace();
        }
        sceneImgs = new Image[sceneNames.length];
        try {
            for (int i=0; i<sceneImgs.length; i++) {
                sceneImgs[i] = new ImageIcon(getClass().getResource(dir2 + sceneNames[i].replace(" ", "_")+".jpg")).getImage();
            }
        }
        catch(Exception ex) {
            System.err.println("makeImgs, sceneImgs: "+ex);
            ex.printStackTrace();
        }
    }

    // displayCharBack - draws everything on the ChoosePanel when choosing character
    public void displayCharBack(Graphics g) {
        int gap = 5, inner = 9, outer = 10;
        g.setFont(new Font("Arial Black", Font.PLAIN, 8));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawImage(new ImageIcon(getClass().getResource("/images/choose/character_select.png")).getImage(), 205, 10, this);
        g.setColor(new Color(26, 26, 26));
        g.fillRect(0, charBtns[0].getY()-gap, 1200, charBtns[0].getHeight()*4+gap);
        g.drawImage(new ImageIcon(getClass().getResource("/images/choose/character_back.png")).getImage(), (1200-(charBtns[0].getWidth()*outer+gap*(outer-1)))/2, 95, this);
        for (int i=0; i<4; i++) {
            g.fillRect(0, charBtns[0].getY()+charBtns[0].getHeight()+i*charBtns[0].getHeight()-20, 1200, 20);
        }
        for (int i=0; i<inner+1; i++) {
            int x = charBtns[0].getX()-gap+(gap+charBtns[0].getWidth())*i;
            g.fillRect(x, charBtns[0].getY(), gap, charBtns[0].getHeight());
            g.fillRect(x, charBtns[0].getY()+charBtns[0].getHeight()*3, gap, charBtns[0].getHeight());
        }
        for (int i=0; i<outer+1; i++) {
            int x = (1200-(charBtns[0].getWidth()*outer+gap*(outer-1)))/2-gap+(charBtns[0].getWidth()+gap)*i;
            g.fillRect(x, charBtns[0].getY()+charBtns[0].getHeight(), gap, charBtns[0].getHeight());
            g.fillRect(x, charBtns[0].getY()+2*charBtns[0].getHeight(), gap, charBtns[0].getHeight());
        }
        int width = charBtns[0].getX()-(1200-(charBtns[0].getWidth()*outer+gap*(outer-1)))/2-gap;
        g.fillRect((1200-(charBtns[0].getWidth()*outer+gap*(outer-1)))/2, charBtns[0].getY(), width, charBtns[0].getHeight());
        g.fillRect((1200-(charBtns[0].getWidth()*outer+gap*(outer-1)))/2, charBtns[0].getY()+charBtns[0].getHeight()*3, width, charBtns[0].getHeight());
        g.fillRect(charBtns[0].getX()+charBtns[0].getWidth()*inner+gap*inner, charBtns[0].getY(), width, charBtns[0].getHeight());
        g.fillRect(charBtns[0].getX()+charBtns[0].getWidth()*inner+gap*inner, charBtns[0].getY()+charBtns[0].getHeight()*3, width, charBtns[0].getHeight());
        g.setColor(Color.WHITE);
        for (int i=0; i<charImgs.length; i++) {
            g.drawImage(charImgs[i], charBtns[i].getX(), charBtns[i].getY(), this);
            String name = charNames[i].toUpperCase();
            int x = charBtns[i].getX()+charBtns[i].getWidth()/2-metrics.stringWidth(name)/2;
            int y = charBtns[i].getY()+charBtns[i].getHeight()-9;
            g.drawString(name, x, y);
        }
        g.setColor(new Color(26, 26, 26));
        gap = 20;
        width = 225;
        int height = 275;
        for (int i=0; i<chosen.length; i++) {
            if (mode == SMASH) {
                if (i < players) {
                    g.drawImage(new ImageIcon(getClass().getResource("/images/choose/player_labels/smash/p"+(i+1)+".png")).getImage(), (1200-width*4-gap*3)/2+(gap+width)*i, 700-height, this);
                }
                else {
                    g.drawImage(new ImageIcon(getClass().getResource("/images/choose/player_labels/smash/none.png")).getImage(), (1200-width*4-gap*3)/2+(gap+width)*i, 700-height, this);
                }
                if (chosen[i] != null) {
                    g.drawImage(new ImageIcon(getClass().getResource("/images/choose/choose_characters/"+charNames[chosen[i]].replace(" ", "_")+".png")).getImage(), (1200-width*4-gap*3)/2+(gap+width)*i+3, displayCharBtns[0].getY()+3, this);
                }
            }
            else {}
        }
        g.setFont(new Font("Arial Black", Font.BOLD, 48));
        metrics = getFontMetrics(g.getFont());
        g.drawString(Integer.toString(time), 923-metrics.stringWidth(Integer.toString(time)), 136-metrics.getHeight());
    }

    // displaySceneBack - draws everything on the ChoosePanel when choosing scene/stage
    public void displaySceneBack(Graphics g) {
        g.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 48));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawImage(new ImageIcon(getClass().getResource("/images/choose/stage_select.png")).getImage(), 245, 10, this);
        for (int i=0; i<sceneImgs.length; i++) {
            g.drawImage(sceneImgs[i], sceneBtns[i].getX(), sceneBtns[i].getY(), this);
        }
        if (chosenScene != null) {
            g.drawImage(new ImageIcon(getClass().getResource("/images/choose/choose_scenes/"+sceneNames[chosenScene].replace(" ", "_")+".jpg")).getImage(), 60, 150, this);
            // put name of stage
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        if (chooseType == CHARACTER) {
            displayCharBack(g);
        }
        else {
            displaySceneBack(g);
        }
    }
}
