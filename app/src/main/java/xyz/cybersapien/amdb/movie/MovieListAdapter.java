package xyz.cybersapien.amdb.movie;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import xyz.cybersapien.amdb.DetailActivity;
import xyz.cybersapien.amdb.R;

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.ItemViewHolder> {

    private ArrayList<Movie> movies;
    private Context context;

    public MovieListAdapter(Context context, ArrayList<Movie> movies) {
        this.movies = movies;
        this.context = context;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.movie_list_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        final Movie currentMovie = movies.get(position);

        Uri posterUri = currentMovie.getPosterPath(context.getResources().getDisplayMetrics().densityDpi);

        Picasso.with(context)
                .load(posterUri).fit()
                .placeholder(R.drawable.movie_placeholder)
                .error(R.drawable.notification_error)
                .into(holder.imageView);

        holder.titleView.setText(currentMovie.getTitle());

        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String movieId = currentMovie.getMovieId();
                Intent detailsIntent = new Intent(context, DetailActivity.class);
                detailsIntent.putExtra(Intent.EXTRA_TEXT, movieId);
                context.startActivity(detailsIntent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView titleView;
        public View rootView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            imageView = itemView.findViewById(R.id.list_item_image);
            titleView = itemView.findViewById(R.id.list_item_title);
        }
    }
}
