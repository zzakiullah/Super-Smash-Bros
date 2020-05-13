/* SSBR.java
 * Zulaikha Zakiullah
 *
 * Welcome to Super Smash Bros. Rumble! The game currently does not feature online mode, but that's alright!
 * Computer players may seem rather OP but that is also fine.
 */

import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;

public class SSBR extends JFrame implements ActionListener, KeyListener, MouseListener {

    public static void main(String[] args) {
        new SSBR();
    }

    private final int TITLE = 0, MENU = 1, CHOOSE = 2, ONLINE = 3, INSTRUCT = 4, CREDITS = 5, GAME = 6, END = 7;

    // frameX, frameY - width and height of JFrame, time - time elapsed
    private int frameX, frameY, time;

    private JPanel cards;	// used for card layout
    private CardLayout cLayout = new CardLayout();
    private int card = TITLE, prevCard = -1;

    private int volume; // volume expressed from 0 to 100

    private ArrayList<Player> players = new ArrayList<Player>();
    private ArrayList<ComputerPlayer> cpus = new ArrayList<ComputerPlayer>();

    // ---------- all panes ---------- //
    private JLayeredPane titlePane = new JLayeredPane();
    private JLayeredPane menuPane = new JLayeredPane();
    private JLayeredPane choosePane = new JLayeredPane();
    private JLayeredPane instructPane = new JLayeredPane();
    private JLayeredPane creditsPane = new JLayeredPane();
    private JLayeredPane gamePane = new JLayeredPane();
    private JLayeredPane endPane = new JLayeredPane();
    private JLayeredPane settingsPane = new JLayeredPane();
    private JLayeredPane pausePane = new JLayeredPane();

    private ChoosePanel choosePanel;    // choose panel
    private GamePanel game;             // game panel

    private JLabel winBack, loseBack, timeBack; // JPanels for endPane

    // ---------- all buttons ---------- //
    private JButton playBtn = new JButton(new ImageIcon(getClass().getResource("/images/choose/play.png")));
    private JButton smashBtn = new JButton(new ImageIcon(getClass().getResource("/images/menu/smash.png")));
    private JButton onlineBtn = new JButton(new ImageIcon(getClass().getResource("/images/menu/online.png")));
    private JButton instructBtn = new JButton(new ImageIcon(getClass().getResource("/images/menu/instruct.png")));
    private JButton creditsBtn = new JButton(new ImageIcon(getClass().getResource("/images/menu/credits.png")));
    private JButton backBtn = new JButton(new ImageIcon(getClass().getResource("/images/menu/back.png")));
    private JButton nextBtn = new JButton(new ImageIcon(getClass().getResource("/images/choose/next.png")));
    private JButton pauseBtn = new JButton(new ImageIcon(getClass().getResource("/images/game/pause.png")));
    private JButton settingsBtn = new JButton(new ImageIcon(getClass().getResource("/images/menu/settings.png")));
    private JButton lessTimeBtn = new JButton(new ImageIcon(getClass().getResource("/images/choose/less.png")));
    private JButton moreTimeBtn = new JButton(new ImageIcon(getClass().getResource("/images/choose/more.png")));
    private JButton restartBtn = new JButton(new ImageIcon(getClass().getResource("/images/end/restart.png")));
    private JButton homeBtn = new JButton(new ImageIcon(getClass().getResource("/images/end/home.png")));

    private Player[] allCharacters;	// array of all characters
    private Scene[] allScenes;		// array of all scenes

    // all dialog boxes
    private DialogBox settingsDialog = new DialogBox("Settings", new ImageIcon(getClass().getResource("/images/other/ssb_logo.png")).getImage(), this);
    private DialogBox pauseDialog = new DialogBox("Game Paused", new ImageIcon(getClass().getResource("/images/other/ssb_logo.png")).getImage(), this);
    private DialogBox confirmRestartDialog = new DialogBox("Confirm Restart", new ImageIcon(getClass().getResource("/images/other/ssb_logo.png")).getImage(), this);
    private DialogBox confirmExitDialog = new DialogBox("Confirm Exit", new ImageIcon(getClass().getResource("/images/other/ssb_logo.png")).getImage(), this);
    private DialogBox musicDialog = new DialogBox("Choose Background Music", new ImageIcon(getClass().getResource("/images/other/ssb_logo.png")).getImage(), this);

    private JSlider volumeSlider;   // JSlider used for changing volume

    // timer
    private javax.swing.Timer myTimer;

    // toolkit used for setting cursor
    private Toolkit toolkit = Toolkit.getDefaultToolkit();
    private Image glove = toolkit.getImage(getClass().getResource("/images/cursors/Mouse2.png"));
    private Image clickGlove = toolkit.getImage(getClass().getResource("/images/cursors/Mouse3.png"));

    // --------------------- all Gif and SoundFX objects --------------------- //

    // fadeback sequences, used when switching screens
    private Gif fadeout = new Gif(getGifFrames(getClass().getResource("/images/other/fadeout/").getFile()), 40);
    private Gif fadein = new Gif(getGifFrames(getClass().getResource("/images/other/fadein/").getFile()), 40);

    // ----------- opening ----------- //
    // opening background
    private Gif opening = new Gif(getGifFrames(getClass().getResource("/images/title/opening/").getFile()), 40);
    private JLabel titleBack = new JLabel(opening.getImageIcon(0));
    private SoundFX openingTheme = new SoundFX(getClass().getResource("/sounds/title/SSB4_Opening_Theme.wav").getFile());

    // ------------ menu ------------ //
    private Gif spaceTime = new Gif(getGifFrames(getClass().getResource("/images/menu/space_time/").getFile()), 30);
    private JLabel menuBack = new JLabel(spaceTime.getImageIcon(0));
    private SoundFX menuTheme = new SoundFX(getClass().getResource("/sounds/menu/SSB4_Menu_Theme.wav").getFile());

    // ----------------------------------------------------------------------- //

