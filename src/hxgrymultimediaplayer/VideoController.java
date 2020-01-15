/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hxgrymultimediaplayer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author haoxi
 */
public class VideoController extends AbstractModel implements Initializable, PropertyChangeListener {

//==================record data====================//    
    
    @FXML
    private TextArea lapField;    

    public boolean statusOne = false;
    public boolean statusTwo = false;

    VideorecordModel videorecordModel;
    
    @FXML
    private Button lapButton;    


//=====================video=====================//
    @FXML
    private Label label;
    @FXML
    private MediaView mediaView;
    @FXML
    private Text lengthText;
    @FXML
    private Text currentText;
    @FXML
    private Text errorText;
    @FXML
    private Slider timeSlider2;
    @FXML
    private Button playPause;
    private Media media;
    private MediaPlayer mediaPlayer;
    private final Double updateInterval = 0.05;
    
    //=======================swtich scene=================//
    private Stage stage;
    public Scene musicScene;
    public MusicController musicController;
    

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //======record data====
        lapField.setText(" No.    Current Time    Time since Start\n");
        lapField.setEditable(false);
        videorecordModel = new VideorecordModel();
        videorecordModel.setupMonitor();
        videorecordModel.addPropertyChangeListener(this);
    }
         
    private void openMedia(File file) {
        errorText.setText("");
        if (mediaPlayer != null) {
            mediaPlayer.dispose();
        }

        try {
            media = new Media(file.toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            mediaView.setMediaPlayer(mediaPlayer);
            mediaPlayer.setOnReady(() -> {
                handleReady();
            });
            mediaPlayer.setOnEndOfMedia(() -> {
                handleEndOfMedia();
            });
            //mediaPlayer.setAudioSpectrumNumBands(numOfBands);
            mediaPlayer.setAudioSpectrumInterval(updateInterval);
            mediaPlayer.setAudioSpectrumListener((double timestamp, double duration, float[] magnitudes, float[] phases) -> {
                handleVisualize(timestamp, duration, magnitudes, phases);
            });
            mediaPlayer.setAutoPlay(false);
            //filePathText.setText(file.getPath());
        } catch (Exception ex) {
            errorText.setText(ex.toString());
        }
        
        
    }

    private void handleReady() {
        Duration duration = mediaPlayer.getTotalDuration();
        System.out.println(duration);
        lengthText.setText(duration.toString());
        Duration ct = mediaPlayer.getCurrentTime();
        currentText.setText(ct.toString());
        
        timeSlider2.setMin(0);
        timeSlider2.setMax(duration.toMillis());
    }

    private void handleEndOfMedia() {
        mediaPlayer.stop();
        mediaPlayer.seek(Duration.ZERO);
        timeSlider2.setValue(0);
    }

    private void handleVisualize(double timestamp, double duration, float[] magnitudes, float[] phases) {
        Duration ct = mediaPlayer.getCurrentTime();
        double ms = ct.toMillis();
        currentText.setText(String.format("%.1f ms", ms));
        timeSlider2.setValue(ms);
    }

    @FXML
    private void handleOpen2(Event event) {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            openMedia(file);
        }
    }

    @FXML
    private void handlePlayPause2(ActionEvent event) {
        if (mediaPlayer != null) {
            if (!isPlaying()) {
                play(); 
            } else {
                pause(); 
            }
        }
        else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText("There was an error playing the Video.");
            alert.setContentText("You must select a Video first before I can play it for you.");

            alert.showAndWait();
        }
        
        //======record table========//
        if (!statusOne) {
            statusOne = true;
        if(!(videorecordModel.isTimerRunning())){ 
            statusTwo = true;
            videorecordModel.start();
            lapButton.setText("Record");
            lapButton.setStyle("-fx-base: #eac381");
            }
        }
      else{ 
            statusOne = false;
            videorecordModel.pause();
            lapButton.setText("Reset");
            lapButton.setStyle("-fx-base: #eddede");
        }
    }
    
    public void play(){
        if(mediaPlayer != null){
            mediaPlayer.play(); 
            playPause.setText("Pause");
        }   
    }
    
    public void pause(){
        if(mediaPlayer != null){
            mediaPlayer.pause();
            playPause.setText("Play");
        }
    }
    
    public void stop(){
        if(mediaPlayer != null){
            mediaPlayer.stop();
            playPause.setText("Play"); 
        }
    }
    
    @FXML
    private void handleStop(ActionEvent event) {
        stop(); 
    }

    @FXML
    private void handleSliderMousePressed2(Event event) {
        pause(); 
    }

    @FXML
    private void handleSliderMouseReleased2(Event event) {
        if (mediaPlayer != null) {
            mediaPlayer.seek(new Duration(timeSlider2.getValue()));
            System.out.println(timeSlider2.getValue());
            play(); 
        }
    }
    public boolean isPlaying(){
        if(mediaPlayer != null){
            if(mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING){
                return true; 
            }
        }
        return false; 
    }
    
    //========================= record  table ===========================//
     @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("videolapIndex")) {
            lapField.appendText("\n"+"    "+evt.getNewValue());
        }else if(evt.getPropertyName().equals("videolapEnd")){
            Duration ct = mediaPlayer.getCurrentTime();
            double ms = ct.toMillis();
            String rounded = String.format("%.0f", ms);
            lapField.appendText("             "+ rounded + "ms");
            lapField.appendText("             "+ evt.getNewValue());
       }
    }
    
    @FXML
    public void lapButtonAction(){
        if(statusTwo){
            if (!statusOne) {
                stopTheClock();
            }
            else{
              videorecordModel.setuplap();
            }
        }
    }
    
    public void stopTheClock(){
        videorecordModel.stop();
        statusTwo =false;
        lapButton.setText("Record");
        lapButton.setStyle("-fx-base: #eac381");
        cleanTable();
    }
    
    
    
    @FXML
    public void startButtonAction(){
        if (!statusOne) {
            statusOne = true;
            if(!(videorecordModel.isTimerRunning())){ 
                statusTwo = true;
                videorecordModel.start();
                lapButton.setText("Record");
                lapButton.setStyle("-fx-base: #eac381");
            }
        }else{ 
           statusOne = false;
           videorecordModel.pause();
           lapButton.setText("Reset");
           lapButton.setStyle("-fx-base: #eddede");
        }
    }
    
    public void cleanTable(){
        videorecordModel.cleanLap();
        lapField.setText(" No.    Current Time    Time since Start\n");
    }
    
    @FXML 
    public void saveFile(){
        Object newValue = lapField.getText();
        FileChooser fileChooser = new FileChooser(); 
        File file = fileChooser.showSaveDialog(stage); 
        FileWriter writer = null; 
        if(file!=null){
        try {
            writer = new FileWriter(file);
            writer.write(lapField.getText());
        } catch (IOException ex) {
            Logger.getLogger(MusicController.class.getName()).log(Level.SEVERE, null, ex);
        }catch(Exception ex){
            displayExceptionAlert(ex);}
        finally{
        if(writer != null){
            try {
               writer.close();
            } catch (IOException ex) {
                displayExceptionAlert(ex);
            }
        }
        }}
        firePropertyChange("timeContent", null, newValue);
    }
    
    private void displayExceptionAlert(Exception ex){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Exception Dialog");
        alert.setHeaderText("Exception!");
        alert.setContentText(ex.getMessage());
        
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("The exception stacktrace was:");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        alert.getDialogPane().setExpandableContent(expContent);

        alert.showAndWait(); 
    }
    
    //=========================== switch scene=========================
    public void start(Stage stage) {
        System.out.println("Page2Controller started!");
        this.stage = stage; 
    }
    
    @FXML
    private void goBackToPage1(ActionEvent event) {
       stage.setScene(musicScene);
    }
}
