package com.anastasia.app.zong_suen.view;

import com.anastasia.app.zong_suen.algo.GradientProcessor;
import com.anastasia.app.zong_suen.algo.ImageProcessor;
import com.anastasia.app.zong_suen.algo.ThresholdFilterProcessor;
import com.anastasia.app.zong_suen.algo.ZongSuenProcessor;
import com.anastasia.app.zong_suen.exception.AnastasiaException;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
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
    Canvas imageCanvas;

    @FXML
    TextField parametersTextField;

    @FXML
    Button loadImageButton;

    @FXML
    Button saveImageButton;

    @FXML
    Button gradientButton;

    @FXML
    Button thresholdFilterButton;

    @FXML
    Button zongSuenAlgoButton;

    private Image[] images;

    private int selectedImageIndex;

    public ZongSuenController() {
        this.images = new Image[4];
    }

    private void showExceptionMessage(Exception e) {
        showMessage(e.getLocalizedMessage(), Alert.AlertType.ERROR);
    }

    private void showMessage(String message, Alert.AlertType type) {
        Alert alert = new Alert(type, message, ButtonType.OK);
        alert.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initCanvas();
        initTextField();
        initButtons();
    }

    private void clearCanvas() {
        GraphicsContext graphics = imageCanvas.getGraphicsContext2D();
        graphics.setFill(Color.WHITE);
        graphics.fillRect(0, 0, imageCanvas.getWidth(), imageCanvas.getHeight());
    }

    private void initCanvas() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        imageCanvas.setWidth(screenSize.width / 2 - 100);
        imageCanvas.setHeight(screenSize.height / 2  - 100);

        clearCanvas();
    }

    private void initTextField() {

    }

    private void initButtons() {
        initLoadImageButton();
        initSaveImageButton();

        setImageProcessor(gradientButton, new GradientProcessor(), 0, 1);
        setImageProcessor(thresholdFilterButton, new ThresholdFilterProcessor(), 1, 2);
        setImageProcessor(zongSuenAlgoButton, new ZongSuenProcessor(), 2, 3);
    }

    private static final int LOAD = 0, SAVE = 1;

    private File selectFile(ActionEvent event, int type) {
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
    private void initLoadImageButton() {
        loadImageButton.setOnAction(event -> {
            File file = selectFile(event, LOAD);
            if (file == null) return;

            try {
                BufferedImage inputImage = ImageIO.read(file);
                Image image = SwingFXUtils.toFXImage(inputImage, null);

                Arrays.fill(images, null);
                images[0] = image;

                showImage(0);
            } catch (IOException e) {
                showMessage(
                        "Невозможно загрузить картинку: " + e.getLocalizedMessage(),
                        Alert.AlertType.WARNING);
            }
        });
    }

    private void showImage(int imageIndex) {
        selectedImageIndex = imageIndex;

        GraphicsContext context = imageCanvas.getGraphicsContext2D();
        context.drawImage(images[imageIndex], 0, 0, imageCanvas.getWidth(), imageCanvas.getHeight());
    }

    private void initSaveImageButton() {
        saveImageButton.setOnAction(event -> {
            Image savedImage = images[selectedImageIndex];
            if (savedImage == null) return;

            File file = selectFile(event, SAVE);
            if (file == null) return;

            BufferedImage outputImage = SwingFXUtils.fromFXImage(savedImage, null);

            try {
                ImageIO.write(outputImage, "png", file);
            } catch (IOException e) {
                showMessage(
                        "Невозможно сохранить картинку: " + e.getLocalizedMessage(),
                        Alert.AlertType.WARNING);
            }
        });
    }

    private String[] extractParameters() {
        String parameterString = parametersTextField.getText();
        if (parameterString == null) return new String[0];

        return parameterString.trim().split(" ");
    }

    private void setImageProcessor(Button button, ImageProcessor processor, int sourceIndex, int targetIndex) {
        button.setOnAction( event -> {
            try {
                Image sourceImage = images[sourceIndex];
                if (sourceImage == null) {
                    throw new AnastasiaException("Отсутствует изображение для обработки");
                }

                String[] parameters = extractParameters();

                images[targetIndex] = processor.process(sourceImage, parameters);

                showImage(targetIndex);
            } catch (AnastasiaException e) {
                showExceptionMessage(e);
            }
        });
    }

}
