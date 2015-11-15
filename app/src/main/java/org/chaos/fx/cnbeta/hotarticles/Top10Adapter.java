package org.chaos.fx.cnbeta.hotarticles;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.chaos.fx.cnbeta.R;
import org.chaos.fx.cnbeta.app.BaseArticleAdapter;
import org.chaos.fx.cnbeta.net.model.ArticleSummary;

/**
 * @author Chaos
 *         2015/11/15.
 */
public class Top10Adapter extends BaseArticleAdapter {
    public Top10Adapter(Context context, RecyclerView bindView) {
        super(context, bindView);
    }

    @Override
    public void onBindViewHolder(BaseArticleAdapter.ViewHolder holder, int position) {
        ArticleSummary summary = getArticles().get(position);
        holder.title.setText(summary.getTitle());
        holder.time.setText(String.format(mContext.getString(R.string.prevalence_count), summary.getCounter()));
        Picasso.with(mContext).load(summary.getThumb()).into(holder.image);
    }
}
