package com.example.mynotes.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mynotes.R;
import com.example.mynotes.interfaces.RecyclerViewItemClick;
import com.example.mynotes.models.NoteModel;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder> {

    private ArrayList<NoteModel> noteModelArrayList;
    private RecyclerViewItemClick onItemClickListener;

    public RecyclerViewAdapter(ArrayList<NoteModel> recyclerDataArrayList, Context mContext) {
        this.noteModelArrayList = recyclerDataArrayList;
    }


    @NonNull
    @Override
    public RecyclerViewAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.RecyclerViewHolder holder, int position) {
        NoteModel noteModel = noteModelArrayList.get(position);
        holder.title.setText(noteModel.getTitle());
        holder.content.setText(noteModel.getContent());
    }

    @Override
    public int getItemCount() {
        return (null != noteModelArrayList ? noteModelArrayList.size() : 0);
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        private final TextView title;
        private final TextView content;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.itemTitle);
            content = itemView.findViewById(R.id.itemContent);
        }
    }

    public RecyclerViewItemClick getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(RecyclerViewItemClick onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
