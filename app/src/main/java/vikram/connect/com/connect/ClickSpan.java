package vikram.connect.com.connect;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

/**
 * Class which makes the text in TextView clickable and attaches a listener
 */
public class ClickSpan extends ClickableSpan {

    private OnClickListener mListener; // corresponding listener for the current clickable text

    /**
     * Constructor to take in inputs
     *
     * @param listener
     */
    public ClickSpan(OnClickListener listener) {
        mListener = listener;
    }

    /**
     * Calls listener corresponding to text when the text is clicked
     *
     * @param view view which was clicked
     */
    @Override
    public void onClick(View view) {
        if (mListener != null){
            mListener.onClick();
        }
    }

    /**
     * Specifies interface for the input listener
     */
    public interface OnClickListener {
        void onClick();
    }

    /**
     * Updates the color of the TextPaint of text which is clickable
     *
     * @param ds paint color of the text currently
     */
    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setColor(0xff0000ff); // overwrites of clickable text to blue
    }
}