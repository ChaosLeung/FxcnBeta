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

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.chaos.fx.cnbeta.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * @author Chaos
 *         16/02/2017
 */

public class ImageFragment extends Fragment {

    private static final String TAG = "ImageFragment";

    private static final String KEY_IMAGE_URL = "image_url";

    public static ImageFragment newInstance(String url) {
        Bundle args = new Bundle();
        args.putString(KEY_IMAGE_URL, url);
        ImageFragment fragment = new ImageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @BindView(R.id.image) PhotoView mPhotoView;
    @BindView(R.id.progress) ProgressBar mProgressBar;

    private PhotoViewAttacher mAttacher;
    private String mUrl;


    private PhotoViewAttacher.OnPhotoTapListener mImageTapListener = new PhotoViewAttacher.OnPhotoTapListener() {
        @Override
        public void onPhotoTap(View view, float x, float y) {
            loadImage();
        }

        @Override
        public void onOutsidePhotoTap() {
            // no-op
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_image, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);

        mUrl = getArguments().getString(KEY_IMAGE_URL);

        mAttacher = new PhotoViewAttacher(mPhotoView);
        loadImage();
    }

    private void loadImage() {
        showLoading(true);
        Picasso.with(getActivity())
                .load(mUrl)
                .into(mPhotoView, new Callback() {
                    @Override
                    public void onSuccess() {
                        showLoading(false);
                        mAttacher.update();
                        mAttacher.setOnPhotoTapListener(null);
                    }

                    @Override
                    public void onError() {
                        showLoading(false);
                        mPhotoView.setImageResource(R.drawable.default_content_image_failed);
                        mAttacher.update();
                        mAttacher.setOnPhotoTapListener(mImageTapListener);
                    }
                });
    }

    private void showLoading(boolean loading) {
        mProgressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        mPhotoView.setVisibility(!loading ? View.VISIBLE : View.GONE);
    }
}
