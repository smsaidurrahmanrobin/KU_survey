package UI;

import Entities.PersonalField;
import Entities.Question;
import Entities.Survey;
import Source.Serializer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.*;

public class SurveyEditor extends JFrame {
    private final String SAVED_SURVEY_MESSAGE = "The survey has been saved!";

    private Serializer serializer;
    private Survey survey;
    private List<Survey> surveys;
    private DefaultListModel<Survey> surveysModel;
    private DefaultListModel<PersonalField> personalModel;
    private DefaultListModel<Question> questionsModel;

    private CardLayout mainCardLayout;
    private CardLayout reorderCardLayout;
    private CardLayout buttonsCardLayout;

    private JPanel topPanel;
    private JPanel mainPanel;
    private JPanel surveyInfoPanel;
    private JScrollPane personalPane;
    private JScrollPane questionsPane;
    private JPanel reorderPanel;
    private JPanel reorderPersonalPanel;
    private JPanel reorderQuestionPanel;
    private JPanel buttonsPanel;
    private JPanel personalFormButtonsPanel;
    private JPanel questionsButtonsPanel;
    private JPanel surveyButtonsPanel;
    private JLabel titleLabel;
    private JLabel descriptionLabel;
    private JLabel introductionLabel;
    private JLabel goodbyeLabel;

    private JList<PersonalField> personalJList;
    private JList<Question> questionsJList;
    private JTextField titleField;
    private JTextField descriptionField;
    private JTextArea introductionField;
    private JTextArea goodbyeField;

    private JButton addPersonalFieldButton;
    private JButton editPersonalFieldButton;
    private JButton deletePersonalFieldButton;
    private JButton addQuestionButton;
    private JButton editQuestionButton;
    private JButton deleteQuestionButton;
    private JButton saveButton;
    private JButton surveyInfoButton;
    private JButton personalButton;
    private JButton questionsButton;
    private JButton personalUpButton;
    private JButton personalDownButton;
    private JButton questionUpButton;
    private JButton questionDownButton;

    public SurveyEditor(
            Serializer serializer,
            List<Survey> surveys,
            DefaultListModel<Survey> surveysModel
    ) {
        super("Create Survey");
        this.serializer = serializer;
        this.surveys = surveys;
        this.surveysModel = surveysModel;
        this.survey = new Survey();
        survey.initializeData();
        newSurveyEditor();
    }

    public SurveyEditor(
            Serializer serializer,
            List<Survey> surveys,
            DefaultListModel<Survey> surveysModel,
            Survey survey
    ) {
        super("Edit Survey");
        this.serializer = serializer;
        this.surveys = surveys;
        this.surveysModel = surveysModel;
        this.survey = survey;
        newSurveyEditor();
    }

