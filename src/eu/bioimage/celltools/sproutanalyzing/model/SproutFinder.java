package eu.bioimage.celltools.sproutanalyzing.model;

import java.awt.Color;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import eljarasok.CycleLineSeparator;
import eu.bioimage.celltools.general.imageprocessing.FloodFill2D;
import ij.IJ;
import ij.ImagePlus;
import ij.gui.OvalRoi;
import ij.gui.Overlay;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.measure.Calibration;
import ij.measure.ResultsTable;
import ij.process.ByteProcessor;
import ij.process.FloatPolygon;

public class SproutFinder {
	ImagePlus imp;
	ImagePlus cylinderImp;

	private OvalRoi[] korok; // Sholl k�r�k t�mbje.
	private Vector<SzakaszZstack> szakaszok;
	private Vector<Vector<Vector<PolygonRoi>>> AllCycleAllszakaszok;
	private int[] holes;
	private float[] avrHoles;
	private List<ArrayList<Point2D>> holesCenter;
	private ArrayList<ArrayList<Double>> holesDegs;

	public ArrayList<ArrayList<Double>> getHolesDegs() {
		return holesDegs;
	}

	public List<ArrayList<Point2D>> getHolesCenter() {
		return holesCenter;
	}

	/**
	 * Megkeresi a nyulv�nyokat sholl anal�zissel
	 * 
	 * @param imp
	 *            Elemzend� RGB k�p referenci�ja. Csaj a teljesen fekete pixelt
	 *            veszi h�tt�rnek
	 */
	public SproutFinder(ImagePlus imp) {
		this.imp = imp;
	}

	public ImagePlus getImp() {
		return imp;
	}

	/**
	 * Kisz�molja hogy maximum h�ny k�r f�r bele a megadott maxim�lis t�vols�g
	 * rangebe.
	 * 
	 * @param rec
	 *            kezd� ellipszis befog�n�gysz�ge
	 * @param maxd
	 *            maxim�lis t�vols�g
	 * @param dist
	 *            k�r�k k�zti t�vols�g
	 * @return
	 */
	private int CalcMaxEllipse(Rectangle rec, double maxd, double dist) {
		double w = maxd - rec.width / 2f;
		double h = maxd - rec.height / 2f;
		double m = Math.min(w, h);
		return (int) Math.floor(m / dist) + 1;
	}

	/**
	 * K�r�k sz�m�nak �s t�vols�g�nak be�ll�t�sa micronban
	 * 
	 * @param fromCenter
	 *            centert�l vett maxim�lis t�vols�g
	 * @param distance
	 *            elipszisek t�vols�ga
	 */
	public void setCircle(int fromCenter, int distance) {
		Roi r = imp.getRoi();
		if (r == null || r.getType() != Roi.OVAL)
			return;
		Rectangle rec = r.getBounds();
		setCircle(rec, fromCenter, distance);
	}

	public void setCircle(Rectangle rec, int fromCenter, int distance) {

		double fromUm = imp.getCalibration().getRawX(fromCenter);
		double distUm = imp.getCalibration().getRawX(distance);

		int xOff = (int) distUm;
		int yOff = (int) distUm;
		int numCycle = CalcMaxEllipse(rec, fromUm, distUm);

		Overlay ov = new Overlay();
		korok = new OvalRoi[numCycle];
		for (int i = 0; i < numCycle; i++)
			korok[i] = new OvalRoi(rec.x - xOff * i, rec.y - yOff * i,
					rec.width + xOff * i * 2, rec.height + yOff * i * 2);
		drawBaseCycle(ov);
		imp.setOverlay(ov);
		imp.updateAndDraw();
	}

	/**
	 * Felrajzolja a k�r�ket a k�pre overlayerk�nt
	 * 
	 * @param ov
	 */
	public void drawBaseCycle(Overlay ov) {
		for (int i = 0; i < korok.length; i++) {
			korok[i].setStrokeColor(Color.red);
			ov.add(korok[i]);
		}
	}

