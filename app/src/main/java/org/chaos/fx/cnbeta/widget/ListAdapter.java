package org.chaos.fx.cnbeta.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Chaos
 *         2015/11/23.
 */
public abstract class ListAdapter<E, VH extends RecyclerView.ViewHolder> extends BaseAdapter<VH> {

    private final List<E> mList = new ArrayList<>();

    private View mHeaderView;

    public ListAdapter(Context context, RecyclerView bindView) {
        super(context, bindView);
    }

    public void add(int location, E object) {
        mList.add(location, object);
        notifyItemInserted(location);
    }

    public boolean add(E object) {
        boolean b = mList.add(object);
        if (b) {
            notifyItemInserted(mList.size() - 1);
        }
        return b;
    }

    public boolean addAll(int location, @NonNull Collection<? extends E> collection) {
        boolean b = mList.addAll(location, collection);
        if (b) {
            notifyItemRangeInserted(location, collection.size());
        }
        return b;
    }

    public boolean addAll(@NonNull Collection<? extends E> collection) {
        boolean b = mList.addAll(collection);
        if (b) {
            notifyItemRangeInserted(mList.size() - collection.size(), collection.size());
        }
        return b;
    }

    public void clear() {
        int len = mList.size();
        mList.clear();
        notifyItemRangeRemoved(0, len);
    }

    public boolean contains(Object object) {
        return mList.contains(object);
    }

    public boolean containsAll(@NonNull Collection<?> collection) {
        return mList.containsAll(collection);
    }

    public E get(int location) {
        return mList.get(location);
    }

    public int indexOf(Object object) {
        return mList.indexOf(object);
    }

    public boolean isEmpty() {
        return mList.isEmpty();
    }

    public int lastIndexOf(Object object) {
        return mList.lastIndexOf(object);
    }

    public E remove(int location) {
        E o = mList.remove(location);
        if (o != null) {
            notifyItemRemoved(location);
        }
        return o;
    }

    public boolean remove(Object object) {
        int index = mList.indexOf(object);
        boolean b = mList.remove(object);
        if (b) {
            notifyItemRemoved(index);
        }
        return b;
    }

    public boolean removeAll(@NonNull Collection<?> collection) {
        boolean modified = false;

        for (int i = 0; i < mList.size(); i++) {
            Object object = mList.get(i);
            if (collection.contains(object)) {
                mList.remove(i);
                notifyItemRemoved(i);
                modified = true;
            }
        }

        return modified;
    }

    public boolean retainAll(@NonNull Collection<?> collection) {
        boolean modified = false;

        for (int i = 0; i < mList.size(); i++) {
            Object object = mList.get(i);
            if (!collection.contains(object)) {
                mList.remove(i);
                notifyItemRemoved(i);
                modified = true;
            }
        }

        return modified;
    }

    public E set(int location, E object) {
        E origin = mList.set(location, object);
        notifyItemChanged(location);
        return origin;
    }

    public List<E> getList() {
        return mList;
    }
}
