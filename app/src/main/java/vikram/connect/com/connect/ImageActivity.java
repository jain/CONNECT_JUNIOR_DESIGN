package vikram.connect.com.connect;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

/**
 * Created by vikram on 4/21/16.
 */

public class ImageActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_act);
        //video.png
        ImageView mImageView;
        mImageView = (ImageView) findViewById(R.id.imageView);
        mImageView.setImageResource(R.drawable.video);
    }
}
