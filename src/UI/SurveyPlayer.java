package UI;

import Entities.*;
import Source.Serializer;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;


public class SurveyPlayer extends JFrame {
    Serializer serializer;
    Survey survey;
    Question question;
    AnswerType answerType;
    Response response;
    Map<String, JTextField> personalForm;
    File responsesFolder;
    int questionIndex = 0;

    CardLayout cardLayout;
    CardLayout buttonsCardLayout;

    JPanel mainPanel;
    JPanel contentPanel;
    JPanel personalPanel;
    JPanel questionPanel;
    JPanel questionDynamicPanel;
    JPanel buttonsPanel;

    JLabel questionTitle;
    JLabel questionBody;

    JTextArea freeText;
    List<JCheckBox> options;
    JSlider slider;
    JTextField sliderValue;

    JButton personalButton;
    JButton startButton;
    JButton nextButton;
    JButton submitButton;

    JButton selectVersionButton;

    public SurveyPlayer(Serializer serializer, Survey survey) {
        super(survey.getTitle());
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        this.serializer = serializer;
        this.survey = survey;
        response = new Response();
        response.initializeData(survey);

        responsesFolder = new File(System.getProperty("user.dir")
                + "/surveys/" + survey.getSurveyID());
        if (!responsesFolder.exists()) {
            responsesFolder.mkdir();
        }

        initializeMainPanel();
        setupQuestionPanel();

        freeText = new JTextArea();
        options = new ArrayList<>();
        slider = new JSlider();
        sliderValue = new JTextField();
        sliderValue.setHorizontalAlignment(JTextField.CENTER);

        buttonsCardLayout = new CardLayout();
        buttonsPanel = new JPanel(buttonsCardLayout);

        selectVersionButton = new JButton("Select Version");
        selectVersionButton.addActionListener(e -> showVersionSelection());

        personalButton = new JButton("Next");
        personalButton.addActionListener(e -> showPersonalForm());

        startButton = new JButton("Start survey");
        startButton.addActionListener(e -> savePersonalForm());

        nextButton = new JButton("Next");
        nextButton.addActionListener(e -> nextQuestion());

        submitButton = new JButton("Submit");
        submitButton.addActionListener(e -> submitSurvey());

        buttonsPanel.add(selectVersionButton, "SelectVersion");
        buttonsPanel.add(personalButton, "Personal");
        buttonsPanel.add(startButton, "Start");
        buttonsPanel.add(nextButton, "Next");
        buttonsPanel.add(submitButton, "Submit");

        buttonsCardLayout.show(buttonsPanel, "SelectVersion");

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);

        createPersonalForm();

