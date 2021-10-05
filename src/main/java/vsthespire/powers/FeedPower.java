package vsthespire.powers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;

public class FeedPower extends VsAbstractPower {
    public static final String POWER_ID = "vsthespire:feed";
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public FeedPower(AbstractCreature owner, int amount){
        super(POWER_ID, NAME, owner);
        this.amount = amount;
        loadRegion("unawakened");
        updateDescription();
    }

    //TODO: Continue here
}
