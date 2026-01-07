module org.ieselgrao.hibernatepractica {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;


    // opens org.ieselgrao.hibernatepractica to javafx.fxml;
    opens org.ieselgrao.hibernatepractica.view to javafx.fxml;
    opens org.ieselgrao.hibernatepractica.controller to javafx.fxml;
    opens org.ieselgrao.hibernatepractica.model to com.google.gson;
    exports org.ieselgrao.hibernatepractica;
}