package com.example.mynotes.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mynotes.R;
import com.example.mynotes.models.NoteModel;
import java.util.ArrayList;

public class GridViewAdapter extends ArrayAdapter<NoteModel> {

    public GridViewAdapter(@NonNull Context context, ArrayList<NoteModel> noteModelArrayList) {
        super(context, 0, noteModelArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            // Layout Inflater inflates each item to be displayed in GridView.
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.card_item, parent, false);
        }

        TextView title = listItemView.findViewById(R.id.itemTitle);
        TextView content = listItemView.findViewById(R.id.itemContent);
        ImageView itemPopUp = listItemView.findViewById(R.id.itemPopUp);

        NoteModel noteModel = getItem(position);
        title.setText(noteModel.getTitle());
        content.setText(noteModel.getContent());

        return listItemView;
    }
}
