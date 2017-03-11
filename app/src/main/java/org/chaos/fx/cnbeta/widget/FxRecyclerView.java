/*
 * Copyright 2015 Chaos
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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * @author Chaos
 *         2015/11/23.
 */
public class FxRecyclerView extends RecyclerView {

    private static final int SMOOTH_SCROLL_NEED_FASTER_POSITION = 25;

    public FxRecyclerView(Context context) {
        super(context, null);
    }

    public FxRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public FxRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void smoothScrollToFirstItem() {
        int firstVisibleItem = ((LinearLayoutManager) getLayoutManager())
                .findFirstVisibleItemPosition();
        if (firstVisibleItem > SMOOTH_SCROLL_NEED_FASTER_POSITION) {
            scrollToPosition(SMOOTH_SCROLL_NEED_FASTER_POSITION);
        }
        smoothScrollToPosition(0);
    }
}
