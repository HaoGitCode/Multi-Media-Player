/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hxgrymultimediaplayer;

import static java.lang.Integer.min;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;

/**
 *
 * @author haoxi
 */
public class MusicVisulaizer2 extends AbstractModel implements Visualizer{
    private final String name = "Second Visualizer";
    
    //update the data
    static Rectangle[] rectanglescircle;
    static Rectangle[] rectanglescircle2;
    static Double[] Height1;    //current height
    private Integer numOfBands;
    
    @Override
    public void start(Integer numBands, AnchorPane vizPane) {
        end();
        
        numOfBands = numBands;
        this.vizPane = vizPane;
        width = vizPane.getWidth();
        height = 269.0;
        
        bandWidth = width / numBands;
        bandHeight = height * bandHeightPercentage;
        halfBandHeight = bandHeight / 2;
        rectanglescircle = new Rectangle[numBands];
        rectanglescircle2 = new Rectangle[numBands];
        Height1 = new Double[numBands];
        
        
        for (int i = 0; i < numBands; i++) {
            Rectangle rectangle1 = new Rectangle();
            rectangle1.getTransforms().add(new Rotate(180,0,0));
            rectangle1.setLayoutX(width * 0.3);
            rectangle1.setLayoutY(height * 0.7);
            rectangle1.setWidth(bandWidth * 0.1);
            rectangle1.setHeight(minEllipseRadius);
            rectangle1.setFill(Color.hsb(startHue + (i * 3.0), 1.0, 1.0, 1.0));
            rectangle1.getTransforms().add(new Rotate(-(360/numBands)*i,0,0));
            
            Rectangle rectangle2 = new Rectangle();
            rectangle2.getTransforms().add(new Rotate(180,0,0));
            rectangle2.setLayoutX(width * 0.6);
            rectangle2.setLayoutY(height * 0.7);
            rectangle2.setWidth(bandWidth * 0.1);
            rectangle2.setHeight(minEllipseRadius);
            rectangle2.setFill(Color.hsb(startHue + (i * 3.0), 1.0, 1.0, 1.0));
            rectangle2.getTransforms().add(new Rotate((360/numBands)*i,0,0));
           
            vizPane.getChildren().addAll(rectangle1,rectangle2);
            
            rectanglescircle[i] = rectangle2;
            rectanglescircle2[i] = rectangle1;
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void draw(double timestamp, double lenght, float[] magnitudes, float[] phases) {
        if (rectanglescircle == null) {
            return;
        }
        if (rectanglescircle2 == null) {
            return;
        }
        
       Integer num = min(rectanglescircle.length, magnitudes.length);
       
       for (int i = 0; i < num; i++) {
            Height1[i] = ((60.0 + magnitudes[i])/60.0) * height + minEllipseRadius ;
            rectanglescircle[i].setHeight(Height1[i]*1);
            rectanglescircle2[i].setHeight(Height1[i]*1);
       }
 
    }

    //the end function is executed in the controller to achieve MVC structure
    @Override
    public void end() {
        if (rectanglescircle != null) {
             for (Rectangle rectangle : rectanglescircle) {
                 vizPane.getChildren().remove(rectangle);
             }
            rectanglescircle = null;
        } 
        if (rectanglescircle2 != null) {
             for (Rectangle rectangle : rectanglescircle2) {
                 vizPane.getChildren().remove(rectangle);
             }
            rectanglescircle2 = null;
        } 
    }
}
