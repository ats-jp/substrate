package jp.ats.substrate.util.exif;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 千葉 哲嗣
 */
public class IFDUtilities {

	static final int EXIF_IFD_POINTER_TAG = 34665;

	static final int GPS_INFO_IFD_POINTER_TAG = 34853;

	private static int tags[] = {
		//TIFF Rev.6.0
		//A.
		256,
		257,
		258,
		259,
		262,
		274,
		277,
		284,
		530,
		531,
		282,
		283,
		296,
		//B.
		273,
		278,
		279,
		513,
		514,
		//C.
		301,
		318,
		319,
		529,
		532,
		//D.
		306,
		270,
		271,
		272,
		305,
		315,
		33432,
		//pointer
		EXIF_IFD_POINTER_TAG,
		GPS_INFO_IFD_POINTER_TAG,

		//Exif 2.21
		//A.
		36864,
		40960,
		//B.
		40961,
		42240,
		//C.
		37121,
		37122,
		40962,
		40963,
		//D.
		37500,
		37510,
		//E.
		40964,
		//F.
		36867,
		36868,
		37520,
		37521,
		37522,
		//G.
		33434,
		33437,
		34850,
		34852,
		34855,
		34856,
		34864,
		34865,
		34866,
		34867,
		34868,
		34869,
		37377,
		37378,
		37379,
		37380,
		37381,
		37382,
		37383,
		37384,
		37385,
		37386,
		37396,
		41483,
		41484,
		41486,
		41487,
		41488,
		41492,
		41493,
		41495,
		41728,
		41729,
		41730,
		41985,
		41986,
		41987,
		41988,
		41989,
		41990,
		41991,
		41992,
		41993,
		41994,
		41995,
		41996,
		//H.
		42016,
		42032,
		42033,
		42034,
		42035,
		42036,
		42037,
		//pointer
		40965,

		//GPS
		0,
		1,
		2,
		3,
		4,
		5,
		6,
		7,
		8,
		9,
		10,
		11,
		12,
		13,
		14,
		15,
		16,
		17,
		18,
		19,
		20,
		21,
		22,
		23,
		24,
		25,
		26,
		27,
		28,
		29,
		30,
		31 };

	private static String names[] = {
		//TIFF Rev.6.0
		//A.
		"ImageWidth",
		"ImageLength",
		"BitsPerSample",
		"Compression",
		"PhotometricInterpretation",
		"Orientation",
		"SamplesPerPixel",
		"PlanarConfiguration",
		"YCbCrSubSampling",
		"YCbCrPositioning",
		"XResolution",
		"YResolution",
		"ResolutionUnit",
		//B.
		"StripOffsets",
		"RowsPerStrip",
		"StripByteCounts",
		"JPEGInterchangeFormat",
		"JPEGInterchangeFormatLength",
		//C.
		"TransferFunction",
		"WhitePoint",
		"PrimaryChromaticities",
		"YCbCrCoefficients",
		"ReferenceBlackWhite",
		//D.
		"DateTime",
		"ImageDescription",
		"Make",
		"Model",
		"Software",
		"Artist",
		"Copyright",
		//pointer
		"ExifIFDPointer",
		"GPSInfoIFDPointer",

		//Exif 2.21
		//A.
		"ExifVersion",
		"FlashpixVersion",
		//B.
		"ColorSpace",
		"Gamma",
		//C.
		"ComponentsConfiguration",
		"CompressedBitsPerPixel",
		"PixelXDimension",
		"PixelYDimension",
		//D.
		"MakerNote",
		"UserComment",
		//E.
		"RelatedSoundFile",
		//F.
		"DateTimeOriginal",
		"DateTimeDigitized",
		"SubSecTime",
		"SubSecTimeOriginal",
		"SubSecTimeDigitized",
		//G.
		"ExposureTime",
		"FNumber",
		"ExposureProgram",
		"SpectralSensitivity",
		"PhotographicSensitivity",
		"OECF",
		"SensitivityType",
		"StandardOutputSensitivity",
		"RecommendedExposureIndex",
		"ISOSpeed",
		"ISOSpeedLatitudeyyy",
		"ISOSpeedLatitudezzz",
		"ShutterSpeedValue",
		"ApertureValue",
		"BrightnessValue",
		"ExposureBiasValue",
		"MaxApertureValue",
		"SubjectDistance",
		"MeteringMode",
		"LightSource",
		"Flash",
		"FocalLength",
		"SubjectArea",
		"FlashEnergy",
		"SpatialFrequencyResponse",
		"FocalPlaneXResolution",
		"FocalPlaneYResolution",
		"FocalPlaneResolutionUnit",
		"SubjectLocation",
		"ExposureIndex",
		"SensingMethod",
		"FileSource",
		"SceneType",
		"CFAPattern",
		"CustomRendered",
		"ExposureMode",
		"WhiteBalance",
		"DigitalZoomRatio",
		"FocalLengthIn35mmFilm",
		"SceneCaptureType",
		"GainControl",
		"Contrast",
		"Saturation",
		"Sharpness",
		"DeviceSettingDescription",
		"SubjectDistanceRange",
		//H.
		"ImageUniqueID",
		"CameraOwnerName",
		"BodySerialNumber",
		"LensSpecification",
		"LensMake",
		"LensModel",
		"LensSerialNumber",
		//pointer
		"InteroperabilityIFDPointer",

		//GPS
		"GPSVersionID",
		"GPSLatitudeRef",
		"GPSLatitude",
		"GPSLongitudeRef",
		"GPSLongitude",
		"GPSAltitudeRef",
		"GPSAltitude",
		"GPSTimeStamp",
		"GPSSatellites",
		"GPSStatus",
		"GPSMeasureMode",
		"GPSDOP",
		"GPSSpeedRef",
		"GPSSpeed",
		"GPSTrackRef",
		"GPSTrack",
		"GPSImgDirectionRef",
		"GPSImgDirection",
		"GPSMapDatum",
		"GPSDestLatitudeRef",
		"GPSDestLatitude",
		"GPSDestLongitudeRef",
		"GPSDestLongitude",
		"GPSDestBearingRef",
		"GPSDestBearing",
		"GPSDestDistanceRef",
		"GPSDestDistance",
		"GPSProcessingMethod",
		"GPSAreaInformation",
		"GPSDateStamp",
		"GPSDifferential",
		"GPSHPositioningError" };

