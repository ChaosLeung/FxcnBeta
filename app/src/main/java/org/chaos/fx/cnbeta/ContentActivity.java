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

package org.chaos.fx.cnbeta;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.chaos.fx.cnbeta.net.CnBetaApi;
import org.chaos.fx.cnbeta.net.CnBetaApiHelper;
import org.chaos.fx.cnbeta.net.model.Comment;
import org.chaos.fx.cnbeta.net.model.NewsContent;
import org.chaos.fx.cnbeta.util.TimeStringHelper;
import org.chaos.fx.cnbeta.widget.BaseAdapter;
import org.chaos.fx.cnbeta.widget.SwipeLinearRecyclerView;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author Chaos
 *         2015/11/15.
 */
public class ContentActivity extends SwipeBackActivity implements SwipeLinearRecyclerView.OnLoadMoreListener {

    private static final String KEY_SID = "sid";
    private static final String KEY_TOPIC_LOGO = "topic_logo";

    public static void start(Context context, int sid, String topicLogoLink) {
        Intent intent = new Intent(context, ContentActivity.class);
        intent.putExtra(KEY_SID, sid);
        intent.putExtra(KEY_TOPIC_LOGO, topicLogoLink);
        context.startActivity(intent);
    }

    private int mSid;
    private String mLogoLink;

    private Call<CnBetaApi.Result<List<Comment>>> mCommentCall;

    private static final Callback<CnBetaApi.Result<String>> NO_OP_CALLBACK = new Callback<CnBetaApi.Result<String>>() {
        @Override
        public void onResponse(Call<CnBetaApi.Result<String>> call,
                               Response<CnBetaApi.Result<String>> response) {
            // no-op
        }

        @Override
        public void onFailure(Call<CnBetaApi.Result<String>> call, Throwable t) {
            // no-op
        }
    };

    @Bind(R.id.loading_view)
    View mLoadingBar;
    @Bind(R.id.error_layout)
    View mErrorLayout;
    @Bind(R.id.error_button)
    View mRetryButton;

    @Bind(R.id.swipe_recycler_view)
    SwipeLinearRecyclerView mCommentView;
    private CommentAdapter mCommentAdapter;

    private HeaderWrapper mHeaderWrapper;

    private int mPositionForDisplayedMenu = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        mSid = getIntent().getIntExtra(KEY_SID, -1);
        mLogoLink = getIntent().getStringExtra(KEY_TOPIC_LOGO);

        ButterKnife.bind(this);

        mCommentAdapter = new CommentAdapter(this, mCommentView.getRecyclerView());

