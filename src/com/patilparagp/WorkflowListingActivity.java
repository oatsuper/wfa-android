package com.patilparagp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.*;
import com.patilparagp.R;

public class WorkflowListingActivity extends Activity {

    private ListView workflowListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        System.out.println(" ********* WorkflowListingActivity.onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        workflowListView = (ListView)findViewById(R.id.listView);

        final Intent intent = new Intent(Intent.ACTION_SYNC, null, getApplicationContext(), RestApiCallingService.class);
        final WorkflowResultReceiver workflowResultReceiver = new WorkflowResultReceiver(new Handler());

        ProgressDialog dialog =  ProgressDialog.show(this, null ,"Getting workflows from server");

        workflowResultReceiver.setWorkflowListView(workflowListView);
        workflowResultReceiver.setContext(getApplicationContext());
        workflowResultReceiver.setProgressDialog(dialog);

        intent.putExtra("receiver", workflowResultReceiver);

        String server = getIntent().getStringExtra("server") == null ? "10.72.68.205" : getIntent().getStringExtra("server");
        String userName = getIntent().getStringExtra("userName") == null ? "admin" : getIntent().getStringExtra("userName");
        String password = getIntent().getStringExtra("password") == null ? "admin" : getIntent().getStringExtra("password");

        intent.putExtra("server",server);
        intent.putExtra("username",userName);
        intent.putExtra("password",password);


        startService(intent);

        workflowListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                startActivity(new Intent(getApplicationContext(), ExecuteActivity.class));

            }
        });

    }
    public ListView getWorkflowListView() {
        return workflowListView;
    }

    public void setWorkflowListView(ListView workflowListView) {
        this.workflowListView = workflowListView;
    }

}
