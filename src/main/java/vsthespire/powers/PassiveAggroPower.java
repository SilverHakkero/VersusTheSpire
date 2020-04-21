package vsthespire.powers;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import vsthespire.actions.ChivalryModeAction;

public class PassiveAggroPower extends VsAbstractPower{
    public static final String ID = "vsthespire:passive";
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public PassiveAggroPower(AbstractCreature owner, int amount) {
        super(ID, NAME, owner);
        this.amount = amount;
        this.priority = 101;
        updateDescription();
        loadRegion("deva");
    }

    @Override
    public void stackPower(int amt) {
        super.stackPower(amt);
        if(this.amount > 99999) {
            this.amount = 99999;
        }
    }

    @Override
    public void atStartOfTurn() {
        if(owner.isPlayer) {
            AbstractDungeon.actionManager.addToBottom(new ChivalryModeAction(this.owner, ChivalryPower.DamageMode.STRIKE));

            addToBot(new DamageAllEnemiesAction(this.owner, DamageInfo.createDamageMatrix(this.amount, true),
                    DamageInfo.DamageType.THORNS, AbstractGameAction.AttackEffect.NONE));

            AbstractDungeon.actionManager.addToBottom(new ChivalryModeAction(this.owner, ChivalryPower.DamageMode.CHARGE));
            if(!this.owner.hasPower(AggroPower.ID)) {
                for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
                    AbstractDungeon.actionManager.addToBottom(new ChivalryModeAction(m, ChivalryPower.DamageMode.BLOCK));
                }
            }
            addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, PassiveAggroPower.ID));
        }
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }
}
