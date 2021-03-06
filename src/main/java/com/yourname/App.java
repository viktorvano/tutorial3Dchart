package com.yourname;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import org.jzy3d.chart.AWTChart;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.javafx.JavaFXChartFactory;
import org.jzy3d.javafx.JavaFXRenderer3d;
import org.jzy3d.javafx.controllers.mouse.JavaFXCameraMouseController;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.Builder;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.rendering.canvas.Quality;

/**
 * Showing how to pipe an offscreen Jzy3d chart image to a JavaFX ImageView.
 *
 * {@link JavaFXChartFactory} delivers dedicated  {@link JavaFXCameraMouseController}
 * and {@link JavaFXRenderer3d}
 *
 * Support
 * Rotation control with left mouse button hold+drag
 * Scaling scene using mouse wheel
 * Animation (camera rotation with thread)
 *
 * TODO :
 * Mouse right click shift
 * Keyboard support (rotate/shift, etc)
 *
 * @author Martin Pernollet
 */
public class App extends Application {
    public static void main(String[] args) {
        Application.launch(args);
    }
    private Button btnGenerate;
    private double a,b;
    private Pane pane;
    private ImageView imageView;
    private AWTChart chart;
    private JavaFXChartFactory factory;
    private Scene scene;

    @Override
    public void start(Stage stage) {
        stage.setTitle(App.class.getSimpleName());

        a=Math.random();
        b=Math.random();

        // Jzy3d
        factory = new JavaFXChartFactory();
        chart  = getDemoChart(factory, "offscreen");
        imageView = factory.bindImageView(chart);

        // JavaFX
        pane = new Pane();
        scene = new Scene(pane);
        stage.setScene(scene);
        stage.show();
        pane.getChildren().add(imageView);

        factory.addSceneSizeChangedListener(chart, scene);

        btnGenerate = new Button("Generate");
        btnGenerate.setLayoutX(50);
        btnGenerate.setLayoutY(50);
        btnGenerate.setOnAction(event -> {
            a=Math.random();
            b=Math.random();
            pane.getChildren().remove(imageView);
            pane.getChildren().remove(btnGenerate);

            chart  = getDemoChart(factory, "offscreen");
            imageView = factory.bindImageView(chart);

            pane.getChildren().add(imageView);
            pane.getChildren().add(btnGenerate);
            factory.addSceneSizeChangedListener(chart, scene);
            factory.resetSize(chart, scene.getWidth(), scene.getHeight());
        });
        pane.getChildren().add(btnGenerate);

        stage.setWidth(500);
        stage.setHeight(500);
    }

    private AWTChart getDemoChart(JavaFXChartFactory factory, String toolkit) {
        // -------------------------------
        // Define a function to plot
        Mapper mapper = new Mapper() {
            @Override
            public double f(double x, double y) {
                return Math.cos(x*a) * Math.sin(b*x * y);
            }
        };

        // Define range and precision for the function to plot
        Range range = new Range(-4, 4);
        int steps = 80;

        // Create the object to represent the function over the given range.
        final Shape surface = Builder.buildOrthonormal(mapper, range, steps);
        surface.setColorMapper(new ColorMapper(new ColorMapRainbow(), surface.getBounds().getZmin(), surface.getBounds().getZmax(), new Color(1, 1, 1, .5f)));
        surface.setFaceDisplayed(true);
        surface.setWireframeDisplayed(false);

        // -------------------------------
        // Create a chart
        Quality quality = Quality.Advanced;
        //quality.setSmoothPolygon(true);
        //quality.setAnimated(true);

        // let factory bind mouse and keyboard controllers to JavaFX node
        AWTChart chart = (AWTChart) factory.newChart(quality, toolkit);
        chart.getScene().getGraph().add(surface);
        return chart;
    }
}