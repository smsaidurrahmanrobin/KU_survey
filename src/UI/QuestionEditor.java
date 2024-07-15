package UI;

import Entities.*;
import Entities.Question.Version;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class QuestionEditor extends JFrame {
    private Survey survey;
    private Question question;
    private DefaultListModel<Question> questionsModel;
    private DefaultListModel<String> optionsModel;

    private JPanel mainPanel;
    private JPanel answerTypePanel;
    private JPanel titlePanel;
    private JPanel freeTextPanel;
    private JPanel selectPanel;
    private JPanel scalePanel;
    private JPanel buttonsPanel;

    private JPanel versionPanel;

    private CardLayout answerCardLayout;

    private JLabel titleLabel;
    private JLabel bodyLabel;
    private JLabel minCharactersLabel;
    private JLabel maxCharactersLabel;
    private JLabel minSelectedLabel;
    private JLabel maxSelectedLabel;
    private JLabel minValueLabel;
    private JLabel maxValueLabel;

    private  JLabel versionLabel;

    private JTextField titleField;
    private JCheckBox mandatoryCheckBox;
    private JRadioButton versionARadioButton;
    private JRadioButton versionBRadioButton;
    private JTextArea bodyTextArea;
    private JTextField minCharactersField;
    private JTextField maxCharactersField;
    private JTextField minSelectedField;
    private JTextField maxSelectedField;
    private JTextField minValueField;
    private JTextField maxValueField;

    private JComboBox answerTypesCombo;
    private JList optionsJList;

    private JButton addOptionButton;
    private JButton deleteOptionButton;
    private JButton clearButton;
    private JButton saveButton;

//    public enum Version { VERSION_A, VERSION_B }


    public QuestionEditor(
            Survey survey,
            DefaultListModel<Question> questionsModel
    ) {
        super("Create Question");
        this.survey = survey;
        this.question = new Question();
        this.questionsModel = questionsModel;
        newQuestionEditor();
    }

    public QuestionEditor(
            Survey survey,
            DefaultListModel<Question> questionsModel,
            Question question
    ) {
        super("Edit Question");
        this.survey = survey;
        this.questionsModel = questionsModel;
        this.question = question;
        newQuestionEditor();
    }

    private void updateAnswerType() {
        if (answerTypesCombo.getSelectedItem() == AnswerType.FREETEXT) {
            answerCardLayout.show(answerTypePanel, "FreeText");
        }
        else if (answerTypesCombo.getSelectedItem() == AnswerType.SELECT) {
            answerCardLayout.show(answerTypePanel, "Select");
        }
        else if (answerTypesCombo.getSelectedItem() == AnswerType.SCALE) {
            answerCardLayout.show(answerTypePanel, "Scale");
        }
    }

    private void newQuestionEditor() {
        setSize(520, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // setting
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.weightx = 0.1;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        // Title
        titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titleLabel = new JLabel("Title");
        titleField = new JTextField(30);
        if (question.getTitle() != null) {
            titleField.setText(question.getTitle());
        }
        mandatoryCheckBox = new JCheckBox("Mandatory");
        mandatoryCheckBox.setSelected(question.isMandatory());
        titlePanel.add(titleLabel);
        titlePanel.add(titleField);
        titlePanel.add(mandatoryCheckBox);
        gbc.gridy = 0;
        mainPanel.add(titlePanel, gbc);

        // Version Panel
        versionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        versionLabel = new JLabel("Version:");
        versionARadioButton = new JRadioButton("Version A");
        versionBRadioButton = new JRadioButton("Version B");
        ButtonGroup versionGroup = new ButtonGroup();
        versionGroup.add(versionARadioButton);
        versionGroup.add(versionBRadioButton);


        // Set initial selection based on the question's current version
        if (question.getVersion() != null) {
            if (question.getVersion() == Version.VERSION_A) {
                versionARadioButton.setSelected(true);
            } else if (question.getVersion() == Version.VERSION_B) {
                versionBRadioButton.setSelected(true);
            }
        } else {
            // default Version A
            versionARadioButton.setSelected(true);
            question.setVersion(Version.VERSION_A);
        }


        versionPanel.add(versionLabel);
        versionPanel.add(versionARadioButton);
        versionPanel.add(versionBRadioButton);
        gbc.gridy = 1;  // Assuming this follows immediately after the Title section
        mainPanel.add(versionPanel, gbc);


        // Body
        bodyLabel = new JLabel("Body");
        bodyTextArea = new JTextArea(6, 20);
        if (question.getBody() != null) {
            bodyTextArea.setText(question.getBody());
        }
        bodyTextArea.setLineWrap(true);
        bodyTextArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(bodyTextArea);
        JPanel bodyPanel = new JPanel(new BorderLayout());
        bodyPanel.add(bodyLabel, BorderLayout.NORTH);
        bodyPanel.add(scrollPane, BorderLayout.CENTER);
        gbc.gridy = 2;
        gbc.gridheight = 2;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(bodyPanel, gbc);

        // Reset grid height and weight
        gbc.gridheight = 1;
        gbc.weighty = 0;

        // Answer Types
        answerTypesCombo = new JComboBox(AnswerType.values());
        answerTypesCombo.addActionListener(e -> updateAnswerType());
        gbc.gridy = 4;
        mainPanel.add(answerTypesCombo, gbc);
        gbc.gridy = 5;
        mainPanel.add(new JSeparator(SwingConstants.HORIZONTAL), gbc);

        // Answer Type Panel
        answerCardLayout = new CardLayout();
        answerTypePanel = new JPanel(answerCardLayout);
        configureAnswerTypePanels();
        gbc.gridy = 6;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(answerTypePanel, gbc);

        // Buttons
        buttonsPanel = new JPanel();
        clearButton = new JButton("Clear");
        saveButton = new JButton("Save");
        clearButton.addActionListener(e -> clearFields());
        saveButton.addActionListener(e -> saveQuestion());
        buttonsPanel.add(saveButton);
        buttonsPanel.add(clearButton);

        gbc.gridy = 7;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(buttonsPanel, gbc);

        add(mainPanel, BorderLayout.CENTER);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void configureAnswerTypePanels() {
        // FreeText Panel
        freeTextPanel = new JPanel(new GridLayout(5, 2));
        minCharactersLabel = new JLabel("Min. characters:");
        minCharactersField = new JTextField(10);
        maxCharactersLabel = new JLabel("Max. characters:");
        maxCharactersField = new JTextField(10);
        freeTextPanel.add(minCharactersLabel);
        freeTextPanel.add(minCharactersField);
        freeTextPanel.add(maxCharactersLabel);
        freeTextPanel.add(maxCharactersField);
        answerTypePanel.add(freeTextPanel, "FreeText");

        // Select Panel
        selectPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc1 = new GridBagConstraints();

        gbc1.fill = GridBagConstraints.BOTH;
        gbc1.insets = new Insets(4, 4, 4, 4);

        gbc1.gridx = 0;
        gbc1.gridy = 0;
        minSelectedLabel = new JLabel("Min. options:");
        selectPanel.add(minSelectedLabel, gbc1);

        gbc1.gridx = 1;
        gbc1.gridy = 0;
        minSelectedField = new JTextField(10);
        selectPanel.add(minSelectedField, gbc1);

        gbc1.gridx = 2;
        gbc1.gridy = 0;
        addOptionButton = new JButton("Add option");
        selectPanel.add(addOptionButton, gbc1);

        gbc1.gridx = 0;
        gbc1.gridy = 1;
        maxSelectedLabel = new JLabel("Max. options:");
        selectPanel.add(maxSelectedLabel, gbc1);

        gbc1.gridx = 1;
        gbc1.gridy = 1;
        maxSelectedField = new JTextField(10);
        selectPanel.add(maxSelectedField, gbc1);

        gbc1.gridx = 2;
        gbc1.gridy = 1;

        deleteOptionButton = new JButton("Delete option");
        selectPanel.add(deleteOptionButton, gbc1);
        deleteOptionButton.setEnabled(false);

        gbc1.gridx = 0;
        gbc1.gridy = 2;
        gbc1.gridwidth = 3;
        optionsModel = new DefaultListModel<>();
        optionsJList = new JList<>(optionsModel);
        JScrollPane listScrollPane = new JScrollPane(optionsJList);
        selectPanel.add(listScrollPane, gbc1);

        answerTypePanel.add(selectPanel, "Select");

        optionsJList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && optionsJList.getSelectedIndex() != -1) {
                    int index = optionsJList.getSelectedIndex();
                    String currentItem = optionsModel.getElementAt(index);
                    String newOption = JOptionPane.showInputDialog("Edit Option", currentItem);
                    if (newOption != null && !newOption.isEmpty()) {
                        optionsModel.setElementAt(newOption, index);
                    }
                }
            }
        });

        // Scale Panel
        scalePanel = new JPanel(new GridLayout(5, 2));
        minValueLabel = new JLabel("Min. value:");
        minValueField = new JTextField(10);
        maxValueLabel = new JLabel("Max. value:");
        maxValueField = new JTextField(10);
        scalePanel.add(minValueLabel);
        scalePanel.add(minValueField);
        scalePanel.add(maxValueLabel);
        scalePanel.add(maxValueField);
        answerTypePanel.add(scalePanel, "Scale");

        if (question.getAnswer() != null) {
            Answer answer = question.getAnswer();
            AnswerType type = answer.getType();
            answerTypesCombo.setSelectedItem(type);

            if (type == AnswerType.FREETEXT) {
                minCharactersField.setText(String.valueOf(answer.getMinValue()));
                maxCharactersField.setText(String.valueOf(answer.getMaxValue()));
            }
            else if (type == AnswerType.SELECT) {
                minSelectedField.setText(String.valueOf(answer.getMinValue()));
                maxSelectedField.setText(String.valueOf(answer.getMaxValue()));

                Select select = (Select) answer;
                optionsModel.addAll(select.getOptions());
            }
            else if (type == AnswerType.SCALE) {
                minValueField.setText(String.valueOf(answer.getMinValue()));
                maxValueField.setText(String.valueOf(answer.getMaxValue()));
            }
        }

        addOptionButton.addActionListener(e -> {
            String option = JOptionPane.showInputDialog(this, "Enter new option:");
            if (option != null && !option.isEmpty()) {
                optionsModel.addElement(option);
            }
        });

        deleteOptionButton.addActionListener(e -> {
            int selectedIndex = optionsJList.getSelectedIndex();
            if (selectedIndex != -1) {
                optionsModel.remove(selectedIndex);
            }
        });

        optionsJList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {

                if (!e.getValueIsAdjusting() && optionsJList.getSelectedIndex() != -1) {
                    deleteOptionButton.setEnabled(true);
                } else {
                    deleteOptionButton.setEnabled(false);
                }
            }
        });
    }

    private void saveQuestion() {
//        title empty
        if (titleField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Title cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int index = questionsModel.indexOf(question);
        if (questionsModel.contains(question)) {
            questionsModel.removeElement(question);
        }
        else {
            question.initializeData();
        }

        question.setTitle(titleField.getText());
        question.setMandatory(mandatoryCheckBox.isSelected());
        if (versionARadioButton.isSelected()) {
            question.setVersion(Version.VERSION_A);
        } else if (versionBRadioButton.isSelected()) {
            question.setVersion(Version.VERSION_B);
        }


        question.setBody(bodyTextArea.getText());
        Answer answerType = null;
        if (answerTypesCombo.getSelectedItem() == AnswerType.FREETEXT) {
            if (minCharactersField.getText().trim().isEmpty()||maxCharactersField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Characters cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int minCharacters = Integer.parseInt(minCharactersField.getText());
            int maxCharacters = Integer.parseInt(maxCharactersField.getText());
            answerType = new FreeText(minCharacters, maxCharacters);
        }
        else if (answerTypesCombo.getSelectedItem() == AnswerType.SELECT) {
            if (minSelectedField.getText().trim().isEmpty()||maxSelectedField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Options cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int minSelected = Integer.parseInt(minSelectedField.getText());
            int maxSelected = Integer.parseInt(maxSelectedField.getText());
            List options = IntStream
                    .range(0, optionsJList.getModel().getSize())
                    .mapToObj(optionsJList.getModel()::getElementAt)
                    .collect(Collectors.toList());
            answerType = new Select(minSelected, maxSelected, options);
        }
        else if (answerTypesCombo.getSelectedItem() == AnswerType.SCALE) {
            if (minValueField.getText().trim().isEmpty()||minValueField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Values cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int minValue = Integer.parseInt(minValueField.getText());
            int maxValue = Integer.parseInt(maxValueField.getText());
            answerType = new Scale(minValue, maxValue);
        }
        question.setAnswer(answerType);

        if (index != -1) {
            questionsModel.add(index, question);
            survey.updateQuestion(index, question);
        }
        else {
            questionsModel.addElement(question);
            survey.addQuestion(question);
        }

        dispose();
    }

    private void clearFields() {
        titleField.setText("");
        bodyTextArea.setText("");
        mandatoryCheckBox.setSelected(false);
    }
}
