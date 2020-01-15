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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author haoxi
 */
public class MusicController extends AbstractModel implements Initializable, PropertyChangeListener {
    
//=============Record data=============//
    @FXML
    private TextArea lapField;   
    public boolean statusOne = false;
    public boolean statusTwo = false;

    MusicrecordModel musicrecordModel;

    @FXML
    private Button lapButton;
    
//==============   
    private Stage stage;
    private Scene musicScene;
    private Scene videoScene;
    private VideoController videoController;
    
    @FXML
    private Label label;
    
    // music play
    @FXML
    private AnchorPane vizPane;

    @FXML
    private Text lengthText;

    @FXML
    private Text currentText;

    @FXML
    private Text errorText;

    @FXML
    private Menu visualizersMenu;

    @FXML
    private Menu bandsMenu;

    @FXML
    private Slider timeSlider;

    @FXML
    private Button playPause;
    
    
    private Media media;
    private MediaPlayer mediaPlayer;

    private Integer numOfBands = 40;
    private final Double updateInterval = 0.05;

    private ArrayList<Visualizer> visualizers;
    private Visualizer currentVisualizer;
    private final Integer[] bandsList = {1, 2, 4, 8, 16, 40, 60, 100, 120};
    
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        visualizers = new ArrayList<>();
        visualizers.add(new MusicVisualizer());
        visualizers.add(new MusicVisulaizer2());
        
        for (Visualizer visualizer : visualizers) {
            MenuItem menuItem = new MenuItem(visualizer.getName());
            menuItem.setUserData(visualizer);
            menuItem.setOnAction((ActionEvent event) -> {
                selectVisualizer(event);
            });
            visualizersMenu.getItems().add(menuItem);
        }
        currentVisualizer = visualizers.get(0);

        for (Integer bands : bandsList) {
            MenuItem menuItem = new MenuItem(Integer.toString(bands));
            menuItem.setUserData(bands);
            menuItem.setOnAction((ActionEvent event) -> {
                selectBands(event);
            });
            bandsMenu.getItems().add(menuItem);
        }
        
