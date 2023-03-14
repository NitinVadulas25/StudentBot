package com.github.mittyrobotics.intake.commands;

import com.github.mittyrobotics.autonomous.Odometry;
import com.github.mittyrobotics.autonomous.pathfollowing.math.Angle;
import com.github.mittyrobotics.intake.IntakeConstants;
import com.github.mittyrobotics.intake.IntakeSubsystem;
import com.github.mittyrobotics.intake.StateMachine;
import com.github.mittyrobotics.pivot.ArmKinematics;
import com.github.mittyrobotics.pivot.PivotSubsystem;
import com.github.mittyrobotics.telescope.TelescopeSubsystem;
import com.github.mittyrobotics.util.OI;
import com.github.mittyrobotics.util.Util;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;

import java.sql.Array;
import java.util.ArrayList;
import java.util.PriorityQueue;

public class AutoIntakeCommand extends CommandBase {
    boolean indexing = false;
    boolean outtaking = false;

    private final double threshold = 70;

    private final double long_term_avg_k = 20;
    private final double short_term_avg_k = 5;
    private double lavg, savg;
    private ArrayList<Double> lqueue, squeue;
    private boolean intook;

    public AutoIntakeCommand() {
        setName("auto intake");
        addRequirements(IntakeSubsystem.getInstance());
    }

    @Override
    public void initialize() {
        super.initialize();
        lqueue = new ArrayList<>();
        squeue = new ArrayList<>();
        lavg = 0;
        savg = 0;
        intook = false;
    }

    @Override
    public void execute() {
        IntakeSubsystem.getInstance().updateCurrent();
//        System.out.println(StateMachine.getInstance().getProfile());
        if (OI.getInstance().getOperatorController().getRightBumper()) {
            //Outtake override
            IntakeSubsystem.getInstance().setMotor(IntakeConstants.OUTTAKE_SPEED);
            StateMachine.getInstance().setIntakeOff();
            Odometry.getInstance().setScoringCam(false);
        } else if (OI.getInstance().getOperatorController().getLeftBumper()) {
            //Intake override
            IntakeSubsystem.getInstance().setMotor(IntakeConstants.INTAKE_SPEED);
            StateMachine.getInstance().setIntakeStowing();
            Odometry.getInstance().setScoringCam(true);
        } else if (StateMachine.getInstance().getIntakingState() == StateMachine.IntakeState.OUTTAKE) {
            //Outtake
            if (StateMachine.getInstance().getCurrentPieceState() == StateMachine.PieceState.CONE) {
                IntakeSubsystem.getInstance().setMotor(IntakeConstants.OUTTAKE_SPEED_CONE);
            } else {
                IntakeSubsystem.getInstance().setMotor(IntakeConstants.OUTTAKE_SPEED);
            }
        } else if (StateMachine.getInstance().getIntakingState() == StateMachine.IntakeState.INTAKE) {
            //Intake
            IntakeSubsystem.getInstance().setMotor(IntakeConstants.INTAKE_SPEED);

//            If prox sensor detected index for another second then stow
            if(IntakeSubsystem.getInstance().getAveragedCurrent() >= threshold && !indexing) {
                indexing = true;
                Util.triggerFunctionAfterTime(() -> {
                    OI.getInstance().zeroAll();
                    StateMachine.getInstance().setIntakeStowing();
                    Odometry.getInstance().setScoringCam(true);
                    indexing = false;
                }, 100);
            }
        } else if (StateMachine.getInstance().getIntakingState() == StateMachine.IntakeState.STOW) {
            //Piece stowed
            IntakeSubsystem.getInstance().setMotor(IntakeConstants.STOW_SPEED);
        } else if (StateMachine.getInstance().getIntakingState() == StateMachine.IntakeState.OFF) {
            //Intake off
            IntakeSubsystem.getInstance().setMotor(0);
        }
    }

    @Override
    public void end(boolean interrupted) {
        IntakeSubsystem.getInstance().setMotor(0);
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    public double updateAvg(ArrayList<Double> q, double avg, double k, double val) {
//        if(q.size() == k) {
//            avg -= (1/k) * q.poll();
//            avg += (1/k) * val;
//        } else {
//            double n = q.size();
//            avg = (n / (n + 1)) * avg + (1. / (n + 1)) * val;
//        }
        q.add(val);
        if(q.size() > k) q.remove(0);

        double sum = 0;
        for (double i : q) {
            sum += i;
        }
//        System.out.println(k);
        return sum / k;
    }
}
