/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hxgrymultimediaplayer;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 *
 * @author haoxi
 */
public class HxgryMultiMediaPlayer extends Application {
    
    @Override
    public void start(Stage stage) throws Exception  {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Music.fxml"));
        Parent root = loader.load();
        MusicController controller = loader.getController(); //can get access to the controller with loader
        
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.show(); 
        
        controller.start(stage);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