    private void newSurveyEditor() {
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        createTopPanel();
        createMainPanel();
        createReorderPanel();
        createButtonsPanel();

        add(topPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
        add(reorderPanel, BorderLayout.EAST);
        add(buttonsPanel, BorderLayout.SOUTH);
        reorderPanel.setVisible(false);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    //region UI Creation
    private void createTopPanel() {
        topPanel = new JPanel(new GridLayout(1,3));

        surveyInfoButton = new JButton("Survey Information");
        surveyInfoButton.addActionListener(e -> showSurveyInformation());

        personalButton = new JButton("Personal Form Fields");
        personalButton.addActionListener(e -> showPersonalForm());

        questionsButton = new JButton("Questions");
        questionsButton.addActionListener(e -> showQuestions());

        topPanel.add(surveyInfoButton);
        topPanel.add(personalButton);
        topPanel.add(questionsButton);
    }

    private void createMainPanel() {
        mainCardLayout = new CardLayout();
        mainPanel = new JPanel(mainCardLayout);


        surveyInfoPanel = new JPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(30, 50, 5, 50); 

        titleLabel = new JLabel("Title");
        titleField = new JTextField(survey.getTitle());
        titleField.setPreferredSize(new Dimension(300,30));


        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        surveyInfoPanel.add(titleLabel, gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        surveyInfoPanel.add(titleField, gbc);


        descriptionLabel = new JLabel("Description");
        descriptionField = new JTextField(survey.getDescription());
        descriptionField.setPreferredSize(new Dimension(300, 60));


        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        surveyInfoPanel.add(descriptionLabel, gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        surveyInfoPanel.add(descriptionField, gbc);


        introductionLabel = new JLabel("Introduction message");
        introductionField = new JTextArea(survey.getIntroduction());
        introductionField.setLineWrap(true);
        introductionField.setWrapStyleWord(true);
        JScrollPane introductionScrollPane = new JScrollPane(introductionField);
        introductionScrollPane.setPreferredSize(new Dimension(300, 90));


        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        surveyInfoPanel.add(introductionLabel, gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        surveyInfoPanel.add(introductionScrollPane, gbc);


        goodbyeLabel = new JLabel("Goodbye message");
        goodbyeField = new JTextArea(survey.getGoodbye());
        goodbyeField.setLineWrap(true);
        goodbyeField.setWrapStyleWord(true);
        JScrollPane goodbyeScrollPane = new JScrollPane(goodbyeField);
        goodbyeScrollPane.setPreferredSize(new Dimension(300, 90));

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.weighty = 0;
        surveyInfoPanel.add(goodbyeLabel, gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        surveyInfoPanel.add(goodbyeScrollPane, gbc);

        personalPane = new JScrollPane();
        personalModel = new DefaultListModel<>();
        survey.getPersonalFields().values()
                .stream().forEach(personalModel::addElement);
        personalJList = new JList<>(personalModel);
        personalJList.addListSelectionListener(
                e -> {
                    if (personalModel.size() > 1) {
                        personalUpButton.setEnabled(true);
                        personalDownButton.setEnabled(true);
                    }
                }
        );
        personalPane = new JScrollPane(personalJList);




        questionsModel = new DefaultListModel<>();
        survey.getQuestions().values()
                .stream().forEach(questionsModel::addElement);
        questionsJList = new JList<Question>(questionsModel);
        questionsJList.addListSelectionListener(
                e -> {
                    if (questionsModel.size() > 1) {
                        questionUpButton.setEnabled(true);
                        questionDownButton.setEnabled(true);
                    }
                }
        );
        questionsPane = new JScrollPane(questionsJList);


        personalJList.addListSelectionListener(e -> {
            boolean isSelection = !personalJList.isSelectionEmpty();
            editPersonalFieldButton.setEnabled(isSelection);
            deletePersonalFieldButton.setEnabled(isSelection);
        });


        questionsJList.addListSelectionListener(e -> {
            boolean isSelection = !questionsJList.isSelectionEmpty();
            System.out.println("Selection changed: " + isSelection);
            editQuestionButton.setEnabled(isSelection);
            deleteQuestionButton.setEnabled(isSelection);
        });

        mainPanel.add(surveyInfoPanel, "Survey Information");
        mainPanel.add(personalPane, "Personal Form Pane");
        mainPanel.add(questionsPane, "Questions Pane");
    }

    private void createReorderPanel() {
        reorderCardLayout = new CardLayout();
        reorderPanel = new JPanel(reorderCardLayout);

        reorderPersonalPanel = new JPanel(new GridLayout(2, 1));
        personalUpButton = new JButton("⌃");
        personalUpButton.addActionListener(e -> movePersonalUp());
        personalUpButton.setEnabled(false);

        personalDownButton = new JButton("⌄");
        personalDownButton.addActionListener(e -> movePersonalDown());
        personalDownButton.setEnabled(false);

        reorderPersonalPanel.add(personalUpButton);
        reorderPersonalPanel.add(personalDownButton);

        reorderQuestionPanel = new JPanel(new GridLayout(2, 1));
        questionUpButton = new JButton("⌃");
        questionUpButton.addActionListener(e -> moveQuestionUp());
        questionUpButton.setEnabled(false);

        questionDownButton = new JButton("⌄");
        questionDownButton.addActionListener(e -> moveQuestionDown());
        questionDownButton.setEnabled(false);

        reorderQuestionPanel.add(questionUpButton);
        reorderQuestionPanel.add(questionDownButton);

        reorderPanel.add(reorderPersonalPanel, "Personal Fields");
        reorderPanel.add(reorderQuestionPanel, "Questions");

        reorderCardLayout.show(reorderPanel, "Personal Fields");
    }

    private void createButtonsPanel() {
        buttonsCardLayout = new CardLayout();
        buttonsPanel = new JPanel(buttonsCardLayout);
        personalFormButtonsPanel = new JPanel();
        questionsButtonsPanel = new JPanel();
        surveyButtonsPanel = new  JPanel();

        addPersonalFieldButton = new JButton("Add Field");
        addPersonalFieldButton.addActionListener(e ->
                new FormFieldEditor(survey, personalModel));

        editPersonalFieldButton = new JButton("Edit Field");
        editPersonalFieldButton.addActionListener(e ->
                new FormFieldEditor(survey, personalModel,
                        personalJList.getSelectedValue()));

        personalJList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && !e.isConsumed()) {
                    e.consume(); // Prevent multiple handling
                    PersonalField selectedPersonalfield = personalJList.getSelectedValue();
                    if (selectedPersonalfield != null) {
                        new FormFieldEditor(survey, personalModel, selectedPersonalfield).setVisible(true);
                    }
                }
            }
        });

        deletePersonalFieldButton = new JButton("Delete Field");
        deletePersonalFieldButton.addActionListener(e -> deleteFormField());

        addQuestionButton = new JButton("Add Question");
        addQuestionButton.addActionListener(e ->
                new QuestionEditor(survey, questionsModel));

        editQuestionButton = new JButton("Edit Question");
        editQuestionButton.addActionListener(e ->
                new QuestionEditor(survey, questionsModel,
                        questionsJList.getSelectedValue()));

        questionsJList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && !e.isConsumed()) {
                    e.consume(); // Prevent multiple handling
                    Question selectedQuestion = questionsJList.getSelectedValue();
                    if (selectedQuestion != null) {
                        new QuestionEditor(survey, questionsModel, selectedQuestion).setVisible(true);
                    }
                }
            }
        });

        deleteQuestionButton = new JButton("Delete Question");
        deleteQuestionButton.addActionListener(e -> deleteQuestion());

        saveButton = new JButton("Save");
        saveButton.addActionListener(e -> saveSurvey());

        personalFormButtonsPanel.add(addPersonalFieldButton, "Add Field");
        personalFormButtonsPanel.add(editPersonalFieldButton, "Edit Field");
        personalFormButtonsPanel.add(deletePersonalFieldButton, "Delete Field");
        personalFormButtonsPanel.add(saveButton, "Save Survey");

        questionsButtonsPanel.add(addQuestionButton, "Add Question");
        questionsButtonsPanel.add(editQuestionButton, "Edit Question");
        questionsButtonsPanel.add(deleteQuestionButton, "Delete Question");

        surveyButtonsPanel.add(saveButton, "Save Survey");
        buttonsPanel.add(surveyButtonsPanel, "Survey Information Buttons");
        buttonsPanel.add(personalFormButtonsPanel, "Fields Buttons");
        buttonsPanel.add(questionsButtonsPanel, "Questions Buttons");

        addPersonalFieldButton.setEnabled(false);
        editPersonalFieldButton.setEnabled(false);
        deletePersonalFieldButton.setEnabled(false);
        addQuestionButton.setEnabled(false);
        editQuestionButton.setEnabled(false);
        deleteQuestionButton.setEnabled(false);
    }
    //endregion

    private void showSurveyInformation() {
        questionUpButton.setEnabled(false);
        questionDownButton.setEnabled(false);
        addPersonalFieldButton.setEnabled(false);
        editPersonalFieldButton.setEnabled(false);
        deletePersonalFieldButton.setEnabled(false);
        addQuestionButton.setEnabled(false);
        editQuestionButton.setEnabled(false);
        deleteQuestionButton.setEnabled(false);
        reorderPanel.setVisible(false);
        mainCardLayout.show(mainPanel, "Survey Information");
        surveyButtonsPanel.add(saveButton, "Save Survey");
        buttonsCardLayout.show(buttonsPanel, "Survey Information Buttons");
    }

    private void showPersonalForm() {
        personalJList.clearSelection();

        addPersonalFieldButton.setEnabled(true);
        addQuestionButton.setEnabled(false);
        editQuestionButton.setEnabled(false);
        deleteQuestionButton.setEnabled(false);
        reorderPanel.setVisible(true);

        mainCardLayout.show(mainPanel, "Personal Form Pane");
        reorderCardLayout.show(reorderPanel, "Personal Fields");
        personalFormButtonsPanel.add(saveButton, "Save Survey");
        buttonsCardLayout.show(buttonsPanel, "Fields Buttons");
    }

    private void showQuestions() {
        questionsJList.clearSelection();

        addPersonalFieldButton.setEnabled(false);
        editPersonalFieldButton.setEnabled(false);
        deletePersonalFieldButton.setEnabled(false);
        addQuestionButton.setEnabled(true);
        reorderPanel.setVisible(true);

        mainCardLayout.show(mainPanel, "Questions Pane");
        reorderCardLayout.show(reorderPanel, "Questions");
        questionsButtonsPanel.add(saveButton, "Save Survey");
        buttonsCardLayout.show(buttonsPanel, "Questions Buttons");
    }

    private void movePersonalUp() {
        int index = personalJList.getSelectedIndex();
        if (index > 0) {
            PersonalField field = personalModel.get(index);
            personalModel.set(index, personalModel.get(index-1));
            personalModel.set(index - 1, field);

            personalJList.setSelectedIndex(index - 1);
            personalJList.ensureIndexIsVisible(index - 1);
            personalJList.requestFocusInWindow();

            survey.movePersonalUp(personalJList.getSelectedValue());
        }
    }

    private void movePersonalDown() {
        int index = personalJList.getSelectedIndex();
        if (index < (personalModel.size() - 1)) {
            PersonalField field = personalModel.get(index);
            personalModel.set(index, personalModel.get(index + 1));
            personalModel.set(index + 1, field);

            personalJList.setSelectedIndex(index + 1);
            personalJList.ensureIndexIsVisible(index + 1);
            personalJList.requestFocusInWindow();

            survey.movePersonalDown(personalJList.getSelectedValue());
        }
    }

    private void moveQuestionUp() {
        int index = questionsJList.getSelectedIndex();
        if (index > 0) {
            Question question = questionsModel.get(index);
            questionsModel.set(index, questionsModel.get(index - 1));
            questionsModel.set(index - 1, question);

            questionsJList.setSelectedIndex(index - 1);
            questionsJList.ensureIndexIsVisible(index - 1);
            questionsJList.requestFocusInWindow();

            survey.moveQuestionUp(questionsJList.getSelectedValue());
        }
    }

    private void moveQuestionDown() {
        int index = questionsJList.getSelectedIndex();
        if (index < (questionsModel.size() - 1)) {
            Question question = questionsModel.get(index);
            questionsModel.set(index, questionsModel.get(index + 1));
            questionsModel.set(index + 1, question);

            questionsJList.setSelectedIndex(index + 1);
            questionsJList.ensureIndexIsVisible(index + 1);
            questionsJList.requestFocusInWindow();

            survey.moveQuestionDown(questionsJList.getSelectedValue());
        }
    }

    private void saveSurvey() {
        if (titleField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Title cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        survey.setTitle(titleField.getText());
        survey.setDescription(descriptionField.getText());
        survey.setIntroduction(introductionField.getText());
        survey.setGoodbye(goodbyeField.getText());

        if (!surveys.stream()
                .anyMatch(s -> s.getSurveyID() == survey.getSurveyID())){
            surveys.add(survey);
        }

        Collections.sort(surveys, Comparator.comparing(Survey::getTitle));
        surveysModel.clear();
        surveys.forEach(surveysModel::addElement);
        serializer.saveSurvey(survey);
        JOptionPane.showMessageDialog(this, SAVED_SURVEY_MESSAGE);
        dispose();
    }

    private void deleteFormField() {
        int index = personalJList.getSelectedIndex();
        personalModel.remove(index);
        survey.deleteFormField(index);

        personalJList.setSelectedIndex(index);
        personalJList.ensureIndexIsVisible(index);
        personalJList.requestFocusInWindow();

        if (personalModel.size() < 1) {
            personalUpButton.setEnabled(false);
            personalDownButton.setEnabled(false);
        }
    }

    private void deleteQuestion() {
        int index = questionsJList.getSelectedIndex();
        questionsModel.remove(index);
        survey.deleteQuestion(index);

        questionsJList.setSelectedIndex(index);
        questionsJList.ensureIndexIsVisible(index);
        questionsJList.requestFocusInWindow();

        if (questionsModel.size() < 1) {
            questionUpButton.setEnabled(false);
            questionDownButton.setEnabled(false);
        }
    }
}
