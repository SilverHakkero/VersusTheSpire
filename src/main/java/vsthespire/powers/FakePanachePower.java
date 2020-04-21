package vsthespire.powers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import vsthespire.powers.interfaces.OnRivalPlayCardPower;

public class FakePanachePower extends VsAbstractPower implements OnRivalPlayCardPower {
    public static final String POWER_ID = "vsthespire:fakepanache";
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("Panache");
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private int damage;

    public FakePanachePower(AbstractCreature owner, int damage) {
        super(POWER_ID, NAME, owner);
        this.amount = 5;
        this.damage = damage;
        updateDescription();
        loadRegion("panache");
    }

    @Override
    public void updateDescription() {
        if (this.amount == 1) {
            this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1] + this.damage + DESCRIPTIONS[2];
        } else {
            this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[3] + this.damage + DESCRIPTIONS[2];
        }
    }

    @Override
    public void stackPower(int stackAmount) {
        this.fontScale = 8.0F;
        this.damage += stackAmount;
        updateDescription();
    }

    @Override
    public void atStartOfTurn() {
        this.amount = 5;
        updateDescription();
    }

    @Override
    public void onRivalPlayCard(AbstractCard c) {
        this.amount--;
        if(this.amount == 0){
            flash();
            this.amount = 5;
        }
        updateDescription();
    }
}
