package cn.yunzhisheng.vui.assistant.model;

import java.util.ArrayList;

public class TVSearchResult {
	public String channel = "";
	public ArrayList<TVByDateGroupItem> byDate = new ArrayList<TVByDateGroupItem>();

	public TVSearchResult() {}
	
	public TVSearchResult(String channel) {
		this.channel = channel;
	}
}
