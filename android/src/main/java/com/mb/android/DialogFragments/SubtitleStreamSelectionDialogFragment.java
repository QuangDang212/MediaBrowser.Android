package com.mb.android.DialogFragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.mb.android.R;
import com.mb.android.logging.AppLogger;
import com.mb.android.ui.mobile.playback.PlaybackActivity;
import com.mb.android.utils.Utils;
import mediabrowser.model.dlna.StreamInfo;
import mediabrowser.model.entities.MediaStream;
import mediabrowser.model.entities.MediaStreamType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mark on 2014-07-25.
 */
public class SubtitleStreamSelectionDialogFragment extends DialogFragment {

    private StreamInfo mStreamInfo;
    private List<MediaStream> mStreams;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mStreamInfo = ((PlaybackActivity)getActivity()).getStreamInfo();

        if (mStreamInfo != null && mStreamInfo.getMediaSource() != null && mStreamInfo.getMediaSource().getMediaStreams() != null) {
            mStreams = new ArrayList<>();
            MediaStream noStream = new MediaStream();
            noStream.setLanguage("");
            noStream.setCodec("NONE");
            noStream.setIndex(-1);
            mStreams.add(noStream);

            for (MediaStream stream : mStreamInfo.getMediaSource().getMediaStreams()) {
                if (stream.getType() != null && stream.getType().equals(MediaStreamType.Subtitle)) {
                    mStreams.add(stream);
                }
            }
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater inflater = LayoutInflater.from(getActivity());

        View contentView = inflater.inflate(R.layout.widget_stream_selection, null, false);

        if (mStreams != null && mStreams.size() > 0) {
            ListView streamList = (ListView) contentView.findViewById(R.id.lvStreamList);
            streamList.setAdapter(new StreamAdapter());
            streamList.setOnItemClickListener(new SubtitleStreamListener());
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select Subtitle");
        builder.setView(contentView);

        return builder.create();
    }

    private class StreamAdapter extends BaseAdapter {

        private LayoutInflater inflater;

        public StreamAdapter() {
            inflater = LayoutInflater.from(getActivity());
        }

        @Override
        public int getCount() {
            return mStreams.size();
        }

        @Override
        public Object getItem(int position) {
            return mStreams.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.widget_audio_stream, parent, false);
            }

            MediaStream stream = mStreams.get(position);

            if (position == 0 && mStreamInfo.getSubtitleStreamIndex() == null) {
                convertView.findViewById(R.id.ivCheckmark).setVisibility(View.VISIBLE);
            } else if (mStreamInfo.getSubtitleStreamIndex() != null && stream.getIndex() == mStreamInfo.getSubtitleStreamIndex()) {
                convertView.findViewById(R.id.ivCheckmark).setVisibility(View.VISIBLE);
            } else {
                convertView.findViewById(R.id.ivCheckmark).setVisibility(View.INVISIBLE);
            }

            TextView streamDetails = (TextView) convertView.findViewById(R.id.tvStreamInfo);
            streamDetails.setText(Utils.buildSubtitleDisplayString(stream));

            return convertView;
        }
    }

    private class SubtitleStreamListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            AppLogger.getLogger().Debug("SubtitleStreamListener", "OnClick");
            Activity activity = getActivity();
            if (activity != null) {
                ((PlaybackActivity)activity).onSubtitleStreamSelected(mStreams.get(position).getIndex());
            }
            SubtitleStreamSelectionDialogFragment.this.dismiss();
        }
    }
}
