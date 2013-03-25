package com.patilparagp;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.patilparagp.R;

import java.util.ArrayList;
import java.util.List;


public class WorkflowResultReceiver extends ResultReceiver {


    private ListView workflowListView;
    private List<String> workflowNames = new ArrayList<String>();
    private Context context;
    private ProgressDialog progressDialog;

    public WorkflowResultReceiver(Handler handler) {
        super(handler);
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        System.out.println(" ********* WorkflowResultReceiver.onReceiveResult");
        if (workflowListView != null) {
            workflowNames.addAll(resultData.getStringArrayList("workflowNames"));
            workflowListView.setAdapter(new ArrayAdapter<String>(context, R.layout.listrow, workflowNames));
        }
        if(resultCode == 2) {
            progressDialog.dismiss();
        }

    }

    public ListView getWorkflowListView() {
        return workflowListView;
    }

    public void setWorkflowListView(ListView workflowListView) {
        this.workflowListView = workflowListView;
    }
    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }


    public void setProgressDialog(ProgressDialog progressDialog) {
        this.progressDialog = progressDialog;
    }
}
