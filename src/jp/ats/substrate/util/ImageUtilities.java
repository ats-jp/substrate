package jp.ats.substrate.util;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.AreaAveragingScaleFilter;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageObserver;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

/**
 * @author 千葉 哲嗣
 */
public class ImageUtilities {

	public static int[] grabPixels(Image image) {
		int width = image.getWidth(null);
		int height = image.getHeight(null);

		int[] pixels = new int[width * height];
		PixelGrabber grabber = new PixelGrabber(
			image,
			0,
			0,
			width,
			height,
			pixels,
			0,
			width);
		try {
			grabber.grabPixels();
		} catch (InterruptedException ex) {
			throw new IllegalStateException();
		}
		if ((grabber.status() & ImageObserver.ABORT) != 0) {
			throw new IllegalStateException();
		}
		return pixels;
	}

	public static void drawImage(Image dst, int width, int height, int[] pixels) {
		dst.getGraphics().drawImage(
			Toolkit.getDefaultToolkit().createImage(
				new MemoryImageSource(width, height, pixels, 0, width)),
			0,
			0,
			null);
	}

	public static void shrinkJPG(int longerSideLength, File dest, File src)
		throws IOException {
		shrink("jpg", longerSideLength, dest, src);
	}

	public static void shrinkGIF(int longerSideLength, File dest, File src)
		throws IOException {
		shrink("gif", longerSideLength, dest, src);
	}

	public static void shrinkPNG(int longerSideLength, File dest, File src)
		throws IOException {
		shrink("png", longerSideLength, dest, src);
	}

	public static void shrink(
		String suffix,
		int longerSideLength,
		File dest,
		File src) throws IOException {
		scale(suffix, longerSideLength, dest, src, true);
	}

	public static void scale(
		String suffix,
		int longerSideLength,
		File dest,
		File src) throws IOException {
		scale(suffix, longerSideLength, dest, src, false);
	}

	private static void scale(
		String suffix,
		int longerSideLength,
		File dest,
		File src,
		boolean shrink) throws IOException {
		Iterator<ImageReader> readers = ImageIO.getImageReadersBySuffix(suffix);
		readers.hasNext();

		ImageReader imageReader = readers.next();
		try {
			imageReader.setInput(ImageIO.createImageInputStream(src));
			BufferedImage base = imageReader.read(0);

			int width = base.getWidth();
			int height = base.getHeight();

			float rate;

			if (shrink
				&& width <= longerSideLength
				&& height <= longerSideLength) {
				//縮小の場合、元の画像が長辺より小さい場合、縮小しない
				rate = 1;
			} else {
				rate = ((float) (height < width ? width : height))
					/ longerSideLength;
			}

			int shrinkedWidth = Math.round(width / rate);
			int shrinkedHeight = Math.round(height / rate);

			BufferedImage shrinkImage = new BufferedImage(
				shrinkedWidth,
				shrinkedHeight,
				base.getType());

			ImageFilter imgfilter = new AreaAveragingScaleFilter(
				shrinkedWidth,
				shrinkedHeight);
			Image temp = Toolkit.getDefaultToolkit().createImage(
				new FilteredImageSource(base.getSource(), imgfilter));
			shrinkImage.getGraphics().drawImage(temp, 0, 0, null);

			ImageWriter imageWriter = ImageIO.getImageWriter(imageReader);
			try {

				ImageWriteParam param = imageWriter.getDefaultWriteParam();
				param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
				param.setCompressionQuality(1.0f);

				ImageOutputStream stream = ImageIO.createImageOutputStream(dest);
				try {
					imageWriter.setOutput(stream);
					imageWriter.write(null, new IIOImage(
						shrinkImage,
						null,
						null), param);

					stream.flush();
				} finally {
					stream.close();
				}
			} finally {
				imageWriter.dispose();
			}
		} finally {
			imageReader.dispose();
		}
	}
}
