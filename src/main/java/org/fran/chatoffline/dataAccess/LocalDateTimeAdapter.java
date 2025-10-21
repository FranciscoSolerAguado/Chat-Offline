package org.fran.chatoffline.dataAccess;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


/**
 * La clase LocalDateTimeAdapter es fundamental para la serialización y deserialización de objetos LocalDateTime a y desde XML utilizando la API JAXB (Jakarta XML Binding).
 * En términos sencillos, sirve como un traductor entre el formato de fecha y hora de Java (LocalDateTime) y una representación de cadena (String)
 * que puede ser fácilmente almacenada en un archivo XML.
 */
public class LocalDateTimeAdapter extends XmlAdapter<String, LocalDateTime> {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public LocalDateTime unmarshal(String v) throws Exception {
        return LocalDateTime.parse(v, formatter);
    }

    @Override
    public String marshal(LocalDateTime v) throws Exception {
        return v.format(formatter);
    }
}
