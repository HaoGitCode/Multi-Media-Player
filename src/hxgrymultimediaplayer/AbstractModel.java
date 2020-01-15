/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hxgrymultimediaplayer;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

/**
 *
 * @author haoxi
 */
public abstract class AbstractModel  {
    AnchorPane vizPane;
    
    protected PropertyChangeSupport propertyuChangeSupport;
    
    Double width = 0.0;
    Double height = 0.0;
    Double bandWidth = 0.0;
    Double bandHeight = 0.0;
    Double halfBandHeight = 0.0;
    
    Double bandHeightPercentage = 1.2;
    Double minEllipseRadius = 10.0;  
    Double bandWeightPercentage = 0.8;
    Double startHue = 260.0;
    
    //=====================Record Table=====================//
    protected double tickTimeInSeconds;
    protected  Timeline timeline;
    protected  KeyFrame keyFrame;
    protected static int minute = 0;
    protected static int second = 0;
    protected static int millisecond = 0;
    
    public void setupMonitor(){
        keyFrame = new KeyFrame(Duration.millis(tickTimeInSeconds * 1000), (event) -> {
            updateMonitor();
        });
        timeline = new Timeline(keyFrame);
        timeline.setCycleCount(Animation.INDEFINITE);
    }
    
    public void updateMonitor(){
        getTime();
    }
    
    public boolean isTimerRunning(){
    if(getTimeLine() != null){
            if(getTimeLine().getStatus() == Animation.Status.RUNNING){
                return true;
            }
       }
        return false;
    }
    
    public void start(){
        timeline.play();
    }
    
    public void pause(){
        timeline.pause();
    }
    
    public void stop(){
        timeline.stop();
        millisecond = 0;
        second =0;
        minute =0;
    }
    
    public Timeline getTimeLine(){
        return timeline; 
    }
    
    public void setTimeLine(Timeline timeline){
        this.timeline = timeline; 
    }
    
    public KeyFrame getKeyFrame(){
        return keyFrame; 
    }
    
    public void setKeyFrame(KeyFrame keyFrame){
        this.keyFrame = keyFrame; 
    }
    
    public double getTickTimeInSeconds(){
        return tickTimeInSeconds; 
    }
    
    public void getTime(){
        if (millisecond>100) {
            millisecond = 0;
            second++;
        }
        if(second>60)
        {
            millisecond = 0;
            second=0;
            minute++;   
        }
        millisecond++;
    }

    public double getHeight(){
        return vizPane.getHeight();
    }
    
    public double getWidth(){
        return vizPane.getWidth();
    }
    
    public AbstractModel(){
    propertyuChangeSupport = new PropertyChangeSupport(this);
    }
    
    public void addPropertyChangeListener(PropertyChangeListener listerner){
    propertyuChangeSupport.addPropertyChangeListener(listerner);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener listener){
    propertyuChangeSupport.removePropertyChangeListener(listener);
    }
    
    public void firePropertyChange(String propertyName, Object oldValue, Object newValue){
    propertyuChangeSupport.firePropertyChange(propertyName,oldValue,newValue);
    }
    
}
