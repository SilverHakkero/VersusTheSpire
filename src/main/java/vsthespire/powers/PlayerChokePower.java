package vsthespire.powers;

import com.megacrit.cardcrawl.actions.common.MakeTempCardInDiscardAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import vsthespire.cards.Agony;
import vsthespire.powers.interfaces.OnRivalPlayCardPower;

public class PlayerChokePower extends VsAbstractPower implements OnRivalPlayCardPower {
    public static final String POWER_ID = "vsthespire:playerchoke";
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("Choked");
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public PlayerChokePower(AbstractCreature owner, int HpLoss) {
        super(POWER_ID, NAME, owner);
        this.amount = HpLoss;
        this.description = DESCRIPTIONS[0] + amount + DESCRIPTIONS[1];
        loadRegion("choke");
        this.type = PowerType.DEBUFF;
    }

    @Override
    public void atStartOfTurn() {
        addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, POWER_ID));
    }

    @Override
    public void onRivalPlayCard(AbstractCard c) {
        flash();
        Agony a = new Agony();
        a.setX(this.amount);
        addToBot(new MakeTempCardInDiscardAction(a, 1));
    }
}
