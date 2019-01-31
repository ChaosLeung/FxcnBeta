/*
 * Copyright 2019 Chaos
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

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;

public class ImageStore implements Target {

    private final String mCachePath;
    private final Callback mCallback;

    private StoreTask mStoreTask;

    public ImageStore(String path, Callback callback) {
        mCachePath = path;
        mCallback = callback;
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        mStoreTask = new StoreTask(this);
        mStoreTask.execute(bitmap);
    }

    @Override
    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
        if (mCallback != null) {
            mCallback.onError(e);
        }
    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {
        // no-op
    }

    private static class StoreTask extends AsyncTask<Bitmap, Void, Exception> {

        private final WeakReference<ImageStore> mRef;

        private StoreTask(ImageStore ref) {
            mRef = new WeakReference<>(ref);
        }

        @Override
        protected Exception doInBackground(Bitmap... bitmaps) {
            if (mRef.get() == null) {
                return null;
            }
            Bitmap b = bitmaps[0];

            long space = Environment.getExternalStorageDirectory().getFreeSpace();

            if (space <= ((long) b.getByteCount()) * 5) {
                return new NoSpaceLeftException();
            }

            File f = new File(mRef.get().mCachePath);
            if (f.exists()) {
                f.delete();
            }
            try {
                b.getRowBytes();
                f.createNewFile();
                FileOutputStream fos = new FileOutputStream(f);
                b.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
                fos.close();
            } catch (IOException e) {
                return e;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Exception e) {
            if (mRef.get() == null || mRef.get().mCallback == null) {
                return;
            }

            if (e == null) {
                mRef.get().mCallback.onSuccess();
            } else {
                mRef.get().mCallback.onError(e);
            }
        }
    }
}
