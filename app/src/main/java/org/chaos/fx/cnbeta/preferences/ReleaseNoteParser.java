package org.chaos.fx.cnbeta.preferences;

import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

class ReleaseNoteParser {

    static CharSequence parse(String html) {
        Elements elements = Jsoup.parse(html).body().children();
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        for (int i = 0; i < elements.size(); i += 2) {
            String version = elements.get(i).text();
            addSpanFromText(ssb, version, new RelativeSizeSpan(1.2f), new StyleSpan(Typeface.BOLD));
            ssb.append("\n");

            Elements ol = elements.get(i + 1).getElementsByTag("li");
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < ol.size(); j++) {
                sb.append("  •  ").append(ol.get(j).text()).append("\n");
            }
            ssb.append(sb).append("\n");
        }
        if (ssb.length() > 0) {
            ssb.delete(ssb.length() - 2, ssb.length());
        }
        return ssb;
    }

    private static void addSpanFromText(SpannableStringBuilder target, CharSequence text, Object... spans) {
        int where = target.length();
        target.append(text);
        int len = target.length();
        if (where != len) {
            for (Object span : spans) {
                target.setSpan(span, where, len, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }
}