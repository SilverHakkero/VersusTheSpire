package vsthespire.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import vsthespire.VsTheSpire;

public class RivalDoneAction extends AbstractGameAction {
    public RivalDoneAction(AbstractCreature owner) {
        setValues(owner, owner, 0);
        this.actionType = ActionType.WAIT;
    }

    @Override
    public void update() {
        if(VsTheSpire.isConnected) {
            VsTheSpire.netIO.trySend("done;null");
        }
        this.isDone = true;
    }
}
