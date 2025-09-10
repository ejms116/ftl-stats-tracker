package net.gausman.ftl.view.shipstatus;

public class SystemListItem {
    private String name;
    private int level;

    public SystemListItem(String name, int level) {
        this.name = name;
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public void changeLevel(int change){
        level += change;
    }
}
