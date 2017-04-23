package uk.co.pauldavies83.popularmovies.views.moviedetail;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import uk.co.pauldavies83.popularmovies.R;
import uk.co.pauldavies83.popularmovies.model.Review;

public class ReviewListAdapter extends RecyclerView.Adapter<ReviewListAdapter.ReviewListItemViewHolder> {

    private Review[] reviews = {};
    private Context context;

    public ReviewListAdapter(Context context) {
        this.context = context;
    }

    public void setReviews(Review[] reviews) {
        this.reviews = reviews;
        notifyDataSetChanged();
    }

    @Override
    public ReviewListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View reviewListItem = inflater.inflate(R.layout.review_list_item, parent, false);
        return new ReviewListItemViewHolder(reviewListItem);
    }

    @Override
    public void onBindViewHolder(ReviewListItemViewHolder holder, final int position) {
        holder.reviewerName.setText(reviews[position].getAuthor());
        holder.reviewBody.setText(reviews[position].getBody());
        holder.review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                    .setTitle(reviews[position].getAuthor())
                    .setMessage(reviews[position].getBody())
                    .setCancelable(true).create().show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return reviews.length;
    }


    class ReviewListItemViewHolder extends RecyclerView.ViewHolder {

        private final View review;
        private final TextView reviewerName;
        private final TextView reviewBody;

        public ReviewListItemViewHolder(View reviewListItem) {
            super(reviewListItem);
            review = reviewListItem.findViewById(R.id.review);
            reviewerName = (TextView) reviewListItem.findViewById(R.id.tv_reviewer_name);
            reviewBody = (TextView) reviewListItem.findViewById(R.id.tv_review_body);
        }
    }
}
