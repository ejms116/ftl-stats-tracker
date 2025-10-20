package net.blerf.ftl.xml.event;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement( name = "event" )
@XmlAccessorType( XmlAccessType.FIELD )
public class Repair extends AbstractBuildableTreeNode {
}
