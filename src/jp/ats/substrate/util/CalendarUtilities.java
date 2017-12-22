package jp.ats.substrate.util;

import java.util.Calendar;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author 千葉 哲嗣
 */
public class CalendarUtilities {

	private static final Map<Integer, String[][]> holidayTables = new TreeMap<>();

	public static String getHolidayName(Day day) {
		int year = day.getYear();
		synchronized (holidayTables) {
			String[][] holidays = holidayTables.get(year);
			if (holidays == null) {
				holidays = buildHolidayTable(year);
				holidayTables.put(year, holidays);
			}
			return holidays[day.getMonthIndex()][day.getDayOfMonth()];
		}
	}

	public static String getHolidayName(Calendar calendar) {
		return getHolidayName(new Day(calendar.getTime()));
	}

	public static void clearHolidayTablesCache() {
		synchronized (holidayTables) {
			holidayTables.clear();
		}
	}

	private static String[][] buildHolidayTable(int year) {
		String[][] map = new String[12][];

		int 春分の日;
		{
			//2213 1851年-1999年
			//2089 2000年-2150年
			春分の日 = (int) (Math.floor((31 * year + (year < 2000 ? 2213 : 2089)) / 128)
				- Math.floor(year / 4) + Math.floor(year / 100));
		}

		int 秋分の日;
		{
			//2525 1851年-1999年
			//2395 2000年-2150年
			秋分の日 = (int) (Math.floor((31 * year + (year < 2000 ? 2525 : 2395)) / 128)
				- Math.floor(year / 4) + Math.floor(year / 100));
		}

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.DAY_OF_MONTH, 1);

		for (int month = 0; month < 12; month++) {
			calendar.set(Calendar.MONTH, month);
			int 月の最終日 = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

			Calendar calendarForDayOfWeek = (Calendar) calendar.clone();

			String[] holidays = new String[月の最終日 + 2];
			int mondayIndex = 0;
			for (int day = 1; day <= 月の最終日; day++) {
				calendarForDayOfWeek.set(Calendar.DAY_OF_MONTH, day);
				int dayOfWeek = calendarForDayOfWeek.get(Calendar.DAY_OF_WEEK);

				if (dayOfWeek == Calendar.MONDAY) {
					mondayIndex++;
				}

				if (day == 1 && month == Calendar.JANUARY && 1949 <= year) {
					holidays[day] = "元旦";
				} else if (day == 15
					&& month == Calendar.JANUARY
					&& 1949 <= year
					&& year < 2000) {
					holidays[day] = "成人の日";
				} else if (dayOfWeek == Calendar.MONDAY
					&& mondayIndex == 2
					&& month == 0
					&& 2000 <= year) {
					holidays[day] = "成人の日";
				} else if (day == 11
					&& month == Calendar.FEBRUARY
					&& 1967 <= year) {
					holidays[day] = "建国記念の日";
				} else if (day == 春分の日 && month == Calendar.MARCH) {
					holidays[day] = "春分の日";
				} else if (day == 29
					&& month == Calendar.APRIL
					&& 1989 <= year
					&& year < 2007) {
					holidays[day] = "みどりの日";
				} else if (day == 29
					&& month == Calendar.APRIL
					&& 1949 <= year
					&& year < 1989) {
					holidays[day] = "天皇誕生日";
				} else if (day == 4 && month == Calendar.MAY && 2007 <= year) {
					holidays[day] = "みどりの日";
				} else if (day == 29 && month == Calendar.APRIL && 2007 <= year) {
					holidays[day] = "昭和の日";
				} else if (day == 3 && month == Calendar.MAY && 1949 <= year) {
					holidays[day] = "憲法記念日";
				} else if (day == 4
					&& month == Calendar.MAY
					&& 1986 <= year
					&& year < 2007) {
					holidays[day] = "国民の休日";
				} else if (day == 5 && month == Calendar.MAY && 1949 <= year) {
					holidays[day] = "こどもの日";
				} else if (day == 20
					&& month == Calendar.JULY
					&& 1996 <= year
					&& year < 2003) {
					holidays[day] = "海の日";
				} else if (mondayIndex == 3
					&& month == Calendar.JULY
					&& dayOfWeek == Calendar.MONDAY
					&& 2003 <= year) {
					holidays[day] = "海の日";
				} else if (day == 11
					&& month == Calendar.AUGUST
					&& 2016 <= year) {
					holidays[day] = "山の日";
				} else if (day == 15
					&& month == Calendar.SEPTEMBER
					&& 1966 <= year
					&& year < 2003) {
					holidays[day] = "敬老の日";
				} else if (mondayIndex == 3
					&& month == Calendar.SEPTEMBER
					&& dayOfWeek == Calendar.MONDAY
					&& 2003 <= year) {
					holidays[day] = "敬老の日";
				} else if (秋分の日 == day && month == Calendar.SEPTEMBER) {
					holidays[day] = "秋分の日";
				} else if (day == 10
					&& month == Calendar.OCTOBER
					&& 1966 <= year
					&& year < 2001) {
					holidays[day] = "体育の日";
				} else if (mondayIndex == 2
					&& month == Calendar.OCTOBER
					&& dayOfWeek == Calendar.MONDAY
					&& 2000 <= year) {
					holidays[day] = "体育の日";
				} else if (day == 3
					&& month == Calendar.NOVEMBER
					&& 1948 <= year) {
					holidays[day] = "文化の日";
				} else if (day == 23
					&& month == Calendar.NOVEMBER
					&& 1948 <= year) {
					holidays[day] = "勤労感謝の日";
				} else if (day == 23
					&& month == Calendar.DECEMBER
					&& 1989 <= year) {
					holidays[day] = "天皇誕生日";
				}
			}

			boolean has振替休日 = year > 1973
				|| year == 1973
				&& month > Calendar.APRIL;

			boolean oldRule = has振替休日 && year < 2006;
			boolean currentRule = has振替休日 && year >= 2006;

			boolean has国民の休日 = year >= 1988;

			for (int day = 1; day <= 月の最終日; day++) {
				calendarForDayOfWeek.set(Calendar.DAY_OF_MONTH, day);
				int dayOfWeek = calendarForDayOfWeek.get(Calendar.DAY_OF_WEEK);

				if (has振替休日) {
					if (dayOfWeek == Calendar.SUNDAY && holidays[day] != null) {
						int next = day + 1;
						if (oldRule) {
							if (holidays[next] != null) continue;
						} else if (currentRule) {
							for (; holidays[next] != null; next++) {}
						}

						holidays[next] = "振替休日";
					} else if (has国民の休日
						&& holidays[day - 1] != null
						&& holidays[day + 1] != null
						&& holidays[day] == null) {
						holidays[day] = "国民の休日";
					}
				}
			}

			map[month] = holidays;
		}

		return map;
	}
}
