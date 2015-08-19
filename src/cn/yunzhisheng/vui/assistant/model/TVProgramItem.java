package cn.yunzhisheng.vui.assistant.model;

public class TVProgramItem {
	public String pid = "";
	public String channel = "";
	public String title = "";
	public String time = "";

	public TVProgramItem(){}
	
	public TVProgramItem(String pid, String channel, String title, String time) {
		this.pid = pid;
		this.channel = channel;
		this.title = title;
		this.time = time;
	}
}
