package Source;

import Entities.Response;
import Entities.Survey;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

public class Serializer {
    private String path;
    private String surveysPath;

    public Serializer() {
        this.path = System.getProperty("user.dir");
        this.surveysPath = path + "/surveys";
    }

    public void saveSurvey(Survey survey) {
        serializeSurvey(survey, surveysPath + "/" + survey.getSurveyID() + ".xml");
    }

    public void exportSurvey(Survey survey, String path) {
        serializeSurvey(survey, path + "/" + survey.getTitle() + ".xml");
    }

    public Survey importSurvey(String path) {
        Survey survey = deserializeSurvey(new File(path));
        saveSurvey(survey);

        return survey;
    }

    private void serializeSurvey(Survey survey, String path) {
        try {
            FileOutputStream fileOutput = new FileOutputStream(path);
            XMLEncoder xmlEncoder = new XMLEncoder(new BufferedOutputStream(fileOutput));
            xmlEncoder.writeObject(survey);
            xmlEncoder.close();
            fileOutput.close();
        } catch (FileNotFoundException fileNotFound) {
            System.out.println("ERROR creating or accessing file " + path);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public Survey deserializeSurvey(File file) {
        Survey survey = new Survey();
        try {
            FileInputStream fileInput = new FileInputStream(file);
            XMLDecoder xmlDecoder = new XMLDecoder(new BufferedInputStream(fileInput));
            survey = (Survey) xmlDecoder.readObject();
            xmlDecoder.close();
            fileInput.close();
        } catch (FileNotFoundException fileNotFound) {
            System.out.println("ERROR creating or accessing the XML file");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return survey;
    }

    public void deleteSurvey(Survey survey) {
        File surveyFile = new File(surveysPath + "/" + survey.getSurveyID() + ".xml");
        surveyFile.delete();
    }

    public void serializeResponse(Response response) {
        String filePath = surveysPath + "/" +
                + response.getSurveyID() + "/" +  response.getResponseID() + ".xml";
        try {
            FileOutputStream fileOutput = new FileOutputStream(filePath);
            XMLEncoder xmlEncoder = new XMLEncoder(new BufferedOutputStream(fileOutput));
            xmlEncoder.writeObject(response);
            xmlEncoder.close();
            fileOutput.close();
        } catch (FileNotFoundException fileNotFound) {
            System.out.println("ERROR creating or accessing file " + filePath);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public Response deserializeResponse(File file) {
        Response response = new Response();
        try {
            FileInputStream fileInput = new FileInputStream(file);
            XMLDecoder xmlDecoder = new XMLDecoder(new BufferedInputStream(fileInput));
            response = (Response) xmlDecoder.readObject();
            xmlDecoder.close();
            fileInput.close();
        } catch (FileNotFoundException fileNotFound) {
            System.out.println("ERROR creating or accessing the XML file");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return response;
    }

    public void exportResponseCSV(List<Response> responses, String path) {
        try (Writer writer  = new FileWriter(path)) {
            List<String> headers = responses.stream()
                    .flatMap(response -> response.getAnswers()
                            .keySet().stream())
                    .distinct()
                    .collect(Collectors.toList());

            writer.append(String.join(",", headers)).append("\n");

            for (Response response : responses) {
                for (String key : headers) {
                    writer.append(response.getAnswers()
                            .getOrDefault(key, "")).append(",");
                }
                writer.append("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
