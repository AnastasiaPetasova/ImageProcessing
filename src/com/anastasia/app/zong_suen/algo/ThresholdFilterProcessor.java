package com.anastasia.app.zong_suen.algo;

import com.anastasia.app.zong_suen.exception.AnastasiaException;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

public class ThresholdFilterProcessor extends ImageProcessorImpl {

    private double threshold;

    @Override
    protected void initParameters(String... parameters) throws AnastasiaException {
        if (parameters.length == 0) {
            throw new AnastasiaException("Введите порог для преобразования в черно-белое изображение");
        }

        threshold = Double.NaN;

        try {
            threshold = Double.parseDouble(parameters[0]);
        } catch (NumberFormatException e) {
            throw new AnastasiaException("Порог преобразования должен быть числом от 0 до 1");
        }
    }

    @Override
    protected void processImage(PixelReader reader, PixelWriter writer, int width, int height) {
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                Color cellColor = reader.getColor(x, y);

                Color grayScale = cellColor.grayscale();
                double grayValue = grayScale.getRed();

                Color resultColor = (grayValue < threshold ? Color.BLACK : Color.WHITE);
                writer.setColor(x, y, resultColor);
            }
        }
    }
}
