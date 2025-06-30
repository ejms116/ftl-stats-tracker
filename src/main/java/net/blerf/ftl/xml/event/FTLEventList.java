package net.blerf.ftl.xml.event;

import jakarta.xml.bind.annotation.*;
import net.blerf.ftl.parser.DataManager;

import java.util.List;


@XmlRootElement(name = "eventList")
@XmlAccessorType(XmlAccessType.FIELD)
public class FTLEventList extends AbstractFTLEventNode {
	@XmlAttribute(name = "name")
	private String id;

	@XmlElement(name="event",required=false)
	private List<FTLEvent> eventList;

	public String getId() {
		return id;
	}

	public void setId( String id ) {
		this.id = id;
	}

	public List<FTLEvent> getEventList() {
		return eventList;
	}

	public void setEventList(List<FTLEvent> eventList) {
		this.eventList = eventList;
	}

	@Override
	public String toString() {
		return ""+id;
	}

	@Override
	public String getDisplayText(DataManager dataManager, BuildContext context){
		StringBuilder sb = new StringBuilder("<html><b>")
				.append("List ")
				.append(id != null ? id : "")
				.append("</b> ")
				.append("The game normally chooses one randomly")
				.append(" File: ")
				.append(getSourceFile())
				.append("</html>");
		return sb.toString();
	}

}
