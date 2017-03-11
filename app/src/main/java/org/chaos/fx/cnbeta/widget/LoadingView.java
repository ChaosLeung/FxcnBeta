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

import com.chad.library.adapter.base.loadmore.LoadMoreView;

import org.chaos.fx.cnbeta.R;

/**
 * @author Chaos
 *         10/03/2017
 */

public class LoadingView extends LoadMoreView {
    @Override
    public int getLayoutId() {
        return R.layout.layout_loading_more_view;
    }

    @Override
    protected int getLoadingViewId() {
        return R.id.loading_view;
    }

    @Override
    protected int getLoadFailViewId() {
        return R.id.empty;
    }

    @Override
    protected int getLoadEndViewId() {
        return R.id.empty;
    }
}
