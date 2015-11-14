package org.chaos.fx.cnbeta;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.chaos.fx.cnbeta.net.model.ArticleSummary;
import org.chaos.fx.cnbeta.util.TimeStringHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author Chaos
 *         2015/11/14.
 */
public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {

    private Context mContext;
    private List<ArticleSummary> mSummaries = new ArrayList<>();

    public ArticleAdapter(Context context) {
        mContext = context;
    }

    public void addArticle(ArticleSummary summary) {
        mSummaries.add(summary);
    }

    public void addArticle(int position, ArticleSummary summary) {
        mSummaries.add(position, summary);
    }

    public void addArticles(Collection<? extends ArticleSummary> summaries) {
        mSummaries.addAll(summaries);
    }

    public void addArticles(int position, Collection<? extends ArticleSummary> summaries) {
        mSummaries.addAll(position, summaries);
    }

    public List<ArticleSummary> getArticles() {
        return mSummaries;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.article_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ArticleSummary summary = mSummaries.get(position);
        holder.title.setText(summary.getTitle());
        holder.time.setText(TimeStringHelper.getTimeString(summary.getPubtime()));
        Picasso.with(mContext).load(summary.getThumb()).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return mSummaries.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.title) TextView title;
        @Bind(R.id.time) TextView time;
        @Bind(R.id.image) ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