        mHeaderWrapper = new HeaderWrapper();
        mCommentAdapter.addHeaderView(mHeaderWrapper.headerView);
        mCommentAdapter.addFooterView(
                getLayoutInflater().inflate(R.layout.layout_loading, mCommentView, false));
        mCommentAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                mPositionForDisplayedMenu = position;
                mCommentView.showContextMenuForChild(v);
            }
        });
        mCommentView.setAdapter(mCommentAdapter);
        mCommentView.setOnLoadMoreListener(this);
        mCommentView.setShowLoadingBar(false);
        registerForContextMenu(mCommentView.getRecyclerView());

        mRetryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadContent();
            }
        });

        loadContent();

        mCommentView.setLoading(true);
        loadComments(1);

        setupActionBar();
    }

    private void setupActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setOnClickListener(new View.OnClickListener() {

            private long preBarClickTime;

            @Override
            public void onClick(View v) {
                long currentTime = SystemClock.elapsedRealtime();
                if (currentTime - preBarClickTime > 750) {
                    preBarClickTime = currentTime;
                } else {
                    onActionBarDoubleClick();
                }
            }
        });

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.content);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void onActionBarDoubleClick() {
        mCommentView.getRecyclerView().scrollToPosition(0);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle(R.string.comment_menu_title);
        getMenuInflater().inflate(R.menu.comment_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int tid = mCommentAdapter.get(mPositionForDisplayedMenu - 1).getTid();
        switch (item.getItemId()) {
            case R.id.support:
                CnBetaApiHelper.supportComment(tid).enqueue(NO_OP_CALLBACK);
                break;
            case R.id.against:
                CnBetaApiHelper.againstComment(tid).enqueue(NO_OP_CALLBACK);
                break;
            case R.id.reply:
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onContextMenuClosed(Menu menu) {
        super.onContextMenuClosed(menu);
        mPositionForDisplayedMenu = -1;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                scrollToFinishActivity();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        if (mHeaderWrapper.contentCall != null) {
            mHeaderWrapper.contentCall.cancel();
        }
        if (mCommentCall != null) {
            mCommentCall.cancel();
        }
        super.onDestroy();
    }

    private void loadContent() {
        mLoadingBar.setVisibility(View.VISIBLE);
        mErrorLayout.setVisibility(View.GONE);
        mCommentView.setVisibility(View.GONE);
        mHeaderWrapper.loadContent();
    }

    private void loadComments(int page) {
        mCommentCall = CnBetaApiHelper.comments(mSid, page);
        mCommentCall.enqueue(new Callback<CnBetaApi.Result<List<Comment>>>() {
            @Override
            public void onResponse(Call<CnBetaApi.Result<List<Comment>>> call,
                                   Response<CnBetaApi.Result<List<Comment>>> response) {
                List<Comment> result = response.body().result;
                if (!result.isEmpty()) {
                    // HeaderView 太高时，调用 notifyItemInserted 相关方法
                    // 会导致 RecyclerView 跳转到奇怪的位置
                    mCommentAdapter.getList().addAll(result);
                    mCommentAdapter.notifyDataSetChanged();
                } else {
                    showSnackBar(R.string.no_more_comments);
                }
                hideProgress();
            }

            @Override
            public void onFailure(Call<CnBetaApi.Result<List<Comment>>> call, Throwable t) {
                showSnackBar(R.string.load_articles_failed);
                hideProgress();
            }
        });
    }

    private void showSnackBar(@StringRes int strId) {
        Snackbar.make(mCommentView, strId, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onLoadMore() {
        int size = mCommentAdapter.getList().size();
        if (size % 20 == 0) {
            if (mCommentAdapter.getFooterView() != null) {
                mCommentAdapter.getFooterView().setVisibility(View.VISIBLE);
                mCommentAdapter.notifyItemInserted(mCommentAdapter.getItemCount());
            }
            loadComments(size / 20 + 1);
        } else {
            hideProgress();
        }
    }

    private void hideProgress() {
        mCommentView.setLoading(false);
        if (mCommentAdapter.getFooterView() != null) {
            mCommentAdapter.getFooterView().setVisibility(View.GONE);
            mCommentAdapter.notifyItemRemoved(mCommentAdapter.getItemCount());
        }
    }

    class HeaderWrapper {

        @Bind(R.id.title) TextView title;
        @Bind(R.id.source) TextView source;
        @Bind(R.id.author_and_time) TextView authorAndTime;
        @Bind(R.id.author_image) ImageView authorImg;

        @Bind(R.id.content_layout) LinearLayout contentLayout;

        View headerView = getLayoutInflater().inflate(R.layout.article_content_header, mCommentView, false);

        Call<CnBetaApi.Result<NewsContent>> contentCall;

        Transformation transformation = new Transformation() {
            @Override
            public Bitmap transform(Bitmap source) {
                int targetWidth = contentLayout.getWidth();

                double aspectRatio = (double) source.getHeight() / (double) source.getWidth();
                int targetHeight = (int) (targetWidth * aspectRatio);
                Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
                if (result != source) {
                    // Same bitmap is returned if sizes are the same
                    source.recycle();
                }
                return result;
            }

            @Override
            public String key() {
                return "desiredWidth";
            }
        };

        HeaderWrapper() {
            ButterKnife.bind(this, headerView);
        }

        private void loadContent() {
            contentCall = CnBetaApiHelper.articleContent(mSid);
            contentCall.enqueue(new Callback<CnBetaApi.Result<NewsContent>>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(Call<CnBetaApi.Result<NewsContent>> call,
                                       Response<CnBetaApi.Result<NewsContent>> response) {
                    NewsContent newsContent = response.body().result;

                    Picasso.with(ContentActivity.this)
                            .load(TextUtils.isEmpty(mLogoLink)
                                    ? "http://static.cnbetacdn.com" + newsContent.getThumb()
                                    : mLogoLink)
                            .into(authorImg);

                    title.setText(newsContent.getTitle());
                    authorAndTime.setText("By " + newsContent.getAid() + "\n" + TimeStringHelper.getTimeString(newsContent.getTime()));

                    Document doc = Jsoup.parseBodyFragment(newsContent.getSource());
                    source.setText(findTagText(doc));

                    doc = Jsoup.parseBodyFragment(newsContent.getHometext() + newsContent.getBodytext());
                    addViewByNode(doc.body());

                    mLoadingBar.setVisibility(View.GONE);
                    mCommentView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onFailure(Call<CnBetaApi.Result<NewsContent>> call, Throwable t) {
                    mLoadingBar.setVisibility(View.GONE);
                    mErrorLayout.setVisibility(View.VISIBLE);
                }
            });
        }

        private String findTagText(Node node) {
            String tagText = "";
            for (Node subNode : node.childNodes()) {
                if ("#text".equals(subNode.nodeName())) {
                    return ((TextNode) subNode).text();
                } else {
                    tagText = findTagText(subNode);
                }
            }
            return tagText;
        }

        private void addViewByNode(Node node) {
            StringBuilder sb = new StringBuilder();
            addView(sb, node);
            if (sb.length() > 0) {
                removeLastEnterChars(sb);
                addTextView(sb.toString());// 移除最后两个回车符
            }
        }

        private void addView(StringBuilder sb, Node node) {
            int preSBLen = sb.length();
            for (Node subNode : node.childNodes()) {
                if ("img".equals(subNode.nodeName())) {
                    if (sb.length() > 0) {
                        removeLastEnterChars(sb);
                        addTextView(sb.toString());// 移除最后两个回车符
                        sb.delete(0, sb.length());
                        preSBLen = 0;
                    }
                    addImageView(subNode.attributes().get("src"));
                } else if ("#text".equals(subNode.nodeName())) {
                    sb.append(((TextNode) subNode).text());
                } else {
                    addView(sb, subNode);
                }
            }
            if (sb.length() - preSBLen > 0 && "p".equals(node.nodeName())) {
                sb.append("\n\n");
            }
        }

        private void removeLastEnterChars(StringBuilder sb) {
            if (sb.length() > 1 && sb.lastIndexOf("\n\n") == sb.length() - 2) {
                sb.delete(sb.length() - 2, sb.length());
            }
        }

        private void addTextView(String text) {
            TextView view = (TextView) getLayoutInflater().inflate(R.layout.article_content_text_item, contentLayout, false);
            contentLayout.addView(view);
            view.setText(text);
        }

        private void addImageView(String link) {
            ImageView view = (ImageView) getLayoutInflater().inflate(R.layout.article_content_img_item, contentLayout, false);
            contentLayout.addView(view);
            Picasso.with(ContentActivity.this).load(link).transform(transformation).into(view);
        }
    }
}
