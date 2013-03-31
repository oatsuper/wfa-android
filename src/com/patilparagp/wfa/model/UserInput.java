package com.patilparagp.wfa.model;

import android.view.View;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserInput implements Serializable {

    private String name;
    private String type;
    private String description;


    private transient View view;

    private boolean mandatory;
    private String defaultValue;

    private List<String> values = new ArrayList<String>();

    public UserInput(String name, String type, String description, boolean mandatory, String defaultValue) {
        this.name = name;
        this.type = type;
        this.description = description;
        this.mandatory = mandatory;
        this.defaultValue = defaultValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserInput userInput = (UserInput) o;

        if (name != null ? !name.equals(userInput.name) : userInput.name != null) return false;
        if (type != null ? !type.equals(userInput.type) : userInput.type != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    public List<String> getValues() {
        return values;
    }

    public boolean isMandatory() {
        return mandatory;
    }


    public String getDefaultValue() {
        return defaultValue;
    }


    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    @Override
    public String toString() {
        return "UserInput{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", description='" + description + '\'' +
                ", values=" + values +
                '}';
    }
}
