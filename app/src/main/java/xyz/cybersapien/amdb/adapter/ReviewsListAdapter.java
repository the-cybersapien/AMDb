package xyz.cybersapien.amdb.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.noties.markwon.Markwon;
import xyz.cybersapien.amdb.R;
import xyz.cybersapien.amdb.model.Movie;

/**
 * Created by ogcybersapien on 13/11/17.
 */

public class ReviewsListAdapter extends RecyclerView.Adapter<ReviewsListAdapter.ViewHolder> {

    private static final String TAG = "ReviewsListAdapter";
    private ArrayList<Movie.ReviewResults> reviewsList;
    private Context context;

    public ReviewsListAdapter(ArrayList<Movie.ReviewResults> reviewsList, Context context) {
        this.reviewsList = reviewsList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.movie_review_list_item, parent, false);
        Log.d(TAG, "onCreateViewHolder: " + reviewsList.size());
        // If there is more than 1 item, decrease the size a little to give a visual indication to the user
        if (reviewsList.size() > 1) {
            Configuration config = context.getResources().getConfiguration();
            // The calculation were acquired by a day's worth of trial and error
            // Ideally I should decrease the size, but somehow, increasing it works better!
            int screenWidthDp = (config.screenWidthDp * 25) / 9;
            rootView.setLayoutParams(new CardView.LayoutParams(screenWidthDp, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Movie.ReviewResults currentResult = reviewsList.get(position);
        holder.nameTextView.setText(currentResult.getAuthor());
        Markwon.setMarkdown(holder.reviewDetail, currentResult.getContent());
        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(currentResult.getUrl())));
            }
        });
        holder.reviewDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(currentResult.getUrl())));
            }
        });
    }

    @Override
    public int getItemCount() {
        return reviewsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.reviewer_name_text_view)
        TextView nameTextView;
        @BindView(R.id.review_details_text)
        TextView reviewDetail;
        View rootView;

        public ViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            ButterKnife.bind(this, rootView);
        }
    }
}
