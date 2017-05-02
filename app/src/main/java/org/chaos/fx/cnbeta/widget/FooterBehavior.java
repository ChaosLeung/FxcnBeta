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
import android.util.AttributeSet;
import android.view.View;

/**
 * @author Chaos
 *         30/04/2017
 */

class FooterBehavior<V extends View> extends ViewOffsetBehavior<V> {

    private static final String TAG = "FooterBehavior";

    public FooterBehavior() {
    }

    public FooterBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    int setFooterTopBottomOffset(CoordinatorLayout parent, V footer, int newOffset) {
        return setFooterTopBottomOffset(parent, footer, newOffset,
                Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    int setFooterTopBottomOffset(CoordinatorLayout parent, V footer, int newOffset,
                                 int minOffset, int maxOffset) {
        final int curOffset = getTopAndBottomOffset();
        int consumed = 0;

        if (curOffset >= minOffset && curOffset - footer.getHeight() <= maxOffset) {
            // If we have some scrolling range, and we're currently within the min and max
            // offsets, calculate a new offset
            newOffset = MathUtils.constrain(newOffset, minOffset, maxOffset);

            if (curOffset != newOffset) {
                setTopAndBottomOffset(newOffset);
                // Update how much dy we have consumed
                consumed = curOffset - newOffset;
            }
        }

        return consumed;
    }

    int getTopBottomOffsetForScrolling() {
        return getTopAndBottomOffset();
    }

    final int scroll(CoordinatorLayout coordinatorLayout, V footer,
                     int dy, int minOffset, int maxOffset) {
        return setFooterTopBottomOffset(coordinatorLayout, footer,
                getTopBottomOffsetForScrolling() + dy, minOffset, maxOffset);
    }
}