	/**
	 * Megkeresi az �ssze k�r �sszes szakasz�t az �sszes s�kon �s berendezi a
	 * AllCycleAllszakaszok vektorba a k�vetkez�k szerint: 1. zStack 2. k�r�k 3.
	 * roi szakaszok
	 */
	private void OsszesSzakaszMegkeresese() {
		int zStack = imp.getStackSize();
		// if (zStack == 1) {
		// IJ.log("nincs stack");
		// return;
		// }

		AllCycleAllszakaszok = new Vector<Vector<Vector<PolygonRoi>>>(zStack);
		for (int i = 1; i <= zStack; i++) {
			AllCycleAllszakaszok.add(Cross(imp.getStack().getPixels(i)));
		}
		Overlay ov = imp.getOverlay();
		for (int i = 1; i <= zStack; i++) {
			for (Vector<PolygonRoi> kor : AllCycleAllszakaszok.get(i - 1))
				for (PolygonRoi roi : kor) {
					roi.setPosition(i);
					roi.setStrokeColor(Color.yellow);
					ov.add(roi);
				}
		}

		// �talak�t�s pont vektorr� zStack/k�r�k/Polgonok/Pontok

		szakaszok = new Vector<SzakaszZstack>(AllCycleAllszakaszok.size());
		for (Vector<Vector<PolygonRoi>> zk : AllCycleAllszakaszok)
			szakaszok.add(new SzakaszZstack(zk));

		/*
		 * int[] korproj = new int[szakaszok.get(0).getKorok().size()]; for (int
		 * k = 0; k < szakaszok.get(0).getKorok().size(); k++) { SzakaszKor sza
		 * = szakaszok.get(0).getKorok().get(k); for (int i = 1; i <
		 * szakaszok.size(); i++) { sza =
		 * sza.Egyesit(szakaszok.get(i).getKorok().get(k)); } korproj[k] =
		 * sza.getSzakaszPoligonok().size(); }
		 */
		imp.updateAndDraw();

	}

	/**
	 * Ez a f�ggv�ny fogja az adott zStack pixeleit �s a "k�r�k" t�mbj�t, majd
	 * veszi az adott k�r �ltal lefedett pixelek .
	 * 
	 * @param pix
	 * @return
	 */
	private Vector<Vector<PolygonRoi>> Cross(Object pix) {
		Vector<Vector<PolygonRoi>> szakaszok = new Vector<Vector<PolygonRoi>>(
				korok.length);
		for (int i = 0; i < korok.length; i++) {
			Vector<Point> pk = Roi2Point(korok[i]);
			szakaszok.add(FindSections(pix, pk));
		}
		return szakaszok;

	}

	/**
	 * Visszaadja a k�pre vet�tett pixelek koordin�t�it.
	 * 
	 * @param r
	 * @return
	 */
	public static Vector<Point> Roi2Point(Roi r) {
		FloatPolygon fp = r.getInterpolatedPolygon();
		Vector<Point> pk = new Vector<Point>(fp.npoints);

		for (int i = 0; i < fp.npoints; i++)
			pk.add(new Point((int) Math.round(fp.xpoints[i]), (int) Math
					.round(fp.ypoints[i])));

		return pk;
	}

	/**
	 * Megkeresi a k�rvonal pixelek �s a k�p alapj�n a szakaszokat.
	 * 
	 * @param pix
	 * @param pk
	 * @return
	 */
	private Vector<PolygonRoi> FindSections(Object pix, Vector<Point> pk) {

		CycleLineSeparator cs = new CycleLineSeparator(pix, imp.getWidth(),
				imp.getHeight(), pk);
		return cs.Separator();
	}

	public List<SzakaszZstack> getCylinder(boolean show) {
		// TODO Auto-generated method stub
		OsszesSzakaszMegkeresese();
		if (korok == null)
			return null;

		ByteProcessor[] bps = new ByteProcessor[korok.length];
		int w = 0;
		int h = 0;

		for (int i = korok.length - 1; i >= 0; i--) {
			ByteProcessor bp = Laposit(i);
			if (w < bp.getWidth())
				w = bp.getWidth();
			h += bp.getHeight();
			bps[korok.length - 1 - i] = bp;
		}

		byte[] pix = new byte[w * h];
		byte[] fpix = (byte[]) bps[0].getPixels();

		for (int i = 0; i < fpix.length; i++) {
			pix[i] = fpix[i];
		}

		for (int i = 1; i < korok.length; i++) {
			fpix = (byte[]) bps[i].getPixels();
			int dif = (bps[0].getWidth() - bps[i].getWidth()) / 2;
			for (int y = 0; y < bps[i].getHeight(); y++)
				for (int x = 0; x < bps[i].getWidth(); x++)
					pix[x + dif + (y + bps[0].getHeight() * i) * w] = fpix[x
							+ y * bps[i].getWidth()];

		}
		Overlay ov = new Overlay();
		for (int i = 0; i < korok.length; i++) {
			int dif = (bps[0].getWidth() - bps[i].getWidth()) / 2;
			Roi r = new Roi(dif, bps[0].getHeight() * i, bps[i].getWidth(),
					bps[i].getHeight());
			ov.add(r);
		}
		cylinderImp = new ImagePlus(imp.getTitle().replace(".zvi", "-spr.jpg"),
				new ByteProcessor(w, h, pix));
		cylinderImp.setOverlay(ov);
		if (show)
			cylinderImp.show();
		szamolPalast(bps);

		ResultsTable table = new ResultsTable();

		for (int i = 0; i < holes.length; i++) {
			table.incrementCounter();
			table.addValue("Distance", getXthDistance(i));
			table.addValue("holes", holes[i]);
			table.addValue("avr Holes", avrHoles[i]);
		}
		if (show)
			table.show("Holes of cylinder");
		return szakaszok;
	}

