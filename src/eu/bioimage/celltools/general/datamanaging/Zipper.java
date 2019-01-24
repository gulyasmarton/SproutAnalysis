package eu.bioimage.celltools.general.datamanaging;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;

import eu.bioimage.celltools.general.imageprocessing.BooleanImage;
import ij.ImagePlus;
import ij.ImageStack;
import ij.io.FileInfo;

public class Zipper {

	public static void main(String[] args) {
		String path = "/mnt/home_shared/marci/meas/motil/S475/analysis/test.zip";

		ImagePlus imp1 = new ImagePlus("img", getImageStackFromZip(path));
		imp1.show();

		if (true)
			return;

		ZipFile zipFile;
		try {
			zipFile = new ZipFile(path);
			Enumeration<? extends ZipEntry> entries = zipFile.entries();

			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				if (!entry.getName().contains(".png"))
					continue;
				InputStream stream = zipFile.getInputStream(entry);
				ImageInputStream istream = ImageIO.createImageInputStream(stream);

				FileInfo fi = new FileInfo();
				fi.fileFormat = FileInfo.IMAGEIO;
				// fi.fileName = f.getName();
				// fi.directory = f.getParent()+File.separator;

				BufferedImage i = ImageIO.read(istream);
				ImagePlus imp = new ImagePlus("none", i);

				// ImageReader reader = new ImageReader(fi);

				imp.show();

				System.out.println(entry.getName());

			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static ImageStack getImageStackFromZip(String path) {

		ImageStack st = null;

		ZipFile zipFile;
		try {
			zipFile = new ZipFile(path);
			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			int idx = 0;
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				if (!entry.getName().contains(".png"))
					continue;
				InputStream stream = zipFile.getInputStream(entry);
				ImageInputStream istream = ImageIO.createImageInputStream(stream);

				BufferedImage i = ImageIO.read(istream);
				ImagePlus imp = new ImagePlus("img" + idx++, i);

				if (st == null) {
					st = new ImageStack(imp.getWidth(), imp.getHeight());
				}
				st.addSlice(imp.getProcessor());
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return st;
	}

	public static void saveImageToZip(String path, List<BooleanImage> images) {

		File f = new File(path);

		try {
			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(f));
			int idx = 0;
			System.out.println("zipping..");
			for (BooleanImage img : images) {
				ZipEntry e = new ZipEntry("img" + idx++ + ".png");
				out.putNextEntry(e);

				BufferedImage buffer = img.getByteProcessor().getBufferedImage();

				ImageIO.write(buffer, "png", out);

				out.closeEntry();
				System.out.println("zip " + idx);
			}

			out.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void saveImageToZip(String path, ImageStack st) {
		File f = new File(path);

		try {
			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(f));
			int idx = 0;
			System.out.println("zipping..");
			for (int i = 0; i < st.getSize(); i++) {			
				ZipEntry e = new ZipEntry("img" + idx++ + ".png");
				out.putNextEntry(e);

				BufferedImage buffer = st.getProcessor(i+1).getBufferedImage();

				ImageIO.write(buffer, "png", out);

				out.closeEntry();
				System.out.println("zip " + idx);
			}

			out.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
