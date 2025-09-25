package net.gausman.ftl.model.change;

import net.blerf.ftl.parser.SavedGameParser;
import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.record.Jump;
import net.gausman.ftl.util.GausmanUtil;

public class ReactorEvent extends Event {
    private int amount;
    private int newReactorAmount;

    public ReactorEvent(){};

    public ReactorEvent(String text, Jump jump, int amount, int newReactorAmount){
        super(text, jump);
        this.amount = amount;
        this.newReactorAmount = newReactorAmount;
    }

//    @Override
//    public String getDisplayText(){
//        return GausmanUtil.getTextToId(getItemType(), getText());
//    }

    public int getAmount() {
        return amount;
    }

    public int getNewReactorAmount() {
        return newReactorAmount;
    }
}
