package com.example.yogaclassmanagement;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ClassInstanceAdapter extends RecyclerView.Adapter<ClassInstanceAdapter.ViewHolder> {
    private List<YogaClassInstance> instances;
    private OnDeleteClickListener deleteListener;
    private OnEditClickListener editListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(YogaClassInstance instance);
    }

    public interface OnEditClickListener {
        void onEditClick(YogaClassInstance instance);
    }

    public ClassInstanceAdapter(List<YogaClassInstance> instances, 
                              OnDeleteClickListener deleteListener,
                              OnEditClickListener editListener) {
        this.instances = instances;
        this.deleteListener = deleteListener;
        this.editListener = editListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_class_instance, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        YogaClassInstance instance = instances.get(position);
        holder.tvDate.setText(instance.getDate());
        holder.tvTeacher.setText(instance.getTeacher());
        holder.tvComments.setText(instance.getAdditionalComments());

        holder.btnEdit.setOnClickListener(v -> editListener.onEditClick(instance));
        holder.btnDelete.setOnClickListener(v -> deleteListener.onDeleteClick(instance));
    }

    @Override
    public int getItemCount() {
        return instances.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvTeacher, tvComments;
        Button btnEdit, btnDelete;

        ViewHolder(View view) {
            super(view);
            tvDate = view.findViewById(R.id.tvDate);
            tvTeacher = view.findViewById(R.id.tvTeacher);
            tvComments = view.findViewById(R.id.tvComments);
            btnEdit = view.findViewById(R.id.btnEdit);
            btnDelete = view.findViewById(R.id.btnDelete);
        }
    }
}
