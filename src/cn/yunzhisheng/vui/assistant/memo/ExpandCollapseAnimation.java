package cn.yunzhisheng.vui.assistant.memo;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout.LayoutParams;

/**
 * This animation class is animating the expanding and reducing the size of a
 * view.
 * The animation toggles between the Expand and Reduce, depending on the current
 * state of the view
 * @author Udinic
 * 
 */
public class ExpandCollapseAnimation extends Animation {
	private View mAnimatedView;
	private LayoutParams mViewLayoutParams;
	private int mMarginStart, mMarginEnd;
	private boolean mExpand = false;
	private boolean mWasEndedAlready = false;

	/**
	 * Initialize the animation
	 * @param view The layout we want to animate
	 * @param duration The duration of the animation, in ms
	 */
	public ExpandCollapseAnimation(View view, boolean expand, int duration) {
		setDuration(duration);
		mAnimatedView = view;
		mExpand = expand;
		mViewLayoutParams = (LayoutParams) view.getLayoutParams();

		if (expand) {
			mMarginStart = -view.getHeight();
			mMarginEnd = 0;
			view.setVisibility(View.VISIBLE);
		} else {
			mMarginStart = 0;
			mMarginEnd = -view.getHeight();
		}
	}

	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {
		super.applyTransformation(interpolatedTime, t);

		if (interpolatedTime < 1.0f) {
			// Calculating the new bottom margin, and setting it
			mViewLayoutParams.bottomMargin = mMarginStart + (int) ((mMarginEnd - mMarginStart) * interpolatedTime);
			// Invalidating the layout, making us seeing the changes we made
			mAnimatedView.requestLayout();
		} else if (!mWasEndedAlready) {
			mViewLayoutParams.bottomMargin = mMarginEnd;
			mAnimatedView.requestLayout();
			if (!mExpand) {
				mAnimatedView.setVisibility(View.GONE);
			}
			mWasEndedAlready = true;
		}
	}
}
