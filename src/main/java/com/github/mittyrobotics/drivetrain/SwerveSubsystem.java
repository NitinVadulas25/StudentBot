package com.github.mittyrobotics.drivetrain;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.TalonFXControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.github.mittyrobotics.autonomous.pathfollowing.math.Angle;
import com.github.mittyrobotics.autonomous.pathfollowing.math.Point;
import com.github.mittyrobotics.autonomous.pathfollowing.math.Pose;
import com.github.mittyrobotics.autonomous.pathfollowing.math.Vector;
import com.github.mittyrobotics.drivetrain.commands.JoystickThrottleCommand;
import com.github.mittyrobotics.util.Gyro;
import com.github.mittyrobotics.util.interfaces.IMotorSubsystem;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import java.util.ArrayList;

public class SwerveSubsystem extends SubsystemBase implements IMotorSubsystem {

    private static SwerveSubsystem instance;

    private InverseKinematics inverseKinematics;
    public ForwardKinematics forwardKinematics;
    private DiffDriveKinematics diffDriveKinematics;

    private TalonFX[] driveFalcon = new TalonFX[4];
    private TalonFX[] rotationFalcon = new TalonFX[4];

    private Encoder[] encoder = new Encoder[4];

    private double[] prevEnc = new double[]{0, 0, 0, 0};

    boolean flip;

    public SwerveSubsystem() {
        super();
        setName("Swerve Modules");
    }

    public Pose getPose() {
        return new Pose(forwardKinematics.getLatestPose(), new Angle(Gyro.getInstance().getHeadingRadians()));
    }

    public Vector getVel() {
        return forwardKinematics.vel;
    }

    public Angle getDirectionOfTravel() {
        return forwardKinematics.getCurHeading();
    }

    public static SwerveSubsystem getInstance() {
        if (instance == null) {
            instance = new SwerveSubsystem();
        }
        return instance;
    }

    @Override
    public void updateDashboard() {

    }

    @Override
    public void initHardware() {
        for(int i = 0; i < 4; i++) {
            driveFalcon[i] = new WPI_TalonFX(SwerveConstants.DRIVE_FALCON[i]);
            driveFalcon[i].configFactoryDefault();
            driveFalcon[i].setNeutralMode(NeutralMode.Coast);
            driveFalcon[i].setSelectedSensorPosition(0);
//            driveFalcon[i].configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor);
            driveFalcon[i].config_kP(0, SwerveConstants.LINEAR_VELOCITY_P);
            driveFalcon[i].config_kI(0, SwerveConstants.LINEAR_VELOCITY_I);
            driveFalcon[i].config_kD(0, SwerveConstants.LINEAR_VELOCITY_D);
            driveFalcon[i].config_kF(0, SwerveConstants.SPEED_FEED_FORWARD);

            driveFalcon[i].configClosedloopRamp(0.5);

            rotationFalcon[i] = new WPI_TalonFX(SwerveConstants.ROTATION_FALCON[i]);
            rotationFalcon[i].configFactoryDefault();
            rotationFalcon[i].setNeutralMode(NeutralMode.Coast);
            rotationFalcon[i].setSelectedSensorPosition(0);
            rotationFalcon[i].configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor);
            rotationFalcon[i].config_kP(0, SwerveConstants.ANGULAR_POSITION_P);
            rotationFalcon[i].config_kI(0, SwerveConstants.ANGULAR_POSITION_I);
            rotationFalcon[i].config_kD(0, SwerveConstants.ANGULAR_POSITION_D);

//            encoder[i] = new Encoder(SwerveConstants.MAG_ENCODER_CHANNEL[i][0], SwerveConstants.MAG_ENCODER_CHANNEL[i][1], true);
//            encoder[i].reset();
//            encoder[i].setDistancePerPulse(1./SwerveConstants.TICKS_PER_RADIAN_MAG_ENCODER);

//            encoder[i] = new Encoder(SwerveConstants.MAG_ENCODER_CHANNEL[i][0], SwerveConstants.MAG_ENCODER_CHANNEL[i][1]);
            prevEnc[i] = driveFalcon[i].getSelectedSensorPosition();
//            encoder[i].setDistancePerRotation(2048);
//            encoder[i].setPositionOffset(10);
//            rotationFalcon[i].setSelectedSensorPosition(encoder[i].getAbsolutePosition() * SwerveConstants.TICKS_PER_RADIAN_MAG_ENCODER);

        }

