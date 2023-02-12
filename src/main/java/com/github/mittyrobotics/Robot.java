package com.github.mittyrobotics;

import com.github.mittyrobotics.autonomous.pathfollowing.SwerveAutoPickupCommand;
import com.github.mittyrobotics.autonomous.pathfollowing.SwervePath;
import com.github.mittyrobotics.autonomous.pathfollowing.SwervePurePursuitCommand;
import com.github.mittyrobotics.autonomous.pathfollowing.math.Angle;
import com.github.mittyrobotics.autonomous.pathfollowing.math.Point;
import com.github.mittyrobotics.autonomous.pathfollowing.math.QuinticHermiteSpline;
import com.github.mittyrobotics.drivetrain.SwerveSubsystem;
import com.github.mittyrobotics.intake.ClawGrabberSubsystem;
import com.github.mittyrobotics.intake.ClawRollerSubsystem;
import com.github.mittyrobotics.pivot.PivotConstants;
import com.github.mittyrobotics.pivot.PivotSubsystem;
import com.github.mittyrobotics.telescope.TelescopeConstants;
import com.github.mittyrobotics.telescope.TelescopeSubsystem;
import com.github.mittyrobotics.util.Gyro;
import com.github.mittyrobotics.util.OI;
import com.github.mittyrobotics.util.TrapezoidalMotionProfile;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
6trt * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  @Override
  public void robotInit() {
    SwerveSubsystem.getInstance().initHardware();
    TelescopeSubsystem.getInstance().initHardware();
    PivotSubsystem.getInstance().initHardware();
    ClawGrabberSubsystem.getInstance().initHardware();
    ClawRollerSubsystem.getInstance().initHardware();
    Gyro.getInstance().initHardware();
    OI.getInstance().setUpTuningControls();
    PivotSubsystem.getInstance().setBrakeMode();
    TelescopeSubsystem.getInstance().setBrakeMode();
  }

  @Override
  public void robotPeriodic() {
    CommandScheduler.getInstance().run();
    SwerveSubsystem.getInstance().updateForwardKinematics();
  }

  @Override
  public void autonomousInit() {
    SwerveSubsystem.getInstance().resetPose();

//    SwervePath[] paths = {
//            new SwervePath(
//                    new QuinticHermiteSpline(
//                            new Point(0, 0), new Angle(2.5*Math.PI/2),
//                            new Point(-26/39.37, (-224+26)/39.37), new Angle(3*Math.PI/2)),
//                    new Angle(0), new Angle(Math.PI),
//                    0, 0, 6., 12., 1, 0.2, 0.4, 2.5, 0, 0.02, 0.5
//            ),
//            new SwervePath(
//                    new QuinticHermiteSpline(
//                            new Point(-26/39.37, (-224+26)/39.37), new Angle(Math.PI/2),
//                            new Point(0, 0), new Angle(0.5*Math.PI/2)),
//                    new Angle(Math.PI), new Angle(0),
//                    0, 0, 6., 12., 1, 0.2, 0.4, 2.5, 0, 0.02, 0.5
//            )
//    };

//    SwervePurePursuitCommand command = new SwervePurePursuitCommand(0.05, 0.07, paths);
//    SwerveSubsystem.getInstance().setDefaultCommand(command);

//    SwervePath path = new SwervePath(
//            new QuinticHermiteSpline(new Point(0, 0), new Angle(Math.PI/2), new Point(-1, 1), new Angle(Math.PI/2)),
//            new Angle(0), new Angle(0),
//            0, 0, 6., 12., 3, 0.0, 0.2, 0.0, 0, 0.00, 0.5
//    );
//
//    SwerveAutoPickupCommand command = new SwerveAutoPickupCommand(0.07, path);
//    SwerveSubsystem.getInstance().setDefaultCommand(command);
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {
    System.out.println("RAD: " + PivotSubsystem.getInstance().getPositionRadians());
    System.out.println("EXT: " + TelescopeSubsystem.getInstance().getDistanceMeters());
  }

  /** This function is called once when teleop is enabled. */
  @Override
  public void teleopInit() {
      OI.getInstance().setupControls();
      TelescopeSubsystem.getInstance().getNeo().getEncoder().setPosition(0);
  }

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {
//    ClawRollerSubsystem.getInstance().setRoller(-0.4);  //NEGATIVE IS ROLLER INWARDS

//    SmartDashboard.putNumber("Neo Current", ClawRollerSubsystem.getInstance().getCurrent());

//    System.out.println(ClawGrabberSubsystem.getInstance().getProximitySensor());
//    System.out.println(ClawGrabberSubsystem.getInstance().getEncoderValue());

  }
  /** This function is called once when the robot is disabled. */
  @Override
  public void disabledInit() {
    TelescopeSubsystem.getInstance().setCoastMode();
    PivotSubsystem.getInstance().setCoastMode();
  }

  /** This function is called periodically when disabled. */
  @Override
  public void disabledPeriodic() {}

  /** This function is called once when test mode is enabled. */
  @Override
  public void testInit() {}

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {}

  /** This function is called once when the robot is first started up. */
  @Override
  public void simulationInit() {}

  /** This function is called periodically whilst in simulation. */
  @Override
  public void simulationPeriodic() {}
}
