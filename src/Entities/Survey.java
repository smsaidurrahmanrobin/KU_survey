package Entities;

import java.util.*;

public class Survey {
    private long surveyID;
    private User creator;
    private Date creationDate;
    private String title;
    private String description;
    private String introduction;
    private String goodbye;
    private Map<Integer, PersonalField> personalFields;
    private Map<Integer, Question> questions;
    private ArrayList<User> assistants;
    private String surveyFilePath;

    private SurveyVersion version;

    public enum SurveyVersion {
        VERSION_A("A"),
        VERSION_B("B");

        private final String displayName;

        SurveyVersion(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }



    //region Constructors
    public Survey() {
    }

    public Survey(String title, String description){
        this.title = title;
        this.description = description;
    }
    //endregion

    //region Getters and Setters
    public long getSurveyID() {
        return surveyID;
    }

    public void setSurveyID(long surveyID) {
        this.surveyID = surveyID;
    }


    public SurveyVersion getSurveyVersion() {
        return version;
    }

    public void setSurveyVersion(SurveyVersion version) {
        this.version = version;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getGoodbye() {
        return goodbye;
    }

    public void setGoodbye(String goodbye) {
        this.goodbye = goodbye;
    }

    public Map<Integer, PersonalField> getPersonalFields() {
        return personalFields;
    }

    public void setPersonalFields(Map<Integer, PersonalField> personalFields) {
        this.personalFields = personalFields;
    }

    public Map<Integer, Question> getQuestions() {
        return questions;
    }

    public void setQuestions(Map<Integer, Question> questions) {
        this.questions = questions;
    }

    public ArrayList<User> getAssistants() {
        return assistants;
    }

    public void setAssistants(ArrayList<User> assistants) {
        this.assistants = assistants;
    }

    public String getSurveyFilePath() {
        return surveyFilePath;
    }

    public void setSurveyFilePath(String surveyFilePath) {
        this.surveyFilePath = surveyFilePath;
    }


    //endregion

    //region Public Functions
    public void initializeData() {
        this.surveyID = Math.abs(UUID.randomUUID().getLeastSignificantBits());
        this.creationDate = new Date();
        this.personalFields = new HashMap<>();
        this.questions = new HashMap<>();
    }

    public void addFormField(PersonalField personalField) {
        personalFields.put(personalFields.size(), personalField);
    }

    public void updatePersonalField(int index, PersonalField personalField) {
        personalFields.remove(index);
        personalFields.put(index, personalField);
    }

    public void deleteFormField(int fieldIndex) {
        personalFields.remove(fieldIndex);

        personalFields.entrySet().stream()
                .filter(f -> f.getKey() > fieldIndex)
                .sorted(Map.Entry.comparingByKey())
                .forEach(f -> personalFields.put(f.getKey() - 1,
                        personalFields.remove(f.getKey())));
    }

    public void deleteFormField(PersonalField personalField) {
        Integer fieldIndex = personalFields.entrySet().stream()
                .filter(f -> f.getValue().equals(personalField))
                .map(Map.Entry::getKey)
                .findFirst().orElse(null);

        if (fieldIndex != null) {
            personalFields.remove(fieldIndex);

            personalFields.entrySet().stream()
                    .filter(f -> f.getKey() > fieldIndex)
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(f -> personalFields.put(f.getKey() - 1,
                            personalFields.remove(f.getKey())));
        }
    }

    public void addQuestion(Question question) {
        questions.put(questions.size(), question);
    }

    public void updateQuestion(int index, Question question) {
        questions.remove(index);
        questions.put(index, question);
    }

    public void deleteQuestion(int questionIndex) {
        questions.remove(questionIndex);

        questions.entrySet().stream()
                .filter(q -> q.getKey() > questionIndex)
                .sorted(Map.Entry.comparingByKey())
                .forEach(q -> questions.put(q.getKey() - 1,
                        questions.remove(q.getKey())));
    }

    public void deleteQuestion(Question question) {
        Integer questionIndex = questions.entrySet().stream()
                .filter(q -> q.getValue().equals(question))
                .map(Map.Entry::getKey)
                .findFirst().orElse(null);

        if (questionIndex != null) {
            questions.remove(questionIndex);

            questions.entrySet().stream()
                    .filter(q -> q.getKey() > questionIndex)
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(q -> questions.put(q.getKey() - 1,
                            questions.remove(q.getKey())));
        }
    }

    public void movePersonalUp(PersonalField field) {
        Integer index = personalFields.entrySet().stream()
                .filter(f -> f.getValue().equals(field))
                .map(HashMap.Entry::getKey)
                .findFirst().orElse(null);

        if (index != null && index > 0) {
            personalFields.remove(index);
            personalFields.put(index, personalFields.remove(index - 1));
            personalFields.put(index - 1, field);
        }
    }

    public void movePersonalDown(PersonalField field) {
        Integer index = personalFields.entrySet().stream()
                .filter(f -> f.getValue().equals(field))
                .map(HashMap.Entry::getKey)
                .findFirst().orElse(null);

        if (index != null && index < (personalFields.size() - 1)) {
            personalFields.remove(index);
            personalFields.put(index, personalFields.remove(index + 1));
            personalFields.put(index + 1, field);
        }
    }

    public void moveQuestionUp(Question question) {
        Integer index = questions.entrySet().stream()
                .filter(q -> q.getValue().equals(question))
                .map(HashMap.Entry::getKey)
                .findFirst().orElse(null);

        if (index != null && index > 0) {
            questions.remove(index);
            questions.put(index, questions.remove(index - 1));
            questions.put(index - 1, question);
        }
    }

    public void moveQuestionDown(Question question) {
        Integer index = questions.entrySet().stream()
                .filter(q -> q.getValue().equals(question))
                .map(HashMap.Entry::getKey)
                .findFirst().orElse(null);

        if (index != null && index < (questions.size() - 1)) {
            questions.remove(index);
            questions.put(index, questions.remove(index + 1));
            questions.put(index + 1, question);
        }
    }

    public String toString() {
        return this.title;
    }
    //endregion
}
