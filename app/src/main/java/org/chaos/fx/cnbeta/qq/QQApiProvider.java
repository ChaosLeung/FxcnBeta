/*
 * Copyright 2018 Chaos
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

package org.chaos.fx.cnbeta.qq;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.tencent.connect.share.QQShare;
import com.tencent.tauth.Tencent;

import org.chaos.fx.cnbeta.BuildConfig;

/**
 * @author Chaos
 * 6/15/18
 */
public class QQApiProvider {

    private static Tencent sQQApi;

    public static void initialize(Context context) {
        sQQApi = Tencent.createInstance(BuildConfig.QQ_APPID, context);
    }

    public static Tencent getInstance() {
        return sQQApi;
    }

    public static void shareUrl(Activity act, String url, String title, String description, String imageUrl) {
        final Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, title);
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, description);
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, url);
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, imageUrl);
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, act.getApplicationInfo().loadLabel(act.getPackageManager()).toString());
        sQQApi.shareToQQ(act, params, null);
    }
}