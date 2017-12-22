package jp.ats.substrate.util;

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;

import jp.ats.substrate.U;

/**
 * @author 千葉 哲嗣
 */
public class Day implements Serializable, Comparable<Day> {

	private static final long serialVersionUID = -6977396021196621484L;

	private final int yyyy;

	private final int mm;

	private final int dd;

	public Day() {
		this(new Date());
	}

	public Day(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		this.yyyy = calendar.get(YEAR);
		this.mm = calendar.get(MONTH) + 1;
		this.dd = calendar.get(DAY_OF_MONTH);
	}

	public Day(int yyyy, int mm, int dd) {
		this(createDate(yyyy, mm, dd));
	}

	public Day(String day) {
		this(U.parseDate("yyyy/MM/dd", day));
	}

	public Day(String day, String delimiter) {
		this(U.parseDate("yyyy" + delimiter + "MM" + delimiter + "dd", day));
	}

	public int getYear() {
		return yyyy;
	}

	public int getMonth() {
		return mm;
	}

	public int getMonthIndex() {
		return mm - 1;
	}

	public int getDayOfMonth() {
		return dd;
	}

	public Date getDate() {
		return createDate(yyyy, mm, dd);
	}

	public Day add(int years, int months, int days) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(YEAR, yyyy);
		calendar.set(MONTH, mm - 1);
		calendar.set(DAY_OF_MONTH, dd);

		calendar.add(YEAR, years);
		calendar.add(MONTH, months);
		calendar.add(DAY_OF_MONTH, days);

		return new Day(calendar.getTime());
	}

	public int toInt() {
		return yyyy * 10000 + mm * 100 + dd;
	}

	public BigInteger toBigInteger() {
		return new BigInteger(toString(""));
	}

	public BigDecimal toBigDecimal() {
		return new BigDecimal(toString(""));
	}

	@Override
	public String toString() {
		return yyyy
			+ "/"
			+ (mm < 10 ? "0" + mm : mm)
			+ "/"
			+ (dd < 10 ? "0" + dd : dd);
	}

	public String toString(String delimiter) {
		return yyyy
			+ delimiter
			+ (mm < 10 ? "0" + mm : mm)
			+ delimiter
			+ (dd < 10 ? "0" + dd : dd);
	}

	@Override
	public int hashCode() {
		return U.sumHashCodesOf(yyyy, mm, dd);
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof Day)) return false;
		Day another = (Day) object;
		return dd == another.dd && mm == another.mm && yyyy == another.yyyy;
	}

	@Override
	public int compareTo(Day another) {
		int yDiff, mDiff;
		return (yDiff = yyyy - another.yyyy) != 0 ? yDiff : (mDiff = mm
			- another.mm) != 0 ? mDiff : dd - another.dd;
	}

	private static Date createDate(int yyyy, int mm, int dd) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(YEAR, yyyy);
		calendar.set(MONTH, mm - 1);
		calendar.set(DAY_OF_MONTH, dd);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		return calendar.getTime();
	}
}
