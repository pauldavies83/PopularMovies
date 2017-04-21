package uk.co.pauldavies83.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
    public void onBindViewHolder(VideoListItemViewHolder holder, int position) {
        holder.videoTitle.setText(videos[position].getName());
    }

    @Override
    public int getItemCount() {
        return videos.length;
    }


    class VideoListItemViewHolder extends RecyclerView.ViewHolder {

        private final TextView videoTitle;

        VideoListItemViewHolder(View reviewListItem) {
            super(reviewListItem);
            videoTitle = (TextView) reviewListItem.findViewById(R.id.tv_title);
        }
    }
}
