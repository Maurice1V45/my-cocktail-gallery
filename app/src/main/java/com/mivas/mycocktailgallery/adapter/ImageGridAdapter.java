package com.mivas.mycocktailgallery.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.mivas.mycocktailgallery.R;
import com.mivas.mycocktailgallery.view.SquareImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageGridAdapter extends RecyclerView.Adapter<ImageGridAdapter.GridItemViewHolder> {

    private List<String> imageList;

    private Context c;

    public class GridItemViewHolder extends RecyclerView.ViewHolder {
        SquareImageView siv;

        public GridItemViewHolder(View view) {
            super(view);
            siv = view.findViewById(R.id.image);
        }
    }

    public ImageGridAdapter(Context c, List imageList) {
        this.c = c;
        this.imageList = imageList;
    }

    @Override
    public GridItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item_cocktail, parent, false);

        return new GridItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(GridItemViewHolder holder, int position) {
        final String path = imageList.get(position);

        Picasso.get()
                .load("https://docs.google.com/uc?id=1Fqo3z1-9X2FrZcfRBkIZV-oQlB2Xsf8D")
                .resize(250, 250)
                .centerCrop()
                .into(holder.siv);

        holder.siv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //handle click event on image
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

}
