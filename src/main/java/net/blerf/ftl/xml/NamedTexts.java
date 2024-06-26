package net.blerf.ftl.xml;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;


/**
 * Container for "text" tags in string lookup files.
 *
 * FTL 1.5.4 introduced "misc.xml".
 * FTL 1.6.1 introduced "text_*.xml" (and removed "misc.xml").
 */
@XmlRootElement( name = "namedTexts" )
@XmlAccessorType( XmlAccessType.FIELD )
public class NamedTexts {

	@XmlElement( name = "text" )
	private List<NamedText> namedTexts;


	public void setNamedTexts( List<NamedText> namedTexts ) {
		this.namedTexts = namedTexts;
	}

	public List<NamedText> getNamedTexts() {
		return namedTexts;
	}
}
