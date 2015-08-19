/**
 * Copyright (c) 2012-2012 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : WeatherDay.java
 * @ProjectName : V Plus 1.0
 * @PakageName : cn.yunzhisheng.vui.assistant.weather
 * @Author : Dancindream
 * @CreateDate : 2012-5-29
 */
package cn.yunzhisheng.vui.assistant.model;

import java.util.Calendar;

import android.text.TextUtils;

/**
 * @Module : WeatherDay
 * @Comments : 描述
 * @Author : Dancindream
 * @CreateDate : 2012-5-29
 * @ModifiedBy : Dancindream
 * @ModifiedDate: 2012-5-29
 * @Modified:
 * 2012-5-29: 实现基本功能
 */
public class WeatherDay {
	public static final String TAG = "WeatherDay";

	private int mYear = 2012;
	private int mMonth = 6;
	private int mDay = 5;
	private int mDayOfWeek = 2;

	private String mWeather = "";

	private int mHighestTemperature = 30;
	private int mLowestTemperature = 20;
	private int mCurrentTemperature = -1;

	private String mWind = "";
	private String mWindExt = "";

	private String mAdvice = "";
	private String mImageTitle = "";
	
	private boolean mIsFocusDay = false;
	
	public void setYear(int year) {
	    mYear = year;
	}
	
	public void setMonth(int month) {
	    mMonth = month;
	}
	
	public void setDay(int day) {
	    mDay = day;
	}
	
	public void setCurrentTemp(int currentTemp) {
	    mCurrentTemperature = currentTemp;
	}

	public void setFocusDay() {
		mIsFocusDay = true;
	}
	
	public boolean isFocusDay() {
		return mIsFocusDay;
	}

	public void setDate(int year, int month, int day) {
		mYear = Math.max(0, year);
		mMonth = Math.max(0, month);
		mDay = Math.max(0, day);
	}

	public void setDayOfWeek(int dayOfWeek) {
		dayOfWeek = Math.max(Calendar.SUNDAY, dayOfWeek);
		dayOfWeek = Math.min(Calendar.SATURDAY, dayOfWeek);

		mDayOfWeek = dayOfWeek;
	}

	public void setTemperatureRange(int highestTemperature, int lowestTemperature) {
		int maxTemperature = Math.max(highestTemperature, lowestTemperature);
		int minTemperature = Math.min(highestTemperature, lowestTemperature);

		mHighestTemperature = maxTemperature;
		mLowestTemperature = minTemperature;
	}

	public void setWeather(String weather) {
		if (!TextUtils.isEmpty(weather)) {
			mWeather = weather.trim();
		}
	}

	public void setWind(String wind, String windExt) {
		if (!TextUtils.isEmpty(wind)) {
			mWind = wind.trim();
		}
		if (!TextUtils.isEmpty(windExt)) {
			mWindExt = windExt.trim();
		}
	}

	public void setAdvice(String advice) {
		if (!TextUtils.isEmpty(advice)) {
			mAdvice = advice.trim();
		}
	}

	public void setImageTitle(String img) {
		if (!TextUtils.isEmpty(img)) {
			mImageTitle = img.trim();
		}
	}

	/**
	 * @return the mYear
	 */
	public int getYear() {
		return mYear;
	}

	/**
	 * @return the mMonth
	 */
	public int getMonth() {
		return mMonth;
	}

	/**
	 * @return the mDay
	 */
	public int getDay() {
		return mDay;
	}

	/**
	 * @return the mDayOfWeek
	 */
	public int getDayOfWeek() {
		return mDayOfWeek;
	}

	/**
	 * @return the mWeather
	 */
	public String getWeather() {
		return mWeather;
	}

	/**
	 * @return the mMaxTemperature
	 */
	public int getHighestTemperature() {
		return mHighestTemperature;
	}

	/**
	 * @return the mMinTemperature
	 */
	public int getLowestTemperature() {
		return mLowestTemperature;
	}

	public String getTemperatureRange() {
		return getLowestTemperature() + "/" + getHighestTemperature() + "℃";
	}

	/**
	 * @return the mNowTemperature
	 */
	public int getCurrentTemperature() {
		return mCurrentTemperature;
	}

	/**
	 * @return the mWind
	 */
	public String getWind() {
		return mWind;
	}

	/**
	 * @return the mWindExt
	 */
	public String getWindExt() {
		return mWindExt;
	}

	/**
	 * @return the mAdvice
	 */
	public String getAdvice() {
		return mAdvice;
	}

	/**
	 * @return the mImageTitle
	 */
	public String getImageTitle() {
		return mImageTitle;
	}

}
