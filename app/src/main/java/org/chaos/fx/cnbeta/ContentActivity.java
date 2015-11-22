package org.chaos.fx.cnbeta;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.chaos.fx.cnbeta.net.CnBetaApi;
import org.chaos.fx.cnbeta.net.CnBetaApiHelper;
import org.chaos.fx.cnbeta.net.model.NewsContent;
import org.chaos.fx.cnbeta.util.TimeStringHelper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * @author Chaos
 *         2015/11/15.
 */
public class ContentActivity extends AppCompatActivity {

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

    private Call<CnBetaApi.Result<NewsContent>> mCall;

    @Bind(R.id.title) TextView mTitle;
    @Bind(R.id.source) TextView mSource;
    @Bind(R.id.author_and_time) TextView mAuthorAndTime;
    @Bind(R.id.author_image) ImageView mAuthorImg;

    @Bind(R.id.content_layout) LinearLayout mContentLayout;

    @Bind(R.id.loading_view) ProgressBar mLoadingBar;
    @Bind(R.id.error_layout) LinearLayout mErrorLayout;
    @Bind(R.id.error_button) Button mRetryButton;

    private Transformation mTransformation = new Transformation() {
        @Override
        public Bitmap transform(Bitmap source) {
            int targetWidth = mContentLayout.getWidth();

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        mSid = getIntent().getIntExtra(KEY_SID, -1);
        mLogoLink = getIntent().getStringExtra(KEY_TOPIC_LOGO);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.content);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        ButterKnife.bind(this);

        mRetryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadContent();
            }
        });

        loadContent();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        mCall.cancel();
        super.onDestroy();
    }

    private void loadContent() {
        mLoadingBar.setVisibility(View.VISIBLE);
        mErrorLayout.setVisibility(View.GONE);
        mCall = CnBetaApiHelper.articleContent(mSid);
        mCall.enqueue(new Callback<CnBetaApi.Result<NewsContent>>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Response<CnBetaApi.Result<NewsContent>> response, Retrofit retrofit) {
                mLoadingBar.setVisibility(View.GONE);
                NewsContent newsContent = response.body().result;

                Picasso.with(ContentActivity.this)
                        .load(TextUtils.isEmpty(mLogoLink)
                                ? "http://static.cnbetacdn.com" + newsContent.getThumb()
                                : mLogoLink)
                        .into(mAuthorImg);

                mTitle.setText(newsContent.getTitle());
                mAuthorAndTime.setText("By " + newsContent.getAid() + "\n" + TimeStringHelper.getTimeString(newsContent.getTime()));

                Document doc = Jsoup.parseBodyFragment(newsContent.getSource());
                mSource.setText(findTagText(doc));

                doc = Jsoup.parseBodyFragment(newsContent.getHometext() + newsContent.getBodytext());
                addViewByNode(doc.body());
            }

            @Override
            public void onFailure(Throwable t) {
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
            addTextView(sb.delete(sb.length() - 3, sb.length()).toString());// 移除最后两个回车符
        }
    }

    private void addView(StringBuilder sb, Node node) {
        int preSBLen = sb.length();
        for (Node subNode : node.childNodes()) {
            if ("img".equals(subNode.nodeName())) {
                if (sb.length() > 0) {
                    addTextView(sb.delete(sb.length() - 3, sb.length()).toString());// 移除最后两个回车符
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

    private void addTextView(String text) {
        TextView view = (TextView) getLayoutInflater().inflate(R.layout.article_content_text_item, mContentLayout, false);
        mContentLayout.addView(view);
        view.setText(text);
    }

    private void addImageView(String link) {
        ImageView view = (ImageView) getLayoutInflater().inflate(R.layout.article_content_img_item, mContentLayout, false);
        mContentLayout.addView(view);
        Picasso.with(this).load(link).transform(mTransformation).into(view);
    }
}
