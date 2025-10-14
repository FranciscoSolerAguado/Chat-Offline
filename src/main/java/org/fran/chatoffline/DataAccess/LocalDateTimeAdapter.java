package org.fran.chatoffline.DataAccess;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDateTime;

/**
 * Adaptador para que JAXB pueda manejar el tipo de dato LocalDateTime.
 * JAXB no sabe cómo instanciar LocalDateTime por defecto, por lo que este adaptador
 * lo convierte a un String (formato ISO) para guardarlo en XML y viceversa.
 */
public class LocalDateTimeAdapter extends XmlAdapter<String, LocalDateTime> {

    @Override
    public LocalDateTime unmarshal(String v) throws Exception {
        // Convierte el String del XML a un objeto LocalDateTime
        return LocalDateTime.parse(v);
    }

    @Override
    public String marshal(LocalDateTime v) throws Exception {
        // Convierte el objeto LocalDateTime a un String para guardarlo en el XML
        return v.toString();
    }
}
