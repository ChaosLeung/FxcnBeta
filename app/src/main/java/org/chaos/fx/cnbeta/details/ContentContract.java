/*
 * Copyright 2016 Chaos
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

package org.chaos.fx.cnbeta.details;

import android.graphics.Bitmap;

import org.chaos.fx.cnbeta.BasePresenter;
import org.chaos.fx.cnbeta.BaseView;
import org.chaos.fx.cnbeta.net.model.WebCommentResult;

/**
 * @author Chaos
 *         10/26/16
 */

interface ContentContract {

    interface View extends BaseView {
        void showLoadingView(boolean show);

        void showLoadingError(boolean show);

        void setupChildViews();

        void showTransition();
    }

    interface Presenter extends BasePresenter<View> {
        void loadArticleHtml();

        String getArticleToken();

        String getHtmlBody();

        WebCommentResult getWebComments();
    }

    interface SubView extends BaseView {
        void loadAuthorImage(String authorImgLink);

        void setTitle(String title);

        void setAuthor(String author);

        void setTimeString(String timeString);

        void setCommentCount(int count);

        void setSource(String source);

        void clearViewInContent();

        void addTextToContent(String text);

        void addImageToContent(String imgLink);

        void showTransition();
    }

    interface SubPresenter extends BasePresenter<SubView> {
        void shareUrlToWechat(Bitmap bitmap, boolean toTimeline);

        String[] getAllImageUrls();

        int indexOfImage(String url);
    }
}
