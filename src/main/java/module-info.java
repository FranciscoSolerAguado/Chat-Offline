module org.fran.chatoffline {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.xml.bind;
    requires java.logging;
    requires javafx.graphics;
    requires javafx.base;


    opens org.fran.chatoffline to javafx.fxml;
    opens org.fran.chatoffline.controller to javafx.fxml;

    opens org.fran.chatoffline.model to java.xml.bind;
    opens org.fran.chatoffline.DataAccess to java.xml.bind;
    opens org.fran.chatoffline.service to java.xml.bind;

    exports org.fran.chatoffline;
}
