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

package org.chaos.fx.cnbeta.wxapi;

import android.content.Context;
import android.graphics.Bitmap;

import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.chaos.fx.cnbeta.BuildConfig;
import org.jsoup.helper.StringUtil;

import java.io.ByteArrayOutputStream;

/**
 * @author Chaos
 *         7/7/16
 */
public class WXApiProvider {

    private static IWXAPI sWXApi;

    public static void initialize(Context context) {
        sWXApi = WXAPIFactory.createWXAPI(context, BuildConfig.WECHAT_APPID);
        sWXApi.registerApp(BuildConfig.WECHAT_APPID);
    }

    public static IWXAPI getInstance() {
        return sWXApi;
    }

    public static void shareUrl(String url, String title, String description, Bitmap thumbnail, boolean toTimeline) {
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = new WXWebpageObject(url);
        msg.title = title;
        msg.description = description;
        if (thumbnail != null && !thumbnail.isRecycled()) {
            msg.thumbData = bitmap2Bytes(thumbnail);
        }

        sendRequest2Wechat(msg, toTimeline);
    }

    public static void sharePicture(Bitmap thumbnail, String path, final boolean toTimeline) {
        WXImageObject image;
        if (!StringUtil.isBlank(path)) {
            image = new WXImageObject();
            image.setImagePath(path);
        } else {
            image = new WXImageObject(thumbnail);
        }
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = image;
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(thumbnail, 88, 88, true);
        msg.thumbData = bitmap2Bytes(scaledBitmap);
        sendRequest2Wechat(msg, toTimeline);
    }

    private static void sendRequest2Wechat(WXMediaMessage msg, boolean toTimeline) {
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());// 请求标识
        req.message = msg;
        req.scene = toTimeline ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;

        sWXApi.sendReq(req);
    }

    private static byte[] bitmap2Bytes(final Bitmap bmp) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}