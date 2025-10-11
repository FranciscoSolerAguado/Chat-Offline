module org.fran.chatoffline {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.xml.bind;


    opens org.fran.chatoffline to javafx.fxml;
    exports org.fran.chatoffline;
}