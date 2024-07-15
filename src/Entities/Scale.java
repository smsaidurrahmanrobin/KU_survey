package Entities;

public class Scale implements Answer {
    private AnswerType type;
    private int minValue;
    private int maxValue;

    public Scale() {}

    public Scale(int minValue, int maxValue) {
        this.type = AnswerType.SCALE;
        this.minValue = minValue;
        this.maxValue = maxValue;
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
        return minValue;
    }

    @Override
    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }

    @Override
    public int getMaxValue() {
        return maxValue;
    }

    @Override
    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }
    //endregion
}
