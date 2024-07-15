package Entities;

import java.util.List;

public class Select implements Answer {
    private AnswerType type;
    private int minSelected;
    private int maxSelected;
    private List<String> options;

    public Select() {}

    public Select(int minSelected, int maxSelected, List<String> options) {
        this.type = AnswerType.SELECT;
        this.minSelected = minSelected;
        this.maxSelected = maxSelected;
        this.options = options;
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
        return minSelected;
    }

    @Override
    public void setMinValue(int minSelected) {
        this.minSelected = minSelected;
    }

    @Override
    public int getMaxValue() {
        return maxSelected;
    }

    @Override
    public void setMaxValue(int maxSelected) {
        this.maxSelected = maxSelected;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }
    //endregion

    //region Public Functions
    public void addField(String field) {

    }

    public void deleteField(String field) {

    }
    //endregion
}
