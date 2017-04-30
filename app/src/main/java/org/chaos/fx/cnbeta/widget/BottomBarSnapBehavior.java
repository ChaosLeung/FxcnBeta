/*
 * Copyright 2017 Chaos
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.chaos.fx.cnbeta.widget;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

import com.roughike.bottombar.BottomBar;

/**
 * @author Chaos
 *         29/04/2017
 */

public class BottomBarSnapBehavior extends FooterBehavior<BottomBar> {

    private static final String TAG = "BottomBarSnapBehavior";

    private static final int MAX_OFFSET_ANIMATION_DURATION = 600; // ms

    private boolean mSkipNestedPreScroll;
    private boolean mWasNestedFlung;

    private ValueAnimatorCompat mOffsetAnimator;

    public BottomBarSnapBehavior() {
    }

    public BottomBarSnapBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, BottomBar child, View directTargetChild, View target, int nestedScrollAxes) {
        // Return true if we're nested scrolling vertically, and we have scrollable children
        // and the scrolling view is big enough to scroll
        final boolean started = (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;

        if (started && mOffsetAnimator != null) {
            // Cancel any offset animation
            mOffsetAnimator.cancel();
        }

        return started;
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, BottomBar child,
                               View target, int dxConsumed, int dyConsumed,
                               int dxUnconsumed, int dyUnconsumed) {
        if (dyUnconsumed < 0) {
            // If the scrolling view is scrolling down but not consuming, it's probably be at
            // the top of it's content
            scroll(coordinatorLayout, child, dyUnconsumed, 0, child.getHeight());
            // Set the expanding flag so that onNestedPreScroll doesn't handle any events
            mSkipNestedPreScroll = true;
        } else {
            // As we're no longer handling nested scrolls, reset the skip flag
            mSkipNestedPreScroll = false;
        }
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, BottomBar child,
                                  View target, int dx, int dy, int[] consumed) {
        if (dy != 0 && !mSkipNestedPreScroll) {
            int min, max;
            if (dy < 0) {
                // We're scrolling down
                min = 0;
                max = child.getHeight();
            } else {
                // We're scrolling up
                min = 0;
                max = child.getHeight();
            }
            consumed[1] = scroll(coordinatorLayout, child, dy, min, max);
        }
    }

    @Override
    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, BottomBar bb, View target) {
        if (!mWasNestedFlung) {
            // If we haven't been flung then let's see if the current view has been set to snap
            snapIfNeeded(coordinatorLayout, bb);
        }

        // Reset the flags
        mSkipNestedPreScroll = false;
        mWasNestedFlung = false;
    }

    @Override
    public boolean onNestedFling(final CoordinatorLayout coordinatorLayout,
                                 final BottomBar child, View target, float velocityX, float velocityY,
                                 boolean consumed) {
        boolean flung = false;

        // If we're scrolling up and the child also consumed the fling. We'll fake scroll
        // up to our 'collapsed' offset
        if (velocityY < 0) {
            // We're scrolling down
            final int targetScroll = 0;
            if (getTopBottomOffsetForScrolling() > targetScroll) {
                // If we're currently not expanded more than the target scroll, we'll
                // animate a fling
                animateOffsetTo(coordinatorLayout, child, targetScroll, velocityY);
                flung = true;
            }
        } else {
            // We're scrolling up
            final int targetScroll = child.getHeight();
            if (getTopBottomOffsetForScrolling() < targetScroll) {
                // If we're currently not expanded less than the target scroll, we'll
                // animate a fling
                animateOffsetTo(coordinatorLayout, child, targetScroll, velocityY);
                flung = true;
            }
        }

        mWasNestedFlung = flung;
        return flung;
    }

    private void animateOffsetTo(final CoordinatorLayout coordinatorLayout,
                                 final BottomBar child, final int offset, float velocity) {
        final int distance = Math.abs(getTopAndBottomOffset() - offset);

        final int duration;
        velocity = Math.abs(velocity);
        if (velocity > 0) {
            duration = 3 * Math.round(1000 * (distance / velocity));
        } else {
            final float distanceRatio = (float) distance / child.getHeight();
            duration = (int) ((distanceRatio + 1) * 150);
        }

        animateOffsetWithDuration(coordinatorLayout, child, offset, duration);
    }

    private void snapIfNeeded(CoordinatorLayout coordinatorLayout, BottomBar bb) {
        final int offset = getTopBottomOffsetForScrolling();

        // We're set the snap, so animate the offset to the nearest edge

        int snapTop = 0;
        int snapBottom = bb.getHeight();

        final int newOffset = offset < (snapBottom + snapTop) / 2
                ? snapBottom
                : snapTop;
        animateOffsetTo(coordinatorLayout, bb,
                MathUtils.constrain(newOffset, bb.getHeight(), 0), 0);
    }

    private void animateOffsetWithDuration(final CoordinatorLayout coordinatorLayout,
                                           final BottomBar child, final int offset, final int duration) {
        final int currentOffset = getTopBottomOffsetForScrolling();
        if (currentOffset == offset) {
            if (mOffsetAnimator != null && mOffsetAnimator.isRunning()) {
                mOffsetAnimator.cancel();
            }
            return;
        }

        if (mOffsetAnimator == null) {
            mOffsetAnimator = ViewUtils.createAnimator();
            mOffsetAnimator.setInterpolator(AnimationUtils.DECELERATE_INTERPOLATOR);
            mOffsetAnimator.addUpdateListener(new ValueAnimatorCompat.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimatorCompat animator) {
                    setFooterTopBottomOffset(coordinatorLayout, child,
                            animator.getAnimatedIntValue());
                }
            });
        } else {
            mOffsetAnimator.cancel();
        }

        mOffsetAnimator.setDuration(Math.min(duration, MAX_OFFSET_ANIMATION_DURATION));
        mOffsetAnimator.setIntValues(currentOffset, offset);
        mOffsetAnimator.start();
    }
}
