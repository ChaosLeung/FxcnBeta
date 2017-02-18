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
import android.support.v7.widget.SwitchCompat;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.ViewParent;

/**
 * @author Chaos
 *         16/02/2017
 */

public class FxSwitch extends SwitchCompat {

    public FxSwitch(Context context) {
        super(context);
    }

    public FxSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FxSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean isShown() {
        ViewParent p = getParent();
        if (p != null && (p instanceof ViewGroup)
                && ((ViewGroup) p).getId() == android.R.id.widget_frame) {
            // fix: animateThumbToCheckedState never invoke
            return true;
        }
        return super.isShown();
    }
}
