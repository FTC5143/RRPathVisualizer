import com.acmerobotics.roadrunner.followers.TrajectoryFollower
import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.geometry.Vector2d
import com.acmerobotics.roadrunner.path.heading.*
import com.acmerobotics.roadrunner.trajectory.Trajectory
import com.acmerobotics.roadrunner.trajectory.TrajectoryBuilder
import com.acmerobotics.roadrunner.trajectory.constraints.DriveConstraints

object TrajectoryGen {
    private val constraints = DriveConstraints(45.0, 90.0, 0.0, 180.0.toRadians, 90.0.toRadians, 0.0)
    private val startPose = Pose2d(-33.0, 63.0, 270.0.toRadians)

    private var foundationPos = Vector2d(55.0, 45.0)

    fun createTrajectory(): Trajectory {
        val builder = TrajectoryBuilder(startPose, constraints)

        builder.splineTo(Pose2d(-29.0, 37.0, 180.0.toRadians)) // Drive to and take in skystone
        builder.strafeTo(Vector2d(-29.0, 21.0))
        builder.strafeTo(Vector2d(-37.0, 21.0))
        builder.strafeTo(Vector2d(-29.0, 21.0))
        builder.strafeTo(Vector2d(-29.0, 37.0))


        builder.strafeTo(Vector2d(0.0, 37.0))   // Strafe under bridge


        builder.setReversed(true)
        builder.splineTo(Pose2d(30.0, 50.0, 270.0.toRadians))
        builder.setReversed(false)

        builder.strafeTo(Vector2d(50.0, 32.0)) // Strafe to foundation

        builder.strafeTo(Vector2d(50.0, 63.0))  // Drag foundation to wall

        builder.strafeTo(Vector2d(21.0, 63.0)) // Get out from behind the foundation

        builder.strafeTo(Vector2d(21.0, 45.0)) // Drive up next to foundation
        builder.strafeTo(Vector2d(29.0, 45.0)) // Push foundation into wall
        builder.strafeTo(Vector2d(21.0, 45.0)) // Back off of foundation

        builder.splineTo(Pose2d(0.0, 37.0, 180.0.toRadians)) // Move under bridge

        builder.splineTo(Pose2d(-52.0, 37.0, 180.0.toRadians)) // Get to the side of the second skystone

        builder.strafeTo(Vector2d(-52.0, 21.0))
        builder.strafeTo(Vector2d(-60.0, 21.0))
        builder.strafeTo(Vector2d(-52.0, 21.0))

        builder.setReversed(true)

        builder.splineTo(Pose2d(-20.0, 37.0, 180.0.toRadians))
        builder.splineTo(Pose2d(0.0, 37.0, 180.0.toRadians))
        builder.splineTo(Pose2d(29.0, 45.0, 180.0.toRadians))

        builder.setReversed(false)

        builder.splineTo(Pose2d(0.0, 37.0, 180.0.toRadians))




        return builder.build()
    }

    fun drawOffbounds() {
        GraphicsUtil.fillRect(Vector2d(0.0, 63.0), 18.0, 18.0) // robot against the wall
        GraphicsUtil.fillRect(foundationPos, 18.5, 34.5)  // foundation against the wall
    }
}

val Double.toRadians get() = (Math.toRadians(this))