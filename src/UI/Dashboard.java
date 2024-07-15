package UI;

import Entities.Response;
import Entities.Survey;
import Source.Serializer;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class Dashboard extends JFrame {
    private Serializer serializer;
    private List<Survey> surveys;
    private JList<Survey> surveysJList;
    private DefaultListModel<Survey> surveysModel;

    private JPanel buttonsPanel;

    private JButton createButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton startButton;
    private JButton exportResponsesButton;
    private JButton importButton;
    private JButton exportButton;

    public Dashboard(Serializer serializer, List<Survey> surveys) {
        super("KUSurvey");
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        this.serializer = serializer;
        this.surveys = surveys;

        surveysModel = new DefaultListModel<>();
        this.surveys.forEach(surveysModel::addElement);

        surveysJList = new JList<>(surveysModel);
        surveysJList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    Survey selectedSurvey = surveysJList.getSelectedValue();
                    new SurveyDetails(selectedSurvey);
                }
            }
        });
        add(new JScrollPane(surveysJList), BorderLayout.CENTER);

        buttonsPanel = new JPanel();

        createButton = new JButton("Add New Survey");
        createButton.addActionListener(e ->
            new SurveyEditor(this.serializer, this.surveys, surveysModel));
        buttonsPanel.add(createButton);

        editButton = new JButton("Edit Survey");
        editButton.addActionListener(e ->
                new SurveyEditor(this.serializer, this.surveys, surveysModel,
                        surveysJList.getSelectedValue()));
        buttonsPanel.add(editButton);

        deleteButton = new JButton("Delete Survey");
        deleteButton.addActionListener(e -> deleteSurvey());
        buttonsPanel.add(deleteButton);

        startButton = new JButton("Start Survey");
        startButton.addActionListener(e -> startSurvey());
        buttonsPanel.add(startButton);

        exportResponsesButton = new JButton("Export Responses");
        exportResponsesButton.addActionListener(e -> exportResponses());
        buttonsPanel.add(exportResponsesButton);

        importButton = new JButton("Import Survey");
        importButton.addActionListener(e -> importSurvey());
        buttonsPanel.add(importButton);

        exportButton = new JButton("Export Survey");
        exportButton.addActionListener(e -> exportSurvey());
        buttonsPanel.add(exportButton);

        add(buttonsPanel, BorderLayout.SOUTH);
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
        startButton.setEnabled(false);
        exportButton.setEnabled(false);
        exportResponsesButton.setEnabled(false);

        surveysJList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && surveysJList.getSelectedIndex() != -1) {
                    editButton.setEnabled(true);
                    deleteButton.setEnabled(true);
                     if (surveysJList.getSelectedValue().getQuestions().size() > 0) {
                         startButton.setEnabled(true);
                     }
                    exportButton.setEnabled(true);
                    exportResponsesButton.setEnabled(true);
                } else {
                    editButton.setEnabled(false);
                    deleteButton.setEnabled(false);
                    startButton.setEnabled(false);
                    exportButton.setEnabled(false);
                    exportResponsesButton.setEnabled(false);
                }
            }
        });
        setVisible(true);
    }

    private void deleteSurvey() {
        surveys.remove(surveysJList.getSelectedValue());
        serializer.deleteSurvey(surveysJList.getSelectedValue());
        surveysModel.remove(surveysJList.getSelectedIndex());
    }

    private void startSurvey() {
        Survey selectedSurvey = surveysJList.getSelectedValue();
        if (selectedSurvey != null) {
            new SurveyPlayer(serializer, selectedSurvey);
        }
        else {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a survey to start.",
                    "No Survey Selected",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }

    private void exportResponses() {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle("Choose Folder");
        chooser.setApproveButtonText("Export");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);

        Survey selectedSurvey = surveysJList.getSelectedValue();
        File responsesFolder = new File( System.getProperty("user.dir")
                + "/surveys/" + selectedSurvey.getSurveyID()
        );

        if (responsesFolder.exists()) {
            List<Response> responses = new LinkedList<>();
            for (File file : responsesFolder.listFiles()) {
                if (file.isFile()) {
                    responses.add(serializer.deserializeResponse(file));
                }
            }

            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                String filePath = chooser.getSelectedFile().getAbsolutePath() + "/"
                        + selectedSurvey.getTitle() + "_Responses.csv";
                serializer.exportResponseCSV(responses, filePath);
            }
        }
        else {
            JOptionPane.showMessageDialog(this, "The survey has no answers", "Error",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void importSurvey() {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle("Choose Folder");
        chooser.setApproveButtonText("Import");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);

        FileNameExtensionFilter xmlfilter = new FileNameExtensionFilter("xml files (*.xml)", "xml");
        chooser.setFileFilter(xmlfilter);

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            String filePath = chooser.getSelectedFile().getAbsolutePath();
            Survey survey = serializer.importSurvey(filePath);
            surveysModel.addElement(survey);

            List<Survey> list = Collections.list(surveysModel.elements());
            Collections.sort(list, Comparator.comparing(Survey::getTitle));
            surveysModel.removeAllElements();
            surveysModel.addAll(list);
        }
    }

    private void exportSurvey() {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle("Choose Folder");
        chooser.setApproveButtonText("Export");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);

        Survey survey = surveysJList.getSelectedValue();

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            String folderPath = chooser.getSelectedFile().getAbsolutePath();
            serializer.exportSurvey(survey, folderPath);
        }
    }
}
