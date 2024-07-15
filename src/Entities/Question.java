package Entities;

import java.util.UUID;

public class Question {
    private long questionID;
    private String title;
    private String body;
    private Answer answer;
    private boolean mandatory;


    public enum Version { VERSION_A, VERSION_B };
    private Version version;


    //region Getters and Setters
    public long getQuestionID() {
        return questionID;
    }

    public void setQuestionID(long questionID) {
        this.questionID = questionID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Answer getAnswer() {
        return answer;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }
    //endregion

    public void initializeData() {
        this.questionID = Math.abs(UUID.randomUUID().getLeastSignificantBits());
    }

    public String toString() {
        return title;
    }
}
