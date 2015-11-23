package org.chaos.fx.cnbeta.widget;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.chaos.fx.cnbeta.R;
import org.chaos.fx.cnbeta.net.model.ArticleSummary;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author Chaos
 *         2015/11/14.
 */
public abstract class BaseArticleAdapter extends ListAdapter<ArticleSummary, BaseArticleAdapter.ArticleHolder> {

    public BaseArticleAdapter(Context context, RecyclerView bindView) {
        super(context, bindView);
    }

    @Override
    protected ArticleHolder onCreateHolderInternal(ViewGroup parent, int viewType) {
        return new ArticleHolder(LayoutInflater.from(getContext()).inflate(R.layout.article_item, parent, false));
    }

    @CallSuper
    @Override
    protected void onBindHolderInternal(ArticleHolder holder, int position) {
        ArticleSummary summary = get(position);
        holder.title.setText(summary.getTitle());
        Picasso.with(getContext()).load(summary.getThumb()).into(holder.image);
    }

    @Override
    protected int getItemCountInternal() {
        return getList().size();
    }

    public class ArticleHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.title) public TextView title;
        @Bind(R.id.summary) public TextView summary;
        @Bind(R.id.image) public ImageView image;

        public ArticleHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