	private float getXthDistance(int idx) {
		return (float) imp
				.getCalibration()
				.getX((korok[idx].getBounds().width + korok[idx].getBounds().height) / 4d);
	}

	/**
	 * Ez kalibr�lva van!!!
	 * 
	 * @param ratio
	 * @return
	 */
	public String[] getTXT(double ratio) {
		if (holes == null)
			return null;
		String[] exp = new String[holes.length];
		for (int i = 0; i < exp.length; i++) {
			exp[i] = getXthDistance(i) + "\t" + holes[i] * ratio + "\t"
					+ avrHoles[i];
		}
		return exp;

	}

	private ByteProcessor Laposit(int koridx) {
		int szelesseg = Roi2Point(korok[koridx]).size();
		int zdb = szakaszok.size();
		Calibration cb = imp.getCalibration();
		int szorzoH = (int) Math.round(cb.getZ(1) / cb.getX(1));
		int magassag = szorzoH * zdb;

		byte[] pix = new byte[magassag * szelesseg];

		ByteProcessor bp = new ByteProcessor(szelesseg, magassag, pix);

		Vector<Point> korPontok = Roi2Point(korok[koridx]);
		int y = 0;
		for (SzakaszZstack szz : szakaszok) {
			for (SzakaszPoligon pl : szz.getKorok().get(koridx)
					.getSzakaszPoligonok()) {
				for (Point point : pl.getPontok()) {
					int hol = korPontok.indexOf(point);
					if (hol != -1) {
						for (int sz = 0; sz < szorzoH; sz++)
							pix[hol + (y * szorzoH + sz) * szelesseg] = (byte) 255;
					} else {
						System.out.println("na neee");
					}
				}
			}
			y++;
		}
		return bp;
	}

	private void szamolPalast(ByteProcessor[] bps) {
		holes = new int[bps.length];
		holesCenter = new ArrayList<ArrayList<Point2D>>();
		holesDegs = new ArrayList<ArrayList<Double>>();
		avrHoles = new float[bps.length];
		for (int i = 0; i < bps.length; i++) {
			FloodFill2D f = szamolLyuk(bps[i]);
			holes[bps.length - i - 1] = f.getSize();
			ArrayList<Point2D> lyukKozepek = new ArrayList<>();
			ArrayList<Double> lyukDegs = new ArrayList<>();
			for (Vector<Point> ps : f.reszecskek) {
				avrHoles[bps.length - i - 1] += ps.size();
				
				Point2D center = getMean(ps);
				double deg = getAngle(bps[i].getWidth(),center.getX());
				
				lyukKozepek.add(center);
				lyukDegs.add(deg);
			}
			holesCenter.add(lyukKozepek);
			holesDegs.add(lyukDegs);
			avrHoles[bps.length - i - 1] /= f.getSize();
			if (f.getSize() == 0)
				avrHoles[bps.length - i - 1] = 0;
		}
		/*
		 * ResultsTable table = new ResultsTable(); table.incrementCounter();
		 * for (int i = 0; i < bps.length; i++) { table.addValue((bps.length -
		 * i) + ". ring", szamolLyuk(bps[i])); }
		 * table.show("Holes of cylinder");
		 */
	}

	private double getAngle(int width, double x) {
		return x*360d/width;
	}

	private Point2D getMean(Vector<Point> ps) {
		double x = 0;
		double y = 0;
		for (Point p : ps) {
			x+= p.getX();
			y+=p.getY();
		}
		return new Point2D.Double(x/ps.size(), y/ps.size());
	}

	private FloodFill2D szamolLyuk(ByteProcessor bp) {
		FloodFill2D ff = new FloodFill2D(bp);
		return ff;
	}

	public OvalRoi[] getKorok() {
		return korok;
	}

	public ImagePlus getCylinderImp() {
		return cylinderImp;
	}

	public void setImp(ImagePlus imp) {
		this.imp = imp;
	}

}