	private static final Map<Integer, String> nameMap = new HashMap<>();

	private static final Map<String, Integer> tagMap = new HashMap<>();

	static {
		for (int i = 0; i < tags.length; i++)
			nameMap.put(tags[i], names[i]);

		for (int i = 0; i < names.length; i++)
			tagMap.put(names[i], tags[i]);
	}

	public static String getName(int tag) {
		return nameMap.get(tag);
	}

	public static int getTag(String name) {
		return tagMap.get(name);
	}

	static class Endian {

		final boolean littleEndian;

		final Bytes bytes;

		private static final int byteBoundary = 256;

		private final byte[] buffer = new byte[2];

		private final int[] word = new int[2];

		Endian(boolean littleEndian, Bytes bytes) {
			this.littleEndian = littleEndian;
			this.bytes = bytes;
		}

		int readWord() {
			bytes.readTo(buffer, word);
			return littleEndian
				? convertToLittleEndian(word)
				: convertToBigEndian(word);
		}

		long readDWord() {
			long word1 = readWord();
			long word2 = readWord();

			return littleEndian
				? word2 * byteBoundary * byteBoundary + word1
				: word1 * byteBoundary * byteBoundary + word2;
		}

		long readSignedDWord() {
			long word1 = readWord();
			long word2 = readWord();

			if (littleEndian) {
				if (word2 >= byteBoundary / 2 * byteBoundary) word2 = -word2;
				return word2 * byteBoundary * byteBoundary + word1;
			}

			if (word1 >= byteBoundary / 2 * byteBoundary) word1 = -word1;
			return word1 * byteBoundary * byteBoundary + word2;
		}

		int getIndex() {
			return bytes.index;
		}

		void setIndex(int index) {
			bytes.setIndex(index);
		}

		static int convertToBigEndian(int[] word) {
			return word[0] * byteBoundary + word[1];
		}

		private static int convertToLittleEndian(int[] word) {
			return word[1] * byteBoundary + word[0];
		}
	}

	static class Bytes {

		int index;

		private final byte[] bytes;

		Bytes(byte[] bytes) {
			this.bytes = bytes;
		}

		void readTo(byte[] buffer) {
			System.arraycopy(bytes, index, buffer, 0, buffer.length);
			index += buffer.length;
		}

		void readTo(byte[] buffer, int[] unsignedBytes) {
			readTo(buffer);
			convertUnsignedBytes(buffer, unsignedBytes);
		}

		void setIndex(int index) {
			this.index = index;
		}
	}

	static void convertUnsignedBytes(byte[] buffer, int[] unsignedBytes) {
		for (int i = 0; i < buffer.length; i++) {
			unsignedBytes[i] = buffer[i] & 0xff;
		}
	}
}
