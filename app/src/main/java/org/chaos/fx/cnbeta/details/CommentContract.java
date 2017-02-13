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

import org.chaos.fx.cnbeta.BasePresenter;
import org.chaos.fx.cnbeta.BaseView;
import org.chaos.fx.cnbeta.net.model.Comment;

import java.util.List;

/**
 * @author Chaos
 *         10/14/16
 */

interface CommentContract {

    interface View extends BaseView {
        void addComments(List<Comment> comments);

        /**
         * 显示评论 dialog, 给用户添加/回复评论
         *
         * @param pid 对应评论的 id, 若为 0, 则为添加评论
         */
        void showCommentDialog(int pid);

        void showLoadingFailed();

        void showNoMoreComments();

        void showAddCommentSucceed();

        void showAddCommentFailed(String error);

        void showOperationFailed();

        void showNoCommentTipsIfNeed();

        void hideProgress();

        void notifyItemChanged(Comment c);
    }

    interface Presenter extends BasePresenter<View> {
        void refreshComments(int page);

        void against(Comment c);

        void support(Comment c);

        void addComment();

        void replyComment(Comment c);

        void publishComment(String content, String captcha, int pid);

        void updateToken(String token);
    }
}
