/**
 * Copyright (c) 2012-2012 Mango(Shanghai) Co.Ltd. All right reserved.
 * @FileName : FunctionView.java
 * @ProjectName : iShuoShuo2
 * @PakageName : cn.yunzhisheng.vui.assistant.view
 * @Author : Brant
 * @CreateDate : 2012-11-12
 */
package cn.yunzhisheng.vui.assistant.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;
import cn.yunzhisheng.common.net.Network;
import com.ilincar.voice.R;
import cn.yunzhisheng.vui.assistant.model.KnowledgeMode;
import cn.yunzhisheng.vui.assistant.preference.SessionPreference;
import cn.yunzhisheng.vui.assistant.talk.TalkService;

public class FunctionView extends RelativeLayout implements ISessionView {
	public static final String TAG = "FunctionView";
	private LayoutInflater mLayoutInflater;
	private Context mContext;
	
	private Context context;
	private float textSize;
	private int textColor;
	private int wordMargin;
	private int lineMargin;
	private int textPaddingLeft;
	private int textPaddingRight;
	private int textPaddingTop;
	private int textPaddingBottom;
	private Drawable textBackground;

	private int layout_width;
	
	private int totalLength;
	
	private Resources mResource ;

	public FunctionView(Context context, AttributeSet attrs) {
		super(context);
		mContext = context;
		mLayoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		this.context = context;

		TypedArray array = context.obtainStyledAttributes(attrs,R.styleable.MyView);
		
		textColor = array.getColor(R.styleable.MyView_textColor, 0XFF00FF00); // 提供默认值，放置未指定
		textSize = array.getDimension(R.styleable.MyView_textSize, 24);
		textSize=px2sp(context,textSize);
		wordMargin = array.getDimensionPixelSize(R.styleable.MyView_textWordMargin, 0);
		lineMargin = array.getDimensionPixelSize(R.styleable.MyView_textLineMargin, 0);
		textBackground = array.getDrawable(R.styleable.MyView_textBackground);
		textPaddingLeft = array.getDimensionPixelSize(R.styleable.MyView_textPaddingLeft, 0);
		textPaddingRight = array.getDimensionPixelSize(R.styleable.MyView_textPaddingRight, 0);
		textPaddingTop = array.getDimensionPixelSize(R.styleable.MyView_textPaddingTop, 0);
		textPaddingBottom = array.getDimensionPixelSize(R.styleable.MyView_textPaddingBottom, 0);
		array.recycle();
		//下边是获取系统属性
		int[] attrsArray = new int[] { android.R.attr.id, // 0
				android.R.attr.background, // 1
				android.R.attr.layout_marginLeft, // 4
				android.R.attr.layout_marginRight, // 5
				android.R.attr.layout_width, // 2
				android.R.attr.layout_height, // 3
				android.R.attr.layout_marginTop // 5
		};
		
		mResource = context.getResources();
		
		float width = mResource.getDimension(R.dimen.window_width);
		
		TypedArray ta = context.obtainStyledAttributes(attrs, attrsArray);
		
		DisplayMetrics dm = getResources().getDisplayMetrics();
		//layout_width = dm.widthPixels;
		layout_width = (int) width;
		
		int ddpi = dm.densityDpi;
		float desity = dm.density;
		Log.i(TAG, "layout_width2: " +layout_width +" " +width+ " "+ ddpi +" " + desity);
	
	    int marginRight = ta.getDimensionPixelSize(2, 0);
	    int marginLeft = ta.getDimensionPixelSize(3, 0);
	    layout_width = layout_width - marginRight - marginLeft;

	    Log.i(TAG, "layout_width :"+layout_width +"marginRight:" +marginRight +" marginLeft: "+marginLeft+"ddd_"+textSize);
	
	    ta.recycle();
	    
	    initFunctionViews();
	    createShowText();
	}
	
