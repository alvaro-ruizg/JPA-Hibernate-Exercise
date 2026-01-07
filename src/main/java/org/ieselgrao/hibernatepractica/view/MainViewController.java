package org.ieselgrao.hibernatepractica.view;

import javafx.fxml.FXML;
import org.ieselgrao.hibernatepractica.UniGraoVerse;

import java.io.IOException;

public class MainViewController {

    /**
     * It goes to the "play" scene.
     */
    @FXML
    public void newLogin(){
        try{
            UniGraoVerse.main.goScene("play");
        }catch(IOException e){
            System.exit(1);
        }
    }

    /**
     * It goes to the "credits" scene.
     */
    @FXML
    public void readCredits(){
        try{
            UniGraoVerse.main.goScene("credits");
        }catch(IOException e){
            System.exit(1);
        }
    }

}
