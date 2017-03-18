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

package org.chaos.fx.cnbeta.preferences;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.v4.content.res.ResourcesCompat;

import org.chaos.fx.cnbeta.R;

import de.psdev.licensesdialog.LicensesDialogFragment;

/**
 * @author Chaos
 *         22/02/2017
 */

class LicenseDialogFragmentProvider {

    static LicensesDialogFragment newFragment(Context context) {
        return new LicensesDialogFragment.Builder(context)
                .setNotices(R.raw.licenses)
                .setShowFullLicenseText(false)
                .setUseAppCompat(true)
                .setIncludeOwnLicense(true)
                .setNoticesCssStyle(newCssStyle(context))
                .build();
    }

    private static String newCssStyle(Context context) {
        Resources res = context.getResources();
        String format = res.getString(R.string.custom_notices_format_style);
        int background = ResourcesCompat.getColor(res, R.color.css_body_background, context.getTheme());
        int preBackground = ResourcesCompat.getColor(res, R.color.css_pre_background, context.getTheme());
        int liColor = ResourcesCompat.getColor(res, R.color.css_li_color, context.getTheme());
        int aColor = ResourcesCompat.getColor(res, R.color.css_a_color, context.getTheme());

        String body = getRGBAString(context, background);
        String pre = getRGBAString(context, preBackground);
        String li = getRGBAString(context, liColor);
        String a = getRGBAString(context, aColor);
        return String.format(format, body, pre, li/*pre text*/, li, a);
    }

    private static String getRGBAString(Context context, @ColorInt int color) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        float alpha = ((float) Color.alpha(color) / 255);
        return String.format(context.getString(R.string.rgba_format), red, green, blue, alpha);
    }
}
