package cn.yunzhisheng.vui.assistant.talk;

interface ITalkService {
	void startTalk();
	void stopTalk();
	void cancelTalk(boolean callback);
	void putCustomText(String text);
	void setProtocal(String protocal);
	void setRecognizerTalkType(String type);
	void onStart();
	void onStop();
	void playTTS(String text);
	void cancelTTS();
	String getContactName(String number);
}