package vsthespire.powers;

import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class FakeNightmarePower extends VsAbstractPower {
    public static final String POWER_ID = "vsthespire:fakenightmare";
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("Night Terror");
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public FakeNightmarePower(AbstractCreature owner, int cardAmt) {
        super(POWER_ID, NAME, owner);
        this.amount = cardAmt;
        loadRegion("nightmare");
        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + " unknown" + DESCRIPTIONS[1];
    }

    @Override
    public void atStartOfTurn() {
        addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, FakeNightmarePower.POWER_ID));
    }
}