	@Override
	protected void dispatchDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.dispatchDraw(canvas);
	}

	
	public void setTextViews() {
		if (showTexts ==null||showTexts.size()==0) {
			return;
		}
		
		// 每一行拉伸
		int line = 0;
		Map<Integer, List<TextView>> lineMap = new HashMap<Integer, List<TextView>>();
		List<TextView> lineList = new ArrayList<TextView>();
		lineMap.put(0, lineList);

		int x = 0;
		int y = 0;

		for (int i = 0; i < showTexts.size(); i++) {
			TextView tv = new TextView(context);
			tv.setText(showTexts.get(i));
			tv.setTextSize(textSize);
			if (textBackground != null)
				tv.setBackgroundDrawable(textBackground);

			tv.setTextColor(textColor);
			tv.setPadding(textPaddingLeft, textPaddingTop, textPaddingRight,textPaddingBottom);
			tv.setBackgroundResource(R.drawable.view_text_bk);
			tv.setGravity(Gravity.CENTER_VERTICAL);
			tv.setTag(i);// 标记position
		
			int w = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
			int h = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
			tv.measure(w, h);
			int tvh = tv.getMeasuredHeight();
			int tvw = getMeasuredWidth(tv);
			
			Log.i(TAG, "w: " +w +" h: "+h +" tvh:"+tvh +" tvw:"+tvw);
			
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			if (x + tvw > layout_width) {
				x = 0;
				y = y + tvh + lineMargin;
				// 拉伸处理
				line++;
				lineMap.put(line, new ArrayList<TextView>());
			}
			lp.leftMargin = x;
			lp.topMargin = y;
			x = x + tvw + wordMargin;
			tv.setLayoutParams(lp);
			// 拉伸处理
			lineMap.get(line).add(tv);
		}
		// 每一行拉伸
		for (int i = 0; i <= line; i++) {
			// 该行最后一个位置
			int len = lineMap.get(i).size();

			TextView lastView = lineMap.get(i).get(len - 1);
			RelativeLayout.LayoutParams lastLp = (RelativeLayout.LayoutParams) lastView.getLayoutParams();
			int viewLen = lastLp.leftMargin + getMeasuredWidth(lastView) - wordMargin;
			int firstMargin = (layout_width - viewLen) / 2;

			for (int j = 0; j < len; j++) {
				TextView tView2 = lineMap.get(i).get(j);
				RelativeLayout.LayoutParams lp2 = (RelativeLayout.LayoutParams) tView2.getLayoutParams();
				lp2.leftMargin = lp2.leftMargin + firstMargin;
				tView2.setPadding(
						tView2.getPaddingLeft() /*+ padding*/,
						tView2.getPaddingTop(), 
						tView2.getPaddingRight()/*+padding*/, 
						tView2.getPaddingBottom());
				addView(tView2);
			}
		}
	}
	
	public int getMeasuredWidth(View v) {
		//return v.getMeasuredWidth() + v.getPaddingLeft() + v.getPaddingRight();
		return v.getMeasuredWidth() ;
	}
	public static int px2sp(Context context, float pxValue) {  
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;  
        return (int) (pxValue / fontScale + 0.5f);  
    } 
	interface OnMultipleTVItemClickListener {
		public void onMultipleTVItemClick(View view, int position);
	}
	
	private class SupportDomain {
		String type;
		int resourceId;
		boolean hasNetwork;
	}
	
	private ArrayList<SupportDomain> supprotList = new ArrayList<SupportDomain>();
	
	private ArrayList<String> showTexts = new ArrayList<String>();
	
	public void initFunctionViews() {
		removeAllViews();

		if(supprotList.size() > 0 ) {
			supprotList.clear();
		}
		
		if(showTexts.size() > 0) {
			showTexts.clear();
		}
		
		/** 2014-12-2 yujun */
		Network.checkNetworkConnected(mContext);
		/** ---------------- */
		boolean hasNetWork = Network.hasNetWorkConnect();

		ArrayList<String> supportList = TalkService.getSupportList(hasNetWork);

		// Call
		if (isSupport(SessionPreference.DOMAIN_CALL, supportList)) {
			if (hasNetWork) {
				addFunctionItem(SessionPreference.DOMAIN_CALL,
						R.array.function_example_call);
			} else {
				addFunctionItem(SessionPreference.DOMAIN_CALL,
						R.array.function_example_call_offline);
			}
		}

		// SMS
		if (isSupport(SessionPreference.DOMAIN_SMS, supportList)) {
			if (hasNetWork) {
				addFunctionItem(SessionPreference.DOMAIN_SMS,
						R.array.function_example_sms);
			}
		}

		// Music
		if (isSupport(SessionPreference.DOMAIN_MUSIC, supportList)) {
			if (hasNetWork) {
				addFunctionItem(SessionPreference.DOMAIN_MUSIC,
						R.array.function_example_music);
			} else {
				addFunctionItem(SessionPreference.DOMAIN_MUSIC,
						R.array.function_example_music_offline);
			}
		}

		// Broadcast
		if (isSupport(SessionPreference.DOMAIN_BROADCAST, supportList)) {
			if (hasNetWork) {
				addFunctionItem(SessionPreference.DOMAIN_BROADCAST,
						R.array.function_example_broadcast);
			} else {
				addFunctionItem(SessionPreference.DOMAIN_BROADCAST,
						R.array.function_example_broadcast_offline);
			}
		}

		// MapRoute
		if (isSupport(SessionPreference.DOMAIN_ROUTE, supportList)) {
			if (hasNetWork) {
				addFunctionItem(SessionPreference.DOMAIN_ROUTE,
						R.array.function_example_route);
			} /*else {
				addFunctionItem(SessionPreference.DOMAIN_ROUTE,
						R.array.function_example_route_offline);
			}*/
		}

		// Weather
		if (isSupport(SessionPreference.DOMAIN_WEATHER, supportList)) {
			if (hasNetWork) {
				addFunctionItem(SessionPreference.DOMAIN_WEATHER,
						R.array.function_example_weather);
			}
		}

		// Setting
		if (isSupport(SessionPreference.DOMAIN_SETTING, supportList)) {
			if (hasNetWork) {
				addFunctionItem(SessionPreference.DOMAIN_SETTING,
						R.array.function_example_setting);
			} else {
				addFunctionItem(SessionPreference.DOMAIN_SETTING,
						R.array.function_example_setting_offline);
			}
		}

//		// tv
//		 if (isSupport(SessionPreference.DOMAIN_TV, supportList)) {
//		     addFunctionItem( SessionPreference.DOMAIN_TV,
//		     R.array.function_example_tv);
//		 }

		// stock
		if (isSupport(SessionPreference.DOMAIN_STOCK, supportList)) {
		      addFunctionItem( SessionPreference.DOMAIN_STOCK,
		      R.array.function_example_stock);
		  }

//		// NearBy
//		if (isSupport(SessionPreference.DOMAIN_NEARBY_SEARCH, supportList)) {
//		     if(hasNetWork){ 
//		         addFunctionItem(SessionPreference.DOMAIN_NEARBY_SEARCH,
//		                 R.array.function_example_nearby);
//		     }
//		  }
//		 
//		// flight
//	   if (isSupport(SessionPreference.DOMAIN_FLIGHT, supportList)) {
//		  addFunctionItem( SessionPreference.DOMAIN_FLIGHT,
//		  R.array.function_example_flight);
//	   }
//
//		// train
//	  if (isSupport(SessionPreference.DOMAIN_TRAIN, supportList)) {
//		  addFunctionItem( SessionPreference.DOMAIN_TRAIN,
//		                  R.array.function_example_train);
//	   }
//
//		// Contact
//	  if (isSupport(SessionPreference.DOMAIN_CONTACT, supportList)) {
//		  addFunctionItem( SessionPreference.DOMAIN_CONTACT,
//		              R.array.function_example_contact); 
//	   }
//
//		// App
//	   if (isSupport(SessionPreference.DOMAIN_APP, supportList)) {
//		  addFunctionItem( SessionPreference.DOMAIN_APP,
//		              R.array.function_example_app); }
//
//		// sitemap
//	   if (isSupport(SessionPreference.DOMAIN_SITEMAP, supportList)) {
//		  addFunctionItem( SessionPreference.DOMAIN_SITEMAP,
//		              R.array.function_example_sitemap);
//	   }
//
//		// translation
//	   if (isSupport(SessionPreference.DOMAIN_TRANSLATION, supportList)) {
//		   addFunctionItem( SessionPreference.DOMAIN_TRANSLATION,
//		              R.array.function_example_translation); 
//	   }
//
//		// Chat
//	   if (isSupport(SessionPreference.DOMAIN_CHAT, supportList)) {
//		    addFunctionItem( SessionPreference.DOMAIN_CHAT,
//		               R.array.function_example_talk); 
//       }
	}
	
	private void createShowText() {
		
		Log.i(TAG, "supprotList.size()  :"+supprotList.size());
		if(supprotList.size() > 0) {
			int size = supprotList.size();
			int number = mResource.getInteger(R.integer.show_text_number);
			
			int showNo = size > number ? number : size;
			
			int[] numberArray = randomArray(0, size-1 , showNo) ;
			
			for(int i = 0 ; i < numberArray.length ;i ++) {
				
				SupportDomain sd = supprotList.get(numberArray[i]);
				String s = KnowledgeMode.getRandomContentString(getContext(),sd.resourceId);
				
				Log.i(TAG, "i :"+i +" numberArray[i]:" +numberArray[i] +" s" +s);
				showTexts.add(s);
			}
			
			
		} 
	}
	
	private int[] randomArray(int min,int max,int n){  
	    int len = max-min+1;  
	      
	    if(max < min || n > len){  
	        return null;  
	    }  
	      
	    //初始化给定范围的待选数组  
	    int[] source = new int[len];  
	       for (int i = min; i < min+len; i++){  
	        source[i-min] = i;  
	       }  
	         
	       int[] result = new int[n];  
	       Random rd = new Random();  
	       int index = 0;  
	       for (int i = 0; i < result.length; i++) {  
	        //待选数组0到(len-2)随机一个下标  
	           index = Math.abs(rd.nextInt() % len--);  
	           //将随机到的数放入结果集  
	           result[i] = source[index];  
	           //将待选数组中被随机到的数，用待选数组(len-1)下标对应的数替换  
	           source[index] = source[len];  
	       }  
	       return result;  
	}

	private boolean isSupport(String domain, ArrayList<String> list) {
		return isSupport(domain, "", list);
	}

	private boolean isSupport(String domain, String code, ArrayList<String> list) {
		String sign = domain;
		if (code != null && !code.equals("")) {
			sign += "," + code;
		}

		if (list != null) {
			for (String line : list) {
				if (line != null && line.startsWith(sign)) {
					return true;
				}
			}
		} else {
			return true;
		}

		return false;
	}

	private void addFunctionItem(String functionTag, int functionRes) {
		
		SupportDomain sd = new SupportDomain();
		sd.type = functionTag;
		sd.resourceId = functionRes;
		supprotList.add(sd);
		
		/*View view = mLayoutInflater
				.inflate(R.layout.function_item, this, false);
		LinearLayout ll = (LinearLayout) view
				.findViewById(R.id.linearLayoutFunctionItem);
		TextView textViewHead = (TextView) ll.findViewById(R.id.textViewHead);
		textViewHead.setText(KnowledgeMode.getRandomContentString(getContext(),
				functionRes));
		addView(view);*/
	}

	public static interface IFunctionListener {
		public void onFunctionItemClick(String action);
	}

	@Override
	public boolean isTemporary() {
		return true;
	}

	@Override
	public void release() {
		removeAllViews();
	}
}
