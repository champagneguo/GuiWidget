package com.ntian.nguiwidget;

import java.lang.ref.WeakReference;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

public class NTVerticalMotionFrameLayout extends FrameLayout implements android.animation.Animator.AnimatorListener {
	int mTopMarginMin = 0;
	int mTopMarginMax = 0;
	int mTopMarginNormal = -1;
	
    /**
     * is being dragged
     */
    private boolean mIsBeingDragged;

    /**
     * is unabled to drag
     */
    private boolean mIsUnableToDrag;

    /**
     * Touch
     */
    private int mTouchSlop;

    private float mInitialMotionY;

    /**
     * Position of the last motion event.
     */
    private float mLastMotionX;
    private float mLastMotionY;

    /**
     * ID of the active pointer. This is used to retain consistency during drags/flings if multiple
     * pointers are used.
     */
    private int mActivePointerId = INVALID_POINTER;
    /**
     * Sentinel value for no current active pointer. Used by {@link #mActivePointerId}.
     */
    private static final int INVALID_POINTER = -1;

    /**
     * Determines speed during touch scrolling
     */
    private VelocityTracker mVelocityTracker;
    private int mMinimumVelocity;
    private int mMaximumVelocity;
    private int mMaxAnchorDuration;
    private int mFlingDistance;

    private boolean mFakeDragging;
    //private long mFakeDragBeginTime;

    /**
     * Scrolling
     */
    private boolean mScrolling;

    /**
     * Indicates that the pager is in an idle, settled state. The current page is fully in view and
     * no animation is in progress.
     */
    public static final int SCROLL_STATE_IDLE = 0;

    /**
     * Indicates that the pager is currently being dragged by the user.
     */
    public static final int SCROLL_STATE_DRAGGING = 1;

    /**
     * Indicates that the pager is in the process of settling to a final position.
     */
    public static final int SCROLL_STATE_SETTLING = 2;
    private static final int MIN_DISTANCE_FOR_FLING = 25; // dips
    private int mScrollState = SCROLL_STATE_IDLE;
    private OnMotionListener mOnMotionListener;

    public interface OnMotionListener {
    	public static final int DIRECTION_TOP = 1;
    	public static final int DIRECTION_BOTTOM = 2;
        public abstract void onMoved(int toDirection, float currentOffset, float defaultOffset);
        public abstract void onMoveEnd(int destDirection);
    }

	public NTVerticalMotionFrameLayout(Context context) {
		this(context, null);
	}

	public NTVerticalMotionFrameLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public NTVerticalMotionFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public void setOnMotionListener(OnMotionListener onMotionListener) {
		mOnMotionListener = onMotionListener;
	}

    void init() {
        setWillNotDraw(false);
        setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
        setFocusable(true);
        final Context context = getContext();
        final ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledTouchSlop();//getScaledPagingTouchSlop();
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
        mMaxAnchorDuration = 300;//MiuiViewConfiguration.get(context).getMaxAnchorDuration();

        final float density = context.getResources().getDisplayMetrics().density;
        mFlingDistance = (int) (MIN_DISTANCE_FOR_FLING * density);
		mTopMarginMin = (int)context.getResources().getDimension(R.dimen.nt_verticalmotion_topmargin_min);
		mTopMarginMax = (int)context.getResources().getDimension(R.dimen.nt_verticalmotion_topmargin_max);
		//TypedArray a = context.obtainStyledAttributes(attrs, com.android.internal.R.styleable.ViewGroup_MarginLayout, defStyleAttr, 0);
		//mTopMarginNormal = a.getDimensionPixelSize(com.android.internal.R.styleable.ViewGroup_MarginLayout_layout_marginTop, mTopMarginMax);
		//a.recycle();
    }

    public void setMarginParam(int min, int max) {
    	mTopMarginMin = min;
    	mTopMarginMax = max;
    }

