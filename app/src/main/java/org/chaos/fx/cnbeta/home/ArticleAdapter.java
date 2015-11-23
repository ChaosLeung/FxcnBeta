package org.chaos.fx.cnbeta.home;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import org.chaos.fx.cnbeta.widget.BaseArticleAdapter;
import org.chaos.fx.cnbeta.net.model.ArticleSummary;
import org.chaos.fx.cnbeta.util.TimeStringHelper;

/**
 * @author Chaos
 *         2015/11/15.
 */
public class ArticleAdapter extends BaseArticleAdapter {

    public ArticleAdapter(Context context, RecyclerView bindView) {
        super(context, bindView);
    }

    @Override
    protected void onBindHolderInternal(ArticleHolder holder, int position) {
        super.onBindHolderInternal(holder, position);
        ArticleSummary summary = get(position);
        holder.summary.setText(TimeStringHelper.getTimeString(summary.getPubtime()));
    }
}