    public SSBR() {
        super("Super Smash Bros. Rumble (Semi-Broken)");
        setIconImage(new ImageIcon(getClass().getResource("/images/other/ssb_logo.png")).getImage());
        frameX = 1200;
        frameY = 700;
        setSize(frameX, frameY);

        // set mouse cursor when cursor enters JFrame
        setCursor(toolkit.createCustomCursor(glove, new Point(0, 0), "idle glove"));

        // timer
        myTimer = new javax.swing.Timer(10, this);
        time = 0;
        volume = 50;

        // making characters and scenes
        makeCharacters("/info/characters.txt");
        makeScenes("/info/scenes.txt");

        choosePanel = new ChoosePanel(allCharacters, allScenes);
        for (JButton btn : choosePanel.getCharBtns()) {btn.addActionListener(this);}
        for (JButton btn : choosePanel.getDisplayCharBtns()) {btn.addActionListener(this);}
        for (JButton btn : choosePanel.getRemoveCharBtns()) {btn.addActionListener(this);}
        for (JButton btn : choosePanel.getSceneBtns()) {btn.addActionListener(this);}

        game = new GamePanel();

        // ---------- all buttons ---------- //

        // play
        playBtn.addActionListener(this);
        playBtn.setSize(210, 78);
        playBtn.setLocation(frameX-playBtn.getWidth()-40, 10);
        playBtn.setContentAreaFilled(false);
        playBtn.setFocusPainted(false);
        playBtn.setBorderPainted(false);
        playBtn.setCursor(toolkit.createCustomCursor(glove, new Point(0, 0), ""));
        playBtn.setRolloverEnabled(false);

        // smash
        smashBtn.addActionListener(this);
        smashBtn.setSize(517, 234);
        smashBtn.setLocation(frameX/2-smashBtn.getWidth()/2, 110);
        smashBtn.setContentAreaFilled(false);
        smashBtn.setFocusPainted(false);
        smashBtn.setBorderPainted(false);
        smashBtn.setCursor(toolkit.createCustomCursor(clickGlove, new Point(0, 0), ""));
        smashBtn.setRolloverEnabled(true);
        smashBtn.setRolloverIcon(new ImageIcon(getClass().getResource("/images/menu/smash_hover.png")));

        // online (multiplayer)
        onlineBtn.addActionListener(this);
        onlineBtn.setSize(254, 234);
        onlineBtn.setLocation(smashBtn.getX(), smashBtn.getY()+smashBtn.getHeight()+10);
        onlineBtn.setContentAreaFilled(false);
        onlineBtn.setFocusPainted(false);
        onlineBtn.setBorderPainted(false);
        onlineBtn.setCursor(toolkit.createCustomCursor(clickGlove, new Point(0, 0), ""));
        onlineBtn.setRolloverEnabled(true);
        onlineBtn.setRolloverIcon(new ImageIcon(getClass().getResource("/images/menu/online_hover.png")));

        // how to play
        instructBtn.addActionListener(this);
        instructBtn.setSize(254, 118);
        instructBtn.setLocation(smashBtn.getX()+smashBtn.getWidth()-instructBtn.getWidth(), smashBtn.getY()+smashBtn.getHeight()+10);
        instructBtn.setContentAreaFilled(false);
        instructBtn.setFocusPainted(false);
        instructBtn.setBorderPainted(false);
        instructBtn.setCursor(toolkit.createCustomCursor(clickGlove, new Point(0, 0), ""));
        instructBtn.setRolloverEnabled(true);
        instructBtn.setRolloverIcon(new ImageIcon(getClass().getResource("/images/menu/instruct_hover.png")));

        // credits
        creditsBtn.addActionListener(this);
        creditsBtn.setSize(254, 103);
        creditsBtn.setLocation(smashBtn.getX()+smashBtn.getWidth()-instructBtn.getWidth(), smashBtn.getY()+smashBtn.getHeight()+10+onlineBtn.getHeight()-creditsBtn.getHeight());
        creditsBtn.setContentAreaFilled(false);
        creditsBtn.setFocusPainted(false);
        creditsBtn.setBorderPainted(false);
        creditsBtn.setCursor(toolkit.createCustomCursor(clickGlove, new Point(0, 0), ""));
        creditsBtn.setRolloverEnabled(true);
        creditsBtn.setRolloverIcon(new ImageIcon(getClass().getResource("/images/menu/credits_hover.png")));

        // pause
        pauseBtn.addActionListener(this);
        pauseBtn.setSize(64, 64);
        pauseBtn.setLocation(frameX-pauseBtn.getWidth()-16, frameY-pauseBtn.getHeight()-36);
        pauseBtn.setContentAreaFilled(false);
        pauseBtn.setFocusPainted(false);
        pauseBtn.setBorderPainted(false);
        pauseBtn.setCursor(toolkit.createCustomCursor(clickGlove, new Point(0, 0), ""));

        // back
        backBtn.addActionListener(this);
        backBtn.setSize(200, 78);
        backBtn.setLocation(0, 10);
        backBtn.setContentAreaFilled(false);
        backBtn.setFocusPainted(false);
        backBtn.setBorderPainted(false);
        backBtn.setCursor(toolkit.createCustomCursor(clickGlove, new Point(0, 0), ""));
        backBtn.setRolloverEnabled(true);
        backBtn.setRolloverIcon(new ImageIcon(getClass().getResource("/images/menu/back_hover.png")));

        // next
        nextBtn.addActionListener(this);
        nextBtn.setSize(161, 78);
        nextBtn.setLocation(1022, 10);
        nextBtn.setContentAreaFilled(false);
        nextBtn.setFocusPainted(false);
        nextBtn.setBorderPainted(false);

        // settings
        settingsBtn.addActionListener(this);
        settingsBtn.setSize(271, 78);
        settingsBtn.setLocation(frameX-settingsBtn.getWidth(), 10);
        settingsBtn.setContentAreaFilled(false);
        settingsBtn.setFocusPainted(false);
        settingsBtn.setBorderPainted(false);
        settingsBtn.setCursor(toolkit.createCustomCursor(clickGlove, new Point(0, 0), ""));
        settingsBtn.setRolloverEnabled(true);
        settingsBtn.setRolloverIcon(new ImageIcon(getClass().getResource("/images/menu/settings_hover.png")));

        // less time
        lessTimeBtn.addActionListener(this);
        lessTimeBtn.setSize(32, 40);
        lessTimeBtn.setLocation(815, 30);
        lessTimeBtn.setContentAreaFilled(false);
        lessTimeBtn.setFocusPainted(false);
        lessTimeBtn.setBorderPainted(false);
        lessTimeBtn.setCursor(toolkit.createCustomCursor(clickGlove, new Point(0, 0), ""));

        // more time
        moreTimeBtn.addActionListener(this);
        moreTimeBtn.setSize(32, 40);
        moreTimeBtn.setLocation(971, 30);
        moreTimeBtn.setContentAreaFilled(false);
        moreTimeBtn.setFocusPainted(false);
        moreTimeBtn.setBorderPainted(false);
        moreTimeBtn.setCursor(toolkit.createCustomCursor(clickGlove, new Point(0, 0), ""));

        // restart button
        restartBtn.addActionListener(this);
        restartBtn.setSize(250, 87);
        restartBtn.setLocation(130, 400);
        restartBtn.setContentAreaFilled(false);
        restartBtn.setFocusPainted(false);
        restartBtn.setBorderPainted(false);
        restartBtn.setCursor(toolkit.createCustomCursor(clickGlove, new Point(0, 0), ""));
        restartBtn.setRolloverEnabled(true);
        restartBtn.setRolloverIcon(new ImageIcon(getClass().getResource("/images/end/restart_hover.png")));

        // home button
        homeBtn.addActionListener(this);
        homeBtn.setSize(250, 87);
        homeBtn.setLocation(130, 500);
        homeBtn.setContentAreaFilled(false);
        homeBtn.setFocusPainted(false);
        homeBtn.setBorderPainted(false);
        homeBtn.setCursor(toolkit.createCustomCursor(clickGlove, new Point(0, 0), ""));
        homeBtn.setRolloverEnabled(true);
        homeBtn.setRolloverIcon(new ImageIcon(getClass().getResource("/images/end/home_hover.png")));

        // --------------------------------- //

        // ------------- formatting DialogBoxes ------------- //

        // ---------- settingsDialog ---------- //
        settingsDialog.setSize(400, 250);
        settingsDialog.setLocationRelativeTo(null);
        settingsDialog.setCursor(toolkit.createCustomCursor(glove, new Point(0, 0), ""));
        JButton[] settingsBtns = new JButton[2];
        for (int i=0; i<settingsBtns.length; i++) {
            String htmlText = "<html><font color=#000000>" + (i == 0 ? "OK" : "Cancel") + "</font></html>";
            settingsBtns[i] = new JButton(htmlText);
            settingsBtns[i].setSize(100, 40);
            settingsBtns[i].setFont(new Font("Arial Black", Font.PLAIN, 20));
            settingsBtns[i].setFocusPainted(false);
            settingsBtns[i].setBackground(Color.WHITE);
            settingsBtns[i].setOpaque(true);
            settingsBtns[i].setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            int offset = i == 0 ? -10-settingsBtns[i].getWidth() : 10;
            settingsBtns[i].setLocation(settingsDialog.getWidth()/2+offset, settingsDialog.getHeight()-settingsBtns[i].getHeight()-50);
            settingsBtns[i].setCursor(toolkit.createCustomCursor(clickGlove, new Point(0, 0), ""));
        }
        Font arialBig = new Font("Arial", Font.PLAIN, 32);
        FontMetrics arialMetrics = getFontMetrics(arialBig);
        JLabel volumeLabel = new JLabel("<html><font color=#FFFFFF>Volume: 50</font></html>");
        volumeLabel.setFont(arialBig);
        volumeLabel.setHorizontalAlignment(JLabel.CENTER);
        volumeLabel.setSize(400, arialMetrics.getHeight());
        volumeLabel.setLocation(settingsDialog.getWidth()/2-volumeLabel.getWidth()/2, 30);
        settingsDialog.setText(volumeLabel);

        JLabel settingsBack = new JLabel(new ImageIcon(getClass().getResource("/images/menu/settings_back.jpg").getFile()));
        settingsBack.setSize(settingsDialog.getSize());
        settingsBack.setLocation(0, 0);

        // JSlider used for adjustig volume
        volumeSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);
        volumeSlider.addChangeListener(settingsDialog);
        volumeSlider.setMinorTickSpacing(5);
        volumeSlider.setMajorTickSpacing(10);
        volumeSlider.setPaintTicks(true);
        volumeSlider.setFont(new Font("Arial", Font.PLAIN, 18));
        Hashtable labelTable = new Hashtable();
        for (int i=0; i<=100; i+=10) {
            labelTable.put( new Integer(i), new JLabel("<html><font color=#FFFFFF>"+i+"</font></html>") );
        }
        volumeSlider.setLabelTable(labelTable);
        volumeSlider.setPaintLabels(true);
        volumeSlider.setOpaque(false);
        volumeSlider.setSize(320, 100);
        volumeSlider.setLocation(settingsDialog.getWidth()/2-volumeSlider.getWidth()/2, 56);

