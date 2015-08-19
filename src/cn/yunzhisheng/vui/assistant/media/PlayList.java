package cn.yunzhisheng.vui.assistant.media;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayList implements Serializable {
	public static final String TAG = "PlayList";

	private static final long serialVersionUID = 1L;

	public enum PlaylistPlaybackMode {
		NORMAL, SHUFFLE, REPEAT, SHUFFLE_AND_REPEAT
	}

	protected List<TrackInfo> mPlayList = null;

	protected int selected = -1;

	private ArrayList<Integer> mPlayOrder = new ArrayList<Integer>();

	private PlaylistPlaybackMode mPlaylistPlaybackMode = PlaylistPlaybackMode.NORMAL;

	public PlayList() {
		mPlayList = new ArrayList<TrackInfo>();

		calculateOrder(true);
	}

	public PlayList(List<TrackInfo> playList) {
		mPlayList = playList;
		calculateOrder(true);
	}

	public PlaylistPlaybackMode getPlaylistPlaybackMode() {
		return mPlaylistPlaybackMode;
	}

	public void setPlaylistPlaybackMode(PlaylistPlaybackMode aPlaylistPlaybackMode) {
		boolean force = false;
		switch (aPlaylistPlaybackMode) {
		case NORMAL:
		case REPEAT:
			if (mPlaylistPlaybackMode == PlaylistPlaybackMode.SHUFFLE
				|| mPlaylistPlaybackMode == PlaylistPlaybackMode.SHUFFLE_AND_REPEAT) {
				force = true;
			}
			break;
		case SHUFFLE:
		case SHUFFLE_AND_REPEAT:
			if (mPlaylistPlaybackMode == PlaylistPlaybackMode.NORMAL
				|| mPlaylistPlaybackMode == PlaylistPlaybackMode.REPEAT) {
				force = true;
			}
			break;
		}
		mPlaylistPlaybackMode = aPlaylistPlaybackMode;
		calculateOrder(force);
	}

	public void addTrack(TrackInfo track) {
		if (mPlayList != null) {
			mPlayList.add(track);
			mPlayOrder.add(size() - 1);
		}
	}

	public TrackInfo findTrack(String title, String artist) {
		for (TrackInfo track : mPlayList) {
			if (track.getTitle().equals(title) && track.getArtist().equals(artist)) {
				return track;
			}
		}

		return null;
	}

	public boolean isEmpty() {
		if (mPlayList != null) {
			return mPlayList.size() == 0;
		}
		return true;
	}

	public void selectNext() {
		if (!isEmpty()) {
			selected++;
			selected %= mPlayList.size();
		}
	}

	public void selectPrev() {
		if (!isEmpty()) {
			selected--;
			if (selected < 0) selected = mPlayList.size() - 1;
		}
	}

	public void select(int index) {
		if (!isEmpty()) {
			if (index != selected) {
				if (index >= 0 && index < mPlayList.size()) {
					selected = mPlayOrder.indexOf(index);
				}
			}
		}
	}

	public void selectOrAdd(TrackInfo track) {
		for (int i = 0; i < mPlayList.size(); i++) {
			if (mPlayList.get(i).getTitle().equals(track.getTitle())) {
				select(i);
				return;
			}
		}
		addTrack(track);
		select(mPlayList.size() - 1);
	}

	public int getSelectedIndex() {
		if (isEmpty()) {
			selected = -1;
		}
		if (selected == -1 && !isEmpty()) {
			selected = 0;
		}
		return selected;
	}

	public TrackInfo getSelectedTrack() {
		TrackInfo track = null;

		int index = getSelectedIndex();
		if (index == -1) {
			return null;
		}
		index = mPlayOrder.get(index);
		if (index == -1) {
			return null;
		}
		track = mPlayList.get(index);

		return track;

	}

	public int size() {
		return mPlayList == null ? 0 : mPlayList.size();
	}

	public TrackInfo getTrack(int index) {
		if (mPlayList != null && index < mPlayList.size()) {
			return mPlayList.get(index);
		}
		return null;
	}

	public List<TrackInfo> getAllTracks() {
		return mPlayList;
	}

	public void remove(int position) {
		if (mPlayList != null && position < mPlayList.size() && position >= 0) {

			if (selected >= position) {
				selected--;
			}

			mPlayList.remove(position);
			mPlayOrder.remove(position);
		}
	}

	public void removeAll() {
		if (mPlayList != null) {
			mPlayList.clear();
			mPlayList = null;
		}
	}

	private void calculateOrder(boolean force) {
		if (mPlayOrder.isEmpty() || force) {
			int oldSelected = 0;

			if (!mPlayOrder.isEmpty()) {
				oldSelected = mPlayOrder.get(selected);
				mPlayOrder.clear();
			}

			for (int i = 0; i < size(); i++) {
				mPlayOrder.add(i, i);
			}

			if (mPlaylistPlaybackMode == null) {
				mPlaylistPlaybackMode = PlaylistPlaybackMode.NORMAL;
			}

			switch (mPlaylistPlaybackMode) {
			case NORMAL:
			case REPEAT:
				selected = oldSelected;
				break;
			case SHUFFLE:
			case SHUFFLE_AND_REPEAT:
				Collections.shuffle(mPlayOrder);
				selected = mPlayOrder.indexOf(selected);
				break;
			}
		}
	}

	public boolean isLastTrackOnList() {
		if (selected == size() - 1) return true;
		else return false;
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		if (mPlayOrder == null) {
			mPlayOrder = new ArrayList<Integer>();
			calculateOrder(true);
		}
	}
}
