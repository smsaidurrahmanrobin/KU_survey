package UI;

import Entities.PersonalField;
import Entities.Survey;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class FormFieldEditor extends JFrame {
    private Survey survey;
    private PersonalField personalField;
    private DefaultListModel<PersonalField> personalFormModel;

    private JPanel mainPanel;
    private JPanel buttonsPanel;

    private JLabel titleLabel;

    private JTextField titleField;
    private JCheckBox mandatoryCheckBox;

    private JButton clearButton;
    private JButton saveButton;

    public FormFieldEditor(
            Survey survey,
            DefaultListModel<PersonalField> personalFormModel
    ) {
        super("Create Personal Form Field");
        this.survey = survey;
        this.personalField = new PersonalField();
        this.personalFormModel = personalFormModel;
        newFormFieldEditor();
    }

    public FormFieldEditor(
            Survey survey,
            DefaultListModel<PersonalField> personalFormModel,
            PersonalField personalField
    ) {
        super("Edit Personal Form Field");
        this.survey = survey;
        this.personalFormModel = personalFormModel;
        this.personalField = personalField;
        newFormFieldEditor();
    }

    private void newFormFieldEditor() {
        setSize(520, 180);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // setting
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.weightx = 0.1;
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        titleLabel = new JLabel("Title");
        mainPanel.add(titleLabel,gbc);

        gbc.gridy = 1;
        gbc.gridheight = 2;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        titleField = new JTextField(50);
        if (personalField.getTitle() != null) {
            titleField.setText(personalField.getTitle());
        }

        mainPanel.add(titleField,gbc);

        gbc.gridy = 2;
        gbc.gridheight = 2;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        mandatoryCheckBox = new JCheckBox("Mandatory");
        mandatoryCheckBox.setSelected(personalField.isMandatory());
        mainPanel.add(mandatoryCheckBox);

        buttonsPanel = new JPanel();

        saveButton = new JButton("Save");
        saveButton.addActionListener(e -> saveFormField());
        buttonsPanel.add(saveButton);

        clearButton = new JButton("Clear");
        clearButton.addActionListener(e -> clearFields());
        buttonsPanel.add(clearButton);

        add(mainPanel, BorderLayout.CENTER);
        add(buttonsPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void saveFormField() {
        int index = personalFormModel.indexOf(personalField);
        if (titleField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Title cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (personalFormModel.contains(personalField)){
            personalFormModel.removeElement(personalField);
        } else {
            personalField.initializeData();
        }

        personalField.setTitle(titleField.getText());
        personalField.setMandatory(mandatoryCheckBox.isSelected());

        if (index != -1) {
            personalFormModel.add(index, personalField);
            survey.updatePersonalField(index, personalField);
        }
        else {
            personalFormModel.addElement(personalField);
            survey.addFormField(personalField);
        }

        dispose();
    }

    private void clearFields() {

        titleField.setText("");
        mandatoryCheckBox.setSelected(false);
    }

//    test
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> {
//            new FormFieldEditor(new Survey(), new DefaultListModel<>()).setVisible(true);
//        });
//    }
}
