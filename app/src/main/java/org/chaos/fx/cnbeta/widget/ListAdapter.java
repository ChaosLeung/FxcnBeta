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

package org.chaos.fx.cnbeta.widget;

import android.support.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * @author Chaos
 *         10/03/2017
 */

public abstract class ListAdapter<T, VH extends BaseViewHolder> extends BaseQuickAdapter<T, VH> {

    public ListAdapter(int layoutResId, List<T> data) {
        super(layoutResId, data);
    }

    public ListAdapter(List<T> data) {
        super(data);
    }

    public ListAdapter(int layoutResId) {
        super(layoutResId);
    }

    public int size() {
        return getData().size();
    }

    public boolean isEmpty() {
        return getData().isEmpty();
    }

    public boolean contains(Object o) {
        return getData().contains(o);
    }

    @NonNull
    public Iterator<T> iterator() {
        return getData().iterator();
    }

    @NonNull
    public Object[] toArray() {
        return getData().toArray();
    }

    @NonNull
    public <T1> T1[] toArray(@NonNull T1[] a) {
        return getData().toArray(a);
    }

    public void add(T t) {
        addData(t);
    }

    public void add(int index, T t) {
        addData(index, t);
    }

    public boolean containsAll(@NonNull Collection<?> c) {
        return getData().containsAll(c);
    }

    public boolean addAll(@NonNull Collection<? extends T> c) {
        boolean result = getData().addAll(c);
        notifyItemRangeInserted(getData().size() - c.size() + getHeaderLayoutCount(), c.size());
        compatibilityDataSizeChanged(c.size());
        return result;
    }

    public boolean addAll(int index, @NonNull Collection<? extends T> c) {
        boolean result = getData().addAll(index, c);
        notifyItemRangeInserted(index + getHeaderLayoutCount(), c.size());
        compatibilityDataSizeChanged(c.size());
        return result;
    }

    public boolean removeAll(@NonNull Collection<?> c) {
        boolean modified = false;

        Iterator<T> iterator = iterator();
        for (int i = 0; iterator.hasNext(); i++) {
            Object object = iterator.next();
            if (c.contains(object)) {
                iterator.remove();
                notifyItemRemoved(i + getHeaderLayoutCount());
                compatibilityDataSizeChanged(0);
                modified = true;
            }
        }

        return modified;
    }

    public boolean retainAll(@NonNull Collection<?> c) {
        boolean modified = false;

        Iterator<T> iterator = iterator();
        for (int i = 0; iterator.hasNext(); i++) {
            Object object = iterator.next();
            if (!c.contains(object)) {
                iterator.remove();
                notifyItemRemoved(i + getHeaderLayoutCount());
                compatibilityDataSizeChanged(0);
                modified = true;
            }
        }
        return modified;
    }

    public void clear() {
        int len = size();
        getData().clear();
        notifyItemRangeRemoved(0, len);
    }

    public T get(int index) {
        return getItem(index);
    }

    public T set(int index, T element) {
        T result = getData().set(index, element);
        notifyItemChanged(index + getHeaderLayoutCount());
        return result;
    }

    public int indexOf(Object o) {
        return getData().indexOf(o);
    }

    public int lastIndexOf(Object o) {
        return getData().lastIndexOf(o);
    }

    public ListIterator<T> listIterator() {
        return getData().listIterator();
    }

    @NonNull
    public ListIterator<T> listIterator(int index) {
        return getData().listIterator(index);
    }

    @NonNull
    public List<T> subList(int fromIndex, int toIndex) {
        return getData().subList(fromIndex, toIndex);
    }

    private void compatibilityDataSizeChanged(int size) {
        final int dataSize = getData() == null ? 0 : size();
        if (dataSize == size) {
            notifyDataSetChanged();
        }
    }
}
