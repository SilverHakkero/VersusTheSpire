package vsthespire.powers;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.WeakPower;
import vsthespire.actions.ChivalryModeAction;

import static com.badlogic.gdx.math.MathUtils.floor;

public class AggroPower extends VsAbstractPower {
    public static final String ID = "vsthespire:aggro";
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public int hitCount;
    private boolean damageDelay;

    public AggroPower(AbstractCreature owner, int amount) {
        super(ID, NAME, owner);
        this.amount = amount;
        this.hitCount = 1;
        this.priority = 100;
        this.damageDelay = false;
        updateDescription();
        loadRegion("strength");
    }

    @Override
    public void stackPower(int amt){
        super.stackPower(amt);
        if(amt >= 0) {
            this.hitCount++;
        }
        if(this.amount > 99999) {
            this.amount = 99999;
        }
    }

    public void reduceAggroByStrength(int strLost) {
        this.amount -= strLost * this.hitCount;
        if(this.amount < 0) {
            this.amount = 0;
        }
        updateDescription();
        if(owner instanceof AbstractMonster) {
            addToBot(new RollMoveAction((AbstractMonster) this.owner));
        }
    }

    public void delayDamage(){
        this.damageDelay = true;
    }

    @Override
    public void atStartOfTurn() {
        if(this.damageDelay)
            this.damageDelay = false;
        else if(owner.hasPower(ChivalryPower.ID)) {
            if (owner.isPlayer && !((ChivalryPower)owner.getPower(ChivalryPower.ID)).isExtraTurn()) {
                AbstractDungeon.actionManager.addToBottom(new ChivalryModeAction(this.owner, ChivalryPower.DamageMode.STRIKE));

                float damage = this.amount;
                if (owner.hasPower(WeakPower.POWER_ID)) {
                    damage = damage * 0.75F;
                }
                if (AbstractDungeon.getCurrRoom().monsters.monsters.get(0).hasPower(WrathPower.POWER_ID)) {
                    damage = damage * 2.0F;
                }

                addToBot(new DamageAllEnemiesAction(this.owner, DamageInfo.createDamageMatrix(floor(damage), true),
                        DamageInfo.DamageType.NORMAL, AbstractGameAction.AttackEffect.NONE));

                AbstractDungeon.actionManager.addToBottom(new ChivalryModeAction(this.owner, ChivalryPower.DamageMode.CHARGE));
                for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
                    AbstractDungeon.actionManager.addToBottom(new ChivalryModeAction(m, ChivalryPower.DamageMode.BLOCK));
                }

                addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, AggroPower.ID));
            }
        }
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1] + this.hitCount + DESCRIPTIONS[2];
    }
}
