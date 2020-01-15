/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hxgrymultimediaplayer;

import static hxgrymultimediaplayer.MusicVisulaizer2.rectanglescircle;
import static java.lang.Integer.min;
import java.util.Objects;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;

/**
 *
 * @author haoxi
 */
public class MusicVisualizer extends AbstractModel implements Visualizer{
    private final String name = "Music Visualizer";
    
    //update the data
    static Rectangle[] rectangles;
    static Rectangle[] tops;
    static Double[] Height1;    //current height
    static Double[] Height2;    //previous height
    static Integer numOfBands;
    
    @Override
    public void start(Integer numBands, AnchorPane vizPane) {
        end();
        
        numOfBands = numBands;
        this.vizPane = vizPane;
        
        width = getWidth();
        height = 269.0;
        
        bandWidth = width / numBands;
        bandHeight = height * bandHeightPercentage;
        halfBandHeight = bandHeight / 2;
        rectangles = new Rectangle[numBands];
        tops = new Rectangle[numBands];
        Height1 = new Double[numBands];
        Height2 = new Double[numBands];
      
        for (int i = 0; i < numBands; i++) {
            Rectangle top = new Rectangle();
            top.setLayoutX(bandWidth*i);
            top.setLayoutY(height - minEllipseRadius - 8);
            top.setWidth(bandWidth * bandWeightPercentage);
            top.setHeight(5);
            top.setFill(Color.hsb(startHue + (i * 3.0), 1.0, 1.0, 1.0));
            
            Rectangle rectangle = new Rectangle();
            rectangle.setLayoutX( bandWidth * i);
            rectangle.setLayoutY(height - minEllipseRadius);
            rectangle.setWidth(bandWidth * bandWeightPercentage);
            rectangle.setHeight(minEllipseRadius);
            rectangle.setFill(Color.hsb(startHue + (i * 3.0), 1.0, 1.0, 1.0));
            vizPane.getChildren().addAll(top,rectangle);
            
            rectangles[i] = rectangle;
            tops[i] = top;
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void draw(double timestamp, double lenght, float[] magnitudes, float[] phases) {
        if (rectangles == null) {
            return;
        }
        
        if (tops == null) {
            return;
        }
        
       Integer num = min(rectangles.length, magnitudes.length);
       
       for (int i = 0; i < num; i++) {
           Height1[i] = ((60.0 + magnitudes[i])/60.0) * height + minEllipseRadius ;
           
           if (Height2[i]!= null) {
               if (Objects.equals(Height2[i], Height1[i])) { 
                   tops[i].setLayoutY(height - Height1[i] - 5);
                   tops[i].setFill(Color.hsb(startHue + (i * 3.0), 1.0, 1.0, 1.0));
               } 
            else{
                   tops[i].setLayoutY(height - Height2[i] - 12);
                   tops[i].setFill(Color.hsb(0, 0, 0));
               }
            }
           
            rectangles[i].setLayoutY(height - Height1[i]);
            rectangles[i].setHeight(Height1[i]);
            Height2[i] = Height1[i];
       }
 
    }

    //the end function is executed in the controller to achieve MVC structure
    @Override
    public void end() {
        if (rectangles != null) {
                for (Rectangle rectangle : rectangles) {
                    vizPane.getChildren().remove(rectangle);
                }
                rectangles = null;          
           } 
        if (tops!=null) {
                for(Rectangle top : tops){
                    vizPane.getChildren().remove(top);
               }
               tops = null;
            }
    }
}
