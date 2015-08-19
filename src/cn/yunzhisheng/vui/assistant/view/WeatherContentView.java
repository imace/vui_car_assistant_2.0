/**
 * Copyright (c) 2012-2012 Yunzhisheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : WeatherContentView.java
 * @ProjectName : iShuoShuo2
 * @PakageName : cn.yunzhisheng.vui.assistant.view
 * @Author : Brant
 * @CreateDate : 2012-11-15
 */
package cn.yunzhisheng.vui.assistant.view;

import java.util.Calendar;
import java.util.HashMap;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.ilincar.voice.R;
import cn.yunzhisheng.vui.assistant.model.WeatherDay;
import cn.yunzhisheng.vui.assistant.model.WeatherInfo;
import cn.yunzhisheng.vui.assistant.util.Util;

public class WeatherContentView extends FrameLayout implements ISessionView {
	public static final String TAG = "WeatherContentView";

	private HashMap<String, Integer> mBigImageNameIdMap = null;
	
	private String TODAY_STRING = getResources().getString(R.string.today);
	private String TOMMOROW_STRING = getResources().getString(R.string.tomorrow);
	private String DAY_AFTER_TOMMOROW_STRING = getResources().getString(R.string.day_after_tomorrow);
	private String sunny = getResources().getString(R.string.weather_sunny_big);
	private String cloudy = getResources().getString(R.string.weather_cloudy_big);
	private String overcast = getResources().getString(R.string.weather_overcast_big);
	private String foggy = getResources().getString(R.string.weather_foggy_big);
	private String dustblow = getResources().getString(R.string.weather_dustblow_big);
	private String dust = getResources().getString(R.string.weather_dust_big);
	private String sandstorm = getResources().getString(R.string.weather_sandstorm_big);
	private String strong_sandstorm = getResources().getString(R.string.weather_strong_sandstorm_big);
	private String icerain = getResources().getString(R.string.weather_icerain_big);
	private String shower = getResources().getString(R.string.weather_shower_big);
	private String thunder_rain = getResources().getString(R.string.weather_thunder_rain_big);
	private String hail = getResources().getString(R.string.weather_hail_big);
	private String sleety = getResources().getString(R.string.weather_sleety_big);
	private String light_rain = getResources().getString(R.string.weather_light_rain_big);
	private String moderate_rain = getResources().getString(R.string.weather_moderate_rain_big);
	private String heavy_rain = getResources().getString(R.string.weather_heavy_rain_big);
	private String rainstorm = getResources().getString(R.string.weather_rainstorm_big);
	private String big_rainstorm = getResources().getString(R.string.weather_big_rainstorm_big);
	private String super_rainstorm = getResources().getString(R.string.weather_super_rainstorm_big);
	private String snow_shower = getResources().getString(R.string.weather_snow_shower_big);
	private String light_snow = getResources().getString(R.string.weather_light_snow_big);
	private String moderate_snow = getResources().getString(R.string.weather_moderate_snow_big);
	private String heavy_snow_big = getResources().getString(R.string.weather_heavy_snow_big);
	private String blizzard = getResources().getString(R.string.weather_blizzard_big);
	private String haze = getResources().getString(R.string.weather_haze);

