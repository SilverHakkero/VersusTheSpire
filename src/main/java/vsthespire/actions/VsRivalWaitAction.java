package vsthespire.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import vsthespire.VsTheSpire;
import vsthespire.net.VsNetIO;
import vsthespire.powers.AggroPower;

public class VsRivalWaitAction extends AbstractGameAction{
    private VsNetIO myIO;

    public VsRivalWaitAction(VsNetIO newIO, AbstractCreature owner) {
        setValues(owner, owner, 0);
        this.duration = 0.2F;
        this.actionType = ActionType.WAIT;
        this.myIO = newIO;
    }

    @Override
    public void update() {
        if(this.duration == 0.2F) {
            if(!AbstractDungeon.getMonsters().areMonstersBasicallyDead()) {
                if(!VsTheSpire.isConnected){
                    //For Testing
                    addToBot(new GainBlockAction(this.source, 10));
                    addToBot(new ApplyPowerAction(this.source, this.source, new AggroPower(this.source, 3)));
                }
                else {
                    if(myIO.hasInput()){
                        String tmp = myIO.tryReceive();
                        if(!tmp.isEmpty())
                            myIO.interpret(tmp, this.source);
                    }
                    else {
                        addToBot(new VsRivalWaitAction(this.myIO, this.source));
                    }
                }
            }
        }
        tickDuration();
    }
}
