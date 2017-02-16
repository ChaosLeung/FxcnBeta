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

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import org.chaos.fx.cnbeta.R;
import org.chaos.fx.cnbeta.app.BaseFragment;
import org.chaos.fx.cnbeta.preferences.PreferenceHelper;
import org.chaos.fx.cnbeta.util.TimeStringHelper;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Chaos
 *         4/2/16
 */
public class ContentFragment extends BaseFragment implements ContentContract.SubView {

    protected static final String KEY_SID = "sid";
    protected static final String KEY_TOPIC_LOGO = "topic_logo";
    protected static final String KEY_HTML_CONTENT = "html_content";
    protected static final String KEY_COMMENT_COUNT = "comment_count";

    public static ContentFragment newInstance(int sid, String topicLogoLink, String htmlBody, int commentCount) {
        Bundle args = new Bundle();
        args.putInt(KEY_SID, sid);
        args.putString(KEY_TOPIC_LOGO, topicLogoLink);
        args.putString(KEY_HTML_CONTENT, htmlBody);
        args.putInt(KEY_COMMENT_COUNT, commentCount);
        ContentFragment fragment = new ContentFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private int mSid;

    @BindView(R.id.title) TextView mTitleView;
    @BindView(R.id.source) TextView mSourceView;
    @BindView(R.id.author) TextView mAuthorView;
    @BindView(R.id.time) TextView mTimeView;
    @BindView(R.id.comment_count) TextView mCommentCountView;
    @BindView(R.id.author_image) ImageView authorImg;

    @BindView(R.id.content_layout) LinearLayout mContentLayout;

    private OnShowCommentListener mOnShowCommentListener;

    private ContentContract.SubPresenter mPresenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new ContentSubPresenter(new Bundle(getArguments()));
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        setHasOptionsMenu(true);
        mOnShowCommentListener = (OnShowCommentListener) activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_content, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);

        mSid = getArguments().getInt(KEY_SID);

        mCommentCountView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnShowCommentListener.onShowComment();
            }
        });

        mPresenter.subscribe(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.unsubscribe();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            setActionBarTitle(R.string.content);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.content_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.comment:
                mOnShowCommentListener.onShowComment();
                return true;
            case R.id.wechat_friends:
                shareUrlToWechat(false);
                return true;
            case R.id.wechat_timeline:
                shareUrlToWechat(true);
                return true;
            case R.id.open_in_browser:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(
                        String.format(Locale.getDefault(), "http://www.cnbeta.com/articles/%d.htm", mSid))));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void shareUrlToWechat(boolean toTimeline) {
        mPresenter.shareUrlToWechat(
                ((BitmapDrawable) authorImg.getDrawable()).getBitmap(), toTimeline);
    }

    public void updateCommentCount(int count) {
        if (mCommentCountView != null && isVisible()) {
            mCommentCountView.setText(String.format(getString(R.string.content_comment_count), count));
        }
    }

    @Override
    public void loadAuthorImage(String authorImgLink) {
        Picasso.with(getActivity())
                .load(authorImgLink)
                .into(authorImg);
    }

    @Override
    public void setTitle(String title) {
        mTitleView.setText(title);
    }

    @Override
    public void setAuthor(String author) {
        mAuthorView.setText("By " + author);
    }

    @Override
    public void setTimeString(String formattedTime) {
        mTimeView.setText(TimeStringHelper.getTimeString(formattedTime));
    }

    @Override
    public void setCommentCount(int count) {
        mCommentCountView.setText(String.format(getString(R.string.content_comment_count), count));
    }

    @Override
    public void setSource(String source) {
        mSourceView.setText(source);
    }

    @Override
    public void clearViewInContent() {
        mContentLayout.removeAllViews();
    }

    @Override
    public void addTextToContent(String text) {
        TextView view = (TextView) getActivity().getLayoutInflater().inflate(R.layout.article_content_text_item, mContentLayout, false);
        mContentLayout.addView(view);
        view.setText(text);
        Linkify.addLinks(view, Linkify.WEB_URLS);
    }

    @Override
    public void addImageToContent(String link) {
        final ImageView view = (ImageView) getActivity().getLayoutInflater().inflate(R.layout.article_content_img_item, mContentLayout, false);
        mContentLayout.addView(view);

        final ImageListener l = new ImageListener(view, link);
        view.setOnClickListener(l);

        RequestCreator r = Picasso.with(getActivity()).load(link).placeholder(R.drawable.default_content_image_loading);
        if (PreferenceHelper.getInstance().inSafeDataMode()) {
            l.setLoadCacheForFirst(true);
            r.networkPolicy(NetworkPolicy.OFFLINE);
        }
        r.into(view, l);
    }

    private class ImageListener implements View.OnClickListener, Callback {

        private static final int LOADING = 1;
        private static final int SUCCESS = 2;
        private static final int ERROR = 3;

        private int state = LOADING;

        private final ImageView target;
        private final String url;

        private boolean loadCacheForFirst = false;

        private ImageListener(ImageView target, String url) {
            this.target = target;
            this.url = url;
        }

        public void setLoadCacheForFirst(boolean loadCacheForFirst) {
            this.loadCacheForFirst = loadCacheForFirst;
        }

        @Override
        public void onClick(View v) {
            if (loadCacheForFirst) {
                return;
            }

            if (state == ERROR) {
                state = LOADING;
                Picasso.with(getContext())
                        .load(url)
                        .placeholder(R.drawable.default_content_image_loading)
                        .into(target, this);
            } else if (state == SUCCESS) {// Gallery
                ImagePagerActivity.start(getActivity(), mPresenter.getAllImageUrls(), mPresenter.indexOfImage(url));
            }
        }

        @Override
        public void onSuccess() {
            state = SUCCESS;
            loadCacheForFirst = false;
        }

        @Override
        public void onError() {
            state = ERROR;
            Picasso.with(getContext())
                    .load(loadCacheForFirst
                            ? R.drawable.default_content_image_holder
                            : R.drawable.default_content_image_failed)
                    .into(target);// no need callback
            loadCacheForFirst = false;
        }
    }

    public interface OnShowCommentListener {
        void onShowComment();
    }
}
