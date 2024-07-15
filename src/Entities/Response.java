package Entities;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class Response {
    private long responseID;
    private long surveyID;
    private Map<String, String> answers;

    //region Constructors
    public Response() {
    }
    //endregion Constructors

    //region Getters and Setters
    public long getResponseID() {
        return responseID;
    }

    public void setResponseID(long responseID) {
        this.responseID = responseID;
    }

    public long getSurveyID() {
        return surveyID;
    }

    public void setSurveyID(long surveyID) {
        this.surveyID = surveyID;
    }

    public Map<String, String> getAnswers() {
        return answers;
    }

    public void setAnswers(Map<String, String> answers) {
        this.answers = answers;
    }
    //endregion

    //region Public Functions
    public void initializeData(Survey survey) {
        this.responseID = Math.abs(UUID.randomUUID().getLeastSignificantBits());
        this.surveyID = survey.getSurveyID();
        this.answers = new LinkedHashMap<>();
    }

    public void addAnswer(String question) {
        this.answers.put(question, null);
    }

    public void addAnswer(String question, String answer) {
        this.answers.put(question, answer);
    }

    public void updateAnswer(String question, String answer) {
        this.answers.replace(question, answer);
    }

    public String getAnswer(String question) {
        return this.answers.get(question);
    }

    public void deleteAnswer(String question) {

    }
    //endregion
}