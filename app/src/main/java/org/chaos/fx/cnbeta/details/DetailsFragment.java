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

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.SharedElementCallback;
import android.util.TypedValue;
import android.view.ActionMode;
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
import org.chaos.fx.cnbeta.net.model.NewsContent;
import org.chaos.fx.cnbeta.preferences.PreferenceHelper;
import org.chaos.fx.cnbeta.util.TimeStringHelper;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Chaos
 *         4/2/16
 */
public class DetailsFragment extends BaseFragment implements DetailsContract.View {

    protected static final String KEY_SID = "sid";
    protected static final String KEY_TITLE = "title";
    protected static final String KEY_TOPIC_LOGO = "topic_logo";

    public static DetailsFragment newInstance(int sid, String title, String topicLogoLink) {
        Bundle args = new Bundle();
        args.putInt(KEY_SID, sid);
        args.putString(KEY_TITLE, title);
        args.putString(KEY_TOPIC_LOGO, topicLogoLink);
        DetailsFragment fragment = new DetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static final String EXTRA_START_IMAGE_POSITION = "start_image_position";
    public static final String EXTRA_CURRENT_IMAGE_POSITION = "current_image_position";

    private static final float[] TEXT_SIZES = {
            0.8f, 1f, 1.2f
    };

    private int mSid;

    @BindView(R.id.title) TextView mTitleView;
    @BindView(R.id.source) TextView mSourceView;
    @BindView(R.id.author) TextView mAuthorView;
    @BindView(R.id.time) TextView mTimeView;
    @BindView(R.id.comment_count) TextView mCommentCountView;
    @BindView(R.id.author_image) ImageView mAuthorImg;

    @BindView(R.id.content_layout) LinearLayout mContentLayout;

    private OnShowCommentListener mOnShowCommentListener;

    private DetailsContract.Presenter mPresenter;

    private Bundle mTmpReenterState;

    private SharedElementCallback mSharedElementCallback = new SharedElementCallback() {
        @Override
        public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
            if (mTmpReenterState != null) {
                // update transition if position changed
                int startPosition = mTmpReenterState.getInt(EXTRA_START_IMAGE_POSITION);
                int currentPosition = mTmpReenterState.getInt(EXTRA_CURRENT_IMAGE_POSITION);
                if (startPosition != currentPosition) {
                    String transitionName = mPresenter.getAllImageUrls()[currentPosition];
                    View target = mContentLayout.findViewWithTag(transitionName);
                    if (target != null) {
                        names.clear();
                        sharedElements.clear();
                        names.add(transitionName);
                        sharedElements.put(transitionName, target);
                    }
                }
                mTmpReenterState = null;
            }
        }
    };

    private NewsContent mTmpNewsContent;
    private int mTmpCount = -1;

    private float mTextRelativeSize;

