import Entities.Survey;
import Source.Serializer;
import UI.Dashboard;
import com.formdev.flatlaf.FlatLightLaf;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Main {
    private static Serializer serializer;
    private static List<Survey> surveys;

    public static void main(String[] args) {
        FlatLightLaf.setup();

        serializer = new Serializer();
        surveys = new ArrayList<>();

        File surveysFolder = new File(
                System.getProperty("user.dir") + "/surveys");
        if (!surveysFolder.exists()) {
            surveysFolder.mkdir();
        }

        for (File file : surveysFolder.listFiles()) {
            if (file.isFile()) {
                surveys.add(serializer.deserializeSurvey(file));
            }
        }

        Collections.sort(surveys, Comparator.comparing(Survey::getTitle));
        new Dashboard(serializer, surveys);
    }
}