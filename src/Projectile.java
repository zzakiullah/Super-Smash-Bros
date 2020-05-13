/* Porjectile.java
 * Zulaikha Zakiullah
 * This class deals with the projectiles used when attacking.
 */

class Projectile extends ObjOnScreen {
	public static final int STRAIGHT = 5, TARGET = 6, STATIC = 7, DYNAMIC = 8, INFINITE = -1;
	/* type - type of projectile (whether it stays the same image as it flies through the air or changes)
	 * x, y - position of projectile relative to left corner (0, 0) of Attack image sequence
	 * vx, vy - x and y velocities of projectile
	 * time - time projectile lasts
	 */
	private int type, aimType, gifType, vx, vy, time;
	private Gif[] proj = new Gif[2];

	public Projectile(String name, int x, int y, int dir, int aimType, int gifType, int vx, int vy, int time, Gif[] proj) {
		super(name, x, y, dir);
		this.aimType = aimType;
		this.gifType = gifType;
		this.vx = vx;
		this.vy = vy;
		this.time = time;
		this.proj[0] = proj[0];
		this.proj[1] = proj[1];
	}
	
	// ------------ all getter methods ------------ //
	public int getVX() {return vx;}
	public int getVY() {return vy;}
	public Gif getGif() {return proj[0];}
	
	// ------------ all setter methods ------------ //

}
