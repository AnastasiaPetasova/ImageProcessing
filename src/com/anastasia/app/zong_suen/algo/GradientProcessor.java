package com.anastasia.app.zong_suen.algo;

import com.anastasia.app.zong_suen.exception.AnastasiaException;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

public class GradientProcessor extends ImageProcessorImpl {

    private double multiplier;

    @Override
    protected void initParameters(String... parameters) throws AnastasiaException {
        multiplier = 1;
        if (parameters.length > 0) {
            try {
                multiplier = Double.parseDouble(parameters[0]);
            } catch (NumberFormatException ignored) {

            }
        }
    }

    @Override
    protected void processImage(PixelReader reader, PixelWriter writer, int width, int height) {
        double[][] result = new double[width][height];

        for (int x = 0; x < width - 1; ++x) {
            for (int y = 0; y < height - 1; ++y) {
                Color color = reader.getColor(x, y);
                double brightness = color.getBrightness();

                Color nextXColor = reader.getColor(x + 1, y);
                double nextXBrightness = nextXColor.getBrightness();

                Color nextYColor = reader.getColor(x, y + 1);
                double nextYBrightness = nextYColor.getBrightness();

                double dx = nextXBrightness - brightness;
                double dy = nextYBrightness - brightness;

                double resultBrightness = Math.sqrt(dx * dx + dy * dy);
                result[x][y] = resultBrightness;
            }
        }

        for (int x = 0; x < width - 1; ++x) {
            for (int y = 0; y < height - 1; ++y) {
                Color color = reader.getColor(x, y);

                double brightness = color.getBrightness();
                if (brightness == 0) {
                    brightness = 0.05;
                }

                double brightnessFactor = result[x][y] / brightness * multiplier;

                Color resultColor = color.deriveColor(
                        0, 1,
                        brightnessFactor, 1);

                resultColor = resultColor.invert();

                writer.setColor(x, y, resultColor);
            }
        }
    }
}
