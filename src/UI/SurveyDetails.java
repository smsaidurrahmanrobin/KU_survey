package UI;

import Entities.PersonalField;
import Entities.Question;
import Entities.Survey;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;

public class SurveyDetails extends JFrame {
    private Survey survey;
    private DefaultListModel<PersonalField> personalModel;
    private DefaultListModel<Question> questionsModel;

    private CardLayout cardLayout;

    private JPanel infoPanel;
    private JPanel centerPanel;
    private JPanel topPanel;
    private JPanel mainPanel;
    private JPanel surveyInfoPanel;
    private JScrollPane personalPane;
    private JScrollPane questionsPane;
    private JPanel bottomPanel;

    private JTextArea introduction;
    private JTextArea goodbye;
    private JList personalJList;
    private JList questionsJList;

    private JButton surveyInfoButton;
    private JButton personalButton;
    private JButton questionsButton;
    private JButton closeButton;

    public SurveyDetails(Survey survey) {
        super("Details - " + survey.getTitle());
        setSize(480, 480);
        setLayout(new BorderLayout());
        this.survey = survey;

        infoPanel = new JPanel(new GridLayout(3, 1));
        infoPanel.add(new JLabel("Title: " + survey.getTitle()));
        infoPanel.add(new JLabel("Description: " + survey.getDescription()));
        infoPanel.add(new JLabel("Creation Date: " +
                new SimpleDateFormat("dd-MM-yyyy HH:mm")
                        .format(survey.getCreationDate())));

        centerPanel = new JPanel(new BorderLayout());

        topPanel = new JPanel(new GridLayout(1, 3));
        surveyInfoButton = new JButton("Survey Information");
        surveyInfoButton.addActionListener(e ->
                cardLayout.show(mainPanel, "Survey Information"));

        personalButton = new JButton("Personal Form Fields");
        personalButton.addActionListener(e ->
                cardLayout.show(mainPanel, "Personal Form Pane"));

        questionsButton = new JButton("Questions");
        questionsButton.addActionListener(e ->
                cardLayout.show(mainPanel, "Questions Pane"));

        topPanel.add(surveyInfoButton);
        topPanel.add(personalButton);
        topPanel.add(questionsButton);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        surveyInfoPanel = new JPanel(new GridLayout(2, 1));
        introduction = new JTextArea(survey.getIntroduction());
        introduction.setEnabled(false);
        goodbye = new JTextArea(survey.getGoodbye());
        goodbye.setEnabled(false);

        surveyInfoPanel.add(introduction);
        surveyInfoPanel.add(goodbye);

        personalPane = new JScrollPane();
        personalModel = new DefaultListModel<>();
        survey.getPersonalFields().values()
                .stream().forEach(personalModel::addElement);
        personalJList = new JList<>(personalModel);
        personalPane = new JScrollPane(personalJList);

        questionsModel = new DefaultListModel<>();
        survey.getQuestions().values()
                .stream().forEach(questionsModel::addElement);
        questionsJList = new JList<Question>(questionsModel);
        questionsPane = new JScrollPane(questionsJList);

        mainPanel.add(surveyInfoPanel, "Survey Information");
        mainPanel.add(personalPane, "Personal Form Pane");
        mainPanel.add(questionsPane, "Questions Pane");

        bottomPanel = new JPanel(new FlowLayout());
        closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        bottomPanel.add(closeButton);

        centerPanel.add(topPanel, BorderLayout.NORTH);
        centerPanel.add(mainPanel, BorderLayout.CENTER);

        add(infoPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
