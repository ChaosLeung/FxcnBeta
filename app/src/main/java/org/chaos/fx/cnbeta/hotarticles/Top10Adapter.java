package org.chaos.fx.cnbeta.hotarticles;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import org.chaos.fx.cnbeta.R;
import org.chaos.fx.cnbeta.widget.BaseArticleAdapter;
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
    public void onBindHolderInternal(ArticleHolder holder, int position) {
        super.onBindHolderInternal(holder, position);
        ArticleSummary summary = get(position);
        holder.summary.setText(String.format(getContext().getString(R.string.read_count), summary.getCounter()));
    }
}
