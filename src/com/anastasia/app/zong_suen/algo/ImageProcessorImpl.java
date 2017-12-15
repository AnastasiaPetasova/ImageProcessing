package com.anastasia.app.zong_suen.algo;

import com.anastasia.app.zong_suen.exception.AnastasiaException;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

public abstract class ImageProcessorImpl implements ImageProcessor {

    protected abstract void initParameters(String... parameters) throws AnastasiaException;

    protected abstract void processImage(PixelReader reader, PixelWriter writer, int width, int height);

    @Override
    public Image process(Image sourceImage, String... parameters) throws AnastasiaException {
        initParameters(parameters);

        PixelReader source = sourceImage.getPixelReader();

        int width = (int)sourceImage.getWidth(), height = (int) sourceImage.getHeight();

        WritableImage resultImage = new WritableImage(
                source, width, height
        );

        source = resultImage.getPixelReader();
        PixelWriter result = resultImage.getPixelWriter();

        processImage(source, result, width, height);

        return resultImage;
    }
}
