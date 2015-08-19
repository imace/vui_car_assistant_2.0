package cn.yunzhisheng.vui.assistant.model;

import java.util.ArrayList;

public class TVByDateGroupItem {
	public String date = "";
	public ArrayList<TVProgramItem> programs = new ArrayList<TVProgramItem>();
	
	public TVByDateGroupItem(String date) {
		this.date = date;
	}
}
