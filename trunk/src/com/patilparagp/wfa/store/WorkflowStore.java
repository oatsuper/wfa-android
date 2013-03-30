package com.patilparagp.wfa.store;

import android.content.Context;
import com.patilparagp.wfa.model.Workflow;

import java.io.*;
import java.util.List;

public class WorkflowStore {


    private List<Workflow> workflows;
    private Context context;
    private static final String fileName = "workflows";
    private static WorkflowStore workflowStore;

    private WorkflowStore(Context context) {
        this.context = context;
    }

    public static WorkflowStore getWorkflowStore(Context context) {
        if (workflowStore == null) {
            workflowStore = new WorkflowStore(context);
        }
        return workflowStore;
    }

    public boolean load() {
        FileInputStream fis = null;
        List<Workflow> loadedWorkflows = null;
        try {
            fis = context.openFileInput(fileName);
            ObjectInputStream is = new ObjectInputStream(fis);
            loadedWorkflows = (List<Workflow>) is.readObject();
            is.close();

        } catch (Throwable e) {
            return false;
        }
        workflows = loadedWorkflows;
        return true;
    }

    public void save(List<Workflow> workflows) {
        this.workflows = workflows;
        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(workflows);
            os.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Workflow getByName(String name) {
        for (Workflow workflow : workflows) {
            if (name.equals(workflow.getName())) {
                return workflow;
            }
        }
        return null;

    }

    public Workflow getByUUID(String uuid) {
        for (Workflow workflow : workflows) {
            if (uuid.equals(workflow.getUuid())) {
                return workflow;
            }
        }
        return null;
    }

    public Workflow getByIndex(int index) {
        return workflows.get(index);
    }

    public List<Workflow> getWorkflows() {
        return workflows;
    }

}