    public void setMarginParamRelative(int offset) {
    	mTopMarginMin += offset;
    	mTopMarginMax += offset;
    }

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (mTopMarginNormal == -1) {
			ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams)getLayoutParams();
			if (lp != null)	mTopMarginNormal = lp.topMargin;
		}
		super.onLayout(changed, l, t, r, b);
	}

	void computeNewLayout() {
		ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams)getLayoutParams();
		if (lp == null) return;

		if (mTopMarginNormal == -1) {
			if (lp != null)	mTopMarginNormal = lp.topMargin;
		}
		float diff = (mLastMotionY - mInitialMotionY);
		lp.topMargin += diff;
		if (lp.topMargin > mTopMarginMax) {
			lp.topMargin = mTopMarginMax;
		} else if (lp.topMargin < mTopMarginMin) {
			lp.topMargin = mTopMarginMin;
		}
		setLayoutParams(lp);
		if (mOnMotionListener != null) {
			float offset = (lp.topMargin - mTopMarginMin) / (float)(mTopMarginMax - mTopMarginMin);
			float defaultOffset = (mTopMarginNormal - mTopMarginMin) / (float)(mTopMarginMax - mTopMarginMin);
			mOnMotionListener.onMoved(diff > 0?OnMotionListener.DIRECTION_BOTTOM:OnMotionListener.DIRECTION_TOP, offset, defaultOffset);
		}
		//Log.d("test", "xiaoguo test 4, new layout top=" + lp.topMargin);		
	}

	boolean isForceDragged() {
		return _isLayoutInNormalMarginTop();
	}

	boolean _isLayoutInNormalMarginTop() {
		ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams)getLayoutParams();
		if (lp == null) return false;

		if (mTopMarginNormal == -1) {
			if (lp != null)	mTopMarginNormal = lp.topMargin;
		}

		return (mTopMarginMin < lp.topMargin);
	}

	void computeNewLayoutWithAnimation(int velocity) {
		ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams)getLayoutParams();
		if (lp == null) return;

		if (mTopMarginNormal == -1) {
			if (lp != null)	mTopMarginNormal = lp.topMargin;
		}
		float diffY = (mLastMotionY - mInitialMotionY);
		float fromY=0;
		float toY=0;
		float mid = (mTopMarginMax + mTopMarginMin) / 2;
		if (Math.abs(diffY) < (mTopMarginNormal - mTopMarginMin) / 10) {
			//back to raw position
			fromY = lp.topMargin;
			if (lp.topMargin > mid) {
				toY = mTopMarginNormal;
			} else {
				toY = mTopMarginMin;
			}
			setLayoutWithAnimation(fromY, toY, velocity, false);
		} else {
			//move to new position
			fromY = lp.topMargin;
			if (diffY > 0) {
				int velocityFix = Math.abs(velocity) * 10;
				if (velocityFix > mMaximumVelocity) velocityFix = mMaximumVelocity;
				float offset = (mTopMarginMax - mTopMarginNormal) * (velocityFix / (float)mMaximumVelocity);
				toY = mTopMarginNormal + offset;
				//Log.d("test", "xiaoguo test 5, offset=" + offset + ", v=" + velocity + ", minv=" + mMinimumVelocity+ ", maxv=" + mMaximumVelocity);
				
				setLayoutWithAnimation(fromY, toY, velocity, true);
			} else {
				toY = mTopMarginMin;
				setLayoutWithAnimation(fromY, toY, velocity, false);
			}
		}
	}

	private TranslateAnimationListener mAnimListener;
	private ValueAnimator mAnimator;

	protected int getDuration(int paramInt1, int paramInt2) {
		int i = Math.abs(paramInt1);
		int j = (-1 + (1000 + Math.abs(paramInt2))) / 1000;
		if (j > 0) return Math.min(this.mMaxAnchorDuration, i * 2 / j);
		return this.mMaxAnchorDuration;
	}

	void setLayoutWithAnimation(float fromY, float toY, int velocity, boolean debouce) {
	    if (mAnimator != null) {
	        mAnimListener = null;
	        mAnimator.cancel();
	        mAnimator = null;
	    }

		mAnimator = ObjectAnimator.ofFloat(new float[] { 0.0F, 1.0F });
		mAnimator.setInterpolator(new DecelerateInterpolator());
		//mAnimator.setDuration((long)(mMaxAnchorDuration * (1.0f - (velocity / mMaximumVelocity))));
		mAnimator.setDuration(getDuration((int)(fromY - toY), velocity));
		mAnimator.addListener(this);
		mAnimListener = new TranslateAnimationListener((View)this, (int)fromY, (int)toY, debouce, velocity);
		mAnimator.addUpdateListener(this.mAnimListener);
		mAnimator.start();
	}

	@Override
	public void onAnimationCancel(Animator arg0) {
		
	}

	@Override
	public void onAnimationEnd(Animator arg0) {
		if (mAnimListener == null) return;
		if (mAnimListener.needSpringBack() == false) {
			if (mOnMotionListener != null) {
				ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams)getLayoutParams();
				if (lp == null) return;
				mOnMotionListener.onMoveEnd(lp.topMargin >= mTopMarginNormal?OnMotionListener.DIRECTION_BOTTOM:OnMotionListener.DIRECTION_TOP);
			}
			return;
		}
		setLayoutWithAnimation(mAnimListener.mTo, mTopMarginNormal, 0, false);
	}

	@Override
	public void onAnimationRepeat(Animator arg0) {
		
	}

	@Override
	public void onAnimationStart(Animator arg0) {
		
	}

	protected class TranslateAnimationListener implements ValueAnimator.AnimatorUpdateListener {
	    private final int mTo;
	    private final int mFrom;
	    private final boolean mSpringBack;
	    private final int mVelocity;
	    private final WeakReference<View> mViewRef;

	    public TranslateAnimationListener(View view, int from, int to, boolean debounce, int velocity)
	    {
	      this.mViewRef = new WeakReference(view);
	      this.mFrom = from;
	      this.mTo = to;
	      this.mSpringBack = debounce;
	      this.mVelocity = velocity;
	    }

	    public View getView()
	    {
	      return (View)this.mViewRef.get();
	    }

	    public boolean needSpringBack()
	    {
	      return this.mSpringBack;
	    }

	    public void onAnimationUpdate(ValueAnimator paramValueAnimator)
	    {
	      View localView = (View)this.mViewRef.get();
	      if (localView != null)
	      {
	  		ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams)localView.getLayoutParams();
			if (lp == null) return;

	        float f = ((Float)paramValueAnimator.getAnimatedValue()).floatValue();
	        int diff = mTo - mFrom;
	        lp.topMargin = mFrom + (int)(diff * f);
	        localView.setLayoutParams(lp);
			if (mOnMotionListener != null) {
				float offset = (lp.topMargin - mTopMarginMin) / (float)(mTopMarginMax - mTopMarginMin);
				float defaultOffset = (mTopMarginNormal - mTopMarginMin) / (float)(mTopMarginMax - mTopMarginMin);
				mOnMotionListener.onMoved(diff > 0?OnMotionListener.DIRECTION_BOTTOM:OnMotionListener.DIRECTION_TOP, offset, defaultOffset);
			}
	      }
	    }
	}

    /**
     * Tests scrollability within child views of v given a delta of dy.
     *
     * @param v View to test for vertical scrollability
     * @param checkV Whether the view v passed should itself be checked for scrollability (true),
     *               or just its children (false).
     * @param dy Delta scrolled in pixels
     * @param x X coordinate of the active touch point
     * @param y Y coordinate of the active touch point
     * @return true if child views of v can be scrolled by delta of dx.
     */
    protected boolean canScroll(View v, boolean checkV, int dy, int x, int y) {
        if (v instanceof ViewGroup) {
            final ViewGroup group = (ViewGroup) v;
            final int scrollX = v.getScrollX();
            final int scrollY = v.getScrollY();
            final int count = group.getChildCount();
            // Count backwards - let topmost views consume scroll distance first.
            for (int i = count - 1; i >= 0; i--) {
                // This will not work for transformed views in Honeycomb+
                final View child = group.getChildAt(i);
                if (x + scrollX >= child.getLeft() && x + scrollX < child.getRight() &&
                        y + scrollY >= child.getTop() && y + scrollY < child.getBottom() &&
                        canScroll(child, true, dy, x + scrollX - child.getLeft(),
                                y + scrollY - child.getTop())) {
                    return true;
                } /* end of if */
            } /* end of for */
        } /* end of if */

        return checkV && v.canScrollVertically(-dy);
    }

    void smoothScrollTo(int x, int y) {
        smoothScrollTo(x, y, 0);
    }

    void smoothScrollTo(int x, int y, int velocity) {
        mScrolling = true;
        setScrollState(SCROLL_STATE_SETTLING);
    }

    /**
     * Scroll State
     * @param newState new state
     */
    private void setScrollState(int newState) {
        if (mScrollState == newState) {
            return;
        } /* end of if */

        mScrollState = newState;
        /*if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageScrollStateChanged(newState);
        }*/ /* end of if */
    }

    /**
     *  on secondary pointer up
     * This was our active pointer going up. Choose a new active pointer and adjust accordingly.
     * @param ev MotionEvent
     */
    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = ev.getActionIndex();
        final int pointerId = ev.getPointerId(pointerIndex);
        if (pointerId == mActivePointerId) {
            // This was our active pointer going up. Choose a new
            // active pointer and adjust accordingly.
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mLastMotionY = ev.getY(newPointerIndex);
            mActivePointerId = ev.getPointerId(newPointerIndex);
            if (mVelocityTracker != null) {
                mVelocityTracker.clear();
            } /* end of if */
        } /* end of if */
    }

    /**
     * end drag
     */
    private void endDrag(int initialVelocity) {
        mIsBeingDragged = false;
        mIsUnableToDrag = false;
        setScrollState(SCROLL_STATE_IDLE);
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        } /* end of if */
        computeNewLayoutWithAnimation(initialVelocity);
    }

    private void completeScroll() {
        boolean needPopulate = mScrolling;
        if (needPopulate) {
            setScrollState(SCROLL_STATE_IDLE);
        } /* end of if */
        mScrolling = false;
    }

    void setCurrentItemInternal(int item, boolean smoothScroll, boolean always, int velocity) {
        if (smoothScroll) {
            smoothScrollTo(0, 0, velocity);
        } else {
            completeScroll();
            //scrollTo(0, destY);
        } /* end of if */
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        /*
         * This method JUST determines whether we want to intercept the motion.
         * If we return true, onMotionEvent will be called and we do the actual
         * scrolling there.
         */
      
        final int action = ev.getAction() & MotionEvent.ACTION_MASK;
      
        // Always take care of the touch gesture being complete.
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            // Release the drag.
            mIsBeingDragged = false;
            mIsUnableToDrag = false;
            mActivePointerId = INVALID_POINTER;
            if (mVelocityTracker != null) {
                mVelocityTracker.recycle();
                mVelocityTracker = null;
            } /* end of if */
            return false;
        } /* end of if */
      
        // Nothing more to do here if we have decided whether or not we
        // are dragging.
        if (action != MotionEvent.ACTION_DOWN) {
            if (mIsBeingDragged) {
                return true;
            } /* end of if */
            if (mIsUnableToDrag) {
                return false;
            } /* end of if */
        } /* end of if */
      
        switch (action) {
            case MotionEvent.ACTION_MOVE: {
                /*
                 * mIsBeingDragged == false, otherwise the shortcut would have caught it. Check
                 * whether the user has moved far enough from his original down touch.
                 */
      
                /*
                * Locally do absolute value. mLastMotionX is set to the x value
                * of the down event.
                */
                final int activePointerId = mActivePointerId;
                if (activePointerId == INVALID_POINTER) {
                    // If we don't have a valid id, the touch down wasn't on content.
                    break;
                } /* end of if */
      
                final int pointerIndex = ev.findPointerIndex(activePointerId);
                final float x = ev.getX(pointerIndex);
                final float xDiff = Math.abs(x - mLastMotionX);
                final float y = ev.getY(pointerIndex);
                final float dy = y - mLastMotionY;
                final float yDiff = Math.abs(dy);
                
                //if (DEBUG) Log.v(TAG, "Moved x to " + x + "," + y + " diff=" + xDiff + "," + yDiff);
                boolean forceDragged = isForceDragged();
                if (!forceDragged && canScroll(this, false, (int) dy, (int) x, (int) y)) {
                    // Nested view has scrollable area under this point. Let it be handled there.
                    mInitialMotionY = mLastMotionY = y;
                    mLastMotionX = x;
                    return false;
                } /* end of if */
                if ((yDiff > mTouchSlop && yDiff > xDiff)) {
                    //if (DEBUG) Log.v(TAG, "Starting drag!");
                    mIsBeingDragged = true;
                    setScrollState(SCROLL_STATE_DRAGGING);
                    mLastMotionY = y;
                    //setScrollingCacheEnabled(true);
                } else {
                    if (xDiff > mTouchSlop) {
                        // The finger has moved enough in the horizontal
                        // direction to be counted as a drag...  abort
                        // any attempt to drag vertically, to work correctly
                        // with children that have scrolling containers.
                        //if (DEBUG) Log.v(TAG, "Starting unable to drag!");
                        mIsUnableToDrag = true;
                    } /* end of if */
                } /* end of if */
                break;
            } /* end of case */
      
            case MotionEvent.ACTION_DOWN: {
                /*
                 * Remember location of down touch.
                 * ACTION_DOWN always refers to pointer index 0.
                 */
                mLastMotionX = ev.getX();
                 mLastMotionY = mInitialMotionY = ev.getY();              
                mActivePointerId = ev.getPointerId(0);

                if (mScrollState == SCROLL_STATE_SETTLING) {
                    // Let the user 'catch' the pager as it animates.
                    mIsBeingDragged = true;
                    mIsUnableToDrag = false;
                    setScrollState(SCROLL_STATE_DRAGGING);
                } else {
                    //completeScroll();
                    mIsBeingDragged = false;
                    mIsUnableToDrag = false;
                } /* end of if */
      
                /*if (DEBUG) Log.v(TAG, "Down at " + mLastMotionX + "," + mLastMotionY
                        + " mIsBeingDragged=" + mIsBeingDragged
                        + "mIsUnableToDrag=" + mIsUnableToDrag);*/
                break;
            } /* end of case */
      
            case MotionEvent.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;
        } /* end of switch */
      
        if (!mIsBeingDragged) {
            // Track the velocity as long as we aren't dragging.
            // Once we start a real drag we will track in onTouchEvent.
            if (mVelocityTracker == null) {
                mVelocityTracker = VelocityTracker.obtain();
            } /* end of if */
            mVelocityTracker.addMovement(ev);
        } /* end of if */
      
        /*
         * The only time we want to intercept motion events is if we are in the
         * drag mode.
         */
        //Log.d("test", "xiaoguo test 1, action=" + ev.getAction() + ", drag=" + mIsBeingDragged);
        return mIsBeingDragged;
    }

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
        if (mFakeDragging) {
            // A fake drag is in progress already, ignore this real one
            // but still eat the touch events.
            // (It is likely that the user is multi-touching the screen.)
            return true;
        } /* end of if */

        if (ev.getAction() == MotionEvent.ACTION_DOWN && ev.getEdgeFlags() != 0) {
            // Don't handle edge touches immediately -- they may actually belong to one of our
            // descendants.
            return false;
        } /* end of if */

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        } /* end of if */
        mVelocityTracker.addMovement(ev);

        final int action = ev.getAction();

        switch (action & MotionEvent.ACTION_MASK) {
	        case MotionEvent.ACTION_DOWN: {
	            /*
	             * If being flinged and user touches, stop the fling. isFinished
	             * will be false if being flinged.
	             */
	            completeScroll();
	
	            // Remember where the motion event started
	            mLastMotionY = mInitialMotionY = ev.getY();
	            mActivePointerId = ev.getPointerId(0);
	            break;
	        } /* end of case */
	        case MotionEvent.ACTION_MOVE:
	            if (!mIsBeingDragged) {
	                final int pointerIndex = ev.findPointerIndex(mActivePointerId);
	                final float x = ev.getX(pointerIndex);
	                final float xDiff = Math.abs(x - mLastMotionX);
	                final float y = ev.getY(pointerIndex);
	                final float yDiff = Math.abs(y - mLastMotionY);
	                //if (true) Log.v(TAG, "Moved x to " + x + "," + y + " diff=" + xDiff + "," + yDiff);
	                if ((yDiff > mTouchSlop && yDiff > xDiff)) {
	                    //if (DEBUG) Log.v(TAG, "Starting drag!");
	                    mIsBeingDragged = true;
	                    mLastMotionY = y;
	                    setScrollState(SCROLL_STATE_DRAGGING);
	                    //setScrollingCacheEnabled(true);
	                } /* end of if */
	            } /* end of if */
	            if (mIsBeingDragged) {
	                // Scroll to follow the motion event
	                final int activePointerIndex = ev.findPointerIndex(mActivePointerId);
	                final float y = ev.getY(activePointerIndex);
	                //final float deltaY = mLastMotionY - y;
	                mLastMotionY = y;

	                //scrollTo(getScrollX(), (int) y);
	                //pageScrolled((int) scrollY);
	            } /* end of if */
	            break;
	        case MotionEvent.ACTION_UP:
	            if (mIsBeingDragged) {
	                final VelocityTracker velocityTracker = mVelocityTracker;
	                velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
	                int initialVelocity = (int) velocityTracker.getYVelocity(mActivePointerId);
	                final int activePointerIndex = ev.findPointerIndex(mActivePointerId);
	                final float y = ev.getY(activePointerIndex);
	                mLastMotionY = y;
	                setCurrentItemInternal(0, true, true, initialVelocity);
	
	                mActivePointerId = INVALID_POINTER;
	                endDrag(initialVelocity);
	            } /* end of if */
	            break;
	        case MotionEvent.ACTION_CANCEL:
	            if (mIsBeingDragged) {
	            	mLastMotionY = ev.getY(ev.findPointerIndex(mActivePointerId));
	                setCurrentItemInternal(0, true, true, 0);
	                mActivePointerId = INVALID_POINTER;
	                endDrag(0);
	            } /* end of if */
	            break;
	        case MotionEvent.ACTION_POINTER_DOWN: {
	            final int index = ev.getActionIndex();
	            final float y = ev.getY(index);
	            mLastMotionY = y;
	            mActivePointerId = ev.getPointerId(index);
	            break;
	        } /* end of case */
	        case MotionEvent.ACTION_POINTER_UP:
	            onSecondaryPointerUp(ev);
	            mLastMotionY = ev.getY(ev.findPointerIndex(mActivePointerId));
	            break;
        } /* end of switch */

		switch (action) {
			case MotionEvent.ACTION_MOVE:
				if (mIsBeingDragged) computeNewLayout();
				break;
			default:
		}
		//Log.d("test", "xiaoguo test 3, action=" + action + ", initY=" + mInitialMotionY + ", lastY=" + mLastMotionY);
		return true;//super.onTouchEvent(ev);
	}

	public void resetLayout(int dir) {
		ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams)getLayoutParams();
		if (dir == OnMotionListener.DIRECTION_TOP) {
			lp.topMargin = mTopMarginMin;
		} else {
			lp.topMargin = mTopMarginNormal;
		}
		setLayoutParams(lp);
	}
}
