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
 * Class is the custom adapter for RecyclerView present on the download modules screen
 * Will inflate CardViews inside recycler view to match passed in data
 * CardView design is specified in old_cv.xml
 */
public class DownloadAdapter extends RecyclerView.Adapter<DownloadAdapter.NewViewHolder> {
    private ArrayList<String[]> data; // data to populate Recycler View
    private AppCompatActivity act; // reference to calling activity

    /**
     * Internal static class which inflates the CardView specific to this recycler view
     * finds elements and inflates them when passed in input.
     */
    public static class NewViewHolder extends RecyclerView.ViewHolder {
        private CardView cv; // reference to CardView as a whole
        private TextView name; // reference to TextView which displays name of module
        private ImageView photo; // reference to image holder of logo for current view
        private Button download; // reference to download button

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
     * @param myDataset the data set
     * @param act       the calling activity
     */
    public DownloadAdapter(ArrayList<String[]> myDataset, AppCompatActivity act) {
        data = myDataset;
        this.act = act;
    }

    /**
     * Finds and inflates the relevant CardView for this RecyclerView
     *
     * @param viewGroup view under which the inflation occurs
     * @param i         index
     * @return variable representing CardView for this index
     */
    @Override
    public NewViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.new_cv, viewGroup, false);
        NewViewHolder nvh = new NewViewHolder(v);
        return nvh;
    }

    /**
     * Configures parameters inside the CardView to take in values
     * The values are assigned to various elements in CardView creating the UI we see
     * Listeners are assigned to buttons in the CardView and their actions specified
     *
     * @param nvh the variable which represents the CardView
     * @param i   current index of data
     */
    @Override
    public void onBindViewHolder(NewViewHolder nvh, int i) {
        // assign elements
        nvh.name.setText(data.get(i)[0]);
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(data.get(i)[1], nvh.photo);
        final int index = i;
        nvh.download.setOnClickListener(new View.OnClickListener() {
            /**
             * Downloads new module from current local storage and regenerates the UI to reflect this
             *
             * @param view view which was clicked
             */
            @Override
            public void onClick(View view) {
                // try to download new module
                try {
                    Data.modules.put(data.get(index)[0], Data.firebaseJS.get(data.get(index)[0]));
                    Data.save(act);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // regenerate UI
                act.recreate();
            }
        });
    }

    /**
     * @return number of items in recycler view
     */
    @Override
    public int getItemCount() {
        return data.size();
    }
}
