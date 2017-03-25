/*
 * Copyright 2017 Chaos
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

package org.chaos.fx.cnbeta.details;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseViewHolder;

import org.chaos.fx.cnbeta.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Chaos
 *         10/03/2017
 */
public class CommentViewHolder extends BaseViewHolder {

    @BindView(R.id.comment) TextView comment;
    @BindView(R.id.username) TextView username;
    @BindView(R.id.reply_comment) TextView replyComment;
    @BindView(R.id.time) TextView time;
    @BindView(R.id.support) TextView support;
    @BindView(R.id.against) TextView against;
    @BindView(R.id.reply) ImageButton reply;

    public CommentViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);

        // for handle child view onClick event
        addOnClickListener(R.id.support);
        addOnClickListener(R.id.against);
        addOnClickListener(R.id.reply);
    }
}
