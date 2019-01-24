package eu.bioimage.celltools.general.imageprocessing;

import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * This is an basic blob class which contains several general function and
 * manages base of serialization.
 */
public class AbstractBlob implements Serializable {

	private static final long serialVersionUID = 4112707439187511140L;

	protected boolean[] mask;
	protected int size;
	protected int perimter;

	protected Rectangle bound;

	protected Double mean;
	protected Double sd;

	public AbstractBlob(HashSet<Point> points) {
		bound = getBound(points);
		mask = getMask(points);
		size = points.size();
	}

	public AbstractBlob(BooleanImage img) {
		mask = img.getPixels();
		bound = new Rectangle(img.getWidth(), img.getHeight());
		for (int i = 0; i < mask.length; i++)
			if (mask[i])
				size++;
	}

	public AbstractBlob(AbstractBlob blob) {
		this.bound = blob.getBound();
		this.mask = blob.getMask();
		this.size = blob.getSize();
	}

	/**
	 * Convert set of points to boolean mask for space reduction.
	 * 
	 * @param points
	 * @return
	 */
	protected boolean[] getMask(HashSet<Point> points) {
		Rectangle rec = bound;
		boolean[] mask = new boolean[rec.width * rec.height];
		for (Point p : points) {
			mask[p.x - rec.x + (p.y - rec.y) * rec.width] = true;
		}
		return mask;
	}

	/**
	 * Return boundary rectangle of blob.
	 * 
	 * @return
	 */
	public Rectangle getBound() {
		return bound;
	}

	protected Rectangle getBound(HashSet<Point> points) {
		int maxW = -1;
		int maxH = -1;
		int x = Integer.MAX_VALUE;
		int y = Integer.MAX_VALUE;
		for (Point point : points) {
			if (point.x > maxW)
				maxW = point.x;
			if (point.y > maxH)
				maxH = point.y;
			if (point.x < x)
				x = point.x;
			if (point.y < y)
				y = point.y;
		}
		maxW -= x;
		maxH -= y;
		maxW++;
		maxH++;
		return new Rectangle(x, y, maxW, maxH);

	}

	/**
	 * Return blob's points. Warning! For space reduction this HashSet is
	 * recreated at all time!
	 * 
	 * @return
	 */
	public HashSet<Point> getPoints() {
		HashSet<Point> points = new HashSet<Point>();

		for (int i = 0; i < mask.length; i++)
			if (mask[i]) {
				int x = i % bound.width + bound.x;
				int y = i / bound.width + bound.y;
				points.add(new Point(x, y));
			}

		return points;
	}

	/**
	 * Return numbers of blob's points.
	 * 
	 * @return
	 */
	public int getSize() {
		return size;
	}

	/**
	 * Return true if there is common area
	 * 
	 * @param b
	 * @return
	 */
	public boolean isOverlap(AbstractBlob b) {
		if (!bound.intersects(b.getBound()))
			return false;

		for (int y = 0; y < bound.height; y++)
			for (int x = 0; x < bound.width; x++)
				if (b.getBound().contains(x + bound.x, y + bound.y))
					return true;

		return false;
	}

	/**
	 * Return true if the blob contain this point.
	 * 
	 * @param p
	 * @return
	 */
	public boolean isContain(Point p) {
		return isContain(p.x, p.y);
	}

	/**
	 * Return true if the blob contain this point.
	 * 
	 * @param p
	 * @return
	 */
	public boolean isContain(int x, int y) {
		if (!bound.contains(x, y))
			return false;
		x -= bound.x;
		y -= bound.y;

		return mask[x + y * bound.width];
	}

	public int OverlapArea(AbstractBlob b) {
		int size = 0;

		if (!bound.intersects(b.getBound()))
			return 0;

		for (int y = 0; y < bound.height; y++)
			for (int x = 0; x < bound.width; x++)
				if (b.isContain(x + bound.x, y + bound.y))
					size++;

		return size;
	}

	public boolean[] getMask() {
		// TODO Auto-generated method stub
		return mask;
	}

	public double getPerimeter() {
		if (perimter != 0)
			return perimter;

		for (int y = 0; y < bound.height; y++)
			for (int x = 0; x < bound.width; x++)
				if (isEdge(x, y))
					perimter++;

		return perimter;
	}

	public List<Point> getOutlinePoint() {

		List<Point> outline = new ArrayList<Point>();

		for (int y = 0; y < bound.height; y++)
			for (int x = 0; x < bound.width; x++)
				if (isEdge(x, y))
					outline.add(new Point(bound.x + x, bound.y + y));

		return outline;
	}

	protected boolean isEdge(int xk, int yk) {
		if (!mask[xk + yk * bound.width])
			return false;

		for (int y = yk - 1; y <= yk + 1; y++)
			for (int x = xk - 1; x < xk + 1; x++)
				if (!getPixel(x, y))
					return true;

		return false;
	}

	protected boolean getPixel(int x, int y) {
		if (x < 0 || y < 0 || x >= bound.width || y >= bound.height)
			return false;
		return mask[x + y * bound.width];
	}

}
