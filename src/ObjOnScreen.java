import java.awt.*;

class ObjOnScreen {
    public static final int LEFT = 0, RIGHT = 1;
    private String name;					// name - e.g. player name, attack name
    private int x, y, dir;	// x, y - object's position; width, height - object's dimensions; frame - frame number in seq; dir - direction object is facing (left or right)

    public ObjOnScreen(String name, int x, int y, int dir) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.dir = dir;
    }

    // ------------ all getter methods ------------ //
    public String getName() {return name;}
    public int getX() {return x;}
    public int getY() {return y;}
    public int getDir() {return dir;}

    // ------------ all setter methods ------------ //
    protected void setX(int x) {this.x = x;}
    protected void setY(int y) {this.y = y;}
    public void setDir(int d) {dir = (d == LEFT || d == RIGHT) ? d : dir;}
}