        add(mainPanel);
        setVisible(true);
    }

    private void initializeMainPanel() {
        mainPanel = new JPanel(new BorderLayout());

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
   
        JLabel introLabel = new JLabel(survey.getIntroduction(), SwingConstants.CENTER);
        introLabel.setFont(new Font("Sans_Serif", Font.PLAIN, 16));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;  
        gbc.gridy = 0;  
        gbc.weightx = 1.0;  
        gbc.weighty = 1.0;  
        gbc.fill = GridBagConstraints.BOTH;  

        contentPanel.add(introLabel, "Introduction");
        contentPanel.add(new JLabel(survey.getGoodbye()), "Goodbye");
    }

    private void showVersionSelection() {
        JDialog versionDialog = new JDialog(this, "Select Version", true);
        versionDialog.setSize(400, 200);
        versionDialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel label = new JLabel("Please select the version of the survey:", SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(label, gbc);

        JComboBox<Survey.SurveyVersion> versionComboBox = new JComboBox<>(Survey.SurveyVersion.values());
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        panel.add(versionComboBox, gbc);

        JButton okButton = new JButton("OK");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(okButton, gbc);

        okButton.addActionListener(e -> {
            Survey.SurveyVersion selectedVersion = (Survey.SurveyVersion) versionComboBox.getSelectedItem();
            survey.setSurveyVersion(selectedVersion);
            versionDialog.dispose();
            buttonsCardLayout.show(buttonsPanel, "Personal");
        });

        versionDialog.add(panel);
        versionDialog.setVisible(true);
    }


    private void setupQuestionPanel() {
        questionPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        contentPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));


        JPanel containerPanel = new JPanel();
        containerPanel.setLayout(new BoxLayout(containerPanel, BoxLayout.Y_AXIS));

        questionTitle = new JLabel();
        questionTitle.setFont(new Font("Sans_Serif", Font.BOLD, 20));
        questionTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        questionBody = new JLabel();
        questionBody.setFont(new Font("Sans_Serif", Font.PLAIN, 16));
        questionBody.setAlignmentX(Component.CENTER_ALIGNMENT);

        containerPanel.add(questionTitle);
        containerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        containerPanel.add(questionBody);
        containerPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        questionDynamicPanel = new JPanel();
        questionDynamicPanel.setLayout(new BoxLayout(questionDynamicPanel, BoxLayout.Y_AXIS));
        containerPanel.add(questionDynamicPanel);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        questionPanel.add(containerPanel, gbc);

        contentPanel.add(questionPanel, "Question");
    }


    private void createPersonalForm() {
        personalForm = new LinkedHashMap<>();
        personalPanel = new JPanel(new GridLayout(survey.getPersonalFields().size(), 2));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));


        for (PersonalField field : survey.getPersonalFields().values()) {
            JLabel label = new JLabel(field.getTitle() + (field.isMandatory() ? " *" : ""));
            JTextField textField = new JTextField();

            personalPanel.add(label);
            personalPanel.add(textField);

            response.addAnswer(field.getTitle(), null);
            personalForm.put(field.getTitle(), textField);
        }

        contentPanel.add(personalPanel, "Personal Form");
    }

    public boolean validatePersonalForm() {
        boolean allFieldsValid = true;
        Map<Integer, PersonalField> fieldsById = survey.getPersonalFields();

        for (Map.Entry<String, JTextField> entry : personalForm.entrySet()) {
            String title = entry.getKey();
            JTextField textField = entry.getValue();

            PersonalField correspondingField = null;
            for (PersonalField field : fieldsById.values()) {
                if (field.getTitle().equals(title)) {
                    correspondingField = field;
                    break;
                }
            }

            if (correspondingField == null) {
                System.out.println("Error: No corresponding PersonalField found for title: " + title);
                continue;
            }

            if (correspondingField.isMandatory() && (textField.getText() == null ||
                    textField.getText().trim().isEmpty()))
            {
                System.out.println("Mandatory field '" + correspondingField.getTitle() + "' is empty.");
                allFieldsValid = false;
            }
        }
        return allFieldsValid;
    }

    private void showPersonalForm() {
        cardLayout.show(contentPanel, "Personal Form");
        buttonsCardLayout.show(buttonsPanel, "Start");
    }

    private void savePersonalForm() {
        if (validatePersonalForm()) {
            personalForm.entrySet().stream().forEach(
                    e -> response.updateAnswer(e.getKey(), e.getValue().getText())
            );

            serializer.serializeResponse(response);

            cardLayout.show(contentPanel, "Question");
            buttonsCardLayout.show(buttonsPanel, "Next");
            nextQuestion();
        } else {
            JOptionPane.showMessageDialog(null,
                    "Please fill all mandatory fields marked with * before proceeding.");
        }
    }

    private void nextQuestion() {
        if (questionIndex != 0) {
            if (!isAnswerValid()) {
                JOptionPane.showMessageDialog(this, "This question is mandatory. Please provide an answer.",
                        "Mandatory Question", JOptionPane.ERROR_MESSAGE);
                return;
            }
            saveQuestion();
        }

        while (questionIndex < survey.getQuestions().size() &&
            !isQuestionForCurrentVersion(survey.getQuestions().get(questionIndex))) {
            questionIndex++;
        }

        if (questionIndex >= survey.getQuestions().size()) {
            cardLayout.show(contentPanel, "Goodbye");
            buttonsCardLayout.show(buttonsPanel, "Submit");
        }
        else {
            displayQuestion();
            questionIndex++;
        }
    }

    private boolean isQuestionForCurrentVersion(Question question) {
        Survey.SurveyVersion currentVersion = survey.getSurveyVersion();
        Question.Version questionVersion = question.getVersion();
        return (currentVersion == Survey.SurveyVersion.VERSION_A && questionVersion == Question.Version.VERSION_A)
                || (currentVersion == Survey.SurveyVersion.VERSION_B && questionVersion == Question.Version.VERSION_B);
    }

    private boolean isAnswerValid() {
        if (!question.isMandatory()) {
            if (answerType == AnswerType.FREETEXT) {
                FreeText freeTextAnswer = (FreeText) question.getAnswer();
                int minCharacters = freeTextAnswer.getMinValue();
                String text = freeText.getText().trim();
                if (text.length() > 0 && text.length() < minCharacters) {
                    JOptionPane.showMessageDialog(this, "Please enter at least " + minCharacters + " characters or leave it blank.",
                            "Optional Question", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            } else if (answerType == AnswerType.SELECT) {
                Select selectAnswer = (Select) question.getAnswer();
                long selectedCount = options.stream().filter(JCheckBox::isSelected).count();
                if (selectedCount > 0) {
                    if (selectAnswer.getMinValue() == selectAnswer.getMaxValue()) {
                        if (selectedCount != selectAnswer.getMinValue()) {
                            JOptionPane.showMessageDialog(this, "Please select exactly " + selectAnswer.getMinValue() + " options.",
                                    "Optional Question", JOptionPane.ERROR_MESSAGE);
                            return false;
                        }
                    } else if (selectedCount < selectAnswer.getMinValue() || selectedCount > selectAnswer.getMaxValue()) {
                        JOptionPane.showMessageDialog(this, "Please select " + selectAnswer.getMinValue() + "-" + selectAnswer.getMaxValue() + " options or leave it blank.",
                                "Optional Question", JOptionPane.ERROR_MESSAGE);
                        return false;
                    }
                }
            }
            return true;
        }

        switch (answerType) {
            case FREETEXT:
                FreeText freeTextAnswer = (FreeText) question.getAnswer();
                int minCharacters = freeTextAnswer.getMinValue();
                if (freeText.getText().trim().length() < minCharacters) {
                    JOptionPane.showMessageDialog(this, "Please enter at least " + minCharacters + " characters.",
                            "Mandatory Question", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                return true;
            case SELECT:
                Select selectAnswer = (Select) question.getAnswer();
                long selectedCount = options.stream().filter(JCheckBox::isSelected).count();
                if (selectAnswer.getMinValue() == selectAnswer.getMaxValue()) {
                    if (selectedCount != selectAnswer.getMinValue()) {
                        JOptionPane.showMessageDialog(this, "Please select exactly " + selectAnswer.getMinValue() + " options.",
                                "Mandatory Question", JOptionPane.ERROR_MESSAGE);
                        return false;
                    }
                } else if (selectedCount < selectAnswer.getMinValue() || selectedCount > selectAnswer.getMaxValue()) {
                    JOptionPane.showMessageDialog(this, "Please select " + selectAnswer.getMinValue() + "-" + selectAnswer.getMaxValue() + " options.",
                            "Mandatory Question", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                return true;
            case SCALE:
                return true;
            default:
                return false;
        }
    }

    private void displayQuestion() {
        question = survey.getQuestions().get(questionIndex);
        Answer answer = question.getAnswer();

        String optionalText = !question.isMandatory() ? " (Optional)" : "*";
        questionTitle.setText(question.getTitle() + optionalText);

        if (answer.getType() == AnswerType.SELECT) {
            displaySelectQuestion((Select) answer);
        } else if (answer.getType() == AnswerType.FREETEXT) {
            displayFreeTextQuestion((FreeText) answer);
        } else {
            questionBody.setText(question.getBody());
        }

        response.addAnswer(question.getTitle());
        addAnswerComponents(answer);

        questionPanel.updateUI();
    }

    private void displayFreeTextQuestion(FreeText freeTextAnswer) {
        String freeTextInfo = String.format("<br><span style='font-size: 9px ; color: gray;'>(Please enter %d-%d characters).</span>", freeTextAnswer.getMinValue(), freeTextAnswer.getMaxValue());
        questionBody.setText("<html>" + question.getBody() + freeTextInfo + "</html>");
    }

    private void displaySelectQuestion(Select selectAnswer) {
        String selectionInfo;
        if (selectAnswer.getMinValue() == selectAnswer.getMaxValue()) {
            selectionInfo = String.format("<br><span style='font-size: 9px; color: gray;'>(Please select exactly %d options.)</span>", selectAnswer.getMinValue());
        } else {
            selectionInfo = String.format("<br><span style='font-size: 9px; color: gray;'>(Please select between %d and %d options.)</span>", selectAnswer.getMinValue(), selectAnswer.getMaxValue());
        }
        questionBody.setText("<html>" + question.getBody() + selectionInfo + "</html>");
    }

    private void addAnswerComponents(Answer answer) {
        answerType = answer.getType();
        questionDynamicPanel.removeAll();

        if (answerType == AnswerType.SELECT) {
            questionDynamicPanel.setLayout(new GridLayout(0, 1));
        } else {
            questionDynamicPanel.setLayout(new BorderLayout());
        }

        switch (answerType) {
            case FREETEXT:
                setupFreeTextComponent((FreeText) answer);
                break;
            case SELECT:
                setupSelectComponents((Select) answer);
                break;
            case SCALE:
                setupScaleComponent((Scale) answer);
                break;
        }
    }

    private void setupSelectComponents(Select answer) {
        options.clear();

        questionDynamicPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        for (String option : answer.getOptions()) {
            JCheckBox checkBox = new JCheckBox(option);
            options.add(checkBox);
            questionDynamicPanel.add(checkBox);
        }
    }

    private void setupScaleComponent(Scale answer) {
        int min = answer.getMinValue();
        int max = answer.getMaxValue();
        int mid = (min + max)/2;
        slider.setMinimum(min);
        slider.setMaximum(max);

        Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
        labelTable.put(min, new JLabel(String.valueOf(min)));
        labelTable.put(mid, new JLabel(String.valueOf(mid)));
        labelTable.put(max, new JLabel(String.valueOf(max)));

        slider.setLabelTable(labelTable);
        slider.setPaintLabels(true);
        slider.setPaintTrack(true);
        slider.setPaintTicks(true);

        slider.addChangeListener(e -> {
            sliderValue.setText(String.valueOf(slider.getValue()));
            sliderValue.updateUI();
        });

        questionDynamicPanel.add(slider, BorderLayout.CENTER);
        questionDynamicPanel.add(sliderValue, BorderLayout.SOUTH);
    }

    private void setupFreeTextComponent(FreeText answer) {
        freeText = new JTextArea(5, 30);
        freeText.setText("");
        freeText.setLineWrap(true);
        freeText.setWrapStyleWord(true);
        freeText.setMargin(new Insets(5, 5, 5, 5));

        int maxCharacters = answer.getMaxValue();

        JLabel charCountLabel = new JLabel("<html><span style='font-size: smaller; color: gray;'>0 / " + maxCharacters + "</span></html>");
        charCountLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        JScrollPane scrollPane = new JScrollPane(freeText);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(400, 100));

    
        ((AbstractDocument) freeText.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if ((fb.getDocument().getLength() + string.length()) <= maxCharacters) {
                    super.insertString(fb, offset, string, attr);
                    updateCharCountLabel();
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if ((fb.getDocument().getLength() + text.length() - length) <= maxCharacters) {
                    super.replace(fb, offset, length, text, attrs);
                    updateCharCountLabel();
                }
            }

            @Override
            public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
                super.remove(fb, offset, length);
                updateCharCountLabel();
            }

            private void updateCharCountLabel() {
                int charactersUsed = freeText.getDocument().getLength();
                charCountLabel.setText("<html><span style='font-size: smaller; color: gray;'>" + charactersUsed + " / " + maxCharacters + "</span></html>");

            }
        });

        questionDynamicPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 10, 10, 10);

        questionDynamicPanel.add(scrollPane, gbc);

        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(0, 0, 0, 10);
        questionDynamicPanel.add(charCountLabel, gbc);
    }

    private void submitSurvey() {
        saveQuestion();
        buttonsPanel.removeAll();
    }

    private void saveQuestion() {
        if (answerType == AnswerType.FREETEXT) {
            response.updateAnswer(question.getTitle(), freeText.getText());
        }
        else if (answerType == AnswerType.SELECT) {
            List<String> selectedOptions = options.stream()
                    .filter(e -> e.isSelected())
                    .map(e -> e.getText())
                    .collect(Collectors.toList());

            if (selectedOptions.size() == 1) {
                response.updateAnswer(question.getTitle(), selectedOptions.get(0));
            }
            else if (selectedOptions.size() > 1) {
                StringBuilder sBuilder = new StringBuilder();
                selectedOptions.forEach(e -> sBuilder.append("[").append(e).append("]"));
                response.updateAnswer(question.getTitle(), sBuilder.toString());
            }
        }
        else if (answerType == AnswerType.SCALE) {
            response.updateAnswer(question.getTitle(), String.valueOf(slider.getValue()));
        }

        serializer.serializeResponse(response);
    }
}
