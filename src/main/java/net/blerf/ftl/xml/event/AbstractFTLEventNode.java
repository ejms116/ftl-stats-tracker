package net.blerf.ftl.xml.event;

public abstract class AbstractFTLEventNode extends AbstractBuildableTreeNode implements FTLEventNode {
    protected String sourceFile;

    public String getSourceFile() {
        return sourceFile;
    }

    public void setSourceFile(String sourceFile) {
        this.sourceFile = sourceFile;
    }
}
