module org.example.csc311weatherparser {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.csc311weatherparser to javafx.fxml;
    exports org.example.csc311weatherparser;
}