        inverseKinematics = new InverseKinematics(SwerveConstants.TRACK_WIDTH, SwerveConstants.LENGTH);
        forwardKinematics = new ForwardKinematics(SwerveConstants.TRACK_WIDTH, SwerveConstants.LENGTH);
        diffDriveKinematics = new DiffDriveKinematics(SwerveConstants.TRACK_WIDTH, SwerveConstants.LENGTH);

        setDefaultCommand(new JoystickThrottleCommand());
    }

//    public double getMagEncoderAbsRadians(int i) {
//        return encoder[i].getDistance();
//                / SwerveConstants.TICKS_PER_RADIAN_MAG_ENCODER;
//    }

    public void resetMagEncoder(int i) {
//        encoder[i].reset();
    }

    public void resetMagEncoderAll() {
        for(int i = 0; i < 4; i++) {
//            encoder[i].reset();
        }
    }

    public double getMagEncoderDistance(int i) {
//        return encoder[i].getDistance();
        return 0;
    }

    //set all motors
    @Override
    public void overrideSetMotor(double percent) {
        setWheelPercentOutput(percent);
    }

    public void setAllControlMode(NeutralMode mode) {
        for(int i = 0; i < 4; i++) {
            driveFalcon[i].setNeutralMode(mode);
            rotationFalcon[i].setNeutralMode(mode);
        }
    }

    public void setSwerveInvKinematics(Vector linearVel, double angularVel) {
        inverseKinematics.setSwerveModule(linearVel, angularVel);
    }

    public void setDiffDriveKinematics(double linearVelocity, double radius) {
        diffDriveKinematics.updateFromLinearVelocityAndRadius(linearVelocity, radius);
    }

    public double[] getDiffDriveVels() {
        return diffDriveKinematics.linearVels;
    }

    public double[] getDiffDriveAngles() {
        return diffDriveKinematics.angles;
    }

    public double[] desiredVelocities() {
        double[] velocities = new double[4];

        for(int i = 0; i < 4; i++) {
//            velocities[i] = Math.sqrt(inverseKinematics.swerveModule[i].getX() * inverseKinematics.swerveModule[i].getX() + inverseKinematics.swerveModule[i].getY()
//                    * inverseKinematics.swerveModule[i].getY());
            velocities[i] = inverseKinematics.swerveModule[i].getMagnitude();
        }

        return velocities;
    }

    public double[] desiredAngles() {
        double[] angles = new double[4];
        for(int i = 0; i < 4; i++) {
//            angles[i] = Math.atan2(inverseKinematics.swerveModule[i].getY(), inverseKinematics.swerveModule[i].getX());
            angles[i] = inverseKinematics.swerveModule[i].getAngle().getRadians();
        }

        return angles;
    }

    public void setSwerveVelocity(double[] desiredVelocities) {
        for(int i = 0; i < 4; i++) {
            double acDes = desiredVelocities[i] * (flip ? -1 : 1);

            driveFalcon[i].set(ControlMode.Velocity, acDes * SwerveConstants.TICKS_PER_METER * 0.1);
        }

    }

    public static double standardize(double radians) {
        return (radians %= (Math.PI * 2)) >= 0 ? radians : (radians + 2 * Math.PI);
    }

    public void setZero() {
        for(int i = 0; i < 4; i++) {
            driveFalcon[i].set(TalonFXControlMode.PercentOutput, 0);
        }
    }

    public double vel(int i) {
        return driveFalcon[i].getSelectedSensorVelocity() / SwerveConstants.TICKS_PER_METER * 10;
    }

    public void setWheelPercentOutput(double percent) {
        for(int i = 0; i < 4; i++) {
            driveFalcon[i].set(TalonFXControlMode.PercentOutput, percent);
        }
    }

    public void setAnglesZero() {
        for(int i = 0; i < 4; i++) {
//            rotationFalcon[i].setSelectedSensorPosition(encoder[i].getAbsolutePosition());
            rotationFalcon[i].set(ControlMode.Position, 0);
        }
    }

    public boolean flip() {
        return flip;
    }

    public double angle(int i) {
        double curSP = rotationFalcon[i].getSelectedSensorPosition() / SwerveConstants.TICKS_PER_RADIAN_FALCON_WITH_GEAR_RATIO;
//        double curSP = encoder[i].getDistance();
//        double curSP = encoder[i].getAbsolutePosition();
        return standardize(curSP);
    }

    public double diff(double a1, double a2) {
        double df = (a2 - a1 + Math.PI) % (2 * Math.PI) - Math.PI;
        return df < -Math.PI ? df + 2 * Math.PI : df;
    }

    public void setSwerveAngle(double[] desiredAngles) {
        int flipNum = 0;

        for(int i = 0; i < 4; i++) {
            double norm = angle(i);

            double normDes = standardize(desiredAngles[i]);

            if (flip) norm = standardize(norm + Math.PI);

            if (Math.abs(diff(normDes, norm)) > Math.PI / 2) {
                flipNum++;
            }
        }


        if(flipNum > 2) {
            flip = !flip;
        }

        for(int i = 0; i < 4; i++) {
//            rotationFalcon[i].setSelectedSensorPosition(encoder[i].getAbsolutePosition());
//            double curSP = rotationFalcon[i].getSelectedSensorPosition();

            double curSP = rotationFalcon[i].getSelectedSensorPosition(0);

            double norm = standardize(curSP / SwerveConstants.TICKS_PER_RADIAN_FALCON_WITH_GEAR_RATIO);

            double normDes = standardize(desiredAngles[i]);

            boolean right;
            double diff;

            if (flip) norm = standardize(norm + Math.PI);

            if (normDes < norm) {
                if (norm - normDes > Math.PI) {
                    right = true;
                    diff = normDes + 2 * Math.PI - norm;
                } else {
                    right = false;
                    diff = norm - normDes;
                }
            } else {
                if (normDes - norm > Math.PI) {
                    right = false;
                    diff = norm + 2 * Math.PI - normDes;
                } else {
                    right = true;
                    diff = normDes - norm;
                }
            }

//            rotationFalcon[i].setSelectedSensorPosition(encoder[i].getAbsolutePosition());
//            rotationFalcon[i].set(ControlMode.Position, curSP + (diff * (right ? 1 : -1) * SwerveConstants.TICKS_PER_RADIAN_FALCON_MAG_ENCODER));
            rotationFalcon[i].set(ControlMode.Position, curSP + (diff * (right ? 1 : -1) * SwerveConstants.TICKS_PER_RADIAN_FALCON_WITH_GEAR_RATIO));
        }
    }

    public void setMeterPerSecond(double speed) {
        for(int i = 0; i < 4; i++) {
            driveFalcon[i].set(ControlMode.Velocity, speed * SwerveConstants.TICKS_PER_METER / 10);
        }
    }

    public double getSpeedOneMeters() {
        return driveFalcon[1].getSelectedSensorVelocity() / SwerveConstants.TICKS_PER_METER * 10;
    }

    public double ticks(int i) {
        return driveFalcon[i].getSelectedSensorPosition();
    }

    public double getSpeedOneTicks() {
        return driveFalcon[1].getSelectedSensorVelocity();
    }

    public void setPose(Pose p) {
        forwardKinematics.pose.add(new Pair(System.currentTimeMillis() * 1000000, p.getPosition()));
        forwardKinematics.angles.add(new Pair(System.currentTimeMillis() * 1000000, new Point(
                Math.cos(p.getHeading().getRadians()),
                Math.sin(p.getHeading().getRadians()))));
    }

    public void resetPose() {
        setPose(new Pose(new Point(0, 0), new Angle(0)));
    }

    public void updateForwardKinematics() {
        Vector[] modules = new Vector[4];

        for (int i = 0; i < 4; i++) {
            double cur = driveFalcon[i].getSelectedSensorPosition();

            modules[i] = new Vector(new Angle(-angle(i) + Gyro.getInstance().getHeadingRadians() + Math.PI/2.), (cur - prevEnc[i]) / SwerveConstants.TICKS_PER_METER);
//            System.out.println(i + ": " + angle(i));

            prevEnc[i] = cur;
        }

        forwardKinematics.updateForwardKinematics(modules);
    }

    public static class DiffDriveKinematics {
        protected double linearVelocity, angularVelocity, leftVelocity, rightVelocity, radius, trackWidth, trackLength;
        private final double[] linearVels = new double[4];
        private final double[] angles = new double[4];
        private final int[] radSigns = new int[]{1, -1, -1, 1};
        private final int[] angSigns = new int[]{1, 1, -1, -1};

        public DiffDriveKinematics(double trackWidth, double trackLength) {
            this.trackWidth = trackWidth;
            this.trackLength = trackLength;
        }

        public void updateFromLinearAndAngularVelocity(double linearVelocity, double angularVelocity) {
            this.linearVelocity = linearVelocity;
            this.angularVelocity = angularVelocity;

            if(Math.abs(angularVelocity) < 2e-9) {
                this.radius = Double.POSITIVE_INFINITY;
                for(int i = 0; i < 4; ++i) {
                    angles[i] = 0;
                    linearVels[i] = linearVelocity;
                }
            } else {
                this.radius = linearVelocity / angularVelocity;
                for(int i = 0; i < 4; ++i) {
                    double w = radius + radSigns[i] * trackWidth/2;
                    double l = trackLength / 2;
                    angles[i] = (radius > 0 ? -1 : 1) * angSigns[i] * Math.abs(Math.atan2(l, Math.abs(w)));
//                    angles[i] = 0;
                    linearVels[i] = angularVelocity * w;
                }
            }
        }

        public void updateFromLinearVelocityAndRadius(double linearVelocity, double radius) {
            if(Double.isInfinite(radius)) {
                this.angularVelocity = 0;
            } else {
                this.angularVelocity = linearVelocity / radius;
            }
            updateFromLinearAndAngularVelocity(linearVelocity, this.angularVelocity);
        }
    }

    public static class InverseKinematics {
        private final Vector swerveModule[] = new Vector[4];
        private Vector tangentialVelocityVector[] = new Vector[4];

        private Vector r;

        private Vector linearVel;
        private double angularVel;

        public InverseKinematics(double width, double length) {
            r = new Vector(width/2, length/2);
        }

        public void setSwerveModule(Vector linearVel, double angularVel) {

            this.linearVel = linearVel;
            this.angularVel = angularVel;

            tangentialVelocityVector[0] = new Vector(-this.angularVel*r.getY(), this.angularVel*r.getX());
            tangentialVelocityVector[1] = new Vector(this.angularVel*r.getY(), this.angularVel*r.getX());
            tangentialVelocityVector[2] = new Vector(this.angularVel*r.getY(), -this.angularVel*r.getX());
            tangentialVelocityVector[3] = new Vector(-this.angularVel*r.getY(), -this.angularVel*r.getX());


            for(int i = 0; i < 4; i++) {
                swerveModule[i] = Vector.add(linearVel, tangentialVelocityVector[i]);
            }
        }
    }

    public static class ForwardKinematics {
        private final Vector r;
        private ArrayList<Pair> pose = new ArrayList<>();
        private ArrayList<Pair> angles = new ArrayList<>();

        private Vector vel = new Vector(0, 0);
        private Angle curHeading = new Angle(0);


        public ForwardKinematics(double width, double length) {
            r = new Vector(width/2, length/2);
        }

        public void updateForwardKinematics(Vector[] modules) {

            long nanoTime = System.currentTimeMillis() * 1000000;

            Point new_ = new Point(0, 0);
            for (int i = 0; i < 4; i++) new_ = Point.add(new_, new Point(modules[i]));
            new_ = Point.multiply(0.25, new_);

            // TODO: check this
            new_ = new Point(-new_.getY(), new_.getX());

            pose.add(new Pair(nanoTime, Point.add(pose.get(pose.size() - 1).getValue(), new_)));
            curHeading = new Angle(Gyro.getInstance().getHeadingRadians());

            angles.add(new Pair(nanoTime, curHeading));

        }

        public Vector extrapolatePose(double time) {
            return Vector.multiply(time, vel);
        }

        private int search(ArrayList<Pair> array, double value, boolean greater) {
            int start = 0, end = array.size() - 1;

            int ans = -1;
            while (start <= end) {
                int mid = (start + end) / 2;

                if (greater) {
                    if (array.get(mid).getKey() < value) {
                        start = mid + 1;
                    } else {
                        ans = mid;
                        end = mid - 1;
                    }
                } else {
                    if (array.get(mid).getKey() > value) {
                        end = mid - 1;
                    } else {
                        ans = mid;
                        start = mid + 1;
                    }
                }
            }
            return ans;
        }

        public Point getPoseAtTime(double time) {
            long tl, tr;
            Point pl, pr;

            int left_index = search(pose, time, false);
            int right_index = search(pose, time, true);

            if(left_index == -1) return pose.get(right_index).getValue();
            if(right_index == -1) return pose.get(left_index).getValue();

            tl = pose.get(left_index).getKey();
            tr = pose.get(right_index).getKey();
            pl = pose.get(left_index).getValue();
            pr = pose.get(right_index).getValue();

            if (tr == tl) return pl;

            return Point.add(pl, Point.multiply((time - tl) / (tr - tl), Point.add(pr, Point.multiply(-1, pl))));
//            return Point.add(pl, Point.multiply((time - tl) / (tr - tl),
//                    Point.add(pr, Point.multiply(-1, pl))));
        }

        public Angle getAngleAtTime(double time) {
            long tl, tr;
            Angle pl, pr;

            int left_index = search(angles, time, false);
            int right_index = search(angles, time, true);

            if(left_index == -1) return angles.get(right_index).getAltValue();
            if(right_index == -1) return angles.get(left_index).getAltValue();

            tl = angles.get(left_index).getKey();
            tr = angles.get(right_index).getKey();
            pl = angles.get(left_index).getAltValue();
            pr = angles.get(right_index).getAltValue();

            double a1 = pl.getRadians();
            double a2 = pr.getRadians();

            if (tl == tr) return new Angle(a1);

            return new Angle(a1 + ((time - tl) / (tr - tl)) * (a2 - a1));
        }

        public Vector getR(int i) {
            return new Vector(r.getX() * (i == 0 || i == 3 ? -1 : 1), r.getY() * (i > 1 ? -1 : 1));
        }

        public Point getLatestPose() {
            if (pose.size() == 0) return new Point(0, 0);
            return pose.get(pose.size() - 1).getValue();
        }

        public Angle getLatestAngle() {
            if (angles.size() == 0) return new Angle(0);
            return angles.get(angles.size() - 1).getAltValue();
        }

        public Angle getCurHeading() {
            return curHeading;
        }
    }
}



