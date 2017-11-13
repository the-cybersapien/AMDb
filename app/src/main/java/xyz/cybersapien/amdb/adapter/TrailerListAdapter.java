package xyz.cybersapien.amdb.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import xyz.cybersapien.amdb.R;
import xyz.cybersapien.amdb.model.Movie;

/**
 * Created by ogcybersapien on 12/11/17.
 */

public class TrailerListAdapter extends RecyclerView.Adapter<TrailerListAdapter.ItemViewHolder> {

    private ArrayList<Movie.VideoResults> trailerList;
    private Context context;
    private static final String TAG = "TrailerListAdapter";

    public TrailerListAdapter(ArrayList<Movie.VideoResults> trailerList, Context context) {
        this.trailerList = trailerList;
        this.context = context;
    }

    @Override
    public TrailerListAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.movie_trailer_list_item, parent, false);
        if (trailerList.size() > 1) {
            Configuration config = context.getResources().getConfiguration();
            int screenWidthDp = (config.screenWidthDp * 25) / 9;
            view.setLayoutParams(new CardView.LayoutParams(screenWidthDp, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerListAdapter.ItemViewHolder holder, int position) {
        final Movie.VideoResults currentVideo = trailerList.get(position);
        Picasso.with(context)
                .load(getThumbnailUrl(position))
                .placeholder(R.drawable.video_thumbnail)
                .into(holder.thumbnailView);
        holder.trailerNameView.setText(currentVideo.getTitle());
        holder.trailerTypeView.setText(currentVideo.getType());
        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentVideo.getWebsite().equalsIgnoreCase("youtube")) {
                    Intent youtubeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + currentVideo.getVideoKey()));
                    context.startActivity(youtubeIntent);
                }
            }
        });
        Log.d(TAG, "onBindViewHolder: " + currentVideo);
    }

    @Override
    public int getItemCount() {
        return trailerList.size();
    }

    @Nullable
    private String getThumbnailUrl(int position) {
        Movie.VideoResults video = trailerList.get(position);
        if (video.getWebsite().toLowerCase().equals("youtube")) {
            int density = context.getResources().getDisplayMetrics().densityDpi;
            switch (density) {
                case DisplayMetrics.DENSITY_LOW:
                    return "http://img.youtube.com/vi/" + video.getVideoKey() + "/sddefault.jpg";
                case DisplayMetrics.DENSITY_MEDIUM:
                    return "http://img.youtube.com/vi/" + video.getVideoKey() + "/mqdefault.jpg";
                default:
                    return "http://img.youtube.com/vi/" + video.getVideoKey() + "/hqdefault.jpg";
            }
        }
        return null;
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        public View rootView;

        @BindView(R.id.trailer_thumbnail_view)
        ImageView thumbnailView;
        @BindView(R.id.trailer_name_view)
        TextView trailerNameView;
        @BindView(R.id.trailer_type_view)
        TextView trailerTypeView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            rootView = itemView;
        }
    }
}
