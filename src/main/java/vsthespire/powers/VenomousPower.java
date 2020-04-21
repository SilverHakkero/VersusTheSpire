//Not currently used. Envenom now applies poison regardless of block in vs fights.
package vsthespire.powers;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.PoisonPower;

public class VenomousPower extends VsAbstractPower {
    public static final String POWER_ID = "vsthespire:venomous";
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public  static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public VenomousPower(AbstractCreature owner, int psnAmt) {
        super(POWER_ID, NAME, owner);
        this.amount = psnAmt;
        loadRegion("envenom");
        updateDescription();
    }

    @Override
    public void onAttack(DamageInfo info, int damageAmount, AbstractCreature target) {
        if(info.type == DamageInfo.DamageType.NORMAL && damageAmount > 0 && this.owner != target) {
            flash();
            addToTop(new RemoveSpecificPowerAction(this.owner, this.owner, this.ID));
            if(this.owner.isPlayer) {
                //apply the poison
                addToTop(new ApplyPowerAction(target, this.owner, new PoisonPower(target, this.owner, this.amount)));
            }
        }
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }
}
