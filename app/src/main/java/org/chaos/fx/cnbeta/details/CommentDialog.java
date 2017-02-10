/*
 * Copyright 2016 Chaos
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

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.chaos.fx.cnbeta.R;
import org.chaos.fx.cnbeta.net.CnBetaApiHelper;
import org.chaos.fx.cnbeta.net.WebApi;
import org.chaos.fx.cnbeta.net.exception.RequestFailedException;
import org.chaos.fx.cnbeta.net.model.WebCaptcha;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Chaos
 *         6/29/16
 */

public class CommentDialog extends DialogFragment {

    public static CommentDialog newInstance() {

        Bundle args = new Bundle();

        CommentDialog fragment = new CommentDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Bind(R.id.captcha) ImageView mCaptchaView;
    @Bind(R.id.captcha_text) EditText mCaptchaText;
    @Bind(R.id.comment) EditText mCommentText;

    private DialogInterface.OnClickListener mPositiveListener;

    private View.OnClickListener mButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case android.R.id.button1://Positive
                    if (mPositiveListener != null) {
                        mPositiveListener.onClick(getDialog(), AlertDialog.BUTTON_POSITIVE);
                    }
                    break;
                case android.R.id.button2://Negative
                    dismiss();
                    break;
                case R.id.captcha:
                    flashCaptcha();
                    break;
            }
        }
    };

    private Disposable mCaptchaDisposable;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_comment_dialog, null, false);

        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.comment)
                .setView(view)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.confirm, mPositiveListener)
                .create();

        ButterKnife.bind(this, view);

        mCaptchaView.setOnClickListener(mButtonClickListener);

        flashCaptcha();

        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(mButtonClickListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mCaptchaDisposable.dispose();
    }

    private void flashCaptcha() {
        mCaptchaDisposable = CnBetaApiHelper.getCaptchaDataUrl(((ContentActivity) getActivity()).getToken())
                .subscribeOn(Schedulers.io())
                .map(new Function<WebCaptcha, String>() {
                    @Override
                    public String apply(WebCaptcha webCaptcha) {
                        if (TextUtils.isEmpty(webCaptcha.getUrl())) {
                            throw new RequestFailedException();
                        }
                        return webCaptcha.getUrl();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .retry(3)
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String captchaUrl) throws Exception {
                        new Picasso.Builder(getActivity())
                                .downloader(CnBetaApiHelper.okHttp3Downloader())
                                .build()
                                .load(WebApi.HOST_URL + captchaUrl)
                                .into(mCaptchaView);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable e) throws Exception {
                        Toast.makeText(getActivity(), R.string.failed_to_get_captcha, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void setPositiveListener(DialogInterface.OnClickListener l) {
        mPositiveListener = l;
    }

    public String getCaptcha() {
        return mCaptchaText.getText().toString();
    }

    public String getComment() {
        return mCommentText.getText().toString();
    }

    public void captchaError(@StringRes int errorStr) {
        mCaptchaText.setError(getString(errorStr));
    }

    public void commentError(@StringRes int errorStr) {
        mCommentText.setError(getString(errorStr));
    }
}