        settingsPane.setSize(settingsDialog.getSize());
        settingsPane.setLocation(0, 0);
        settingsPane.add(settingsBack, JLayeredPane.DEFAULT_LAYER);
        settingsPane.add(volumeLabel, JLayeredPane.MODAL_LAYER);
        settingsPane.add(volumeSlider, JLayeredPane.DRAG_LAYER);
        settingsDialog.setPane(settingsPane);
        settingsDialog.setButtons(settingsBtns);

        // ---------- pauseDialog ---------- //
        pauseDialog.setSize(250, 400);
        pauseDialog.setLocationRelativeTo(null);
        pauseDialog.setCursor(toolkit.createCustomCursor(glove, new Point(0, 0), ""));

        pausePane.setSize(pauseDialog.getSize());
        pausePane.setLocation(0, 0);

        JLabel pauseBack = new JLabel(new ImageIcon(getClass().getResource("/images/game/pause_back.jpg").getFile()));
        pauseBack.setSize(pauseDialog.getSize());
        pauseBack.setLocation(0, 0);

        JLabel pauseMsg = new JLabel("<html><font color=#FFFFFF>What would you</font></html>");
        pauseMsg.setFont(new Font("Arial", Font.PLAIN, 24));
        pauseMsg.setHorizontalAlignment(JLabel.CENTER);
        pauseMsg.setSize(250, 32);
        pauseMsg.setLocation(pauseDialog.getWidth()/2-pauseMsg.getWidth()/2, 36);
        JLabel pauseMsg2 = new JLabel("<html><font color=#FFFFFF>like to do?</font></html>");
        pauseMsg2.setFont(pauseMsg.getFont());
        pauseMsg2.setHorizontalAlignment(JLabel.CENTER);
        pauseMsg2.setSize(pauseMsg.getSize());
        pauseMsg2.setLocation(pauseDialog.getWidth()/2-pauseMsg2.getWidth()/2, pauseMsg.getY()+pauseMsg.getHeight());

        JButton[] pauseBtns = new JButton[3];
        for (int i=0; i<pauseBtns.length; i++) {
            String htmlText = "<html><font color=#000000>" + (i == 0 ? "Resume" : i == 1 ? "Restart" : "Exit") + "</font></html>";
            pauseBtns[i] = new JButton(htmlText);
            pauseBtns[i].setSize(180, 50);
            pauseBtns[i].setFont(new Font("Arial Black", Font.PLAIN, 20));
            pauseBtns[i].setFocusPainted(false);
            pauseBtns[i].setBackground(Color.WHITE);
            pauseBtns[i].setOpaque(true);
            pauseBtns[i].setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            int offset = 20;
            pauseBtns[i].setLocation(pauseDialog.getWidth()/2-pauseBtns[i].getWidth()/2, 140+i*(pauseBtns[i].getHeight()+offset));
            pauseBtns[i].setCursor(toolkit.createCustomCursor(clickGlove, new Point(0, 0), ""));
        }

        pausePane.add(pauseBack, JLayeredPane.DEFAULT_LAYER);
        pausePane.add(pauseMsg, JLayeredPane.MODAL_LAYER);
        pausePane.add(pauseMsg2, JLayeredPane.MODAL_LAYER);
        pauseDialog.setPane(pausePane);
        pauseDialog.setButtons(pauseBtns);

        // ---------- confirmRestartDialog ---------- //
        confirmRestartDialog.setSize(400, 200);
        confirmRestartDialog.setLocationRelativeTo(null);
        confirmRestartDialog.setCursor(toolkit.createCustomCursor(glove, new Point(0, 0), ""));

        JLabel confirmRestartBack = new JLabel(new ImageIcon(getClass().getResource("/images/game/confirm_back.jpg").getFile()));
        confirmRestartBack.setSize(confirmRestartDialog.getSize());
        confirmRestartBack.setLocation(0, 0);

        JLayeredPane confirmRestartPane = new JLayeredPane();
        confirmRestartPane.setSize(confirmRestartDialog.getSize());
        confirmRestartPane.setLocation(0, 0);

        JLabel confirmRestartMsg = new JLabel("<html><font color=#FFFFFF>Are you sure you</font></html>");
        confirmRestartMsg.setFont(new Font("Arial", Font.PLAIN, 28));
        confirmRestartMsg.setHorizontalAlignment(JLabel.CENTER);
        confirmRestartMsg.setSize(400, 36);
        confirmRestartMsg.setLocation(confirmRestartDialog.getWidth()/2-confirmRestartMsg.getWidth()/2, 22);
        JLabel confirmRestartMsg2 = new JLabel("<html><font color=#FFFFFF>want to restart?</font></html>");
        confirmRestartMsg2.setFont(confirmRestartMsg.getFont());
        confirmRestartMsg2.setHorizontalAlignment(JLabel.CENTER);
        confirmRestartMsg2.setSize(confirmRestartMsg.getSize());
        confirmRestartMsg2.setLocation(confirmRestartDialog.getWidth()/2-confirmRestartMsg2.getWidth()/2, confirmRestartMsg.getY()+confirmRestartMsg.getHeight());

        JButton[] confirmRestartBtns = new JButton[2];
        for (int i=0; i<confirmRestartBtns.length; i++) {
            String htmlText = "<html><font color=#000000>" + (i == 0 ? "Yes" : "No") + "</font></html>";
            confirmRestartBtns[i] = new JButton(htmlText);
            confirmRestartBtns[i].setSize(100, 40);
            confirmRestartBtns[i].setFont(new Font("Arial Black", Font.PLAIN, 20));
            confirmRestartBtns[i].setFocusPainted(false);
            confirmRestartBtns[i].setBackground(Color.WHITE);
            confirmRestartBtns[i].setOpaque(true);
            confirmRestartBtns[i].setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            int offset = i == 0 ? -10-confirmRestartBtns[i].getWidth() : 10;
            confirmRestartBtns[i].setLocation(confirmRestartDialog.getWidth()/2+offset, confirmRestartDialog.getHeight()-confirmRestartBtns[i].getHeight()-50);
            confirmRestartBtns[i].setCursor(toolkit.createCustomCursor(clickGlove, new Point(0, 0), ""));
        }

        confirmRestartPane.add(confirmRestartBack, JLayeredPane.DEFAULT_LAYER);
        confirmRestartPane.add(confirmRestartMsg, JLayeredPane.MODAL_LAYER);
        confirmRestartPane.add(confirmRestartMsg2, JLayeredPane.MODAL_LAYER);
        confirmRestartDialog.setPane(confirmRestartPane);
        confirmRestartDialog.setButtons(confirmRestartBtns);

        // ---------- confirmExitDialog ---------- //
        confirmExitDialog.setSize(400, 200);
        confirmExitDialog.setLocationRelativeTo(null);
        confirmExitDialog.setCursor(toolkit.createCustomCursor(glove, new Point(0, 0), ""));

        JLabel confirmExitBack = new JLabel(new ImageIcon(getClass().getResource("/images/game/confirm_back.jpg").getFile()));
        confirmExitBack.setSize(confirmExitDialog.getSize());
        confirmExitBack.setLocation(0, 0);

        JLayeredPane confirmExitPane = new JLayeredPane();
        confirmExitPane.setSize(confirmExitDialog.getSize());
        confirmExitPane.setLocation(0, 0);

        JLabel confirmExitMsg = new JLabel("<html><font color=#FFFFFF>Are you sure you</font></html>");
        confirmExitMsg.setFont(new Font("Arial", Font.PLAIN, 28));
        confirmExitMsg.setHorizontalAlignment(JLabel.CENTER);
        confirmExitMsg.setSize(400, 36);
        confirmExitMsg.setLocation(confirmExitDialog.getWidth()/2-confirmExitMsg.getWidth()/2, 22);
        JLabel confirmExitMsg2 = new JLabel("<html><font color=#FFFFFF>want to exit?</font></html>");
        confirmExitMsg2.setFont(confirmExitMsg.getFont());
        confirmExitMsg2.setHorizontalAlignment(JLabel.CENTER);
        confirmExitMsg2.setSize(confirmExitMsg.getSize());
        confirmExitMsg2.setLocation(confirmExitDialog.getWidth()/2-confirmExitMsg2.getWidth()/2, confirmExitMsg.getY()+confirmExitMsg.getHeight());

