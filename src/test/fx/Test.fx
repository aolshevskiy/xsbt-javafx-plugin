import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.shape.Rectangle; 
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

Stage {
    title: "Declaring Is Easy!"
    scene: Scene {
        width: 300
        height: 250
        content: [
            Rectangle {
                x: 25, y: 80 width: 250, height: 80
                arcWidth: 20 arcHeight: 20
                fill: Color.web("#6699ff")
                stroke: Color.web("#003399")
                strokeWidth: 5.0
            }, //Rectangle
            Circle {
                centerX: 150  centerY: 120 radius: 80
                fill: Color.MAROON
                stroke: Color.INDIANRED
                strokeWidth: 10.0
            } //Circle      
        ] //Content
    } //Scene
} //Stage