package com.anastasia.app.zong_suen.view;

import com.anastasia.app.zong_suen.algo.GradientProcessor;
import com.anastasia.app.zong_suen.algo.ImageProcessor;
import com.anastasia.app.zong_suen.algo.ThresholdFilterProcessor;
import com.anastasia.app.zong_suen.algo.ZongSuenProcessor;
import com.anastasia.app.zong_suen.exception.AnastasiaException;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

public class ZongSuenController implements Initializable {

    @FXML
    Canvas originalImageCanvas;

    @FXML
    Canvas gradientImageCanvas;

    @FXML
    Canvas thresholdImageCanvas;

    @FXML
    Canvas zongSuenImageCanvas;

    @FXML
    Button loadOriginalImageButton;

    @FXML
    TextField gradientTextField;

    @FXML
    Button gradientButton;

    @FXML
    TextField thresholdTextField;

    @FXML
    Button thresholdFilterButton;

    @FXML
    Button zongSuenAlgoButton;

    private Canvas[] canvases;
    private Image[] images;
    private TextField[] textFields;

    public ZongSuenController() {
        this.images = new Image[4];
    }

    private static void showExceptionMessage(Exception e) {
        showMessage(e.getLocalizedMessage(), Alert.AlertType.ERROR);
    }

    private static void showMessage(String message, Alert.AlertType type) {
        Alert alert = new Alert(type, message, ButtonType.OK);
        alert.show();
    }

    private static Image loadImage(Event event) {
        File file = selectFile(event, LOAD);
        if (file == null) return null;

        try {
            BufferedImage inputImage = ImageIO.read(file);
            return SwingFXUtils.toFXImage(inputImage, null);
        } catch (IOException e) {
            showMessage(
                    "Невозможно загрузить картинку: " + e.getLocalizedMessage(),
                    Alert.AlertType.WARNING);

            return null;
        }
    }

    private static void saveImage(Event event, Image image) {
        if (image == null) return;

        File file = selectFile(event, SAVE);
        if (file == null) return;

        BufferedImage outputImage = SwingFXUtils.fromFXImage(image, null);

        try {
            ImageIO.write(outputImage, "png", file);
        } catch (IOException e) {
            showMessage(
                    "Невозможно сохранить картинку: " + e.getLocalizedMessage(),
                    Alert.AlertType.WARNING);
        }
    }

    private static void clearCanvas(Canvas canvas) {
        GraphicsContext graphics = canvas.getGraphicsContext2D();
        graphics.setFill(Color.WHITE);
        graphics.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    private static void drawImage(Canvas canvas, Image image) {
        GraphicsContext context = canvas.getGraphicsContext2D();
        context.drawImage(image, 0, 0, canvas.getWidth(), canvas.getHeight());
    }

    private static String[] extractParameters(TextField textField) {
        String[] parameters = new String[0];

        if (textField != null) {
            String parameterString = textField.getText();
            if (parameterString != null) {
                parameters = parameterString.trim().split(" ");
            }
        }

        return parameters;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initCanvases();
        initTextFields();
        initButtons();
    }

    private void initCanvases() {
        this.canvases = new Canvas[]{
                originalImageCanvas, gradientImageCanvas,
                thresholdImageCanvas, zongSuenImageCanvas
        };

        for (int canvasIndex = 0; canvasIndex < canvases.length; ++canvasIndex) {
            Canvas canvas = canvases[canvasIndex];

            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            int sizeCoeff = ZongSuenApplication.SIZE_COEFF;

            canvas.setWidth(screenSize.width * sizeCoeff / (sizeCoeff + 1) - 100);
            canvas.setHeight(screenSize.height * sizeCoeff / (sizeCoeff + 1)  - 100);

            canvas.setWidth(canvas.getWidth() / 2);
            canvas.setHeight(canvas.getHeight() / 2);

            clearCanvas(canvas);

            int finalCanvasIndex = canvasIndex;
            canvas.setOnMouseClicked(event -> {
                Image canvasImage = images[finalCanvasIndex];
                saveImage(event, canvasImage);
            });
        }
    }

    private void initTextFields() {
        this.textFields = new TextField[] {
                null, gradientTextField, thresholdTextField, null
        };
    }

    private void initButtons() {
        initLoadOriginalImageButton();

        setImageProcessor(gradientButton, new GradientProcessor(), 0, 1);
        setImageProcessor(thresholdFilterButton, new ThresholdFilterProcessor(), 1, 2);
        setImageProcessor(zongSuenAlgoButton, new ZongSuenProcessor(), 2, 3);
    }

    private static final int LOAD = 0, SAVE = 1;

    private static File selectFile(Event event, int type) {
        FileChooser fileChooser = new FileChooser();

//            FileChooser.ExtensionFilter bmpFilter = new FileChooser.ExtensionFilter(
//                    "BMP files (*.bmp)", "*.BMP"
//            );
//            fileChooser.getExtensionFilters().addAll(bmpFilter);

        // https://stackoverflow.com/questions/13585590/how-to-get-parent-window-in-fxml-controller
        Window mainWindow = ((Node)event.getTarget()).getScene().getWindow();

        switch (type) {
            case LOAD:
                return fileChooser.showOpenDialog(mainWindow);
            case SAVE:
                return fileChooser.showSaveDialog(mainWindow);
            default:
                return null;
        }
    }

    /**
     * http://java-buddy.blogspot.ru/2013/01/use-javafx-filechooser-to-open-image.html
     */
    private void initLoadOriginalImageButton() {
        loadOriginalImageButton.setOnAction(event -> {
            Image image = loadImage(event);

            Arrays.fill(images, null);
            images[0] = image;

            showImage(0);
        });
    }

    private void showImage(int imageIndex) {
        drawImage(canvases[imageIndex], images[imageIndex]);
    }

    private void setImageProcessor(Button button, ImageProcessor processor, int sourceIndex, int targetIndex) {
        button.setOnAction( event -> {
            try {
                Image sourceImage = images[sourceIndex];
                if (sourceImage == null) {
                    throw new AnastasiaException("Отсутствует изображение для обработки");
                }

                String[] parameters = extractParameters(textFields[targetIndex]);

                images[targetIndex] = processor.process(sourceImage, parameters);

                showImage(targetIndex);
            } catch (AnastasiaException e) {
                showExceptionMessage(e);
            }
        });
    }

}
