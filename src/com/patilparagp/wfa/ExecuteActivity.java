package com.patilparagp.wfa;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.*;
import com.patilparagp.wfa.model.UserInput;
import com.patilparagp.wfa.model.Workflow;
import com.patilparagp.wfa.store.WorkflowStore;
import com.patilparagp.wfa.utils.StreamUtils;
import com.patilparagp.wfa.xml.WorkflowInput;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

public class ExecuteActivity extends Activity {

    private ProgressDialog dialog;
    private final Context context = this;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        int position = getIntent().getIntExtra("position", -1);
        setContentView(getLayoutInflater().inflate(R.layout.execute, null));
        TableLayout tableLayout = (TableLayout) findViewById(R.id.userInputTable);
        final Workflow workflow = WorkflowStore.getWorkflowStore(getApplicationContext()).getByIndex(position);

        TableRow firstRow = new TableRow(getApplicationContext());
        TextView textView = new TextView(getApplicationContext());
        textView.setText("Workflow inputs");
        firstRow.addView(textView);
        tableLayout.addView(firstRow);

        for (final UserInput userInput : workflow.getUserInputs()) {
            TableRow row = new TableRow(getApplicationContext());
            TextView nameView = new TextView(getApplicationContext());
            if (userInput.isMandatory()) {
                nameView.setText("* " + userInput.getName());
            } else {
                nameView.setText(userInput.getName());
            }

            row.addView(nameView);
            if (!userInput.getValues().isEmpty()) {
                ListView listView = new ListView(getApplicationContext());
                listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                listView.setSelector(android.R.color.darker_gray);
                listView.setAdapter(new ArrayAdapter<String>(getApplicationContext(), R.layout.listrow, userInput.getValues()));

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        view.setBackgroundColor(R.color.list_selected);
                        EditText editText = new EditText(getApplicationContext());
                        editText.setText(userInput.getValues().get(position));
                        userInput.setView(editText);
                    }
                });

                row.addView(listView);
            } else {
                EditText inputView = new EditText(getApplicationContext());
                inputView.setText(userInput.getDefaultValue());
                inputView.setMinimumWidth(15);
                userInput.setView(inputView);
                row.addView(inputView);
            }
            tableLayout.addView(row);
        }
        TableRow finalRow = new TableRow(getApplicationContext());
        Button button = new Button(getApplicationContext());
        finalRow.addView(button);
        button.setText("execute");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = ProgressDialog.show(context, null, "Scheduling workflow", false, true);
                WorkflowExecutionTask workflowExecutionTask = new WorkflowExecutionTask();
                workflowExecutionTask.execute(workflow);
            }
        });
        tableLayout.addView(finalRow);
    }

    private class WorkflowExecutionTask extends AsyncTask<Workflow, Void, String> {


        @Override
        protected String doInBackground(Workflow... params) {
            Workflow workflow = params[0];
            SharedPreferences sharedPreferences = getSharedPreferences("wfa", MODE_PRIVATE);
            String server = sharedPreferences.getString("server", null);
            String userName = sharedPreferences.getString("userName", null);
            String password = sharedPreferences.getString("password", null);

            HttpClient client = new DefaultHttpClient();

            HttpPost request = new HttpPost("http://" + server + "/rest/workflows/" + workflow.getUuid() + "/jobs");
            System.out.println(request.getURI());
            String encodedUserNamePassword = Base64.encodeToString((userName + ":" + password).getBytes(), Base64.NO_WRAP);
            request.addHeader("Authorization", "Basic " + encodedUserNamePassword);
            request.addHeader("Content-type", "application/xml");
            try {
                System.out.println("&&& post content");
                System.out.println(new WorkflowInput(workflow.getUserInputs()).toXML());
                request.setEntity(new StringEntity(new WorkflowInput(workflow.getUserInputs()).toXML()));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            HttpResponse httpResponse;
            InputStream inputStream = null;

            try {
                httpResponse = client.execute(request);
                int responseCode = httpResponse.getStatusLine().getStatusCode();
                String message = httpResponse.getStatusLine().getReasonPhrase();
                System.out.println(String.format("responseCode = %d, message = %s ", responseCode, message));
                HttpEntity entity = httpResponse.getEntity();
                if (entity != null) {
                    inputStream = entity.getContent();
                    String readFromStream = StreamUtils.readAll(inputStream);
                    System.out.println("&&& readFromStream = " + readFromStream);
                    return String.valueOf(responseCode);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }


            return null;

        }

        @Override
        protected void onPostExecute(String responseCode) {
            dialog.dismiss();
            finish();
        }

    }
}
