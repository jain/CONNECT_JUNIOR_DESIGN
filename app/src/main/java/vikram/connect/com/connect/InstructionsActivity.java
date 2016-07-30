package vikram.connect.com.connect;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Activity displays instructions for how to use app
 * It is static so nothing much happens
 */
public class InstructionsActivity extends AppCompatActivity {
    /**
     * Loads the content from the xml file and displays it
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);
    }
}