    private SelectedActionCallback mSelectedActionCallback;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        setHasOptionsMenu(true);
        mOnShowCommentListener = (OnShowCommentListener) activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_details, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);

        getActivity().setExitSharedElementCallback(mSharedElementCallback);// for ImagePagerActivity

        setupTextSize();

        mSid = getArguments().getInt(KEY_SID);
        mTitleView.setText(getArguments().getString(KEY_TITLE));

        getActivity().supportStartPostponedEnterTransition();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            mSelectedActionCallback = new SelectedActionCallback(getActivity().getPackageManager());
        }

        mCommentCountView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnShowCommentListener.onShowComment();
            }
        });

        mPresenter = new DetailsPresenter(getArguments().getInt(KEY_SID), getArguments().getString(KEY_TOPIC_LOGO));
        mPresenter.subscribe(this);
        if (mTmpNewsContent != null) {
            handleNewsContent(mTmpNewsContent);
            mTmpNewsContent = null;
        }

        if (mTmpCount != -1) {
            setCommentCount(-1);
            mTmpCount = -1;
        }
    }

    private void setupTextSize() {
        mTextRelativeSize = TEXT_SIZES[PreferenceHelper.getInstance().getContentTextLevel()];
        scaleTextSize(mTitleView, mTextRelativeSize);
        scaleTextSize(mSourceView, mTextRelativeSize);
        scaleTextSize(mTimeView, mTextRelativeSize);
        scaleTextSize(mAuthorView, mTextRelativeSize);
        scaleTextSize(mCommentCountView, mTextRelativeSize);

        ViewGroup.LayoutParams params = mAuthorImg.getLayoutParams();
        int size = Math.round(params.width * mTextRelativeSize);
        params.width = params.height = size;
        mAuthorImg.setLayoutParams(params);
    }

    private void scaleTextSize(TextView v, float relativeSize) {
        v.setTextSize(TypedValue.COMPLEX_UNIT_PX, v.getTextSize() * relativeSize);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.unsubscribe();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            setActionBarTitle(R.string.details);
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
                shareUrlToWeChat(false);
                return true;
            case R.id.wechat_timeline:
                shareUrlToWeChat(true);
                return true;
            /*case R.id.qq:
                mPresenter.shareUrlToQQ(getActivity());
                return true;*/
            case R.id.share_more:
                shareToMore();
                return true;
            case R.id.open_in_browser:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getArticleUrl())));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void shareUrlToWeChat(boolean toTimeline) {
        Drawable img = mAuthorImg.getDrawable();
        Bitmap bitmap;
        if (img != null) {
            bitmap = ((BitmapDrawable) mAuthorImg.getDrawable()).getBitmap();
        } else {
            bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        }
        mPresenter.shareUrlToWeChat(bitmap, toTimeline);
    }

    private void shareToMore() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        String title = mTitleView.getText().toString();
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                getString(R.string.share_link_format, title, getArticleUrl()));
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_to)));
    }

    private String getArticleUrl() {
        return String.format(Locale.getDefault(), "http://www.cnbeta.com/articles/%d.htm", mSid);
    }

    public void handleNewsContent(NewsContent content) {
        if (mPresenter == null) {
            mTmpNewsContent = content;
        } else {
            mPresenter.loadContentByNewsContent(content);
        }
    }

    @Override
    public void loadAuthorImage(String authorImgLink) {
        Picasso.get()
                .load(authorImgLink)
                .placeholder(PreferenceHelper.getInstance().inNightMode() ? R.color.holder_color_night : R.color.holder_color)
                .into(mAuthorImg);
    }

    @Override
    public void loadAuthorImage(int authorImgId) {
        Picasso.get()
                .load(authorImgId)
                .placeholder(PreferenceHelper.getInstance().inNightMode() ? R.color.holder_color_night : R.color.holder_color)
                .into(mAuthorImg);
    }

    @Override
    public void setTitle(String title) {
        mTitleView.setText(title);
    }

    @Override
    public void setAuthor(String author) {
        mAuthorView.setText(String.format(getString(R.string.author_format), author));
    }

    @Override
    public void setTimeString(String formattedTime) {
        mTimeView.setText(TimeStringHelper.getTimeStrByDefaultTimeStr(formattedTime));
    }

    @Override
    public void setCommentCount(int count) {
        if (isAdded()) {
            mCommentCountView.setText(String.format(getString(R.string.content_comment_count), count));
        } else {
            mTmpCount = count;
        }
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
        scaleTextSize(view, mTextRelativeSize);
        view.setText(text);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            view.setCustomSelectionActionModeCallback(mSelectedActionCallback);
        }
        // TODO: 18/05/2017 某些情况下链接会带上了大量文字，暂时关闭该功能
//        Linkify.addLinks(view, Linkify.WEB_URLS);
    }

    @Override
    public void addImageToContent(String link) {
        final ImageView view = (ImageView) getActivity().getLayoutInflater().inflate(R.layout.article_content_img_item, mContentLayout, false);
        mContentLayout.addView(view);
        view.setTag(link);

        final ImageListener l = new ImageListener(view, link);
        view.setOnClickListener(l);

        RequestCreator r = Picasso.get().load(link).placeholder(R.drawable.default_content_image_loading);
        if (PreferenceHelper.getInstance().inSafeDataMode()) {
            l.setLoadCacheForFirst(true);
            r.networkPolicy(NetworkPolicy.OFFLINE);
        }
        r.into(view, l);
    }

    public void onFragmentReenter(Intent data) {
        mTmpReenterState = data.getExtras();
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
                Picasso.get()
                        .load(url)
                        .placeholder(R.drawable.default_content_image_loading)
                        .into(target, this);
            } else if (state == SUCCESS) {// Gallery
                ImagePagerActivity.start(
                        getActivity(),
                        mPresenter.getAllImageUrls(),
                        mPresenter.indexOfImage(url),
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                                getActivity(), target, url));
            }
        }

        @Override
        public void onSuccess() {
            state = SUCCESS;
            loadCacheForFirst = false;
        }

        @Override
        public void onError(Exception e) {
            state = ERROR;
            Picasso.get()
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

    @TargetApi(Build.VERSION_CODES.M)
    private static class SelectedActionCallback extends ActionMode.Callback2 {

        private PackageManager pm;
        private List<ResolveInfo> resolveInfoList;
        private Intent targetIntent;

        private SelectedActionCallback(PackageManager pm) {
            this.pm = pm;
            targetIntent = new Intent(Intent.ACTION_PROCESS_TEXT);
            targetIntent.addCategory(Intent.CATEGORY_DEFAULT);
            targetIntent.setType("text/plain");
            resolveInfoList = pm.queryIntentActivities(targetIntent, PackageManager.GET_META_DATA);
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            int menuItemOrder = 100;
            for (ResolveInfo info : resolveInfoList) {
                Intent intent = new Intent(targetIntent);
                intent.putExtra(Intent.EXTRA_PROCESS_TEXT_READONLY, true)
                        .setClassName(info.activityInfo.packageName, info.activityInfo.name);
                menu.add(Menu.NONE, Menu.NONE, menuItemOrder, info.loadLabel(pm))
                        .setIntent(intent)
                        .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
            }
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

        }
    }
}