        //======record data====
        lapField.setText(" No.    Current Time    Time since Start\n");
        lapField.setEditable(false);
        musicrecordModel = new MusicrecordModel();
        musicrecordModel.setupMonitor();
        musicrecordModel.addPropertyChangeListener(this);
    }    
    
    
    private void selectVisualizer(ActionEvent event) {
        MenuItem menuItem = (MenuItem) event.getSource();
        Visualizer visualizer = (Visualizer) menuItem.getUserData();
        changeVisualizer(visualizer);
    }

    private void selectBands(ActionEvent event) {
        MenuItem menuItem = (MenuItem) event.getSource();
        numOfBands = (Integer) menuItem.getUserData();
        if (currentVisualizer != null) {
            currentVisualizer.start(numOfBands, vizPane);
        }
        if (mediaPlayer != null) {
            mediaPlayer.setAudioSpectrumNumBands(numOfBands);
        }
    }

    private void changeVisualizer(Visualizer visualizer) {
        if (currentVisualizer != null) {
                currentVisualizer.end();
        }
        currentVisualizer = visualizer;
        currentVisualizer.start(numOfBands, vizPane);
        System.out.println(visualizer.getName());
    }

    private void openMedia(File file) {
        errorText.setText("");
        if (mediaPlayer != null) {
            mediaPlayer.dispose();
        }

        try {
            media = new Media(file.toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setOnReady(() -> {
                handleReady();
            });
            mediaPlayer.setOnEndOfMedia(() -> {
                handleEndOfMedia();
            });
            mediaPlayer.setAudioSpectrumNumBands(numOfBands);
            mediaPlayer.setAudioSpectrumInterval(updateInterval);
            mediaPlayer.setAudioSpectrumListener((double timestamp, double duration, float[] magnitudes, float[] phases) -> {
                handleVisualize(timestamp, duration, magnitudes, phases);
            });
            mediaPlayer.setAutoPlay(false);
        } catch (Exception ex) {
            errorText.setText(ex.toString());
        }
    }

    private void handleReady() {
        Duration duration = mediaPlayer.getTotalDuration();
        lengthText.setText(duration.toString());
        Duration ct = mediaPlayer.getCurrentTime();
        currentText.setText(ct.toString());
        currentVisualizer.start(numOfBands, vizPane);
        timeSlider.setMin(0);
        timeSlider.setMax(duration.toMillis());
    }

    private void handleEndOfMedia() {
        mediaPlayer.stop();
        mediaPlayer.seek(Duration.ZERO);
        timeSlider.setValue(0);
    }

    private void handleVisualize(double timestamp, double duration, float[] magnitudes, float[] phases) {
        Duration ct = mediaPlayer.getCurrentTime();
        double ms = ct.toMillis();
        currentText.setText(String.format("%.1f ms", ms));
        timeSlider.setValue(ms);
        currentVisualizer.draw(timestamp, duration, magnitudes, phases);
    }

    @FXML
    private void handleOpen(Event event) {
        Stage primaryStage = (Stage) vizPane.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(primaryStage);
        if (file != null) {
            openMedia(file);
        }
    }

    @FXML
    private void handlePlayPause(ActionEvent event) {
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
            alert.setHeaderText("There was an error playing the song.");
            alert.setContentText("You must select a song first before I can play it for you.");
            alert.showAndWait();
        }
        
        //======record table========//
        if (!statusOne) {
            statusOne = true;
        if(!(musicrecordModel.isTimerRunning())){ 
             statusTwo = true;
             musicrecordModel.start();
             lapButton.setText("Record");
             lapButton.setStyle("-fx-base: #eac381");
             }
         }
        else{ 
              statusOne = false;
              musicrecordModel.pause();
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
    private void handleSliderMousePressed(Event event) {
        pause(); 
    }

    @FXML
    private void handleSliderMouseReleased(Event event) {
        if (mediaPlayer != null) {
            mediaPlayer.seek(new Duration(timeSlider.getValue()));
            System.out.println(timeSlider.getValue());
            currentVisualizer.start(numOfBands, vizPane);
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
    

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        
        if (evt.getPropertyName().equals("lapIndex")) {
            lapField.appendText("\n"+"    "+evt.getNewValue());
       }else if(evt.getPropertyName().equals("lapEnd")){
            Duration ct = mediaPlayer.getCurrentTime();
            double ms = ct.toMillis();
            String rounded = String.format("%.0f", ms);
            lapField.appendText("             "+ rounded + "ms");
            lapField.appendText("             "+ evt.getNewValue());
       }
    }
    

    //============record table==========
    @FXML
    public void lapButtonAction(){
        if(statusTwo){
            if (!statusOne) {
                stopTheClock();
            }
            else{
                musicrecordModel.setuplap();
            }
        }
    }
    
    public void stopTheClock(){
        musicrecordModel.stop();
        statusTwo =false;
        lapButton.setText("Record");
        lapButton.setStyle("-fx-base: #eac381");
        cleanTable();
    }
    
    
    
    @FXML
    public void startButtonAction(){
     if (!statusOne) {
        statusOne = true;
        if(!(musicrecordModel.isTimerRunning())){ 
            statusTwo = true;
            musicrecordModel.start();
            lapButton.setText("Record");
            lapButton.setStyle("-fx-base: #eac381");
        }
      }
      else{ 
            statusOne = false;
            musicrecordModel.pause();
            lapButton.setText("Reset");
            lapButton.setStyle("-fx-base: #eddede");
        }
    }
    
    public void cleanTable(){
        musicrecordModel.cleanLap();
        lapField.setText(" No.    Current Time    Time since Start\n");
    }
    
     
    @FXML 
    public void saveFile(){
        Object newValue = lapField.getText();
        FileChooser fileChooser = new FileChooser(); 
        Stage stage = (Stage) vizPane.getScene().getWindow();
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
        
        // Create expandable Exception.
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

        // Set expandable Exception into the dialog pane.
        alert.getDialogPane().setExpandableContent(expContent);
        alert.showAndWait(); 
    }
     
    @FXML
    private void handleAbout(ActionEvent event){ 
        try {
             FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("AboutMe.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root1));  
            stage.show();
        }catch(Exception e) {
                e.printStackTrace();
        }
    }
     
     
    //==============swtich Scene==============
    public void start(Stage stage) {
        System.out.println("Music page has started");
        this.stage = stage; 
        
        musicScene = stage.getScene();   
    }
    
    @FXML
    private void goToPage2(ActionEvent event) {
        System.out.println("Going to page 2.");
         if (videoScene == null) {
            try {
                System.out.println("page2scene is null");
                
                FXMLLoader loader = new FXMLLoader(getClass().getResource("Video.fxml"));
                Parent page2Root = loader.load(); 
                videoController = loader.getController();
                
                videoController.musicScene = musicScene;    // to get back
                videoController.musicController = this;
                
                videoScene = new Scene(page2Root);
                
            } catch (IOException ex) {
                Logger.getLogger(MusicController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
         
         stage.setScene(videoScene);
         videoController.start(stage);
         
    }
   
    
}
