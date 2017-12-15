package com.anastasia.app.zong_suen.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;

public class ZongSuenApplication extends Application {

    public static final int SIZE_COEFF = 2;

    @Override
    public void start(Stage primaryStage) throws Exception{
        initScene(primaryStage);
        primaryStage.show();
    }

    private void initScene(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("zong_suen_viewer.fxml"));
        primaryStage.setTitle("Petasova ZongSuen app");

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int sizeCoeff = SIZE_COEFF;

        primaryStage.setScene(
                new Scene(root,
                        screenSize.width * sizeCoeff / (sizeCoeff + 1),
                        screenSize.height * sizeCoeff / (sizeCoeff + 1)
                )
        );
    }


    public static void main(String[] args) {
        launch(args);
    }
}
