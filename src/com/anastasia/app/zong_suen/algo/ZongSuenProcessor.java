package com.anastasia.app.zong_suen.algo;

import com.anastasia.app.zong_suen.exception.AnastasiaException;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

import java.util.List;

public class ZongSuenProcessor extends ImageProcessorImpl {

    @Override
    protected void initParameters(String... parameters) throws AnastasiaException {
    }

    private void fillP9(int[][] pixels, int x, int y, int[] p9) {
        p9[0] = pixels[x][y - 1];
        p9[1] = pixels[x + 1][y - 1];
        p9[2] = pixels[x + 1][y];
        p9[3] = pixels[x + 1][y + 1];
        p9[4] = pixels[x][y + 1];
        p9[5] = pixels[x - 1][y + 1];
        p9[6] = pixels[x - 1][y];
        p9[7] = pixels[x - 1][y - 1];
    }

    @Override
    protected void processImage(PixelReader reader, PixelWriter writer, int width, int height) {
        int[][][] pixels = new int[2][width][height];

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                pixels[0][x][y] = Color.WHITE.equals(reader.getColor(x, y)) ? 0 : 1;
            }
        }

        int iteration = 0;

        int[] p = new int[8];

        for (;; ++iteration) {
            boolean changed = false;

            int[][] curPixels = pixels[iteration % 2];
            int[][] nextPixels = pixels[1 - iteration % 2];

            for (int x = 0; x < width; ++x) {
                for (int y = 0; y < height; ++y) {
                    nextPixels[x][y] = curPixels[x][y];
                }
            }

            for (int x = 1; x < width - 1; ++x) {
                for (int y = 1; y < height - 1; ++y) {
                    if (nextPixels[x][y] == 0) continue;

                    fillP9(curPixels, x, y, p);

                    boolean needRemove = true;

                    int sum = 0;
                    for (int k = 0; k < p.length; ++k) sum += p[k];

                    needRemove &= (2 <= sum && sum <= 6);

                    int count01 = 0;
                    for (int k = 0; k < p.length; ++k) {
                        int m = (k + 1) % p.length;

                        if (p[k] == 0 && p[m] == 1) ++count01;
                    }

                    needRemove &= count01 == 1;

                    needRemove &= p[0] * p[2] * p[4] == 0;
                    needRemove &= p[2] * p[4] * p[6] == 0;

                    if (needRemove) {
                        nextPixels[x][y] = 0;
                        changed = true;
                    }
                }
            }

            for (int x = 1; x < width - 1; ++x) {
                for (int y = 1; y < height - 1; ++y) {
                    if (nextPixels[x][y] == 0) continue;

                    fillP9(curPixels, x, y, p);

                    boolean needRemove = true;

                    int sum = 0;
                    for (int k = 0; k < p.length; ++k) sum += p[k];

                    needRemove &= (2 <= sum && sum <= 6);

                    int count01 = 0;
                    for (int k = 0; k < p.length; ++k) {
                        int m = (k + 1) % p.length;

                        if (p[k] == 0 && p[m] == 1) ++count01;
                    }

                    needRemove &= count01 == 1;

                    needRemove &= p[0] * p[2] * p[6] == 0;
                    needRemove &= p[0] * p[4] * p[6] == 0;

                    if (needRemove) {
                        nextPixels[x][y] = 0;
                        changed = true;
                    }
                }
            }

            if (!changed) break;
        }

        int[][] resultPixels = pixels[iteration % 2];
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                Color resultColor = resultPixels[x][y] == 0 ? Color.WHITE : Color.BLACK;
                writer.setColor(x, y, resultColor);
            }
        }
    }
}
