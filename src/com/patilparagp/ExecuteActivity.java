package com.patilparagp;

import android.app.Activity;
import android.os.Bundle;
import com.patilparagp.R;

public class ExecuteActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(getLayoutInflater().inflate(R.layout.execute, null));
    }
}
