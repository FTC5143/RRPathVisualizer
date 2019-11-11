import com.acmerobotics.roadrunner.trajectory.TemporalMarker
import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.application.Application
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.image.Image
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle

import javafx.stage.Stage
import javafx.util.Duration

class App : Application() {
    val robotRect = Rectangle(100.0, 100.0, 18.0, 18.0)
    val startRect = Rectangle(100.0, 100.0, 18.0, 18.0)
    val endRect = Rectangle(100.0, 100.0, 18.0, 18.0)

    var startTime = Double.NaN
    val trajectory = TrajectoryGen.createTrajectory()

    private var remainingMarkers = mutableListOf<TemporalMarker>()

    lateinit var fieldImage: Image
    lateinit var stage: Stage

    companion object {
        var WIDTH = 0.0
        var HEIGHT = 0.0
    }

    override fun start(stage: Stage?) {
        this.stage = stage!!
        fieldImage = Image("/field.png")

        val root = Group()

        WIDTH = fieldImage.width
        HEIGHT = fieldImage.height
        GraphicsUtil.pixelsPerInch = WIDTH / GraphicsUtil.FIELD_WIDTH
        GraphicsUtil.halfFieldPixels = WIDTH / 2.0

        val canvas = Canvas(WIDTH, HEIGHT)
        val gc = canvas.graphicsContext2D
        val t1 = Timeline(KeyFrame(Duration.millis(10.0), EventHandler<ActionEvent> { run(gc) }))
        t1.cycleCount = Timeline.INDEFINITE

        stage.scene = Scene(
            StackPane(
                root
            )
        )

        root.children.addAll(canvas, startRect, endRect, robotRect)

        stage.title = "PathVisualizer"
        stage.isResizable = false

        resetMarkers()

        println("duration ${trajectory.duration()}")

        stage.show()
        t1.play()
    }

    fun resetMarkers() {
        remainingMarkers.clear();
        remainingMarkers.addAll(trajectory.markers);
        remainingMarkers.sortBy {it.time};
    }

    fun run(gc: GraphicsContext) {
        if (startTime.isNaN())
            startTime = Clock.seconds

        GraphicsUtil.gc = gc
        gc.drawImage(fieldImage, 0.0, 0.0)

        gc.lineWidth = GraphicsUtil.LINE_THICKNESS

        gc.globalAlpha = 0.5
        GraphicsUtil.setColor(Color.RED)
        TrajectoryGen.drawOffbounds()
        gc.globalAlpha = 1.0

        val time = Clock.seconds
        val profileTime = time - startTime
        val duration = trajectory.duration()

        val start = trajectory.start()
        val end = trajectory.end()
        val current = trajectory[profileTime]

        while (remainingMarkers.size > 0 && (time-startTime) > remainingMarkers[0].time) {
            remainingMarkers.removeAt(0).callback()
        }


        if (profileTime >= duration)
            startTime = time
            resetMarkers()

        GraphicsUtil.drawSampledPath(trajectory.path)

        GraphicsUtil.updateRobotRect(startRect, start, GraphicsUtil.END_BOX_COLOR, 0.5)
        GraphicsUtil.updateRobotRect(endRect, end, GraphicsUtil.END_BOX_COLOR, 0.5)

        GraphicsUtil.updateRobotRect(robotRect, current, GraphicsUtil.ROBOT_COLOR, 0.75)
        GraphicsUtil.drawRobotVector(current)

        stage.title = "Profile duration : ${"%.2f".format(duration)} - time in profile ${"%.2f".format(profileTime)}"
    }
}

fun main(args: Array<String>) {
    Application.launch(App::class.java, *args)
}