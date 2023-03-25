package com.github.mittyrobotics.autonomous.pathfollowing.v2;

import com.github.mittyrobotics.autonomous.Odometry;
import com.github.mittyrobotics.autonomous.pathfollowing.math.Point;
import com.github.mittyrobotics.autonomous.pathfollowing.math.Pose;
import com.github.mittyrobotics.autonomous.pathfollowing.math.QuinticHermiteSpline;
import com.github.mittyrobotics.autonomous.pathfollowing.math.Vector;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class AutoScoreCommand extends SequentialCommandGroup {
    public AutoScoreCommand(int tag, int index, double maxvel, double maxaccel, double maxdecel, double startvel, double endvel) {
        super();

        boolean left = tag > 4;

        Pose init = Odometry.getInstance().getState();
        // 0 is left from driver perspective
        Pose target = Odometry.getInstance().getScoringZone(tag)[left ? index : 2 - index];

        QuinticHermiteSpline spline = new QuinticHermiteSpline(
                init.getPosition(),
                new Vector(0, 0),
                new Vector(0, 0),
                target.getPosition(),
                new Vector(left ? -100 : 100, 0),
                new Vector(0, 0)
        );

        addCommands(
                new PathFollowingCommand(
                        new SwervePath(spline, 10,
                                maxvel, maxaccel, maxdecel,
                                startvel, endvel, true
                        ),
                        left ? Math.PI : 0, 3, 0.02,
                        0, 0.75, 10, 0, 0.1
                )
        );
    }
}
