package cn.yunzhisheng.vui.assistant.util;


import android.content.Context;
import android.media.AudioManager;


public class AudioUtil {

	private static int current;
	

	/**
	 * 获取当前的媒体的音量
	 *
	 * @param mAudioManager 
	 */
  public static int getcurrentAudio(Context context, AudioManager mAudioManager){
	  current = mAudioManager.getStreamVolume( AudioManager.STREAM_MUSIC );
	
	  return current;
	  
  }
  /**
   * 获取当前铃声的音量
   */
  public static int  getcurrentRing(Context context,AudioManager mAudioManager){
	  current = mAudioManager.getStreamVolume( AudioManager.STREAM_RING );
	  
	return current;
	  
  }
}
