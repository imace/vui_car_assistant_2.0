package cn.yunzhisheng.vui.assistant.model;

import java.util.ArrayList;

public class TVBroadcastsGroupItem {
	public String name = "";
	public String pid = "";
	public String score = "";
	
	public ArrayList<TVProgramItem> programs = new ArrayList<TVProgramItem>();
	
	public TVBroadcastsGroupItem(String name) {
		this.name = name;
	}
}
