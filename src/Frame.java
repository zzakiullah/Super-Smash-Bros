import javax.swing.*;
import java.awt.image.*;
import java.awt.*;

class Frame {
    private Frame next;
    private ImageIcon icon;
    private Rectangle hitbox;

    public Frame(ImageIcon icon) {
        this.icon = icon;
        makeHitbox();
    }

    // makeHitbox - makes Rectangle representing hitbox of Frame
    public void makeHitbox() {
        // ----- code to convert ImageIcon to BufferedImage taken from StackOverflow ----- //
        BufferedImage bi = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics g = bi.createGraphics();
        // paint the ImageIcon to the BufferedImage
        icon.paintIcon(null, g, 0,0);
        g.dispose();
        // ------------------------------------------------------------------------------- //
        // find range of hitbox
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;
        for (int x=0; x<bi.getWidth(); x++) {
            for (int y=0; y<bi.getHeight(); y++) {
                if (bi.getRGB(x, y) != 0 && x < minX) {
                    minX = x;
                }
                if (bi.getRGB(x, y) != 0 && y < minY) {
                    minY = y;
                }
                if (bi.getRGB(x, y) != 0 && x > maxX) {
                    maxX = x;
                }
                if (bi.getRGB(x, y) != 0 && y > maxY) {
                    maxY = y;
                }
            }
        }
        if (minX == Integer.MAX_VALUE && minY == Integer.MAX_VALUE && maxX == Integer.MIN_VALUE && maxY == Integer.MIN_VALUE) {
            hitbox = null;
        }
        else {
            hitbox = new Rectangle(minX, minY, maxX-minX, maxY-minY);
        }
    }

    public Frame getNext() {return next;}
    public ImageIcon getIcon() {return icon;}
    public Rectangle getHitbox() {return hitbox;}

    public void setNext(Frame n) {next = n;}
}
