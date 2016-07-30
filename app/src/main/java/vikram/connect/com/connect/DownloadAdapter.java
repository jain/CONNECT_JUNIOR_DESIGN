package vikram.connect.com.connect;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Adapter for RecyclerView which is in charge of showing possible downloadable modules
 */
public class DownloadAdapter extends RecyclerView.Adapter<DownloadAdapter.NewViewHolder> {
    private ArrayList<String[]> data;
    private AppCompatActivity act;

    /**
     * Class which represents the cardview design for this Recycler View
     */
    public static class NewViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView name;
        ImageView photo;
        Button download;

        public NewViewHolder(View v) {
            super(v);
            cv = (CardView) itemView.findViewById(R.id.cv_new);
            name = (TextView) itemView.findViewById(R.id.new_name);
            download = (Button) itemView.findViewById(R.id.new_download);
            photo = (ImageView) itemView.findViewById(R.id.new_photo);
        }
    }

    /**
     * Constructor which grabs data to be used by this adapter to inflate CardViews
     *
     * @param myDataset
     * @param act
     */
    public DownloadAdapter(ArrayList<String[]> myDataset, AppCompatActivity act) {
        data = myDataset;
        this.act = act;
    }

    /**
     * Creates CardView for specified index
     *
     * @param viewGroup
     * @param i
     * @return
     */
    @Override
    public NewViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.new_cv, viewGroup, false);
        NewViewHolder nvh = new NewViewHolder(v);
        return nvh;
    }

    /**
     * Binds a CardView with particular index in RecyclerView
     *
     * @param nvh
     * @param i
     */
    @Override
    public void onBindViewHolder(NewViewHolder nvh, int i) {
        nvh.name.setText(data.get(i)[0]);
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(data.get(i)[1], nvh.photo);
        final int index = i;
        nvh.download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Data.modules.put(data.get(index)[0], Data.firebaseJS.get(data.get(index)[0]));
                    Data.save(act);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                act.recreate();
            }
        });
    }

    /**
     * returns size of RecyclerView
     *
     * @return
     */
    @Override
    public int getItemCount() {
        return data.size();
    }
}
