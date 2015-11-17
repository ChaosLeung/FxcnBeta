package org.chaos.fx.cnbeta.app;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.chaos.fx.cnbeta.R;
import org.chaos.fx.cnbeta.net.model.ArticleSummary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author Chaos
 *         2015/11/14.
 */
public abstract class BaseArticleAdapter extends RecyclerView.Adapter<BaseArticleAdapter.ViewHolder> {

    protected Context mContext;
    private List<ArticleSummary> mSummaries = new ArrayList<>();
    private RecyclerView mBindView;

    private OnItemClickListener mOnItemClickListener;
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mBindView != null && mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(mBindView.getChildAdapterPosition(v));
            }
        }
    };

    public BaseArticleAdapter(Context context, RecyclerView bindView) {
        mContext = context;
        mBindView = bindView;
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

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.article_item, parent, false));
    }

    @Override
    public final void onBindViewHolder(ViewHolder holder, int position) {
        ArticleSummary summary = getArticles().get(position);
        holder.title.setText(summary.getTitle());
        Picasso.with(mContext).load(summary.getThumb()).into(holder.image);
        onBindHolderInternal(holder, position);
    }

    protected void onBindHolderInternal(ViewHolder holder, int position) {
    }

    @Override
    public int getItemCount() {
        return mSummaries.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.title) public TextView title;
        @Bind(R.id.time) public TextView time;
        @Bind(R.id.image) public ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(mOnClickListener);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
