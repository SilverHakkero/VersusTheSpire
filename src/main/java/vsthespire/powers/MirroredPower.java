//power that mimics the appearance of a power without having its behavior.
//Simply wrap it around the power you wanna mimic.
//May misbehave when wrapped around certain powers.
package vsthespire.powers;

import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.CombustPower;
import vsthespire.powers.interfaces.OnRivalPlayCardPower;

public class MirroredPower extends VsAbstractPower implements OnRivalPlayCardPower {
    public static final String ID_PREFIX = "vsthespire:mirrored";
    private AbstractPower myPower;
    private boolean isLoseNextTurn;
    private TypeRemoval removeOn;

    public enum TypeRemoval {
        ATTACK, SKILL, POWER, CARD, TURN, NONE
    }

    public MirroredPower(AbstractPower p, boolean removeNextTurn, TypeRemoval typeToRemoveOn) {
        super(ID_PREFIX + p.ID, p.name, p.owner);
        this.myPower = p;
        this.isLoseNextTurn = removeNextTurn;
        this.amount = p.amount;
        this.canGoNegative = p.canGoNegative;
        this.region48 = p.region48;
        this.region128 = p.region128;
        this.type = p.type;
        this.removeOn = typeToRemoveOn;
    }

    public MirroredPower(AbstractPower p){
        this(p, false, TypeRemoval.NONE);
    }

    public MirroredPower(AbstractPower p, boolean removeNextTurn) {this(p, removeNextTurn, TypeRemoval.NONE);}

    public MirroredPower(AbstractPower p, TypeRemoval typeToRemoveOn) {this(p, false, typeToRemoveOn);}

    @Override
    public void stackPower(int stackAmount) {
        super.stackPower(stackAmount);
        if(myPower instanceof CombustPower) {
            myPower.stackPower(stackAmount);
        }
        else {
            myPower.amount = this.amount;
        }
    }

    @Override
    public void atStartOfTurn() {
        if(isLoseNextTurn){
            addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, this.ID));
            return;
        }
        if(removeOn == TypeRemoval.TURN) {
            if(this.amount <= 1){
                addToTop(new RemoveSpecificPowerAction(this.owner, this.owner, this.ID));
            }
            else {
                addToTop(new ReducePowerAction(this.owner, this.owner, this.ID, 1));
            }

        }
    }

    public void onRivalPlayCard(AbstractCard c) {
        switch(this.removeOn) {
            case CARD:
                addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, this));
                break;
            case ATTACK:
                if(c.type == AbstractCard.CardType.ATTACK) {
                    addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, this));
                }
                break;
            case SKILL:
                if(c.type == AbstractCard.CardType.SKILL) {
                    addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, this));
                }
                break;
            case POWER:
                if(c.type == AbstractCard.CardType.POWER) {
                    addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, this));
                }
        }
    }

    @Override
    public void updateDescription() {
        myPower.updateDescription();
        this.description = myPower.description;
    }
}
