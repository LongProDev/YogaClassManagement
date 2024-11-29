package com.example.yogaclassmanagement;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class YogaClassAdapter extends RecyclerView.Adapter<YogaClassAdapter.ViewHolder> {
    private List<YogaClass> classes;
    private OnClassClickListener classClickListener;
    private OnDeleteClickListener deleteClickListener;
    private OnEditClickListener editClickListener;

    public interface OnClassClickListener {
        void onClassClick(YogaClass yogaClass);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(YogaClass yogaClass);
    }

    public interface OnEditClickListener {
        void onEditClick(YogaClass yogaClass);
    }

    public YogaClassAdapter(List<YogaClass> classes, 
                          OnClassClickListener classClickListener,
                          OnDeleteClickListener deleteClickListener,
                          OnEditClickListener editClickListener) {
        this.classes = classes;
        this.classClickListener = classClickListener;
        this.deleteClickListener = deleteClickListener;
        this.editClickListener = editClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_yoga_class, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        YogaClass yogaClass = classes.get(position);
        holder.tvType.setText(yogaClass.getType());
        holder.tvDayTime.setText(String.format("%s at %s",
                yogaClass.getDayOfWeek(), yogaClass.getTime()));
        holder.tvDetails.setText(String.format("Duration: %d mins | Capacity: %d | Price: £%.2f",
                yogaClass.getDuration(), yogaClass.getCapacity(), yogaClass.getPrice()));

        holder.btnViewInstances.setOnClickListener(v -> classClickListener.onClassClick(yogaClass));
        holder.btnDelete.setOnClickListener(v -> deleteClickListener.onDeleteClick(yogaClass));
        holder.btnEdit.setOnClickListener(v -> editClickListener.onEditClick(yogaClass));
    }

    @Override
    public int getItemCount() {
        return classes.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvType, tvDayTime, tvDetails;
        Button btnViewInstances, btnEdit, btnDelete;

        ViewHolder(View view) {
            super(view);
            tvType = view.findViewById(R.id.tvType);
            tvDayTime = view.findViewById(R.id.tvDayTime);
            tvDetails = view.findViewById(R.id.tvDetails);
            btnViewInstances = view.findViewById(R.id.btnViewInstances);
            btnEdit = view.findViewById(R.id.btnEdit);
            btnDelete = view.findViewById(R.id.btnDelete);
        }
    }
}