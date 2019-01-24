package eu.bioimage.celltools.general.imageprocessing;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;

/**
 * Find the 8-connected area on an image.
 *
 */
public class FloodFill {
	boolean[] pix;
	int w;
	int h;
	List<AbstractBlob> blobs;
	
	public FloodFill(BooleanImage image){
		w = image.getWidth();
		h = image.getHeight();
		pix = image.getPixels();
	}


	private void FindBlob(boolean[] pix) {
		blobs = new ArrayList<AbstractBlob>();
		for (int y = 0; y < h; y++)
			for (int x = 0; x < w; x++)
				if (pix[x + y * w])					
					blobs.add(new AbstractBlob(FindBlob(x, y)));
	}

	private HashSet<Point> FindBlob(int x, int y) {

		HashSet<Point> points = new HashSet<Point>();
		Stack<Point> stack = new Stack<Point>();
		Point p = new Point(x, y);
		stack.push(p);
		while (!stack.empty()) {
			p = stack.pop();
			points.add(p);
			pix[p.x + p.y * w] = false;
			for (int xk = -1; xk <= 1; xk++)
				for (int yk = -1; yk <= 1; yk++)
					if (pixCheck(p.x + xk, p.y + yk))
						stack.push(new Point(p.x + xk, p.y + yk));
		}

		return points;
	}

	private boolean pixCheck(int x, int y) {
		if (x < 0 || y < 0 || x >= w || y >= h)
			return false;
		return pix[x + y * w];
	}

	/**
	 * Return the found blobs.
	 * @return
	 */
	public List<AbstractBlob> getBlobs() {
		if(blobs==null)
			FindBlob(pix);
		return blobs;
	}


}
