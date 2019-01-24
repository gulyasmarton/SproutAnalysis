package eu.bioimage.celltools.cluster3d.model;

import ij.ImagePlus;
import ij.ImageStack;
import ij.measure.Calibration;

import java.awt.Color;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.Vector;

import eljarasok.Cell;
import eljarasok.Point3D;
import eu.bioimage.celltools.general.datamanaging.IOtoolBox;

/**
 * A feldologoztt Cell entit�lsok t�rol�s�ra �s glob�lis kezel�s�re szolg�l�
 * oszt�ly
 * 
 * @author M�rton Guly�s
 * 
 */
public class C3D implements java.io.Serializable {
	private static final long serialVersionUID = -3289622167007883201L;
	private Vector<Cell> cells;
	private int xSize, ySize, zSize;
	private String filename;

	private transient Calibration calibration;
	private double pixelWidth;
	private double pixelHeight;
	private double pixelDepth;
	private String setUnit;

	private transient ImagePlus colorimage;

	public static void main(String[] args) {
		if (args == null || args.length < 1) {
			System.out.println("No Args");
			System.out.println("input-C3D-file-path output-file-path format[jpg,png]");
			return;
		}

		String input = args[0];

		C3D c3d = IOtoolBox.openC3D(input);
		double[] size = c3d.getVolumes();

		Double[] data = new Double[size.length];
		for (int i = 0; i < data.length; i++)
			data[i] = size[i];

		Arrays.sort(data, Collections.reverseOrder());

		for (double d : data) {
			System.out.println(d);
		}
	}

	/**
	 * 
	 * @param filename
	 *            kimeneti k�pf�jl neve
	 * @param cells
	 *            t�rolt sejtek vektor�nak referenci�ja
	 * @param width
	 *            k�p sz�less�ge
	 * @param height
	 *            k�p magass�ga
	 * @param stacksize
	 *            Z-s�kok sz�ma
	 * @param calibration
	 *            scaling inform�ci�
	 */
	public C3D(String filename, Vector<Cell> cells, int width, int height, int stacksize, Calibration calibration) {
		super();
		this.filename = filename;
		this.cells = cells;
		this.xSize = width;
		this.ySize = height;
		this.zSize = stacksize;
		this.calibration = calibration;
		pixelWidth = this.calibration.pixelWidth;
		pixelHeight = this.calibration.pixelHeight;
		pixelDepth = this.calibration.pixelDepth;
		setUnit = this.calibration.getUnit();
	}

	/**
	 * 
	 * @param filename
	 *            kimeneti k�pf�jl neve
	 * @param cells
	 *            t�rolt sejtek vektor�nak referenci�ja
	 * @param width
	 *            k�p sz�less�ge
	 * @param height
	 *            k�p magass�ga
	 * @param stacksize
	 *            Z-s�kok sz�ma
	 * @param pixelWidth
	 *            X ir�ny� scaling
	 * @param pixelHeight
	 *            Y ir�ny� scaling
	 * @param pixelDepth
	 *            Z ir�ny� scaling
	 * @param setUnit
	 *            scaling egys�ge pl.: micron
	 */
	public C3D(String filename, Vector<Cell> cells, int width, int height, int stacksize, double pixelWidth,
			double pixelHeight, double pixelDepth, String setUnit) {
		super();
		this.filename = filename;
		this.cells = cells;
		this.xSize = width;
		this.ySize = height;
		this.zSize = stacksize;
		this.pixelWidth = pixelWidth;
		this.pixelHeight = pixelHeight;
		this.pixelDepth = pixelDepth;
		this.setUnit = setUnit;
		this.calibration = new Calibration();
		this.calibration.pixelWidth = pixelWidth;
		this.calibration.pixelHeight = pixelHeight;
		this.calibration.pixelDepth = pixelDepth;
		this.calibration.setUnit(setUnit);
	}

	/**
	 * Egyedi soros�t�shoz sz�ks�ges f�ggv�ny
	 * 
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		this.calibration = new Calibration();
		this.calibration.pixelWidth = pixelWidth;
		this.calibration.pixelHeight = pixelHeight;
		this.calibration.pixelDepth = pixelDepth;
		this.calibration.setUnit(setUnit);
	}

	/**
	 * Cell objektumok lek�rdez�se
	 * 
	 * @return
	 */
	public Vector<Cell> getCells() {
		return cells;
	}

