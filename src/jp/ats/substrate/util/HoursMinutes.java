package jp.ats.substrate.util;

import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.MINUTE;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;

import jp.ats.substrate.U;

/**
 * @author 千葉 哲嗣
 */
public class HoursMinutes implements Serializable, Comparable<HoursMinutes> {

	private static final long serialVersionUID = -2341104318332328970L;

	private final int HH;

	private final int mm;

	public HoursMinutes() {
		this(new Date());
	}

	public HoursMinutes(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		this.HH = calendar.get(HOUR_OF_DAY);
		this.mm = calendar.get(MINUTE);
	}

	public HoursMinutes(int hours, int minutes) {
		this.HH = hours;
		this.mm = minutes;
	}

	public HoursMinutes(String hoursMinutes) {
		this(U.parseDate("HH:mm", hoursMinutes));
	}

	public HoursMinutes(String hoursMinutes, String delimiter) {
		this(U.parseDate("HH" + delimiter + "mm", hoursMinutes));
	}

	public int getHours() {
		return HH;
	}

	public int getMinutes() {
		return mm;
	}

	public HoursMinutes add(int hours, int minutes) {
		Calendar calendar = Calendar.getInstance();

		calendar.set(HOUR_OF_DAY, HH);
		calendar.set(MINUTE, mm);

		calendar.add(HOUR_OF_DAY, hours);
		calendar.add(MINUTE, minutes);

		return new HoursMinutes(calendar.getTime());
	}

	public int toInt() {
		return HH * 100 + mm;
	}

	public BigInteger toBigInteger() {
		return new BigInteger(toString(""));
	}

	public BigDecimal toBigDecimal() {
		return new BigDecimal(toString(""));
	}

	public Date setHoursMinutesTo(Date date) {
		Calendar calendar = Calendar.getInstance();

		calendar.setTime(date);

		calendar.set(HOUR_OF_DAY, HH);
		calendar.set(MINUTE, mm);

		return calendar.getTime();
	}

	@Override
	public String toString() {
		return (HH < 10 ? "0" + HH : HH) + ":" + (mm < 10 ? "0" + mm : mm);
	}

	public String toString(String delimiter) {
		return (HH < 10 ? "0" + HH : HH)
			+ delimiter
			+ (mm < 10 ? "0" + mm : mm);
	}

	@Override
	public int hashCode() {
		return U.sumHashCodesOf(HH, mm);
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof HoursMinutes)) return false;
		HoursMinutes another = (HoursMinutes) object;
		return HH == another.HH && mm == another.mm;
	}

	@Override
	public int compareTo(HoursMinutes another) {
		int HHDiff;
		return (HHDiff = HH - another.HH) != 0 ? HHDiff : mm - another.mm;
	}
}
