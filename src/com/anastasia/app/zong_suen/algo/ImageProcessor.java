package com.anastasia.app.zong_suen.algo;

import com.anastasia.app.zong_suen.exception.AnastasiaException;
import javafx.scene.image.Image;

public interface ImageProcessor {

    Image process(Image sourceImage, String... parameters) throws AnastasiaException;
}
