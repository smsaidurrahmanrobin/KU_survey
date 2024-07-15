package Entities;

public class FreeText implements Answer {
    private AnswerType type;
    private int minCharacters;
    private int maxCharacters;

    public FreeText() {}

    public FreeText(int minCharacters, int maxCharacters) {
        this.type = AnswerType.FREETEXT;
        this.minCharacters = minCharacters;
        this.maxCharacters = maxCharacters;
    }

    //region Getters and Setters
    @Override
    public AnswerType getType() {
        return type;
    }

    @Override
    public void setType(AnswerType type) {
        this.type = type;
    }

    @Override
    public int getMinValue() {
        return minCharacters;
    }

    @Override
    public void setMinValue(int minSelected) {
        this.minCharacters = minSelected;
    }

    @Override
    public int getMaxValue() {
        return maxCharacters;
    }

    @Override
    public void setMaxValue(int maxCharacters) {
        this.maxCharacters = maxCharacters;
    }
    //endregion
}
