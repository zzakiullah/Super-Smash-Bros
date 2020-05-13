/* DialogBox.java
 * Zulaikha Zakiullah
 * This class deals with dialog boxes and allows for more customization such as changing the background and styling the buttons.
 */

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.*;
import java.awt.*;
import java.util.*;

class DialogBox extends JDialog implements ActionListener, ChangeListener {
    public static final int VERTICAL = 0, HORIZONTAL = 1;
    private JFrame parent;
    private Image icon;
    private JLabel text;
    private JLayeredPane pane;
    private JButton[] buttons;
    private int response;

    public DialogBox(String title, Image img, JFrame parent) {
        super(parent, title);
        this.parent = parent;
        response = -1;
        if (img != null) {setIconImage(img);}
        setLayout(null);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(false);
        setModalityType(Dialog.DEFAULT_MODALITY_TYPE);
    }

    // --------------- ActionListener methods -------------- //
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source instanceof JButton) {
            response = Arrays.asList(buttons).indexOf(source);
            setVisible(false);
            dispose();
        }
    }
    // ----------------------------------------------------- //

    // --------------- ChangeListener methods -------------- //
    public void stateChanged(ChangeEvent e) {
        Object source = e.getSource();

        if (source instanceof JSlider) {
            JSlider slider = (JSlider) source;
            if (text != null) {
                text.setText("<html><font color=#FFFFFF>Volume: "+slider.getValue()+"</font></html>");
                text.setLocation(getWidth()/2-text.getWidth()/2, text.getY());
                revalidate();
                repaint();
            }
        }
    }
    // ----------------------------------------------------- //

    // ------------ all getter methods ------------ //
    public int getResponse() {return response;}
    public JLabel getText() {return text;}

    // ------------ all setter methods ------------ //
    public void setIcon(Image i) {icon = i;}
    public void setButtons(JButton[] buttons) {
        this.buttons = new JButton[buttons.length];
        for (int i=0; i<buttons.length; i++) {
            this.buttons[i] = buttons[i];
            this.buttons[i].addActionListener(this);
            if (pane != null) {
                pane.add(this.buttons[i], JLayeredPane.DRAG_LAYER);
            }
        }
    }
    public void setText(JLabel txt) {text = txt;}
    public void setPane(JLayeredPane pane) {
        this.pane = pane;
        getContentPane().add(pane);
    }

    public void open() {
        setVisible(true);
        requestFocus();
    }

    /*
    // showOptionDialog - displays dialog box that displays text and allows user to click an option
    public int showOptionDialog(Object[] message, Font font, JButton[] buttons, int buttonArrangement) {
        int width = findWidth();
        int height = findHeight();
        JLabel label = new JLabel();
        label.setFont(font);
        FontMetrics metrics = getFontMetrics(font);
        for (Object msg : message) {
            if (msg.getClass().isAssignableFrom(String.class)) {
                label.setText((String) msg);
            }
            else if (msg.getClass().isAssignableFrom(JSlider.class)) {

            }
        }
        for (JButton btn : buttons) {btn.addActionListener(this);}
        if (buttonArrangement == VERTICAL) {        // if user wants buttons to be arranged vertically

        }
        else if (buttonArrangement == HORIZONTAL) { // if user wants buttons to be arranged verticallyly

        }
        setVisible(true);
        return response;
    }
    // showInputDialog - displays dialog box that displays message and allows user to input something e.g. text in a text box
    public int showInputDialog(Object[] message, Font font, JButton[] buttons, int buttonArrangement) {
        JLabel label = new JLabel();
        label.setFont(font);
        for (Object msg : message) {
            if (msg.getClass().isAssignableFrom(String.class)) {
                label.setText((String) msg);
            }
        }
        for (JButton btn : buttons) {btn.addActionListener(this);}
        if (buttonArrangement == VERTICAL) {        // if user wants buttons to be arranged vertically

        }
        else if (buttonArrangement == HORIZONTAL) { // if user wants buttons to be arranged verticallyly

        }
        setVisible(true);
        return response;
    }*/
}
