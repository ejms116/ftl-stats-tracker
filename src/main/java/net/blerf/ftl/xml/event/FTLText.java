package net.blerf.ftl.xml.event;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import net.blerf.ftl.parser.DataManager;

import javax.swing.tree.DefaultMutableTreeNode;

@XmlRootElement(name = "text")
@XmlAccessorType( XmlAccessType.FIELD )
public class FTLText extends AbstractBuildableTreeNode {
	@XmlAttribute
	private String id;

	@XmlAttribute
	private String load;

	@XmlAttribute
	private String planet;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLoad() {
		return load;
	}

	public void setLoad(String load) {
		this.load = load;
	}

    public String getPlanet() {
        return planet;
    }

    public void setPlanet(String planet) {
        this.planet = planet;
    }

	@Override
	public String getDisplayText(DataManager dataManager,  BuildContext context){
//		return "";
		StringBuilder sb = new StringBuilder("<html><b>")
				.append("Text ")
				.append("</b> ")
//				.append(id != null ? dataManager.getTextForId(id) : "")
				.append("</html>");
		return sb.toString();
	}

	private String getChildText(DataManager dataManager){
		StringBuilder sb = new StringBuilder();

		return sb.toString();
	}

	@Override
    public DefaultMutableTreeNode build(DataManager dataManager, BuildContext context){
		if (load == null){
			return null;
		}
		TextList textList = context.getTextListMap().get(load);
		if (textList != null){
			String rootText = "Textlist (game selects a random text):";
			DefaultMutableTreeNode root = new DefaultMutableTreeNode(rootText);
			int i = 1;
			for (FTLText text : textList.getText()){
				String childText = String.format("%s: %s", i, context.getTextForId(dataManager, text.getId()));
				DefaultMutableTreeNode child = new DefaultMutableTreeNode(childText);
				i++;
				root.add(child);
			}
			return root;
		}

        return null;
    }

}
