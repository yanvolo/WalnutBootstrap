package com.walnuthillseagles.walnutlibrary;

import com.qualcomm.robotcore.hardware.GyroSensor;


/**
 * Created by Yan Vologzhanin on 1/4/2016.
 */
@Deprecated
public class GyroDrive extends DistanceDrive implements Runnable {
    public static final long WAITRESOLUTION = 200;
    //Hardware
    private GyroSensor gyro;
    private Thread runner;
    private int targetHeading;
    private boolean runningLinear;
    private boolean runningTurn;
    //Constatns that allow you to modify behaviors
    //Value used when motors need to be realigned
    public static final double MOTORADJUSTMENTPOW = 0.8;
    public static final int GYROTOLERANCE = 2;

    public GyroDrive(DistanceMotor myLeft, DistanceMotor myRight, double myWidth, GyroSensor myGyro){
        super(myLeft,myRight,myWidth);
        gyro = myGyro;
        runner = new Thread(this);
        targetHeading = 0;
        gyro.calibrate();
        //@TODO Make sure this does not stall
        while(gyro.isCalibrating()){
            try{
                Thread.sleep(WAITRESOLUTION);
            }
            catch(InterruptedException e){
                Thread.currentThread().interrupt();
            }
        }
        runningLinear = false;
        runningTurn = false;
        runner = new Thread(this);
    }
    public void LinearDrive(double inches){
        leftDrive.operate(inches);
        rightDrive.operate(inches);
    }
    public void tankTurn(int degrees){
        double turnOrientation = (degrees < 0) ? -1: 1;
        int tempHeading = degrees;
        int currentPos = gyro.getHeading();
        while(currentPos +tempHeading >= 360){
            tempHeading -= 360;
        }
        while(currentPos + tempHeading < 0){
            tempHeading += 360;
        }
        targetHeading = currentPos + tempHeading;
        runner = new Thread(this);
        runner.start();
    }
    public void waitForCompletion() throws InterruptedException{
        leftDrive.waitForCompletion();
        rightDrive.waitForCompletion();
        runner.join();
    }
    //Parralell Threads
    public void run(){

    }
        
    private int compareOrientation(int target, int currentVal){
        int tarMax = target + GYROTOLERANCE;
        int tarMin = target - GYROTOLERANCE;
        int midPoint = target + 180;

        boolean inRange = false;
        //Adjust Range and check if we are in said range
        //@TODO Pretty sure I messed this up
        if(tarMax >= 360 || tarMin < 0)
            inRange = (currentVal > adjustRange(tarMin)|| currentVal<adjustRange(tarMax));
        else
            inRange = ((currentVal > tarMin) && currentVal < tarMax);
        if(inRange)
            return 0;
        else //if(!inRange)
        {
            if(midPoint<target){
                if(currentVal>midPoint && currentVal<target)
                    return -1;
                else //if(currentVal<midPoint || currentVal>target)
                    return 1;
            }
            else //if(midPoint>target)
            {
                if(currentVal>target && currentVal < target)
                    return 1;
                else // if(currentVal>midPoint || currentVal<target)
                    return -1;
            }
        }
    }
    private int adjustRange(int tarValue){
        int temp = tarValue;
        while(temp>=360){
            temp-=360;
        }
        while(temp<0){
            temp += 360;
        }
        return temp;
    }

}
    //Stuff I worry about

