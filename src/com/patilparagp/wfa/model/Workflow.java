package com.patilparagp.wfa.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Workflow implements Serializable {


    private String uuid;
    private String name;
    private String description;

    private List<UserInput> userInputs = new ArrayList<UserInput>();


    public Workflow(String name) {
        this.name = name;
    }

    public Workflow(String name, String description, String uuid) {
        this.name = name;
        this.description = description;
        this.uuid = uuid;
    }

    public Workflow withName(String name) {
        this.name = name;
        return this;
    }

    public Workflow withDescription(String description) {
        this.description = description;
        return this;
    }

    public Workflow withInputs(List<UserInput> userInputs) {
        this.userInputs = userInputs;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Workflow workflow = (Workflow) o;

        if (name != null ? !name.equals(workflow.name) : workflow.name != null) return false;
        if (uuid != null ? !uuid.equals(workflow.uuid) : workflow.uuid != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = uuid != null ? uuid.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getDescription() {
        return description;
    }

    public List<UserInput> getUserInputs() {
        return userInputs;
    }

    public void setUserInputs(List<UserInput> userInputs) {
        this.userInputs = userInputs;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