	private void initBigImageNameIdMap() {
	    if (mBigImageNameIdMap == null) {
	        mBigImageNameIdMap = new HashMap<String, Integer>();
	    }
	    mBigImageNameIdMap.clear();
		mBigImageNameIdMap.put(sunny, R.drawable.ic_weather_sunny_big);
		mBigImageNameIdMap.put(cloudy, R.drawable.ic_weather_cloudy_big);
		mBigImageNameIdMap.put(overcast, R.drawable.ic_weather_overcast_big);
		mBigImageNameIdMap.put(foggy, R.drawable.ic_weather_foggy_big);
		mBigImageNameIdMap.put(dustblow, R.drawable.ic_weather_dustblow_big);
		mBigImageNameIdMap.put(dust, R.drawable.ic_weather_dust_big);
		mBigImageNameIdMap.put(sandstorm, R.drawable.ic_weather_sandstorm_big);
		mBigImageNameIdMap.put(strong_sandstorm, R.drawable.ic_weather_strong_sandstorm_big);
		mBigImageNameIdMap.put(icerain, R.drawable.ic_weather_icerain_big);
		mBigImageNameIdMap.put(shower, R.drawable.ic_weather_shower_big);
		mBigImageNameIdMap.put(thunder_rain, R.drawable.ic_weather_thunder_rain_big);
		mBigImageNameIdMap.put(hail, R.drawable.ic_weather_hail_big);
		mBigImageNameIdMap.put(sleety, R.drawable.ic_weather_sleety_big);
		mBigImageNameIdMap.put(light_rain, R.drawable.ic_weather_light_rain_big);
		mBigImageNameIdMap.put(moderate_rain, R.drawable.ic_weather_moderate_rain_big);
		mBigImageNameIdMap.put(heavy_rain, R.drawable.ic_weather_heavy_rain_big);
		mBigImageNameIdMap.put(rainstorm, R.drawable.ic_weather_rainstorm_big);
		mBigImageNameIdMap.put(big_rainstorm, R.drawable.ic_weather_big_rainstorm_big);
		mBigImageNameIdMap.put(super_rainstorm, R.drawable.ic_weather_super_rainstorm_big);
		mBigImageNameIdMap.put(snow_shower, R.drawable.ic_weather_snow_shower_big);
		mBigImageNameIdMap.put(light_snow, R.drawable.ic_weather_light_snow_big);
		mBigImageNameIdMap.put(moderate_snow, R.drawable.ic_weather_moderate_snow_big);
		mBigImageNameIdMap.put(heavy_snow_big, R.drawable.ic_weather_heavy_snow_big);
		mBigImageNameIdMap.put(blizzard, R.drawable.ic_weather_blizzard_big);
		mBigImageNameIdMap.put(haze, R.drawable.ic_weather_haze);
	}

	// private View mViewTodayWeather, mViewOtherDaysWeather, mViewDivider;
//	private TextView mTextViwCity, mTextViewUpdateTime,mTextViewFirstDayDate,mTextViewWeatherFirstDayTemperature, mTextViewWeatherFirstDayMaxTemperature,mTextViewWeatherFirstDayMinTemperature, mTextViewWeatherFirstDayWeather, mTextViewWeatherFirstDayWind;
	private TextView mTextViewWeatherSecondDayDatetime;
	private TextView mTextViewWeatherSecondDayTemperature;
	private TextView mTextViewWeatherSecondDayWeather;
	private TextView mTextViewWeatherSecondDayWind;
	
//	private ImageView mImageViewFirstDayWeather;
	private ImageView mImageViewWeatherSecondDayWeather, mImageViewWeatherThirdDayWeather, mImageViewWeatherFouthDayWeather,mImageViewWeatherFifthDayWeather;

	private TextView mTextViewWeatherThirdDayDatetime;
	private TextView mTextViewWeatherThirdDayTemperature;
	private TextView mTextViewWeatherThirdDayWeather;
	private TextView mTextViewWeatherThirdDayWind;

	private TextView mTextViewWeatherFouthDayDatetime;
	private TextView mTextViewWeatherFouthDayTemperature;
	private TextView mTextViewWeatherFouthDayWeather;
	private TextView mTextViewWeatherFouthDayWind;
	
	private TextView mTextViewWeatherFifthDayDatetime;
	private TextView mTextViewWeatherFifthDayTemperature;
	private TextView mTextViewWeatherFifthDayWeather;
	private TextView mTextViewWeatherFifthDayWind;
	private String[] sDayOfWeekNames;
	
//	private View mSecondIndicatorView, mThirdIndicatorView, mFouthIndicatorView, mFifthIndicatorView;

