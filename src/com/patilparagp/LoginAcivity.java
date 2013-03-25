package com.patilparagp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.patilparagp.R;

public class LoginAcivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstance) {

        super.onCreate(savedInstance);
        setContentView(getLayoutInflater().inflate(R.layout.logindialog, null));
        Button loginButton = (Button)findViewById(R.id.loginbutton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String server = ((EditText)findViewById(R.id.server)).getText().toString();
                final String userName = ((EditText)findViewById(R.id.username)).getText().toString();
                final String password = ((EditText)findViewById(R.id.password)).getText().toString();
                final Intent workflowListingIntent = new Intent(getApplicationContext(), WorkflowListingActivity.class);
                workflowListingIntent.putExtra("server", server);
                workflowListingIntent.putExtra("userName", userName);
                workflowListingIntent.putExtra("password", password);

                startActivity(workflowListingIntent);

            }
        });





    }
}
