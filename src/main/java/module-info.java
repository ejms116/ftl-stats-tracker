module net.gausman.ftl {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.slf4j;
    requires jakarta.xml.bind;
    requires java.desktop;
    requires httpcore;
    requires httpclient;
    requires org.jdom2;
    requires com.sun.jna;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;
    //requires org.apache.logging.log4j;

    exports net.gausman.ftl;
    opens net.gausman.ftl to jakarta.xml.bind, javafx.fxml, javafx.base;
    opens net.blerf.ftl.xml to jakarta.xml.bind;
    opens net.blerf.ftl.constants to com.fasterxml.jackson.databind;
    opens net.gausman.ftl.model to javafx.base, com.fasterxml.jackson.databind;
    opens net.gausman.ftl.model.run to javafx.base, com.fasterxml.jackson.databind;
    exports net.gausman.ftl.util;
    opens net.gausman.ftl.util to jakarta.xml.bind, javafx.base, javafx.fxml;
    opens net.gausman.ftl.view to javafx.base;
    opens net.gausman.ftl.controller to javafx.base, com.fasterxml.jackson.datatype.jsr310;

}