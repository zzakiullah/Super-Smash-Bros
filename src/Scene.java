/* Scene.java
 * Zulaikha Zakiullah
 * This class deals with the stages, setting properties such as the gravity.
 */

import java.awt.*;
import java.awt.geom.*;

class Scene extends ObjOnScreen {
	public static final int CURVED = 23, STRAIGHT = 24;
	public final double GRAV;	// gravity constant
	/* A Scene can have either gravType CURVED or STRAIGHT; if it is CURVED, players will fall toward the centre bottom, to follow the
	 * curvature of the Scene. If it is STRAIGHT, players will fall directly down, perpendicular to the horizontal.
	 */
	public final int gravType;		// gravity type
	private Image img;				// image of scene
	private Platform[] platforms;	// all platforms in the scene
	private SoundFX[] soundtrack;   // all background music available for scene
    private String[] trackTitles;   // background music titles
	private SoundFX trackPlaying;	// background music playing
	private Point[] startingPoints;	// starting points for characters
	private Point spawningPoint;	// point where character spawns after falling off edge

	public Scene(String name, double grav, int gravType, Image img, Platform[] platforms, Point[] startingPoints, Point spawningPoint, SoundFX trackPlaying) {
		super(name, 0, 0, 0);
		this.GRAV = grav;
		this.gravType = gravType;
		this.img = img;
		this.platforms = new Platform[platforms.length];
		for (int i=0; i<platforms.length; i++) {
			this.platforms[i] = platforms[i];
		}
		this.startingPoints = new Point[startingPoints.length];
		for (int i=0; i<startingPoints.length; i++) {
			this.startingPoints[i] = startingPoints[i];
		}
		this.spawningPoint = spawningPoint;
		/*this.soundtrack = new SoundFX[soundtrack.length];
		for (int i=0; i<soundtrack.length; i++) {
		    this.soundtrack[i] = soundtrack[i];
        }
        this.trackTitles = new String[trackTitles.length];
        for (int i=0; i<trackTitles.length; i++) {
            this.trackTitles[i] = trackTitles[i];
        }
        trackPlaying = soundtrack[0];
        */
		this.trackPlaying = trackPlaying;
	}
	
	// ------------ all getter methods ------------ //
	public Image getImg() {return img;}
	public Platform[] getPlatforms() {return platforms;}
	public String[] getTrackTitles() {return trackTitles;}
	public Point[] getStartingPoints() {return startingPoints;}
	public Point getSpawningPoint() {return spawningPoint;}
	public SoundFX getTrackPlaying() {return trackPlaying;}

	// ------------ all setter methods ------------ //
	public void setTrackPlaying(int ind) {trackPlaying = soundtrack[ind];}
}
