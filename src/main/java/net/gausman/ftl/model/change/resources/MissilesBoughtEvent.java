package net.gausman.ftl.model.change.resources;

import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.ShipStatusModel;
import net.gausman.ftl.model.change.Event;
import net.gausman.ftl.model.record.Jump;
import net.gausman.ftl.model.record.StoreInfo;

public class MissilesBoughtEvent extends Event {
    public static final int MISSILES_PRICE_STORE = 6;

    public MissilesBoughtEvent() {}

    public MissilesBoughtEvent(Jump jump, int missilesBought) {
        super(Constants.EventDetailType.BUY_MISSILE, jump);
        setResourceEffect(Constants.Resource.MISSILE, missilesBought);
        setResourceEffect(Constants.Resource.SCRAP, -MISSILES_PRICE_STORE * missilesBought);
        addTag(Constants.EventTag.BUY);
        setDisplayText("Bought missiles");
    }

    @Override
    public void applyEventToShipStatusModel(ShipStatusModel model, boolean apply) {
        super.applyEventToShipStatusModel(model, apply);
        int mult = apply ? 1 : -1;

        model.getSectorMetrics().update(
                getJump().getSector(),
                Constants.ScrapUsedCategory.MISSILES,
                mult*-getResourceEffects().getOrDefault(Constants.Resource.SCRAP,0)
        );
        StoreInfo storeInfo = model.getSectorMetrics().getInfo(getJump().getSector()).getStoreInfoMap().get(getJump().getCurrentBeaconId());

        if (storeInfo != null){
            storeInfo.getStore().setMissiles(storeInfo.getStore().getMissiles() - mult*getResourceEffects().getOrDefault(Constants.Resource.MISSILE, 0));
        }
    }
}
