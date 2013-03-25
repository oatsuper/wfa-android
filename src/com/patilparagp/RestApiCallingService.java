package com.patilparagp;

import android.app.IntentService;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Base64;
import android.widget.ProgressBar;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.*;
import javax.xml.xpath.*;
import java.io.*;
import java.util.ArrayList;

public class RestApiCallingService extends IntentService {

    public RestApiCallingService() {
        super("RestApiCallingService");
    }


    public RestApiCallingService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        System.out.println(" ********* RestApiCallingService.onHandleIntent");
        String server = intent.getStringExtra("server").isEmpty() ? "10.72.68.205" : intent.getStringExtra("server");
        String userName = intent.getStringExtra("username").isEmpty() ? "admin" : intent.getStringExtra("username");
        String password = intent.getStringExtra("password").isEmpty() ? "admin" : intent.getStringExtra("password");

        final ResultReceiver receiver = intent.getParcelableExtra("receiver");
        System.out.println(String.format("server %s, userName %s, password %s", server, userName, password));
        HttpClient client = new DefaultHttpClient();

        HttpGet request = new HttpGet("http://" + server + "/rest/workflows");
        String encodedUserNamePassword = Base64.encodeToString((userName + ":" + password).getBytes(),Base64.NO_WRAP);
        request.addHeader("Authorization", "Basic "+encodedUserNamePassword);

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
                //workflowNames = evaluateXpath(stringToDom(streamToString(inputStream)));
                parseAndSend(inputStream, receiver);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }



    }
    private static String streamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
    private static Document stringToDom(String xmlSource)             {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(new InputSource(new StringReader(xmlSource)));
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        return null;
    }

    private ArrayList<String> evaluateXpath(Document document) {
        ArrayList<String> workflowNames = new ArrayList<String>();
        XPathFactory xPathFactory = XPathFactory.newInstance();
        XPath xPath = xPathFactory.newXPath();
        String expression = "/collection/workflow/name";
        try {
            XPathExpression xPathExpression = xPath.compile(expression);
            NodeList nodeList = (NodeList)xPathExpression.evaluate(document, XPathConstants.NODESET);
            for (int i = 0; i< nodeList.getLength() ; ++i) {
                Node node = nodeList.item(i);
                workflowNames.add(node.getChildNodes().item(0).getTextContent());
            }

        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
        return workflowNames;
    }

    private void parseAndSend(InputStream inputStream, final ResultReceiver receiver) {
        final ArrayList<String> workflowNames = new ArrayList<String>();
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {

            SAXParser saxParser = factory.newSAXParser();

            DefaultHandler handler = new DefaultHandler() {
                private boolean foundWorkflow;
                private boolean foundName;
                @Override
                public void startElement(String uri, String localName,String qName, Attributes attributes) throws SAXException {
                    if (qName.equals("workflow")) {
                        foundWorkflow = true;
                    }
                    if (qName.equals("name")) {
                        foundName = true;
                    }
                }

                @Override
                public void characters(char ch[], int start, int length) throws SAXException {

                    if (foundWorkflow && foundName) {
                        String name =  new String(ch, start, length);
                        workflowNames.add(name);
                        foundName = false;
                        foundWorkflow = false;
                        if (workflowNames.size() > 5) {
                            Bundle bundle = new Bundle();
                            bundle.putStringArrayList("workflowNames",new ArrayList<String>(workflowNames));
                            receiver.send(1,bundle);
                            workflowNames.clear();
                        }
                    }
                }
            };

            saxParser.parse(inputStream, handler);

            if (!workflowNames.isEmpty()) {
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("workflowNames",workflowNames);
                receiver.send(2,bundle);

            }



        } catch (Throwable e) {
            e.printStackTrace ();
        }

    }

}
