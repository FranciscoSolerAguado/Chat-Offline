module org.fran.chatoffline {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.fran.chatoffline to javafx.fxml;
    exports org.fran.chatoffline;
}