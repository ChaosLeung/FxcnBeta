package org.chaos.fx.cnbeta.home;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import org.chaos.fx.cnbeta.app.BaseArticleAdapter;
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
    public void onBindHolderInternal(ViewHolder holder, int position) {
        ArticleSummary summary = getArticles().get(position);
        holder.time.setText(TimeStringHelper.getTimeString(summary.getPubtime()));
    }
}
