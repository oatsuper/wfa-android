package com.patilparagp.wfa.xml;

import android.widget.EditText;
import com.patilparagp.wfa.model.UserInput;

import java.util.List;

public class WorkflowInput {

    private String executionDateTime;
    private String comment;
    private List<UserInput> userInputAndValues;

    public WorkflowInput(List<UserInput> userInputAndValues) {
        this.userInputAndValues = userInputAndValues;
    }

    public String toXML() {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sb.append("<workflowInput>");
        sb.append("<userInputValues>");
        for (UserInput userInput : userInputAndValues) {
            sb.append("<userInputEntry ");
            sb.append("key=\"").append(userInput.getName()).append("\"").append(" ");
            sb.append("value=\"").append(((EditText) userInput.getView()).getText()).append("\"");
            sb.append("/>");
        }
        sb.append("</userInputValues>");
        if (executionDateTime != null && !executionDateTime.isEmpty()) {
            sb.append("<executionDateAndTime>").append(executionDateTime).append("</executionDateAndTime>");
        }
        if (comment != null && !comment.isEmpty()) {
            sb.append("<comments>").append(comment).append("</comments>");
        }
        sb.append("</workflowInput>");
        return sb.toString();
    }

    public void setExecutionDateTime(String executionDateTime) {
        this.executionDateTime = executionDateTime;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setUserInputAndValues(List<UserInput> userInputAndValues) {
        this.userInputAndValues = userInputAndValues;
    }
}