	/**
	 * K�p sz�less�ge
	 * 
	 * @return
	 */
	public int getWidth() {
		return xSize;
	}

	/**
	 * K�p magass�ga
	 * 
	 * @return
	 */

	public int getHeight() {
		return ySize;
	}

	/**
	 * K�p Z-s�kjainak a sz�ma.
	 * 
	 * @return
	 */
	public int getStackSize() {
		return zSize;
	}

	/**
	 * Kalibr�ci�s f�jl lek�rdez�se. Ez t�rolja a scaling be�ll�t�sokat
	 * 
	 * @return
	 */
	public Calibration getCalibration() {
		return calibration;
	}

	/**
	 * Kalibr�ci�s f�jl be�ll�t�sa. Ezzel �ll�that� be az XYZ scaling.
	 * 
	 * @param calibration
	 */

	public void setCalibration(Calibration calibration) {
		this.calibration = calibration;
		pixelWidth = this.calibration.pixelWidth;
		pixelHeight = this.calibration.pixelHeight;
		pixelDepth = this.calibration.pixelDepth;
		setUnit = this.calibration.getUnit();
	}

	/**
	 * Kimeneti k�pf�jl neve
	 * 
	 * @return
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * Hamis-sz�nez�s� k�p lek�rdez�se
	 * 
	 * @return
	 */
	public ImagePlus getColorImage() {
		if (colorimage == null)
			colorimage = generateColorImage(null, null);
		return colorimage;
	}

	/**
	 * Hamis-sz�nez�s� k�p �jragener�l�sa �s lek�rdez�se
	 * 
	 * @return
	 */
	public ImagePlus getForceColorImage() {
		colorimage = generateColorImage(null, null);
		return colorimage;
	}

	public ImagePlus getColorImage(int min) {
		return generateColorImage(min, null);
	}

	private ImagePlus generateColorImage(Integer min, Integer max) {
		ImageStack ist = new ImageStack(xSize, ySize);
		int[][] pixels = new int[zSize][];
		int black = Color.black.getRGB();
		for (int z = 0; z < zSize; z++) {
			int[] pix = new int[xSize * ySize];
			Arrays.fill(pix, black);
			pixels[z] = pix;

		}

		for (Cell c : cells) {
			if (min != null && c.getVolume() < min)
				continue;
			if (max != null && c.getVolume() > max)
				continue;
			Color k = c.color == null ? randomColor() : c.color;
			for (Point3D p : c.getPoints())
				pixels[p.z][p.x + xSize * p.y] = k.getRGB();

		}

		for (int z = 0; z < zSize; z++) {
			ist.addSlice("" + z, pixels[z]);
		}
		ImagePlus imp = new ImagePlus(filename, ist);
		imp.setCalibration(calibration);
		return imp;
	}

	/**
	 * Fel�ldefini�lja a sejtek aktu�lis sz�neit egy random sz�nnel
	 */
	public void setRandomColor() {
		for (Cell c : cells)
			c.color = randomColor();
	}

	/**
	 * V�letlen RGB sz�n gener�l�lsa az 50-250 rangeban.
	 * 
	 * @return
	 */
	public static Color randomColor() {
		int Min = 50;
		int Max = 250;
		int r = Min + (int) (Math.random() * ((Max - Min) + 1));
		int g = Min + (int) (Math.random() * ((Max - Min) + 1));
		int b = Min + (int) (Math.random() * ((Max - Min) + 1));

		return new Color(r, g, b);

	}

	public C3D getSizeLimitedVersion(Integer min, Integer max) {
		Vector<Cell> nc = new Vector<Cell>();
		for (Cell c : cells) {
			if (min != null && c.getVolume() < min)
				continue;
			if (max != null && c.getVolume() > max)
				continue;
			nc.add(c);
		}
		return new C3D(filename, nc, getWidth(), getHeight(), getStackSize(), getCalibration());
	}

	/**
	 * Visszaadja a sejtek t�rfogat�t k�bmikronban.
	 * 
	 * @return A getCells() vector�nak sorrendj�ben �rtend�.
	 */
	public double[] getVolumes() {
		double[] arr = new double[cells.size()];
		for (int i = 0; i < arr.length; i++) {
			arr[i] = cells.get(i).getVolume() * pixelHeight * pixelWidth * pixelDepth;
		}
		return arr;
	}

	public double getPixelVolume() {
		return pixelHeight * pixelWidth * pixelDepth;
	}
}
