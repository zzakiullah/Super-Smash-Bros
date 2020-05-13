/* Platform.java
 * Zulaikha Zakiullah
 * This class deals with the platforms in a scene.
 */

import org.w3c.dom.css.Rect;

import java.awt.*;

class Platform extends Polygon {
	private int[] xPoints, yPoints;
	private int nPoints;
	private Rectangle hitbox;

	public Platform(int[] xPoints, int[] yPoints, int nPoints) {
		super(xPoints, yPoints, nPoints);
		this.xPoints = new int[xPoints.length];
		this.yPoints = new int[yPoints.length];
		for (int i=0; i<xPoints.length; i++) {
			this.xPoints[i] = xPoints[i];
			this.yPoints[i] = yPoints[i];
		}
		this.nPoints = nPoints;
		makeHitbox();
	}
	
	// ------------ all getter methods ------------ //
	public int[] getXPoints() {return xPoints;}
	public int[] getYPoints() {return yPoints;}
	public int getNPoints() {return nPoints;}
	public Rectangle getHitbox() {return hitbox;}

	// ------------ all setter methods ------------ //


	// isRectangle - checks if platform is in the shape of a rectangle
	public boolean isRectangle() {
		return ((xPoints.length == 4 && yPoints.length == 4) && (xPoints[0] == xPoints[1] && xPoints[2] == xPoints[3] && yPoints[1] == yPoints[2] && yPoints[0] == yPoints[3]) || (xPoints[1] == xPoints[2] && xPoints[0] == xPoints[3] && yPoints[0] == yPoints[1] && yPoints[2] == yPoints[3]));
	}

	public void makeHitbox() {
		hitbox = new Rectangle(minX(), minY(), maxX()-minX(), maxX()-minY());
	}

	public int minX() {
		int minX = xPoints[0];
		for (int x : xPoints) {
			minX = Math.min(minX, x);
		}
		return minX;
	}
	public int maxX() {
		int maxX = xPoints[0];
		for (int x : xPoints) {
			maxX = Math.max(maxX, x);
		}
		return maxX;
	}


	public int minY() {
		int minY = yPoints[0];
		for (int y : yPoints) {
			minY = Math.min(minY, y);
		}
		return minY;
	}
	public int maxY() {
		int maxY = yPoints[0];
		for (int y : yPoints) {
			maxY = Math.max(maxY, y);
		}
		return maxY;
	}
}
