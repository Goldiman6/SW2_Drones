module co.edu.poli.SW2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires com.fasterxml.jackson.databind;

    opens co.edu.poli.SW2 to javafx.fxml;
    opens com.drone.model to javafx.base, com.fasterxml.jackson.databind;
    opens com.drone.servicios to com.fasterxml.jackson.databind;
    opens com.drone.controller to com.fasterxml.jackson.databind;
    
    exports co.edu.poli.SW2;
}
