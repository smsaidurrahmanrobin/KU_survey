package Entities;

public enum AnswerType {
    FREETEXT("Free text"),
    SELECT("Selection"),
    SCALE("Scale");

    private final String text;

    AnswerType(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}