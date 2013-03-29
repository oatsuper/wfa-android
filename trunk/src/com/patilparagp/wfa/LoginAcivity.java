package com.patilparagp.wfa;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.patilparagp.wfa.R;

import static com.patilparagp.wfa.utils.StringUtils.isNullorEmpty;

public class LoginAcivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstance) {

        super.onCreate(savedInstance);
        setContentView(getLayoutInflater().inflate(R.layout.logindialog, null));
        Button loginButton = (Button) findViewById(R.id.loginbutton);

        final SharedPreferences sharedPreferences = getSharedPreferences("wfa", MODE_PRIVATE);

        final EditText serverEditText = (EditText) findViewById(R.id.server);
        final EditText userNameEditText = (EditText) findViewById(R.id.username);
        final EditText passwordEditText = (EditText) findViewById(R.id.password);

        String server = sharedPreferences.getString("server",null);
        String userName = sharedPreferences.getString("userName",null);
        String password = sharedPreferences.getString("password",null);

        if (!isNullorEmpty(server)) {
            serverEditText.setText(server);
        }
        if (!isNullorEmpty(userName)) {
            userNameEditText.setText(userName);
        }
        if (!isNullorEmpty(password)) {
            passwordEditText.setText(password);
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String server = serverEditText.getText().toString();
                String userName = userNameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                final Intent workflowListingIntent = new Intent(getApplicationContext(), WorkflowListingActivity.class);

                if (isNullorEmpty(server)) {
                    server = "10.72.68.201";
                }
                if (isNullorEmpty(userName)) {
                    userName = "admin";
                }
                if (isNullorEmpty(password)) {
                    password = "password";
                }

                SharedPreferences.Editor preferenceEditor = sharedPreferences.edit();

                preferenceEditor.putString("server", server);
                preferenceEditor.putString("userName", userName);
                preferenceEditor.putString("password", password);
                preferenceEditor.commit();

                workflowListingIntent.putExtra("server", server);
                workflowListingIntent.putExtra("userName", userName);
                workflowListingIntent.putExtra("password", password);

                startActivity(workflowListingIntent);

            }
        });


    }
}
