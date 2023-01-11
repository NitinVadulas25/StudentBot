/*
 * MIT License
 *
 * Copyright (c) 2020 Mitty Robotics (Team 1351)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.mittyrobotics.util;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.PS4Controller;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;

/**
 * OI Class to manage all controllers and input
 */
public class OI {
    private static OI instance;

    private PS4Controller driverController;

    private XboxController throttleWheel;
    private XboxController steeringWheel;
    private XboxController operatorController;
    private XboxController driveTestingController;

    public static OI getInstance() {
        if (instance == null) {
            instance = new OI();
        }
        return instance;
    }

    public XboxController getThrottleWheel() {
        if (throttleWheel == null) {
            throttleWheel = new XboxController(OIConstants.THROTTLE_WHEEL_ID);
        }
        return throttleWheel;
    }

    public XboxController getSteeringWheel() {
        if (steeringWheel == null) {
            steeringWheel = new XboxController(OIConstants.STEERING_WHEEL_ID);
        }
        return steeringWheel;
    }

    public XboxController getOperatorController() {
        if (operatorController == null) {
            operatorController = new XboxController(OIConstants.OPERATOR_CONTROLLER_ID);
        }

        return operatorController;
    }

    public XboxController getDriveController() {
        if(driveTestingController == null){
            driveTestingController = new XboxController(OIConstants.DRIVER_CONTROLLER);
        }

        return driveTestingController;
    }

    public PS4Controller getPS4Controller() {
        if(driverController == null){
            driverController = new PS4Controller(OIConstants.DRIVER_CONTROLLER);
        }

        return driverController;
    }

    public void setupControls() {
        XboxController controller = getOperatorController();

    }

    public void setUpTuningControls() {
        XboxController controller = getOperatorController();

    }

    private void triggerFunctionAfterTime(Runnable runnable, long time){
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        runnable.run();
                    }
                },
                time
        );
    }
}