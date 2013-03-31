package com.patilparagp.wfa;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.patilparagp.wfa.model.ServerCredentials;
import com.patilparagp.wfa.model.UserInput;
import com.patilparagp.wfa.model.Workflow;
import com.patilparagp.wfa.store.WorkflowStore;
import com.patilparagp.wfa.utils.StreamUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static com.patilparagp.wfa.utils.DomUtils.getNodeByName;

public class WorkflowListingActivity extends Activity {

    private ListView workflowListView;
    private ProgressDialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        System.out.println(" ********* WorkflowListingActivity.onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        workflowListView = (ListView) findViewById(R.id.listView);

        final Intent intent = new Intent(Intent.ACTION_SYNC, null, getApplicationContext(), RestApiCallingService.class);
        final WorkflowResultReceiver workflowResultReceiver = new WorkflowResultReceiver(new Handler());

        dialog = ProgressDialog.show(this, null, "Getting workflows from server", false, true);

        workflowResultReceiver.setWorkflowListView(workflowListView);
        workflowResultReceiver.setContext(getApplicationContext());


        intent.putExtra("receiver", workflowResultReceiver);

        String server = getIntent().getStringExtra("server") == null ? "10.72.68.205" : getIntent().getStringExtra("server");
        String userName = getIntent().getStringExtra("userName") == null ? "admin" : getIntent().getStringExtra("userName");
        String password = getIntent().getStringExtra("password") == null ? "admin" : getIntent().getStringExtra("password");

        intent.putExtra("server", server);
        intent.putExtra("username", userName);
        intent.putExtra("password", password);


        if (WorkflowStore.getWorkflowStore(getApplicationContext()).load()) {
            System.out.println(" &&&&&&&  workflows loaded from file");
            workflowListView.setAdapter(new ArrayAdapter<Workflow>(getApplicationContext(), R.layout.listrow, WorkflowStore.getWorkflowStore(getApplicationContext()).getWorkflows()));
            dialog.dismiss();
        } else {
            System.out.println(" &&&&&&&  loading workflows from Server");
            WorkflowFetcherTask workflowFetcherTask = new WorkflowFetcherTask();
            workflowFetcherTask.execute(new ServerCredentials[]{new ServerCredentials(server, userName, password)});
        }


        workflowListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (int i = 0; i < workflowListView.getChildCount(); ++i) {
                    TextView textView = (TextView) workflowListView.getChildAt(i);
                    textView.setBackgroundResource(R.color.list_background);
                }

                view.setBackgroundResource(R.color.list_selected);
                Intent executeIntent = new Intent(getApplicationContext(), ExecuteActivity.class);
                executeIntent.putExtra("position", position);
                startActivity(executeIntent);

            }
        });

    }

    private class WorkflowFetcherTask extends AsyncTask<ServerCredentials, Void, List<Workflow>> {


        @Override
        protected List<Workflow> doInBackground(ServerCredentials... params) {

            String server = params[0].getServer();
            String userName = params[0].getUserName();
            String password = params[0].getPassword();


            HttpClient client = new DefaultHttpClient();

            HttpGet request = new HttpGet("http://" + server + "/rest/workflows");
            String encodedUserNamePassword = Base64.encodeToString((userName + ":" + password).getBytes(), Base64.NO_WRAP);
            request.addHeader("Authorization", "Basic " + encodedUserNamePassword);

            request.addHeader("Content-type", "application/xml");
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

                    return parseStream(inputStream);

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

        private List<Workflow> parseStream(InputStream inputStream) {

            String readFromStream = StreamUtils.readAll(inputStream);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            try {
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(new InputSource(new StringReader(readFromStream)));
                return xml2Objects(document.getChildNodes().item(0).getChildNodes());
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Workflow> workflows) {
            workflowListView.setAdapter(new ArrayAdapter<Workflow>(getApplicationContext(), R.layout.listrow, workflows));
            WorkflowStore.getWorkflowStore(getApplicationContext()).save(workflows);
            System.out.println(" &&&&&&&  workflows saved");
            dialog.dismiss();
        }

        private List<Workflow> xml2Objects(NodeList nodeList) {

            List<Workflow> workflows = new ArrayList<Workflow>();
            for (int index = 0; index < nodeList.getLength(); index++) {
                Node node = nodeList.item(index);
                String uuid = node.getAttributes().getNamedItem("uuid").getTextContent();
                String name = node.getChildNodes().item(0).getTextContent();
                String description = node.getChildNodes().item(1).getTextContent();
                Workflow workflow = new Workflow(name, description, uuid);
                List<UserInput> userInputs = new ArrayList<UserInput>();
                Node userInputNode = getNodeByName(node.getChildNodes(), "userInputList");
                if (userInputNode != null && userInputNode.hasChildNodes()) {
                    for (int index2 = 0; index2 < userInputNode.getChildNodes().getLength(); index2++) {
                        Node inputNode = userInputNode.getChildNodes().item(index2);
                        String inputName = getNodeByName(inputNode.getChildNodes(), "name").getTextContent();
                        String inputType = getNodeByName(inputNode.getChildNodes(), "type").getTextContent();
                        String inputDescription = getNodeByName(inputNode.getChildNodes(), "description") != null ?
                                getNodeByName(inputNode.getChildNodes(), "description").getTextContent() :
                                null;
                        String inputDefaultValue = getNodeByName(inputNode.getChildNodes(), "defaultValue") != null ?
                                getNodeByName(inputNode.getChildNodes(), "defaultValue").getTextContent() :
                                null;
                        boolean inputMandatory = Boolean.parseBoolean(getNodeByName(inputNode.getChildNodes(), "mandatory").getTextContent());
                        Node valuesNode = getNodeByName(inputNode.getChildNodes(), "allowedValues");
                        List<String> allowedValues = new ArrayList<String>();
                        if (valuesNode != null) {
                            for (int index3 = 0; index3 < valuesNode.getChildNodes().getLength(); index3++) {
                                Node valueNode = valuesNode.getChildNodes().item(index3);
                                allowedValues.add(valueNode.getTextContent());
                            }
                        }
                        UserInput userInput = new UserInput(inputName, inputType, inputDescription, inputMandatory, inputDefaultValue);
                        userInput.setValues(allowedValues);
                        userInputs.add(userInput);

                    }

                }
                workflow.setUserInputs(userInputs);
                workflows.add(workflow);
            }
            return workflows;
        }
    }
}
