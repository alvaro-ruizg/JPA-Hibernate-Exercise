package org.ieselgrao.hibernatepractica.view;

import javafx.fxml.FXML;
import org.ieselgrao.hibernatepractica.UniGraoVerse;

import java.io.IOException;

public class CreditsViewController {

    /**
     * It goes to the "main" scene.
     */
    @FXML
    public void backMain(){
        try{
            UniGraoVerse.main.goScene("main");
        }catch(IOException e){
            System.exit(1);
        }
    }

}
