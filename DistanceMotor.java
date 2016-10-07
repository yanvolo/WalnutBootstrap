package com.walnuthillseagles.walnutlibrary;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;

/**
 * Created by Yan Vologzhanin on 1/2/2016.
 */
public class DistanceMotor extends LinearMotor implements Runnable, Auto {
    //Constants
    //How often the code will check if its current operation is done

    //Precisions of wheels.
    public static final int RANGEVAL = 30;
    public static final long CLEARWAIT = 20;
    //Fields
    //Please measure in Inches
    private double circumference;
    private double gearRatio;
    //Distance value used in operate
    private int distance;
    //Parrallel Thread
    private Thread runner;

    public int operateCount;
    public DistanceMotor(DcMotor myMotor, String myName, boolean encoderCheck,boolean isReveresed,
                         double myDiameter,double myGearRatio, int myEncoder){
        //Create Motor
        super(myMotor, myName, encoderCheck, isReveresed);
        motor.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
        while(motor.getMode()!= DcMotorController.RunMode.RUN_TO_POSITION){
            try{
                synchronized (this){
                    this.wait(WAITRESOLUTION);
                }
            }
            catch(InterruptedException e){
                Thread.currentThread().interrupt();
            }
        }
        //Values involving bot
        circumference = myDiameter*Math.PI;
        gearRatio = myGearRatio;
        encoderRot = myEncoder;

        //Values involving distances
        distance = 0;
        speedLimit = 0;

        motor.setTargetPosition(0);
        motor.setPower(0);

        runner = new Thread();

        operateCount = 0;

    }
    //Starts operation with given parameters
    public void operate(double inches, double mySpeedLimit){

        distance = (int)((inches / circumference / gearRatio) * encoderRot * orientation);
        //Speedlimit is always positive
        speedLimit = Math.abs(mySpeedLimit);

        //Start new process
        runner = new Thread(this);

        //Clear locks from main thread
        synchronized (this){
            try{
                this.wait(CLEARWAIT);
            }
            catch(InterruptedException e){
                this.stop();
            }

        }
        runner.start();


    }
    public void operate(double inches) {
        this.operate(inches, 1);
    }
    //Allows other methods to change speed midway through method
    public void changeSpeedLimit(double mySpeedLimit){
        speedLimit = mySpeedLimit;
    }
    public void run(){
        //Go for it
        try {
            this.motor.setTargetPosition(distance);
            this.setPower(speedLimit);
            //Debug
            //Wait until in Pos
            //@TODO Better way to do this?
            //Wait for target position to update
            while(motor.getTargetPosition()==0 && distance != 0){
                synchronized (this){
                    this.wait(WAITRESOLUTION);
                }
            }
            do   {
                synchronized (this){
                    this.wait(WAITRESOLUTION);
                }

            }while(!inRange(distance, motor.getCurrentPosition()));

            this.fullStop();
            motor.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
            motor.setTargetPosition(0);
            distance = 0;
            operateCount++;
            runner = new Thread(this);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            stop();

        }
    }
    public int getDistance(){
        return distance;
    }
    public double getSpeedLimit(){return speedLimit;}
    public void fullStop(){
        motor.setPower(0);
        try{
            resetEncoder();
            motor.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
            while(motor.getMode()!= DcMotorController.RunMode.RUN_TO_POSITION){
                timer.sleep(WAITRESOLUTION);
        }
        }
        catch(InterruptedException e){
            motor.setPower(0);
            Thread.currentThread().interrupt();
        }
    }
    //Timers
    public void waitForCompletion() throws InterruptedException{
        if(runner != null && runner.isAlive())
            runner.join();
    }
    //Private helper methods

}
