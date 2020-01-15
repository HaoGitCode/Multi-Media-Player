/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hxgrymultimediaplayer;

import static hxgrymultimediaplayer.AbstractModel.second;

/**
 *
 * @author haoxi
 */
public class VideorecordModel extends AbstractModel{
    private static int lapMinute = 0;
    private static int lapSecond = 0;
    private static int lapMillisecond = 0;
    private static int lapIndex = 0;
    public  int num;
    public  String endtime;
    
    public VideorecordModel(){
        tickTimeInSeconds = 0.01;
        second = 0;
    }
     
    @Override
    public void updateMonitor(){
        super.updateMonitor();
    }
    
    public void setuplap(){
        updateLap(millisecond, second, minute);
    }
    
    public void updateLap(int millisecond,int second, int minute){
        lapIndex++;
        num = lapIndex;
        endtime = String.format("%02d", minute)+":"+ String.format("%02d", second)+"."+String.format("%02d", millisecond);
        firePropertyChange("videolapIndex", "", num);
        firePropertyChange("videolapEnd", "", endtime);
        lapMillisecond =millisecond;
        lapSecond = second;
        lapMinute = minute;

    }

    public void cleanLap(){
        lapIndex = 0;
        lapMinute = 0;
        lapSecond = 0;
        lapMillisecond = 0;
    }

}
