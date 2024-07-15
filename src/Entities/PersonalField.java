package Entities;

import java.util.UUID;

public class PersonalField {
    private long formFieldID;
    private String title;
    private Answer asnwerType;
    private boolean mandatory;

    //region Getters and Setters
    public long getFormFieldID() {
        return formFieldID;
    }

    public void setFormFieldID(long formFieldID) {
        this.formFieldID = formFieldID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Answer getAsnwerType() {
        return asnwerType;
    }

    public void setAsnwerType(Answer asnwerType) {
        this.asnwerType = asnwerType;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }
    //endregion

    public void initializeData() {
        this.formFieldID = Math.abs(UUID.randomUUID()
                .getLeastSignificantBits());
    }

    public String toString() {
        return this.title;
    }
}