	public WeatherContentView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.weather_content_view, this, true);
		findViews();
		sDayOfWeekNames = context.getResources().getStringArray(R.array.days_of_week);
		initBigImageNameIdMap();
	}

	public WeatherContentView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public WeatherContentView(Context context) {
		this(context, null);
	}

	private void findViews() {
//		mTextViwCity = (TextView) findViewById(R.id.textViewWeatherCity);
//		mTextViewUpdateTime = (TextView) findViewById(R.id.textViewWeatherUpdateTime);
//		mImageViewFirstDayWeather = (ImageView) findViewById(R.id.imageViewWeatherFirstDay);
//		mTextViewFirstDayDate = (TextView) findViewById(R.id.textViewWeatherFirstDayDate);
//		mTextViewWeatherFirstDayTemperature = (TextView) findViewById(R.id.textViewWeatherFirstDayTemperature);
//		mTextViewWeatherFirstDayMaxTemperature = (TextView) findViewById(R.id.textViewWeatherFirstDayMaxTemperature);
//		mTextViewWeatherFirstDayMinTemperature = (TextView) findViewById(R.id.textViewWeatherFirstDayMinTemperature);
//		mTextViewWeatherFirstDayWeather = (TextView) findViewById(R.id.textViewWeatherFirstDayWeather);
//		mTextViewWeatherFirstDayWind = (TextView) findViewById(R.id.textViewWeatherFirstDayWind);

		mTextViewWeatherSecondDayDatetime = (TextView) findViewById(R.id.textViewWeatherSecondDayDatetime);
		mImageViewWeatherSecondDayWeather = (ImageView) findViewById(R.id.imageViewWeatherSecondDayWeather);
		mTextViewWeatherSecondDayTemperature = (TextView) findViewById(R.id.textViewWeatherSecondDayTemperature);
		mTextViewWeatherSecondDayWeather = (TextView) findViewById(R.id.textViewWeatherSecondDayWeather);
		mTextViewWeatherSecondDayWind = (TextView) findViewById(R.id.textViewWeatherSecondDayWind);
//		mSecondIndicatorView = (View) findViewById(R.id.indicatorSecond);

		mTextViewWeatherThirdDayDatetime = (TextView) findViewById(R.id.textViewWeatherThirdDayDatetime);
		mImageViewWeatherThirdDayWeather = (ImageView) findViewById(R.id.imageViewWeatherThirdDayWeather);
		mTextViewWeatherThirdDayTemperature = (TextView) findViewById(R.id.textViewWeatherThirdDayTemperature);
		mTextViewWeatherThirdDayWeather = (TextView) findViewById(R.id.textViewWeatherThirdDayWeather);
		mTextViewWeatherThirdDayWind = (TextView) findViewById(R.id.textViewWeatherThirdDayWind);
//		mThirdIndicatorView = (View) findViewById(R.id.indicatorThird);

		mTextViewWeatherFouthDayDatetime = (TextView) findViewById(R.id.textViewWeatherFouthDayDatetime);
		mImageViewWeatherFouthDayWeather = (ImageView) findViewById(R.id.imageViewWeatherFouthDayWeather);
		mTextViewWeatherFouthDayTemperature = (TextView) findViewById(R.id.textViewWeatherFouthDayTemperature);
		mTextViewWeatherFouthDayWeather = (TextView) findViewById(R.id.textViewWeatherFouthDayWeather);
		mTextViewWeatherFouthDayWind = (TextView) findViewById(R.id.textViewWeatherFouthDayWind);
//		mFouthIndicatorView = (View) findViewById(R.id.indicatorFouth);

		mTextViewWeatherFifthDayDatetime = (TextView) findViewById(R.id.textViewWeatherFifthDayDatetime);
		mImageViewWeatherFifthDayWeather = (ImageView) findViewById(R.id.imageViewWeatherFifthDayWeather);
		mTextViewWeatherFifthDayTemperature = (TextView) findViewById(R.id.textViewWeatherFifthDayTemperature);
		mTextViewWeatherFifthDayWeather = (TextView) findViewById(R.id.textViewWeatherFifthDayWeather);
		mTextViewWeatherFifthDayWind = (TextView) findViewById(R.id.textViewWeatherFifthDayWind);
//		mFifthIndicatorView = (View) findViewById(R.id.indicatorFifth);

	}

	public void setWeatherInfo(WeatherInfo weatherInfo) {
		WeatherDay todayWeather = weatherInfo.getWeatherDay(0);

//		mTextViwCity.setText(weatherInfo.getCityName());
//
//		mTextViewUpdateTime.setText(weatherInfo.getUpdateTime());
//		mImageViewFirstDayWeather.setImageResource(getWeatherImage(todayWeather.getImageTitle()));
		Calendar calendar = Calendar.getInstance();
		calendar.set(todayWeather.getYear(), todayWeather.getMonth() - 1, todayWeather.getDay());
		int days = Util.daysOfTwo(Calendar.getInstance(), calendar);
		// String date = CalendarUtil.getReadableDateTime(getContext(),
		// calendar);
		String date = sDayOfWeekNames[(weatherInfo.getWeatherDay(0).getDayOfWeek() + 6) % 7];
		switch (days) {
		case 0:
			date = "今天 " + date;
			break;
		case 1:
			date = "明天 " + date;
			break;
		case 2:
			date = "后天 " + date;
			break;
		default:
			break;
		}
//		mTextViewFirstDayDate.setText(date);
//		mTextViewWeatherFirstDayTemperature.setText(todayWeather.getCurrentTemperature() + "℃");
//
//		mTextViewWeatherFirstDayWeather.setText(todayWeather.getWeather());
//		mTextViewWeatherFirstDayMaxTemperature.setText(todayWeather.getHighestTemperature() + "℃");
//		mTextViewWeatherFirstDayMinTemperature.setText(todayWeather.getLowestTemperature() + "℃");
//		mTextViewWeatherFirstDayWind.setText(todayWeather.getWind() + todayWeather.getWindExt());

		WeatherDay weatherSecondDay = weatherInfo.getWeatherDay(1);
		mTextViewWeatherSecondDayDatetime.setText(TODAY_STRING);
		mImageViewWeatherSecondDayWeather.setImageResource(getWeatherImage(weatherSecondDay.getImageTitle()));
		mTextViewWeatherSecondDayWind.setText(weatherSecondDay.getWind());
		mTextViewWeatherSecondDayTemperature.setText(weatherSecondDay.getTemperatureRange());
		mTextViewWeatherSecondDayWeather.setText(weatherSecondDay.getWeather());
//		if (weatherSecondDay.isFocusDay()) {
//			mSecondIndicatorView.setSelected(true);
//		} else {
//			mSecondIndicatorView.setSelected(false);
//		}

		WeatherDay weatherThirdDay = weatherInfo.getWeatherDay(2);
		mTextViewWeatherThirdDayDatetime.setText(TOMMOROW_STRING);
		mImageViewWeatherThirdDayWeather.setImageResource(getWeatherImage(weatherThirdDay.getImageTitle()));
		mTextViewWeatherThirdDayWind.setText(weatherThirdDay.getWind());
		mTextViewWeatherThirdDayTemperature.setText(weatherThirdDay.getTemperatureRange());
		mTextViewWeatherThirdDayWeather.setText(weatherThirdDay.getWeather());
//		if (weatherThirdDay.isFocusDay()) {
//			mThirdIndicatorView.setSelected(true);
//		} else {
//			mThirdIndicatorView.setSelected(false);
//		}

		WeatherDay weatherFouthDay = weatherInfo.getWeatherDay(3);
		mTextViewWeatherFouthDayDatetime.setText(DAY_AFTER_TOMMOROW_STRING);
		mImageViewWeatherFouthDayWeather.setImageResource(getWeatherImage(weatherFouthDay.getImageTitle()));
		mTextViewWeatherFouthDayWind.setText(weatherFouthDay.getWind());
		mTextViewWeatherFouthDayTemperature.setText(weatherFouthDay.getTemperatureRange());
		mTextViewWeatherFouthDayWeather.setText(weatherFouthDay.getWeather());
//		if (weatherFouthDay.isFocusDay()) {
//			mFouthIndicatorView.setSelected(true);
//		} else {
//			mFouthIndicatorView.setSelected(false);
//		}

		WeatherDay weatherFifthDay = weatherInfo.getWeatherDay(4);
		String weather_data = getResources().getString(R.string.weather_data, weatherFifthDay.getMonth(),weatherFifthDay.getDay());
		mTextViewWeatherFifthDayDatetime.setText(weather_data);
		mImageViewWeatherFifthDayWeather.setImageResource(getWeatherImage(weatherFifthDay.getImageTitle()));
		mTextViewWeatherFifthDayWind.setText(weatherFifthDay.getWind());
		mTextViewWeatherFifthDayTemperature.setText(weatherFifthDay.getTemperatureRange());
		mTextViewWeatherFifthDayWeather.setText(weatherFifthDay.getWeather());
//		if (weatherFifthDay.isFocusDay()) {
//			mFifthIndicatorView.setSelected(true);
//		} else {
//			mFifthIndicatorView.setSelected(false);
//		}
	}

	private Integer getWeatherImage(String imgTitle) {
		return mBigImageNameIdMap.containsKey(imgTitle) ? mBigImageNameIdMap.get(imgTitle) : mBigImageNameIdMap.get(overcast);
	}

	@Override
	public boolean isTemporary() {
		return true;
	}

	@Override
	public void release() {
		sDayOfWeekNames = null;
	}

}
