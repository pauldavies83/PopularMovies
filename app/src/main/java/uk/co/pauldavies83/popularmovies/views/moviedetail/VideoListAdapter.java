package uk.co.pauldavies83.popularmovies.views.moviedetail;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import uk.co.pauldavies83.popularmovies.R;
import uk.co.pauldavies83.popularmovies.model.Video;

public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.VideoListItemViewHolder> {

    private Video[] videos = {};
    private Context context;

    public VideoListAdapter(Context context) {
        this.context = context;
    }

    public void setVideos(Video[] videos) {
        this.videos = videos;
        notifyDataSetChanged();
    }

    @Override
    public VideoListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View videoListItem = inflater.inflate(R.layout.video_list_item, parent, false);
        return new VideoListItemViewHolder(videoListItem);
    }

    @Override
    public void onBindViewHolder(VideoListItemViewHolder holder, final int position) {
        holder.videoTitle.setText(videos[position].getName());
        holder.videoListItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(
                        new Intent(Intent.ACTION_VIEW,
                                   Uri.parse("http://www.youtube.com/watch?v=" + videos[position].getKey())
                        ));
            }
        });
    }

    @Override
    public int getItemCount() {
        return videos.length;
    }


    class VideoListItemViewHolder extends RecyclerView.ViewHolder {

        private final TextView videoTitle;
        private final View videoListItem;

        VideoListItemViewHolder(View reviewListItem) {
            super(reviewListItem);
            videoListItem = reviewListItem.findViewById(R.id.video_list_item);
            videoTitle = (TextView) reviewListItem.findViewById(R.id.tv_title);
        }
    }
}
