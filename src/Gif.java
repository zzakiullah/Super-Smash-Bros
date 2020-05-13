import javax.swing.*;
import java.awt.*;

class Gif {
    public final int period;			// period - amount of time per frame
    private Frame first, current, last;	// first - first frame in gif; current - current frame; last - last frame in gif
    private int currentIndex;

    public Gif(Frame[] seq, int period) {
        for (Frame f : seq) {
            add(f);
        }
        current = first;
        this.period = period;
        currentIndex = 0;
    }
    public void add(Frame f) {
        if (first == null) {
            first = last = f;
            last.setNext(first);
        }
        else {
            last.setNext(f);
            last = f;
            last.setNext(first);	// loops gif
        }
    }

    // --------------- all getters --------------- //
    public ImageIcon getFirst() {return first.getIcon();}
    public ImageIcon getLast() {return last.getIcon();}
    public int getPeriod() {return period;}
    public int getCurrentIndex() {return currentIndex;}

    public ImageIcon getImageIcon(int time) {   // returns current frame as image
        current = time != 0 && time%period==0 ? current.getNext() : current;
        return current.getIcon();
    }
    public Frame getFrame(int time) {   // returns current frame as image
        current = time != 0 && time%period==0 ? current.getNext() : current;
        return current;
    }
    public Rectangle getHitbox(int time) {   // returns current frame as image
        current = time != 0 && time%period==0 ? current.getNext() : current;
        return current.getHitbox();
    }
    public Frame getCurrent() {return current;}

    public ImageIcon getImageIconAt(int index) {   // returns current frame as image
        Frame tmp = first;
        int ind = 0;
        while ((tmp == first && ind == 0) || (tmp != first && ind != 0)) {
            if (ind == index) {
                current = tmp;
                return current.getIcon();
            }
            else {
                tmp = tmp.getNext();
                ind++;
            }
        }
        return null;
    }
    public Frame getFrameAt(int index) {   // returns current frame as image
        Frame tmp = first;
        int ind = 0;
        while ((tmp == first && ind == 0) || (tmp != first && ind != 0)) {
            if (ind == index) {
                current = tmp;
                return current;
            }
            else {
                tmp = tmp.getNext();
                ind++;
            }
        }
        return null;
    }
    public Rectangle getHitboxAt(int index) {
        Frame tmp = first;
        int ind = 0;
        while ((tmp == first && ind == 0) || (tmp != first && ind != 0)) {
            if (ind == index) {
                current = tmp;
                return current.getHitbox();
            }
            else {
                tmp = tmp.getNext();
                ind++;
            }
        }
        return null;
    }

    public int getIndexOfCurrent() {
        int ind = 0;
        Frame tmp = first;
        while (tmp != current) {
            ind++;
            tmp = tmp.getNext();
        }
        return ind;
    }

    public void setCurrentIndex(int c) {currentIndex = c;}

    public int size() {
        Frame tmp = first;
        int n = 1;
        while (tmp.getNext() != first) {
            n++;
            tmp = tmp.getNext();
        }
        return n;
    }
}
