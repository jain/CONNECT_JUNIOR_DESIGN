package vikram.connect.com.connect;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Class is the custom adapter for recycler view present on the very first screen (selection screen)
 * Will inflate cardviews inside recycler view to match passed in data
 * CardView design is specified in select_cv.xml
 */
public class SelectAdapter extends RecyclerView.Adapter<SelectAdapter.SelViewHolder> {
    private ArrayList<String[]> data;
    private final View.OnClickListener mOnClickListener;

    /**
     * internal static class which inflates the cardview specific to this recycler view
     * finds elements and inflates them when passed in input.
     */
    public static class SelViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private ImageView photo;

        /**
         * constructor for cardview, finds the necessary elements to inflate
         *
         * @param view
         */
        public SelViewHolder(View view) {
            super(view);
            name = (TextView) itemView.findViewById(R.id.sel_name);
            photo = (ImageView) itemView.findViewById(R.id.sel_photo);
        }
    }

    /**
     * constructor to pass ind data which will be utilized for filling recyclerview
     */
    public SelectAdapter(ArrayList<String[]> myDataset, AppCompatActivity act, RecyclerView rView) {
        data = myDataset;
        mOnClickListener = new SelectClickListener(rView, data, act);
    }

    /**
     * Finds and inflates the relevant cardview for this recycler view
     * Assigns listener so each cardview is selectable
     *
     * @param viewGroup view under which the inflation occurs
     * @param i         index
     * @return          variable representing cardview for this index
     */
    @Override
    public SelViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.select_cv, viewGroup, false);
        view.setOnClickListener(mOnClickListener);
        SelViewHolder svh = new SelViewHolder(view);
        return svh;
    }

    /**
     * Configures parameters inside the cardview to take in values
     * The values are assigned to various elements in cardview creating the UI we see
     *
     * @param svh the variable which represents the cardview
     * @param i   current index of data
     */
    @Override
    public void onBindViewHolder(SelViewHolder svh, int i) {
        svh.name.setText(data.get(i)[0]);
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(data.get(i)[1], svh.photo);
    }

    /**
     * @return numsber of items in recycler view
     */
    @Override
    public int getItemCount() {
        return data.size();
    }
}
