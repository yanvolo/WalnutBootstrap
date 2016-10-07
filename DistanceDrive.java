package com.walnuthillseagles.walnutlibrary;

import java.util.ArrayList;

/**
 * Created by Yan Vologzhanin on 1/4/2016.
 */

public class DistanceDrive {
    protected DistanceMotor leftDrive;
    protected DistanceMotor rightDrive;
    protected double robotWidth;

    //Just cause
    public static final int REVERSEORIENTATION = -1;

    //Constructor used for first two motors
    public DistanceDrive(DistanceMotor myLeft, DistanceMotor myRight, double width){
        //Initilize ArrayLists
        leftDrive = myLeft;
        rightDrive = myRight;
        //Initilize other variables
        robotWidth = width;
    }

    //Autonomous Methods
    public void linearDrive(double inches, double pow){
        //Tell motors to start
        leftDrive.operate(inches,pow);
        rightDrive.operate(inches, pow);
    }

    public void linearDrive(double inches){
        linearDrive(inches, 1);
    }
    //Right is positive, left is negetive
    public void tankTurn(double degrees, double pow){
        if(degrees != 0){
            double factor = 360/degrees;
            double distance = (Math.PI * robotWidth)/factor;
            //One is inverted to create a tank turn
            leftDrive.operate(distance,pow);
            rightDrive.operate(distance * REVERSEORIENTATION, pow * REVERSEORIENTATION);
        }

    }
    public void tankTurn(double degrees){
        tankTurn(degrees, 1);
    }

    public void pivotTurn(double degrees, double pow){
        if(degrees!=0){
            double factor = Math.abs(360/degrees);
            double distance =  (Math.PI * robotWidth * 2)/factor;

            if(pow>0){
                if(degrees>0)
                    leftDrive.operate(distance,pow);
                else
                    rightDrive.operate(distance,pow);

            }
            else //if(pow<0)
            {
                if(degrees>0)
                    rightDrive.operate(-distance,pow);
                else
                    leftDrive.operate(-distance,pow);
            }
        }

    }
    public void forwardPivotTurn(double degrees){
        forwardPivotTurn(degrees,1);
    }
    public void forwardPivotTurn(double degrees, double pow){ pivotTurn(degrees, Math.abs(pow));
    }

    public void backwardsPivotTurn(double degrees){
        backwardPivotTurn(degrees,-1);
    }

    public void backwardPivotTurn(double degrees, double pow){pivotTurn(degrees, -Math.abs(pow));}
    public void stop(){
        leftDrive.stop();
        rightDrive.stop();
    }
    public void fullStop(){
        leftDrive.fullStop();
        rightDrive.fullStop();
    }
    
    //Timers
    public void waitForCompletion() throws InterruptedException{
        leftDrive.waitForCompletion();
        rightDrive.waitForCompletion();
    }
    //Helpper Private methods
}