        JButton[] confirmExitBtns = new JButton[2];
        for (int i=0; i<confirmExitBtns.length; i++) {
            String htmlText = "<html><font color=#000000>" + (i == 0 ? "Yes" : "No") + "</font></html>";
            confirmExitBtns[i] = new JButton(htmlText);
            confirmExitBtns[i].setSize(100, 40);
            confirmExitBtns[i].setFont(new Font("Arial Black", Font.PLAIN, 20));
            confirmExitBtns[i].setFocusPainted(false);
            confirmExitBtns[i].setBackground(Color.WHITE);
            confirmExitBtns[i].setOpaque(true);
            confirmExitBtns[i].setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            int offset = i == 0 ? -10-confirmExitBtns[i].getWidth() : 10;
            confirmExitBtns[i].setLocation(confirmExitDialog.getWidth()/2+offset, confirmExitDialog.getHeight()-confirmExitBtns[i].getHeight()-50);
            confirmExitBtns[i].setCursor(toolkit.createCustomCursor(clickGlove, new Point(0, 0), ""));
        }

        confirmExitPane.add(confirmExitBack, JLayeredPane.DEFAULT_LAYER);
        confirmExitPane.add(confirmExitMsg, JLayeredPane.MODAL_LAYER);
        confirmExitPane.add(confirmExitMsg2, JLayeredPane.MODAL_LAYER);
        confirmExitDialog.setPane(confirmExitPane);
        confirmExitDialog.setButtons(confirmExitBtns);

        // ---------- musicDialog ---------- //
        musicDialog.setSize(600, 400);
        musicDialog.setLocationRelativeTo(null);
        musicDialog.setCursor(toolkit.createCustomCursor(glove, new Point(0, 0), ""));
        JScrollPane musicScrollPane = new JScrollPane();    // JScrollPane to contain all possible music themes of a stage/scene
        musicScrollPane.setWheelScrollingEnabled(true);     // allows scrolling with mouse wheel
        musicScrollPane.setVerticalScrollBar(musicScrollPane.createVerticalScrollBar());
        JLabel musicMsg = new JLabel();
        JPanel musicThemesPanel = new JPanel();
        musicScrollPane.getViewport().add(musicThemesPanel);

        // ---------------------------------------- //

        // ------- formatting JLayeredPanes ------- //
        // title pane
        titlePane.setLayout(null);
        titlePane.setSize(frameX, frameY);
        titlePane.setLocation(0, 0);
        titleBack.setSize(frameX, frameY);
        titleBack.setLocation(0, 0);
        JLabel subtitle = new JLabel(new ImageIcon(getClass().getResource("/images/title/rumble.png")));
        subtitle.setSize(397, 154);
        subtitle.setLocation(frameX/2-subtitle.getWidth()/2, 305);
        titlePane.add(titleBack, JLayeredPane.DEFAULT_LAYER);
        titlePane.add(subtitle, JLayeredPane.PALETTE_LAYER);

        // menu pane
        menuPane.setLayout(null);
        menuPane.setSize(frameX, frameY);
        menuPane.setLocation(0, 0);
        menuBack.setSize(frameX, frameY);
        menuBack.setLocation(0, 0);
        menuPane.add(menuBack, JLayeredPane.DEFAULT_LAYER);
        menuPane.add(smashBtn, JLayeredPane.DRAG_LAYER);
        menuPane.add(onlineBtn, JLayeredPane.DRAG_LAYER);
        menuPane.add(instructBtn, JLayeredPane.DRAG_LAYER);
        menuPane.add(creditsBtn, JLayeredPane.DRAG_LAYER);
        menuPane.add(backBtn, JLayeredPane.DRAG_LAYER);
        menuPane.add(settingsBtn, JLayeredPane.DRAG_LAYER);

        // choose pane
        choosePane.setLayout(null);
        choosePane.setSize(frameX, frameY);
        choosePane.setLocation(0, 0);
        choosePanel.setSize(frameX, frameY);
        choosePanel.setLocation(0, 0);
        choosePane.add(choosePanel, JLayeredPane.PALETTE_LAYER);
        choosePane.add(lessTimeBtn, JLayeredPane.DRAG_LAYER);
        choosePane.add(moreTimeBtn, JLayeredPane.DRAG_LAYER);
        choosePane.add(nextBtn, JLayeredPane.DRAG_LAYER);
        for (JButton btn : choosePanel.getCharBtns()) {choosePane.add(btn, JLayeredPane.DRAG_LAYER);}
        for (JButton btn : choosePanel.getDisplayCharBtns()) {choosePane.add(btn, JLayeredPane.DRAG_LAYER);}
        for (JButton btn : choosePanel.getRemoveCharBtns()) {choosePane.add(btn, JLayeredPane.DRAG_LAYER);}

        // game pane
        gamePane.setLayout(null);
        gamePane.setSize(frameX, frameY);
        gamePane.setLocation(0, 0);
        game.setSize(frameX, frameY);
        game.setLocation(0, 0);
        game.setCountdown(new Gif(getGifFrames(getClass().getResource("/images/game/countdown/").getFile()), 1000));
        Frame[] frArrows = getGifFrames(getClass().getResource("/images/game/player_arrows/").getFile());
        Image[] arrows = new Image[frArrows.length];
        for (int i=0; i<arrows.length; i++) {
            arrows[i] = frArrows[i].getIcon().getImage();
        }
        game.setArrows(arrows);
        game.setTimeNumbers(getImages(getClass().getResource("/images/game/time/").getFile()));
        game.setDamageNumbers(getImages(getClass().getResource("/images/game/damage/").getFile()));
        gamePane.add(game, JLayeredPane.DEFAULT_LAYER);
        gamePane.add(pauseBtn, JLayeredPane.DRAG_LAYER);

        // instruction pane
        instructPane.setLayout(null);
        instructPane.setSize(frameX, frameY);
        instructPane.setLocation(0, 0);
        JLabel instructPic = new JLabel(new ImageIcon(getClass().getResource("/images/instruct/instruct.png")));
        instructPic.setSize(frameX, frameY);
        instructPic.setLocation(0, 0);
        instructPane.add(instructPic, JLayeredPane.DEFAULT_LAYER);

        // credits pane
        creditsPane.setLayout(null);
        creditsPane.setSize(frameX, frameY);
        creditsPane.setLocation(0, 0);
        JLabel creditsPic = new JLabel(new ImageIcon(getClass().getResource("/images/credits/credits.png")));
        creditsPic.setSize(frameX, frameY);
        creditsPic.setLocation(0, 0);
        creditsPane.add(creditsPic, JLayeredPane.DEFAULT_LAYER);

        // ending pane
        endPane.setLayout(null);
        endPane.setSize(frameX, frameY);
        endPane.setLocation(0, 0);
        winBack = new JLabel(new ImageIcon(getClass().getResource("/images/end/win.png")));
        winBack.setSize(frameX, frameY);
        winBack.setLocation(0, 0);
        loseBack = new JLabel(new ImageIcon(getClass().getResource("/images/end/lose.png")));
        loseBack.setSize(frameX, frameY);
        loseBack.setLocation(0, 0);
        timeBack = new JLabel(new ImageIcon(getClass().getResource("/images/end/time.png")));
        timeBack.setSize(frameX, frameY);
        timeBack.setLocation(0, 0);
        endPane.add(restartBtn, JLayeredPane.DRAG_LAYER);
        endPane.add(homeBtn, JLayeredPane.DRAG_LAYER);

        // ---------------------------------------- //

        openingTheme.setType(SoundFX.MUSIC);
        menuTheme.setType(SoundFX.MUSIC);

        // adding panels to card layout
        cards = new JPanel(cLayout);
        cards.add(titlePane, "title");
        cards.add(menuPane, "menu");
        cards.add(instructPane, "instruct");
        cards.add(creditsPane, "credits");
        cards.add(choosePane, "choose");
        cards.add(gamePane, "game");
        cards.add(endPane, "end");
        add(cards);

        myTimer.start();
        openingTheme.play(SoundFX.LOOP_CONTINUOUSLY);

