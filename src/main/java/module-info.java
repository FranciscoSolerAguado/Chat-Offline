module org.fran.chatoffline {
    // JavaFX
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;

    // Logging
    requires java.logging;

    // JAXB (Java Architecture for XML Binding) - Jakarta Namespace
    requires jakarta.xml.bind;
    requires org.glassfish.jaxb.runtime;
    requires org.glassfish.jaxb.core; // Requerido para la instanciación de clases
    requires jakarta.activation;
    requires java.desktop;
    requires javafx.media;


    // Abrir paquetes a JavaFX para que pueda acceder a los controladores
    opens org.fran.chatoffline to javafx.fxml;
    opens org.fran.chatoffline.controller to javafx.fxml;

    // Abrir paquetes a la API y a la implementación de JAXB
    opens org.fran.chatoffline.model to jakarta.xml.bind, org.glassfish.jaxb.runtime, org.glassfish.jaxb.core;
    opens org.fran.chatoffline.dataAccess to jakarta.xml.bind, org.glassfish.jaxb.runtime, org.glassfish.jaxb.core;

    exports org.fran.chatoffline;
}
