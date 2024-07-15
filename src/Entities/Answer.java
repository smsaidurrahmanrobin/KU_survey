package Entities;

public interface Answer {
    public AnswerType getType();
    public void setType(AnswerType type);
    public int getMinValue();
    public void setMinValue(int minValue);
    public int getMaxValue();
    public void setMaxValue(int maxValue);
}