        addKeyListener(this);
        addMouseListener(this);
        requestFocus();
        setLocationRelativeTo(null);  // centre the window
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        setVisible(true);
    }

    // --------------- ActionListener methods -------------- //
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == myTimer) {
            time+=myTimer.getDelay();
        }

        // clicking play button
        if (source == playBtn && choosePanel.getChosenScene() != null) {
            menuTheme.stop();
            myTimer.stop();
            choosePanel.resetPlayers();
            choosePane.remove(playBtn);
            choosePane.add(lessTimeBtn, JLayeredPane.DRAG_LAYER);
            choosePane.add(moreTimeBtn, JLayeredPane.DRAG_LAYER);
            choosePane.add(nextBtn, JLayeredPane.DRAG_LAYER);
            for (JButton btn : choosePanel.getCharBtns()) {choosePane.add(btn, JLayeredPane.DRAG_LAYER);}
            for (JButton btn : choosePanel.getDisplayCharBtns()) {
                if (Arrays.asList(choosePanel.getDisplayCharBtns()).indexOf(btn) == 0) {
                    btn.setVisible(true);
                    btn.setCursor(toolkit.createCustomCursor(clickGlove, new Point(0, 0), ""));
                }
                else {
                    btn.setVisible(false);
                }
                choosePane.add(btn, JLayeredPane.DRAG_LAYER);
            }
            for (JButton btn : choosePanel.getRemoveCharBtns()) {
                btn.setVisible(false);
                choosePane.add(btn, JLayeredPane.DRAG_LAYER);
            }
            for (JButton btn : choosePanel.getSceneBtns()) {choosePane.remove(btn);}
            Random rand = new Random();
            game.setStarting(true);
            game.setVolume(volume);
            game.setScene(allScenes[choosePanel.getChosenScene() != choosePanel.getSceneBtns().length-1 ? choosePanel.getChosenScene() : rand.nextInt(choosePanel.getSceneBtns().length-1)]);
            game.setTimeLeft(choosePanel.getTime()*60);
            game.setPlayer(players.get(0));
            ComputerPlayer[] cps = new ComputerPlayer[cpus.size()];
            for (int i=0; i<cps.length; i++) {
                cps[i] = cpus.get(i);
            }
            game.setCPUs(cps);
            game.setStatFaces(getStatFaces(players.get(0), cps));
            choosePanel.reset();
            players = new ArrayList<Player>();
            cpus = new ArrayList<ComputerPlayer>();
            cLayout.show(cards, "game");
            prevCard = CHOOSE;
            card = GAME;
            time = 0;
            myTimer.start();
        }
        // clicking SMASH button
        else if (source == smashBtn) {
            if (!choosePane.isAncestorOf(backBtn)) {choosePane.add(backBtn, JLayeredPane.DRAG_LAYER);}
            cLayout.show(cards, "choose");
            prevCard = MENU;
            card = CHOOSE;
            choosePanel.setMode(ChoosePanel.SMASH);
        }
        // clicking instructions button
        else if (source == instructBtn) {
            if (!instructPane.isAncestorOf(backBtn)) {instructPane.add(backBtn, JLayeredPane.DRAG_LAYER);}
            cLayout.show(cards, "instruct");
            prevCard = MENU;
            card = INSTRUCT;
        }
        // clicking credits button
        else if (source == creditsBtn) {
            if (!creditsPane.isAncestorOf(backBtn)) {creditsPane.add(backBtn, JLayeredPane.DRAG_LAYER);}
            cLayout.show(cards, "credits");
            prevCard = MENU;
            card = CREDITS;
        }
        // clicking restart button
        else if (source == restartBtn) {
            game.reset();
            time = 0;
            cLayout.show(cards, "game");
            card = GAME;
            prevCard = END;
            game.requestFocus();
            requestFocus();
        }
        // clicking home button (leads to menu)
        else if (source == homeBtn) {
            game.reset();
            menuTheme.play(SoundFX.LOOP_CONTINUOUSLY);
            cLayout.show(cards, "menu");
            card = MENU;
            prevCard = END;
        }
        // clicking pause button
        else if (source == pauseBtn) {
            pause();
            while (true) {
                pauseDialog.open(); // open dialog box
                int response = pauseDialog.getResponse();
                if (response == 0 || response == -1) {
                    break;
                }
                else if (response == 1) {        // player would like to restart game
                    confirmRestartDialog.open();
                    int response2 = confirmRestartDialog.getResponse();
                    if (response2 == 0) {        // user (for sure) would like to restart
                        game.reset();
                        time = 0;
                        break;
                    }
                }
                else if (response == 2) {   // player would like to quit/exit game
                    confirmExitDialog.open();
                    int response2 = confirmExitDialog.getResponse();
                    if (response2 == 0) {   // user (for sure) would like to exit
                        game.reset();
                        cLayout.show(cards, "menu");
                        prevCard = GAME;
                        card = MENU;
                        menuTheme.play(SoundFX.LOOP_CONTINUOUSLY);
                        break;
                    }
                }
            }
            resume();
            game.requestFocus();
            requestFocus();
        }
        // clicking back button
        else if (source == backBtn) {
            requestFocus();
            // if card showing is menu, back button leads to title screen
            if (card == MENU) {
                cLayout.show(cards, "title");
                prevCard = MENU;
                card = TITLE;
                myTimer.stop();
                myTimer.setDelay(10);
                myTimer.start();
                menuTheme.stop();
                openingTheme.play(SoundFX.LOOP_CONTINUOUSLY);
            }
            // if card showing is choosing character, back button leads to menu screen
            else if (card == CHOOSE && choosePanel.getChooseType() == ChoosePanel.CHARACTER || card == INSTRUCT || card == CREDITS) {
                if (!menuPane.isAncestorOf(backBtn)) {menuPane.add(backBtn, JLayeredPane.DRAG_LAYER);}
                cLayout.show(cards, "menu");
                prevCard = card;
                card = MENU;
                choosePanel.resetChosen();
                nextBtn.setCursor(toolkit.createCustomCursor(glove, new Point(0, 0), ""));
                nextBtn.setRolloverEnabled(false);
            }
            // if card showing is choosing scene, back button leads to choosing character
            else if (card == CHOOSE && choosePanel.getChooseType() == ChoosePanel.SCENE) {
                choosePanel.setChooseType(ChoosePanel.CHARACTER);
                choosePanel.setChosenScene(null);
                for (JButton btn : choosePanel.getCharBtns()) {choosePane.add(btn, JLayeredPane.DRAG_LAYER);}
                for (JButton btn : choosePanel.getDisplayCharBtns()) {choosePane.add(btn, JLayeredPane.DRAG_LAYER);}
                for (JButton btn : choosePanel.getRemoveCharBtns()) {choosePane.add(btn, JLayeredPane.DRAG_LAYER);}
                for (JButton btn : choosePanel.getSceneBtns()) {choosePane.remove(btn);}
                choosePane.add(lessTimeBtn, JLayeredPane.DRAG_LAYER);
                choosePane.add(moreTimeBtn, JLayeredPane.DRAG_LAYER);
                choosePane.add(nextBtn, JLayeredPane.DRAG_LAYER);
                choosePane.remove(playBtn);
                playBtn.setCursor(toolkit.createCustomCursor(glove, new Point(0, 0), ""));
                playBtn.setRolloverEnabled(false);
            }
        }
        // clicking next button (from choosing character to choosing scene)
        else if (source == nextBtn) {
            if (card == CHOOSE && choosePanel.getChooseType() == ChoosePanel.CHARACTER && choosePanel.getPlayers() > 1) {
                choosePanel.setChooseType(ChoosePanel.SCENE);
                for (JButton btn : choosePanel.getCharBtns()) {choosePane.remove(btn);}
                for (JButton btn : choosePanel.getDisplayCharBtns()) {choosePane.remove(btn);}
                for (JButton btn : choosePanel.getRemoveCharBtns()) {choosePane.remove(btn);}
                for (JButton btn : choosePanel.getSceneBtns()) {choosePane.add(btn, JLayeredPane.DRAG_LAYER);}
                choosePane.remove(lessTimeBtn);
                choosePane.remove(moreTimeBtn);
                choosePane.remove(nextBtn);
                choosePane.add(playBtn, JLayeredPane.DRAG_LAYER);
            }
        }
        // clicking settings button
        else if (source == settingsBtn) {
            pause();
            settingsDialog.open();
            int response = settingsDialog.getResponse();
            if (response == 0) {
                volume = volumeSlider.getValue();
                openingTheme.setVolume(volume/100.0);
                menuTheme.setVolume(volume/100.0);
            }
            else {
                volumeSlider.setValue(volume);
            }
            resume();
            requestFocus();
        }
        // clicking less time button
        else if (source == lessTimeBtn) {
            if (choosePanel.getTime() == ChoosePanel.MINIMUM_TIME) {
                choosePanel.setTime(ChoosePanel.MAXIMUM_TIME);
            }
            else if (choosePanel.getTime() > ChoosePanel.MINIMUM_TIME) {
                choosePanel.setTime(choosePanel.getTime()-1);
            }
        }
        // clicking more time button
        else if (source == moreTimeBtn) {
            if (choosePanel.getTime() == ChoosePanel.MAXIMUM_TIME) {
                choosePanel.setTime(ChoosePanel.MINIMUM_TIME);
            }
            else if (choosePanel.getTime() < ChoosePanel.MAXIMUM_TIME) {
                choosePanel.setTime(choosePanel.getTime()+1);
            }
        }
        // choosing a character
        else if (choosePanel.getMode() == ChoosePanel.SMASH && Arrays.asList(choosePanel.getCharBtns()).contains(source)) {
            JButton[] charBtns = choosePanel.getCharBtns();
            JButton btn = (JButton) source;
            int ind = Arrays.asList(charBtns).indexOf(btn);
            choosePanel.setChosen(ind, 0);
            Random rand = new Random();
            if (players.size() == 0) {
                players.add(allCharacters[ind == allCharacters.length ? rand.nextInt(allCharacters.length) : ind].clone());
            }
            else {
                players.set(0, allCharacters[ind == allCharacters.length ? rand.nextInt(allCharacters.length) : ind].clone());
            }
        }
        // choosing a computer character
        else if (choosePanel.getMode() == ChoosePanel.SMASH && Arrays.asList(choosePanel.getDisplayCharBtns()).contains(source)) {
            // if player wants to add a computer player
            JButton[] displayCharBtns = choosePanel.getDisplayCharBtns(), removeCharBtns = choosePanel.getRemoveCharBtns();
            JButton btn = (JButton) source;
            int ind = Arrays.asList(displayCharBtns).indexOf(btn);
            if (ind == cpus.size()) {
                // add to cpus ArrayList
                cpus.add(new ComputerPlayer(new ImageIcon(getClass().getResource("/images/cursors/cpu.png")).getImage()));
                Random rand = new Random();
                int c = rand.nextInt(allCharacters.length+1);
                cpus.get(ind).setPlayer(allCharacters[c == allCharacters.length ? rand.nextInt(allCharacters.length) : c].clone());
                choosePanel.setChosen(c, ind+1);
                if (ind == choosePanel.getPlayers()-1 && ind != displayCharBtns.length) {
                    // player can move on to choose scene if there are 2 or more players
                    if (choosePanel.getPlayers() == 1) {
                        nextBtn.setCursor(toolkit.createCustomCursor(clickGlove, new Point(0, 0), ""));
                        nextBtn.setRolloverEnabled(true);
                        nextBtn.setRolloverIcon(new ImageIcon(getClass().getResource("/images/choose/next_hover.png")));
                    }
                    choosePanel.addPlayer();
                    displayCharBtns[ind].setVisible(false);
                    removeCharBtns[ind].setVisible(true);
                    if (ind < displayCharBtns.length-1) {
                        displayCharBtns[ind+1].setVisible(true);
                        displayCharBtns[ind+1].setCursor(toolkit.createCustomCursor(clickGlove, new Point(0, 0), ""));
                    }
                }
            }
        }
        // removing a computer player
        else if (choosePanel.getMode() == ChoosePanel.SMASH && Arrays.asList(choosePanel.getRemoveCharBtns()).contains(source)) {
            // if player wants to remove a computer player
            JButton[] displayCharBtns = choosePanel.getDisplayCharBtns(), removeCharBtns = choosePanel.getRemoveCharBtns();
            JButton btn = (JButton) source;
            int ind = Arrays.asList(removeCharBtns).indexOf(btn);
            cpus.remove(ind);
            for (JButton button : displayCharBtns) {button.setCursor(toolkit.createCustomCursor(glove, new Point(0, 0), ""));}
            if (ind < choosePanel.getPlayers()-2) {
                displayCharBtns[choosePanel.getPlayers()-2].setCursor(toolkit.createCustomCursor(clickGlove, new Point(0, 0), ""));
                displayCharBtns[choosePanel.getPlayers()-2].setVisible(true);
                removeCharBtns[choosePanel.getPlayers()-2].setVisible(false);
                choosePanel.getChosen()[ind+1] = null;
                for (int i=ind+1; i<choosePanel.getPlayers()-1; i++) {
                    choosePanel.getChosen()[i] = choosePanel.getChosen()[i+1];
                    choosePanel.getChosen()[i+1] = null;
                }
            }
            else {
                displayCharBtns[ind].setCursor(toolkit.createCustomCursor(clickGlove, new Point(0, 0), ""));
                displayCharBtns[ind].setVisible(true);
                removeCharBtns[ind].setVisible(false);
                choosePanel.getChosen()[ind+1] = null;
            }
            // player can no longer move on to choosing a scene if there are less than 2 players
            if (choosePanel.getPlayers() == 2) {
                nextBtn.setCursor(toolkit.createCustomCursor(glove, new Point(0, 0), ""));
                nextBtn.setRolloverEnabled(false);
            }
            choosePanel.removePlayer();
        }
        // choosing a scene
        else if (choosePanel.getMode() == ChoosePanel.SMASH && Arrays.asList(choosePanel.getSceneBtns()).contains(source)) {
            // if player clicks a button to choose a scene, play button can be clicked to start the match
            JButton btn = (JButton) source;
            int ind = Arrays.asList(choosePanel.getSceneBtns()).indexOf(btn);
            choosePanel.setChosenScene(ind);
            playBtn.setCursor(toolkit.createCustomCursor(clickGlove, new Point(0, 0), ""));
            playBtn.setRolloverEnabled(true);
            playBtn.setRolloverIcon(new ImageIcon(getClass().getResource("/images/choose/play_hover.png")));
        }
        else if (source == myTimer && card == TITLE) {
            // change opening frame
            titlePane.remove(titleBack);
            titlePane.revalidate();
            titleBack = new JLabel(opening.getImageIcon(time));
            titleBack.setSize(frameX, frameY);
            titleBack.setLocation(0, 0);
            titlePane.add(titleBack, JLayeredPane.DEFAULT_LAYER);
            titlePane.repaint();
        }
        else if (source == myTimer && card == MENU) {
            if (!menuPane.isAncestorOf(menuBack)) {menuPane.add(menuBack, JLayeredPane.DEFAULT_LAYER);}
            // change spacetime frame
            menuPane.remove(menuBack);
            menuPane.revalidate();
            menuBack = new JLabel(spaceTime.getImageIcon(time));
            menuBack.setSize(frameX, frameY);
            menuBack.setLocation(0, 0);
            menuPane.add(menuBack, JLayeredPane.DEFAULT_LAYER);
            menuPane.repaint();
        }
        else if (source == myTimer && card == CHOOSE) {
            if (!choosePane.isAncestorOf(menuBack)) {choosePane.add(menuBack, JLayeredPane.DEFAULT_LAYER);}
            // change spacetime frame
            choosePane.remove(menuBack);
            choosePane.revalidate();
            menuBack = new JLabel(spaceTime.getImageIcon(time));
            menuBack.setSize(frameX, frameY);
            menuBack.setLocation(0, 0);
            choosePane.add(menuBack, JLayeredPane.DEFAULT_LAYER);
            choosePane.repaint();
            if (choosePanel.getChosen()[0] != null && choosePanel.getChosen()[1] != null) {
                nextBtn.setCursor(toolkit.createCustomCursor(clickGlove, new Point(0, 0), ""));
                nextBtn.setRolloverEnabled(true);
                nextBtn.setRolloverIcon(new ImageIcon(getClass().getResource("/images/choose/next_hover.png")));
            }
            else {
                nextBtn.setCursor(toolkit.createCustomCursor(glove, new Point(0, 0), ""));
                nextBtn.setRolloverEnabled(false);
            }
        }
        // updating game state
        else if (source == myTimer && card == GAME) {
            game.update(time);
            if (game.getPlayer().getStock() == 0 || game.getPlayer().getDamage() == 999) {
                if (!endPane.isAncestorOf(loseBack)) {endPane.add(loseBack, JLayeredPane.DEFAULT_LAYER);}
                if (endPane.isAncestorOf(winBack)) {endPane.remove(winBack);}
                if (endPane.isAncestorOf(timeBack)) {endPane.remove(timeBack);}
                game.getScene().getTrackPlaying().stop();
                SoundFX endSound = new SoundFX(getClass().getResource("/sounds/announcer/failure.wav").getFile(), volume/100.0);
                endSound.play(0);
                cLayout.show(cards, "end");
                card = END;
                prevCard = GAME;
            }
            else {
                // check if player has won or lost (has zero stock or 999% damage)
                boolean won = true;
                for (ComputerPlayer cp : game.getCPUs()) {
                    if (cp.getPlayer().getStock() != 0 && cp.getPlayer().getDamage() != 999) {
                        won = false;
                    }
                }
                if (won) {
                    if (!endPane.isAncestorOf(winBack)) {endPane.add(winBack, JLayeredPane.DEFAULT_LAYER);}
                    if (endPane.isAncestorOf(loseBack)) {endPane.remove(loseBack);}
                    if (endPane.isAncestorOf(timeBack)) {endPane.remove(timeBack);}
                    game.getScene().getTrackPlaying().stop();
                    SoundFX endSound = new SoundFX(getClass().getResource("/sounds/announcer/success.wav").getFile(), volume/100.0);
                    endSound.play(0);
                    cLayout.show(cards, "end");
                    card = END;
                    prevCard = GAME;
                }
            }
            // no one wins if time runs out
            if (game.getTimeLeft() == 0) {
                if (!endPane.isAncestorOf(timeBack)) {endPane.add(timeBack, JLayeredPane.DEFAULT_LAYER);}
                if (endPane.isAncestorOf(winBack)) {endPane.remove(winBack);}
                if (endPane.isAncestorOf(loseBack)) {endPane.remove(loseBack);}
                game.getScene().getTrackPlaying().stop();
                SoundFX endSound = new SoundFX(getClass().getResource("/sounds/announcer/time.wav").getFile(), volume/100.0);
                endSound.play(0);
                cLayout.show(cards, "end");
                card = END;
                prevCard = GAME;
            }
            gamePane.requestFocus();
            requestFocus();
        }
    }
    // ----------------------------------------------------- //

    // --------------- KeyListener methods -------------- //
    public void keyTyped(KeyEvent e) {}
    public void keyPressed(KeyEvent e) {
        game.setKey(e.getKeyCode(), true);
        if (card == TITLE) {
            cLayout.show(cards, "menu");
            prevCard = TITLE;
            card = MENU;
            openingTheme.stop();
            menuTheme.play(SoundFX.LOOP_CONTINUOUSLY);
        }
    }
    public void keyReleased(KeyEvent e) {game.setKey(e.getKeyCode(), false);}
    // -------------------------------------------------- //

    // -------------- MouseListener methods ------------- //
    public void mouseClicked(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {
        if (card == TITLE) {
            cLayout.show(cards, "menu");
            prevCard = card;
            card = MENU;
            openingTheme.stop();
            menuTheme.play(SoundFX.LOOP_CONTINUOUSLY);
        }
    }
    public void mouseReleased(MouseEvent e) {}
    // -------------------------------------------------- //

    // pause - pauses activity e.g. when JDialog opens
    public void pause() {
        myTimer.stop();
    }

    // resume - resumes activity e.g. when JDialog closes
    public void resume() {
        myTimer.start();
    }

    // --------------------------- LOADING IMAGES --------------------------- //

    // getImages - returns an array of images given the directory
    public Image[] getImages(String dir) {
        Image[] imgs = new Image[0];
        try {
            File f = new File(dir);
            imgs = new Image[f.list().length];
            for (int i=0; i<f.list().length; i++) {
                imgs[i] = new ImageIcon(dir+f.list()[i]).getImage();
            }
        }
        catch(Exception ex) {
            System.err.println("getImages: "+ex);
        }
        return imgs;
    }

    // getGifFrames - returns an array of Frame objects given the directory
    public Frame[] getGifFrames(String dir) {
        Frame[] frames = new Frame[0];
        try {
            File f = new File(dir);
            frames = new Frame[f.list().length];
            for (int i=0; i<f.list().length; i++) {
                frames[i] = new Frame(new ImageIcon(dir+f.list()[i]));
            }
        }
        catch(Exception ex) {
            System.out.println("getGifFrames "+new File(dir));
            ex.printStackTrace();
        }
        return frames;
    }

    // --------------------------- CREATING OBJECTS --------------------------- //

    // makeCharacters - creates all Player objects by taking in the file directory which leads to txt file
    public void makeCharacters(String dir) {
        try {
            Scanner inFile = new Scanner(new File(getClass().getResource(dir).getFile()));
            int n = Integer.parseInt(inFile.nextLine());
            String spriteDir = "/images/sprites/", attackDir = "/info/attacks/";
            allCharacters = new Player[n];
            for (int i=0; i<n; i++) {
                String[] info = inFile.nextLine().split(",");
                String filename = info[0].replace(" ", "_");
                // all sprite sequences
                Gif[] idle = new Gif[] {new Gif(getGifFrames(getClass().getResource(spriteDir+filename+"/idle/left/").getFile()), Integer.parseInt(info[3])), new Gif(getGifFrames(getClass().getResource(spriteDir+filename+"/idle/right/").getFile()), Integer.parseInt(info[3]))};
                Gif[] run = new Gif[] {new Gif(getGifFrames(getClass().getResource(spriteDir+filename+"/running/left/").getFile()), Integer.parseInt(info[4])), new Gif(getGifFrames(getClass().getResource(spriteDir+filename+"/running/right/").getFile()), Integer.parseInt(info[4]))};
                Gif[][] jump = new Gif[][] {new Gif[] {new Gif(getGifFrames(getClass().getResource(spriteDir+filename+"/jumping/left/up/").getFile()), Integer.parseInt(info[4])), new Gif(getGifFrames(getClass().getResource(spriteDir+filename+"/jumping/left/down/").getFile()), Integer.parseInt(info[5]))}, {new Gif(getGifFrames(getClass().getResource(spriteDir+filename+"/jumping/right/up/").getFile()), Integer.parseInt(info[5])), new Gif(getGifFrames(getClass().getResource(spriteDir+filename+"/jumping/right/down/").getFile()), Integer.parseInt(info[5]))}};
                Gif[] crouch = new Gif[] {new Gif(getGifFrames(getClass().getResource(spriteDir+filename+"/crouching/left/").getFile()), Integer.parseInt(info[6])), new Gif(getGifFrames(getClass().getResource(spriteDir+filename+"/crouching/right/").getFile()), Integer.parseInt(info[6]))};
                Gif[] injured = new Gif[] {new Gif(getGifFrames(getClass().getResource(spriteDir+filename+"/injured/left/").getFile()), Integer.parseInt(info[7])), new Gif(getGifFrames(getClass().getResource(spriteDir+filename+"/injured/right/").getFile()), Integer.parseInt(info[7]))};
                Attack[] attacks = getAttacks(filename, attackDir+filename+".txt", spriteDir);
                Player ch = new Player(info[0], info[1], 0, 0, Integer.parseInt(info[2]), 0, 0, idle, run, jump, crouch, injured, attacks, 150);
                allCharacters[i] = ch;
            }
            inFile.close();
        }
        catch(Exception ex) {
            System.err.println("makeCharacters");
            ex.printStackTrace();
        }
    }

    // getAttacks - returns array of character's attacks
    public Attack[] getAttacks(String filename, String attackDir, String spriteDir) {
        Attack[] attacks = new Attack[0];
        try {
            Scanner inFile = new Scanner(new File(getClass().getResource(attackDir).getFile()));
            int n = Integer.parseInt(inFile.nextLine());
            attacks = new Attack[n];
            for (int i=0; i<n; i++) {
                String[] info = inFile.nextLine().split(",");
                int key = info[5].equals("F") ? KeyEvent.VK_F : info[5].equals("D") ? KeyEvent.VK_D : info[5].equals("S") ? KeyEvent.VK_S : info[5].equals("A") ? KeyEvent.VK_A : KeyEvent.VK_G;
                // if attack requires jumping and is shooting type
                if (Integer.parseInt(info[3]) == Player.JUMPING && Integer.parseInt(info[2]) == Attack.SHOOT) {
                    Gif[][] jumpAtk = new Gif[][] {new Gif[] {new Gif(getGifFrames(getClass().getResource(spriteDir+filename+"/"+info[0]+"/left/up/").getFile()), Integer.parseInt(info[4])), new Gif(getGifFrames(getClass().getResource(spriteDir+filename+"/"+info[0]+"/left/down/").getFile()), Integer.parseInt(info[4]))}, {new Gif(getGifFrames(getClass().getResource(spriteDir+filename+"/"+info[0]+"/right/up/").getFile()), Integer.parseInt(info[4])), new Gif(getGifFrames(getClass().getResource(spriteDir+filename+"/"+info[0]+"/right/down/").getFile()), Integer.parseInt(info[4]))}};
                    Projectile proj = getProjectile(spriteDir+filename+"/"+info[0]+"/projectile/", info[1], Integer.parseInt(info[8]), Integer.parseInt(info[9]), Integer.parseInt(info[7]), Integer.parseInt(info[10]), Integer.parseInt(info[11]), Integer.parseInt(info[12]), Integer.parseInt(info[13]));
                    attacks[i] = new Attack(info[1], Integer.parseInt(info[2]), Integer.parseInt(info[3]), jumpAtk, proj, key, Integer.parseInt(info[6]));
                }
                // if attack requires jumping and is not shooting type
                else if (Integer.parseInt(info[3]) == Player.JUMPING && Integer.parseInt(info[2]) != Attack.SHOOT) {
                    Gif[][] jumpAtk = new Gif[][] {new Gif[] {new Gif(getGifFrames(getClass().getResource(spriteDir+filename+"/"+info[0]+"/left/up/").getFile()), Integer.parseInt(info[4])), new Gif(getGifFrames(getClass().getResource(spriteDir+filename+"/"+info[0]+"/left/down/").getFile()), Integer.parseInt(info[4]))}, {new Gif(getGifFrames(getClass().getResource(spriteDir+filename+"/"+info[0]+"/right/up/").getFile()), Integer.parseInt(info[4])), new Gif(getGifFrames(getClass().getResource(spriteDir+filename+"/"+info[0]+"/right/down/").getFile()), Integer.parseInt(info[4]))}};
                    attacks[i] = new Attack(info[1], Integer.parseInt(info[2]), Integer.parseInt(info[3]), jumpAtk, key, Integer.parseInt(info[6]));
                }
                // if attack does not require jumping and is shooting type
                else if (Integer.parseInt(info[3]) != Player.JUMPING && Integer.parseInt(info[2]) == Attack.SHOOT) {
                    Gif[] regAtk = new Gif[] {new Gif(getGifFrames(getClass().getResource(spriteDir+filename+"/"+info[0]+"/left/").getFile()), Integer.parseInt(info[4])), new Gif(getGifFrames(getClass().getResource(spriteDir+filename+"/"+info[0]+"/right/").getFile()), Integer.parseInt(info[4]))};
                    Projectile proj = getProjectile(spriteDir+filename+"/"+info[0]+"/projectile/", info[1], Integer.parseInt(info[8]), Integer.parseInt(info[9]), Integer.parseInt(info[7]), Integer.parseInt(info[10]), Integer.parseInt(info[11]), Integer.parseInt(info[12]), Integer.parseInt(info[13]));
                    attacks[i] = new Attack(info[1], Integer.parseInt(info[2]), Integer.parseInt(info[3]), regAtk, proj, key, Integer.parseInt(info[6]));
                }
                // if attack does not require jumping and is not shooting type
                else {
                    Gif[] regAtk = new Gif[] {new Gif(getGifFrames(getClass().getResource(spriteDir+filename+"/"+info[0]+"/left/").getFile()), Integer.parseInt(info[4])), new Gif(getGifFrames(getClass().getResource(spriteDir+filename+"/"+info[0]+"/right/").getFile()), Integer.parseInt(info[4]))};
                    attacks[i] = new Attack(info[1], Integer.parseInt(info[2]), Integer.parseInt(info[3]), regAtk, key, Integer.parseInt(info[6]));
                }
            }
        }
        catch(Exception ex) {
            System.err.println("getAttacks");
            ex.printStackTrace();
        }
        return attacks;
    }

    // getProjectile - gets list of projectiles used when character is shooting something
    public Projectile getProjectile(String dir, String name, int x, int y, int aimType, int vx, int vy, int period, int time) {
        Projectile projs = null;
        Gif[] projSeq = new Gif[] {new Gif(getGifFrames(getClass().getResource(dir+"left/").getFile()), period), new Gif(getGifFrames(getClass().getResource(dir+"right/").getFile()), period)} ;
        int gifType = projSeq[0].size() == 1 ? Projectile.STATIC : Projectile.DYNAMIC;
        projs = new Projectile(name, x, y, 0, aimType, gifType, vx, vy, time, projSeq);
        return projs;
    }

    // makeScenes - creates all Scene objects by taking in the file directory which leads to txt file
    public void makeScenes(String dir) {
        try {
            Scanner inFile = new Scanner(new File(getClass().getResource(dir).getFile()));
            int n = Integer.parseInt(inFile.nextLine());
            allScenes = new Scene[n];
            for (int i=0; i<n; i++) {
                String[] inf = inFile.nextLine().split(";"), info = inf[0].split(","), pointInfo = inf[2].split(",");
                Image img = new ImageIcon(getClass().getResource("/images/scenes/"+info[0].replace(" ", "_")+".jpg")).getImage();
                Point[] startingPts = new Point[pointInfo.length/2-1];
                for (int j=0; j<startingPts.length; j++) {
                    startingPts[j] = new Point(Integer.parseInt(pointInfo[j*2]), Integer.parseInt(pointInfo[j*2+1]));
                }
                Point spawningPt = new Point(Integer.parseInt(pointInfo[pointInfo.length-2]), Integer.parseInt(pointInfo[pointInfo.length-1]));
                SoundFX theme = new SoundFX(getClass().getResource("/sounds/scenes/"+info[0].replace(" ", "_")+"/"+info[3]+".wav").getFile());
                Scene s = new Scene(info[0], Double.parseDouble(info[1]), Integer.parseInt(info[2]), img, getPlatforms(info[0], inf[1].split(",")), startingPts, spawningPt, theme);
                allScenes[i] = s;
            }
            inFile.close();
        }
        catch(Exception ex) {
            System.err.println("makeScenes: "+ex);
            ex.printStackTrace();
        }
    }

    // getPlatforms - returns all Platorm objects
    public Platform[] getPlatforms(String name, String[] nums) {
        if (nums.length % 8 == 0) {
            Platform[] platforms = new Platform[nums.length/8];
            for (int i=0; i<platforms.length; i++) {
                int[] x = new int[] {Integer.parseInt(nums[0+8*i]), Integer.parseInt(nums[1+8*i]), Integer.parseInt(nums[2+8*i]), Integer.parseInt(nums[3+8*i])};
                int[] y = new int[] {Integer.parseInt(nums[4+8*i]), Integer.parseInt(nums[5+8*i]), Integer.parseInt(nums[6+8*i]), Integer.parseInt(nums[7+8*i])};
                platforms[i] = new Platform(x, y, 4);
            }
            return platforms;
        }
        else {
            System.out.println(name+": Invalid number of points.");
            return new Platform[0];
        }
    }

    // getSoundtrack - returns array of SoundFX objects containing clips of all background music available for scene
    public SoundFX[] getSoundtrack(String dir) {
        try {

        }
        catch(Exception ex) {

        }
        return new SoundFX[0];
    }

    // ------------------------------------------------------------------------ //


    // ------------------- loading more images for GamePanel ------------------- //
    public Image[] getStatFaces(Player player, ComputerPlayer[] cpus) {
        Image[] faces = new Image[cpus.length+1];
        faces[0] = new ImageIcon(getClass().getResource("/images/game/stat_faces/"+player.getName().replace(" ", "_")+".png").getFile()).getImage();
        for (int i=0; i<cpus.length; i++) {
            faces[i+1] = new ImageIcon(getClass().getResource("/images/game/stat_faces/"+cpus[i].getPlayer().getName().replace(" ", "_")+".png").getFile()).getImage();
        }
        return faces;
    }
}
