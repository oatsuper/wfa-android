package com.patilparagp.wfa;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import com.patilparagp.wfa.model.Workflow;
import com.patilparagp.wfa.store.WorkflowStore;

public class ExecuteActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        int position = getIntent().getIntExtra("position", -1);
        setContentView(getLayoutInflater().inflate(R.layout.execute, null));
        final EditText workflowEditText = (EditText) findViewById(R.id.editText);
        Workflow workflow = WorkflowStore.getWorkflowStore(getApplicationContext()).getByIndex(position);
        workflowEditText.setText(workflow.getName() + " \n " + workflow.getDescription() + " \n " + workflow.getUserInputs());

    }
}
