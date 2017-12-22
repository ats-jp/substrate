package jp.ats.substrate.util.exif;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;

import jp.ats.substrate.U;
import jp.ats.substrate.util.exif.IFDUtilities.Bytes;
import jp.ats.substrate.util.exif.IFDUtilities.Endian;

public class ExifReader {

	public static LinkedHashMap<Integer, IFD> read(InputStream input)
		throws IOException {
		byte[] buffer = new byte[2];
		int[] bytes = new int[2];

		read(input, buffer, bytes);

		//とりあえずjpegだけ対象
		if (bytes[0] != 0xff || bytes[1] != 0xd8) throw new IllegalExifException(
			"JPEG ではありません");

		read(input, buffer, bytes);

		//画像編集などでExif仕様にはないAPP0が挿入されていた場合、APP0をスキップ
		if (bytes[0] == 0xff && bytes[1] == 0xe0) {
			read(input, buffer, bytes);
			int app0Length = Endian.convertToBigEndian(bytes);
			U.readBytes(input, new byte[app0Length - 2]);
			read(input, buffer, bytes);
		}

		if (bytes[0] != 0xff || bytes[1] != 0xe1) throw new IllegalExifException(
			"APP1 ではありません");

		read(input, buffer, bytes);

		int app1Length = Endian.convertToBigEndian(bytes);

		byte[] app1Bytes = new byte[app1Length - 2];

		U.readBytes(input, app1Bytes);

		Bytes app1 = new Bytes(app1Bytes);

		byte[] exifMarker = new byte[6];

		app1.readTo(exifMarker);

		if (!new String(exifMarker).equals("Exif\0\0")) throw new IllegalExifException(
			"Exif ではありません");

		int tiffHeadPoint = app1.index;

		app1.readTo(buffer);

		String byteOrder = new String(buffer);

		Endian endian;
		if (byteOrder.equals("II")) {
			endian = new Endian(true, app1);
		} else if (byteOrder.equals("MM")) {
			endian = new Endian(false, app1);
		} else {
			throw new IllegalExifException("バイトオーダーがありません");
		}

		int tiffFileID = endian.readWord();

		if (tiffFileID != 42) throw new IllegalExifException("42 がありません");

		long offsetOf0thIFD = endian.readDWord();
		if (offsetOf0thIFD != 8) endian.setIndex((int) offsetOf0thIFD);

		LinkedHashMap<Integer, IFD> ifds = new LinkedHashMap<>();

		readIFDs(endian, tiffHeadPoint, ifds);

		IFD exifIFD = ifds.get(IFDUtilities.EXIF_IFD_POINTER_TAG);
		IFD gpsIFD = ifds.get(IFDUtilities.GPS_INFO_IFD_POINTER_TAG);

		if (exifIFD != null) {
			endian.setIndex((int) exifIFD.getValuesAsLongType()[0]
				+ tiffHeadPoint);
			readIFDs(endian, tiffHeadPoint, ifds);
		}

		if (gpsIFD != null) {
			endian.setIndex((int) gpsIFD.getValuesAsLongType()[0]
				+ tiffHeadPoint);
			readIFDs(endian, tiffHeadPoint, ifds);
		}

		return ifds;
	}

	private static void readIFDs(
		Endian endian,
		int headPoint,
		LinkedHashMap<Integer, IFD> ifds) {
		int count = endian.readWord();

		for (int i = 0; i < count; i++) {
			IFD ifd = new IFD(endian.littleEndian);

			ifd.tag = endian.readWord();
			ifd.type = endian.readWord();
			ifd.count = (int) endian.readDWord();

			ifds.put(ifd.tag, ifd);

			if (ifd.estimateSize() > 4) {
				int offset = (int) endian.readDWord();
				int currentIndex = endian.getIndex();
				endian.setIndex(offset + headPoint);

				ifd.values = new byte[ifd.count][];
				int size = ifd.getSize();
				for (int ii = 0; ii < ifd.count; ii++) {
					byte[] value = new byte[size];
					ifd.values[ii] = value;
					endian.bytes.readTo(value);
				}

				endian.setIndex(currentIndex);
			} else {
				ifd.values = new byte[1][4];
				endian.bytes.readTo(ifd.values[0]);
			}
		}
	}

	private static int read(
		InputStream input,
		byte[] buffer,
		int[] unsignedBytes) throws IOException {
		int readed = U.readBytes(input, buffer);
		IFDUtilities.convertUnsignedBytes(buffer, unsignedBytes);
		return readed;
	}
}
