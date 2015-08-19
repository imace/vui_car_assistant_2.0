/**
 * Copyright (c) 2012-2012 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : DateTimeItem.java
 * @ProjectName : iShuoShuo
 * @PakageName : cn.yunzhisheng.vui.assistant.memo
 * @Author : Dancindream
 * @CreateDate : 2012-8-28
 */
package cn.yunzhisheng.vui.assistant.memo;

import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import cn.yunzhisheng.vui.assistant.preference.SessionPreference;

/**
 * @Module : DateTimeItem
 * @Comments : 描述
 * @Author : Dancindream
 * @CreateDate : 2012-8-28
 * @ModifiedBy : Dancindream
 * @ModifiedDate: 2012-8-28
 * @Modified:
 * 2012-8-28: 实现基本功能
 */
public class DateTimeItem {
	public static final String TAG = "DateTimeItem";

	private Calendar mCalendar = null;
	private boolean mIsHourOrMinuteChange = false;

	public static DateTimeItem getDataTimeFactory(JSONObject jsonProtocol) {
		int stepYear = -1, stepMonth = -1, stepDay = -1, stepHour = -1, stepMinute = -1, stepWeek = -1;
		int year = -1, month = -1, day = -1, hour = -1, minute = -1, weekday = -1;
		String ampm = "";
		
		try {
			if (jsonProtocol.has(SessionPreference.KEY_YEAR_STEP)) {
				stepYear = jsonProtocol.getInt(SessionPreference.KEY_YEAR_STEP);
			}
			
			if (jsonProtocol.has(SessionPreference.KEY_MON_STEP)) {
				stepMonth = jsonProtocol.getInt(SessionPreference.KEY_MON_STEP);
			}
			
			if (jsonProtocol.has(SessionPreference.KEY_DAY_STEP)) {
				stepDay = jsonProtocol.getInt(SessionPreference.KEY_DAY_STEP);
			}
			
			if (jsonProtocol.has(SessionPreference.KEY_HOUR_STEP)) {
				stepHour = jsonProtocol.getInt(SessionPreference.KEY_HOUR_STEP);
			}
			
			if (jsonProtocol.has(SessionPreference.KEY_MIN_STEP)) {
				stepMinute = jsonProtocol.getInt(SessionPreference.KEY_MIN_STEP);
			}
			
			if (jsonProtocol.has(SessionPreference.KEY_WEEK_STEP)) {
				stepWeek = jsonProtocol.getInt(SessionPreference.KEY_WEEK_STEP);
			}
			
			if (jsonProtocol.has(SessionPreference.KEY_MORNING)) {
				ampm = SessionPreference.KEY_MORNING;
			} else if (jsonProtocol.has(SessionPreference.KEY_AFTERNOON)) {
				ampm = SessionPreference.KEY_AFTERNOON;
			}
			
			if (jsonProtocol.has(SessionPreference.KEY_YEAR)) {
				year = jsonProtocol.getInt(SessionPreference.KEY_YEAR);
			}
			
			if (jsonProtocol.has(SessionPreference.KEY_MONTH)) {
				month = jsonProtocol.getInt(SessionPreference.KEY_MONTH);
			}
			
			if (jsonProtocol.has(SessionPreference.KEY_DAY)) {
				day = jsonProtocol.getInt(SessionPreference.KEY_DAY);
			}
			
			if (jsonProtocol.has(SessionPreference.KEY_HOUR)) {
				hour = jsonProtocol.getInt(SessionPreference.KEY_HOUR);
			}
			
			if (jsonProtocol.has(SessionPreference.KEY_MINUTE)) {
				minute = jsonProtocol.getInt(SessionPreference.KEY_MINUTE);
			}
			
			if (jsonProtocol.has(SessionPreference.KEY_WEEKDAY)) {
				weekday = jsonProtocol.getInt(SessionPreference.KEY_WEEKDAY);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		if ((stepYear + stepMonth + stepDay + stepHour + stepMinute + stepWeek == -6) && (year + month + day + hour + minute + weekday == -6)) {
			return null;
		}

		DateTimeItem dti = new DateTimeItem();
		dti.setStep(stepYear, stepMonth, stepDay, stepHour, stepMinute, stepWeek);
		dti.setDateTime(year, month, day, hour, minute, weekday, ampm);
		
		return dti;
	}

	public DateTimeItem() {
		mCalendar = Calendar.getInstance();
	}

	public void setStep(int stepYear, int stepMonth, int stepDay, int stepHour, int stepMinute, int stepWeek) {
		if (stepYear != -1) {
			mCalendar.add(Calendar.YEAR, stepYear);
		}
		if (stepMonth != -1) {
			mCalendar.add(Calendar.MONTH, stepMonth);
		}
		if (stepDay != -1) {
			mCalendar.add(Calendar.DAY_OF_MONTH, stepDay);
		}
		if (stepHour != -1) {
			mCalendar.add(Calendar.HOUR_OF_DAY, stepHour);
			if (!mIsHourOrMinuteChange) {
				mIsHourOrMinuteChange = true;
			}
		}
		if (stepMinute != -1) {
			mCalendar.add(Calendar.MINUTE, stepMinute);
			if (!mIsHourOrMinuteChange) {
				mIsHourOrMinuteChange = true;
			}
		}
		if (stepWeek != -1) {
			// +week
			mCalendar.add(Calendar.DATE, 7 * stepWeek);
		}
	}

	public void setDateTime(int year, int month, int day, int hour, int minute, int weekday, String ampm) {
		if (weekday > 0) {
			weekday = weekday > 7 ? 7 : weekday;
			mCalendar.set(Calendar.DAY_OF_WEEK, (weekday + 1) % 7);
		}

		if (year > 0) {
			mCalendar.set(Calendar.YEAR, year);
		}
		if (month > 0) {
			month = month > 12 ? 12 : month;
			mCalendar.set(Calendar.MONTH, month - 1);
		}
		if (day > 0) {
			day = day > mCalendar.getMaximum(Calendar.DAY_OF_MONTH) ? mCalendar.getMaximum(Calendar.DAY_OF_MONTH) : day;
			mCalendar.set(Calendar.DAY_OF_MONTH, day);
		}

		if (hour < 0 && minute < 0 && !mIsHourOrMinuteChange) {
			if (SessionPreference.KEY_MORNING.equals(ampm)) {
				hour = 8;
				minute = 0;
			} else {
				hour = 14;
				minute = 0;
			}
		}

		if (hour > 0) {
			hour = hour > 24 ? 24 : hour;
			if (hour > 12) {
				mCalendar.set(Calendar.HOUR_OF_DAY, hour);
			} else {
				if (ampm.equals(SessionPreference.KEY_MORNING)) {
					mCalendar.set(Calendar.HOUR_OF_DAY, hour);
				} else if (ampm.equals(SessionPreference.KEY_AFTERNOON)) {
					mCalendar.set(Calendar.HOUR_OF_DAY, hour + 12);
				} else {
					if (getHour() >= 12) {
						mCalendar.set(Calendar.HOUR_OF_DAY, hour + 12);
					} else {
						mCalendar.set(Calendar.HOUR_OF_DAY, hour);
					}
				}
			}

			if (minute <= 0) {
				mCalendar.set(Calendar.MINUTE, 0);
			}
		}
		if (minute > 0) {
			minute = minute > 60 ? 60 : minute;
			mCalendar.set(Calendar.MINUTE, minute);
		}

		mCalendar.set(Calendar.SECOND, 0);
	}

	public int getYear() {
		return mCalendar.get(Calendar.YEAR);
	}

	public int getMonth() {
		return mCalendar.get(Calendar.MONTH) + 1;
	}

	public int getDay() {
		return mCalendar.get(Calendar.DAY_OF_MONTH);
	}

	public int getHour() {
		return mCalendar.get(Calendar.HOUR_OF_DAY);
	}

	public int getMinute() {
		return mCalendar.get(Calendar.MINUTE);
	}

	public int getWeekDay() {
		return mCalendar.get(Calendar.DAY_OF_WEEK) - 1 == 0 ? 7 : mCalendar.get(Calendar.DAY_OF_WEEK) - 1;
	}

	public Calendar getDateTime() {
		return mCalendar;
	}
}
