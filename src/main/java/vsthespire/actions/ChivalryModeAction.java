package vsthespire.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import vsthespire.powers.ChivalryPower;

public class ChivalryModeAction extends AbstractGameAction {
    private ChivalryPower.DamageMode mode;

    public ChivalryModeAction(AbstractCreature owner, ChivalryPower.DamageMode mode) {
        setValues(owner, owner, 0);
        this.actionType = ActionType.SPECIAL;
        this.mode = mode;
    }

    @Override
    public void update() {
        if(this.target.hasPower(ChivalryPower.ID)) {
            ((ChivalryPower)this.target.getPower(ChivalryPower.ID)).setDamageMode(this.mode);
        }
        this.isDone = true;
    }
}
