package com.example.yogaclassmanagement;

import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.NumberFormat;
import java.util.Locale;

public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.SearchViewHolder> {
    private Cursor cursor;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(long classId);
    }

    public SearchResultsAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void updateResults(Cursor newCursor) {
        if (cursor != null) {
            cursor.close();
        }
        cursor = newCursor;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_result, parent, false);
        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        if (cursor != null && cursor.moveToPosition(position)) {
            // Get values from cursor
            String classType = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_TYPE));
            String teacher = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_TEACHER));
            String date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_DATE));
            String time = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_TIME));
            double price = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_PRICE));
            final long classId = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_ID));

            // Format price
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.UK);
            String formattedPrice = currencyFormat.format(price);

            // Set values to views
            holder.classTypeText.setText(classType);
            holder.teacherText.setText("Teacher: " + teacher);
            holder.dateTimeText.setText(String.format("%s at %s", date, time));
            holder.priceText.setText(formattedPrice);

            // Set click listener
            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(classId);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return cursor != null ? cursor.getCount() : 0;
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        if (cursor != null) {
            cursor.close();
            cursor = null;
        }
    }

    static class SearchViewHolder extends RecyclerView.ViewHolder {
        TextView classTypeText;
        TextView teacherText;
        TextView dateTimeText;
        TextView priceText;

        SearchViewHolder(View itemView) {
            super(itemView);
            classTypeText = itemView.findViewById(R.id.classTypeText);
            teacherText = itemView.findViewById(R.id.teacherText);
            dateTimeText = itemView.findViewById(R.id.dateTimeText);
            priceText = itemView.findViewById(R.id.priceText);
        }
    }
}
