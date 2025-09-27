package net.gausman.ftl.model.change.system;

import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.ShipStatusModel;
import net.gausman.ftl.model.change.Event;
import net.gausman.ftl.model.record.Jump;

public class ReactorEvent extends Event {
    private int amount;
    private int newReactorAmount;

    public ReactorEvent(){};

    public ReactorEvent(Jump jump, int amount, int newReactorAmount){
        super(Constants.EventDetailType.REACTOR, jump);
        addTag(Constants.EventTag.BUY);
        this.amount = amount;
        this.newReactorAmount = newReactorAmount;
        setDisplayText(String.format("Reactor upgraded by %s; now level: %s", amount, newReactorAmount));
    }

    public int getAmount() {
        return amount;
    }

    public int getNewReactorAmount() {
        return newReactorAmount;
    }
    @Override
    public void applyEventToShipStatusModel(ShipStatusModel model, boolean apply){
        super.applyEventToShipStatusModel(model, apply);
        int mult = apply ? 1 : -1;
        model.changeReactor(mult * amount);

        model.getSectorMetrics().update(
                getJump().getSector(),
                Constants.ScrapUsedCategory.REACTOR,
                mult*-getResourceEffects().getOrDefault(Constants.Resource.SCRAP,0)
        );
    }
}
