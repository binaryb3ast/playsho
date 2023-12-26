package com.playsho.android.base;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * An abstract base adapter for RecyclerView with common functionalities.
 *
 * @param <T> The type of data model.
 * @param <VH> The type of ViewHolder.
 * @param <VB> The type of ViewDataBinding.
 */
public abstract class BaseAdapter<T extends BaseModel, VH extends RecyclerView.ViewHolder, VB extends ViewDataBinding> extends RecyclerView.Adapter<VH> {

    protected List<T> itemList;
    protected List<T> backupItemList;
    protected Activity activity;
    protected RecyclerOnItemClick<T> onItemClick;
    protected RecyclerOnItemRemove<T> onItemRemove;
    protected RecyclerOnItemAdded<T> onItemAdded;

    /**
     * Gets the layout resource ID for the item view.
     *
     * @return The layout resource ID.
     */
    protected abstract int getLayoutResourceId();

    /**
     * Constructs a BaseAdapter with the provided activity and item list.
     *
     * @param activity The activity associated with the adapter.
     * @param itemList The list of items to be displayed in the RecyclerView.
     */
    public BaseAdapter(Activity activity, List<T> itemList) {
        this.activity = activity;
        this.itemList = itemList;
        this.backupItemList = new ArrayList<>(itemList);
    }

    /**
     * Constructs a BaseAdapter with the provided activity, item list, and item click callback.
     *
     * @param activity   The activity associated with the adapter.
     * @param itemList   The list of items to be displayed in the RecyclerView.
     * @param onItemClick The callback for item click events.
     */
    public BaseAdapter(Activity activity, List<T> itemList, RecyclerOnItemClick<T> onItemClick) {
        this(activity, itemList);
        this.onItemClick = onItemClick;
    }


    /**
     * Constructs a BaseAdapter with the provided activity, item list, item click callback, and item remove callback.
     *
     * @param activity      The activity associated with the adapter.
     * @param itemList      The list of items to be displayed in the RecyclerView.
     * @param onItemClick   The callback for item click events.
     * @param onItemRemove  The callback for item remove events.
     */
    public BaseAdapter(Activity activity, List<T> itemList, RecyclerOnItemClick<T> onItemClick, RecyclerOnItemRemove<T> onItemRemove) {
        this(activity, itemList);
        this.onItemClick = onItemClick;
        this.onItemRemove = onItemRemove;
    }

    /**
     * Retrieves a sublist of items within the specified range.
     *
     * @param first The starting index of the range (inclusive).
     * @param last The ending index of the range (inclusive).
     * @return A sublist of items within the specified range.
     * @throws IndexOutOfBoundsException If the specified range is invalid.
     */
    public List<T> getListInRange(int first, int last) throws IndexOutOfBoundsException {
        if (first < 0 || last >= itemList.size() || first > last) {
            throw new IndexOutOfBoundsException("Invalid range: " + first + " to " + last);
        }
        return itemList.subList(first, last + 1);
    }

    /**
     * Retrieves the total number of items in the adapter.
     *
     * @return The total number of items.
     */
    @Override
    public int getItemCount() {
        return getItemList().size();
    }

    /**
     * Sets the click listener for item clicks.
     *
     * @param onItemClick The click listener for item clicks.
     */
    public void setOnItemClick(RecyclerOnItemClick<T> onItemClick) {
        this.onItemClick = onItemClick;
    }

    /**
     * Adds a list of items to the adapter.
     *
     * @param list The list of items to add.
     */
    public void add(List<T> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        getItemList().addAll(list);
        notifyDataSetChanged();
    }

    /**
     * Removes a list of items from the adapter.
     *
     * @param list The list of items to remove.
     */
    public void remove(List<T> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        getItemList().removeAll(list);
        notifyDataSetChanged();
    }

    /**
     * Retrieves the data model at the specified position.
     *
     * @param position The position of the item.
     * @return The data model at the specified position.
     */
    protected T getCurrent(int position) {
        return this.itemList.get(position);
    }

    /**
     * Retrieves the list of items in the adapter.
     *
     * @return The list of items.
     */
    public List<T> getItemList() {
        return this.itemList;
    }


    /**
     * Notifies that the dataset has changed.
     */
    protected void notifyChanged() {
        this.notifyDataSetChanged();
    }

    /**
     * Sets the listener for item removal.
     *
     * @param onItemRemove The listener for item removal.
     */
    public void setOnItemRemove(RecyclerOnItemRemove<T> onItemRemove) {
        this.onItemRemove = onItemRemove;
    }

    /**
     * Sets the listener for item addition.
     *
     * @param onItemAdded The listener for item addition.
     */
    public void setOnItemAdded(RecyclerOnItemAdded<T> onItemAdded) {
        this.onItemAdded = onItemAdded;
    }

    /**
     * Inflates the ViewHolder based on the provided ViewDataBinding.
     *
     * @param parent The parent view group.
     * @param viewType The type of the view.
     * @return The inflated ViewHolder.
     */
    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // You can use the provided ViewBinding to inflate the layout
        LayoutInflater inflater = LayoutInflater.from(activity);
        return createViewHolder(DataBindingUtil.inflate(inflater, getLayoutResourceId(), parent, false));
    }


    /**
     * Abstract method to create a custom ViewHolder.
     *
     * @param binding The ViewDataBinding associated with the ViewHolder.
     * @return The created ViewHolder.
     */
    protected abstract VH createViewHolder(VB binding); // Abstract method to create ViewHolder

    /**
     * Interface for item click events in the RecyclerView.
     *
     * @param <T> The type of data model.
     */
    public interface RecyclerOnItemClick<T> {
        void onRecyclerItemClick(T item);
    }

    /**
     * Interface for item removal events in the RecyclerView.
     *
     * @param <T> The type of data model.
     */
    public interface RecyclerOnItemRemove<T> {
        void onRecyclerItemRemove(T item);
    }

    /**
     * Interface for item addition events in the RecyclerView.
     *
     * @param <T> The type of data model.
     */
    public interface RecyclerOnItemAdded<T> {
        void onRecyclerItemAdded(T item);
    }

}