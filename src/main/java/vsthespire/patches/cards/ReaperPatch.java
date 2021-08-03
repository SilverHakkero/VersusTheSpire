package vsthespire.patches.cards;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.cards.red.Reaper;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import vsthespire.powers.AggroPower;
import vsthespire.powers.ChivalryPower;

public class ReaperPatch {
    @SpirePatch(clz = Reaper.class, method = "use")
    public static class VampireHealPatch {
        public static void Postfix(Reaper __instance) {
            if(AbstractDungeon.player.hasPower(ChivalryPower.ID)) {
                int healAmount = 0;

                for(AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
                    int mBlockAfterAggro = m.currentBlock;
                    if(AbstractDungeon.player.hasPower(AggroPower.ID)) {
                        mBlockAfterAggro -= AbstractDungeon.player.getPower(AggroPower.ID).amount;
                        if(mBlockAfterAggro < 0) {
                            mBlockAfterAggro = 0;
                        }
                    }
                    if(__instance.damage > mBlockAfterAggro) {
                        healAmount += __instance.damage - mBlockAfterAggro;
                    }
                }

                if(healAmount > 0) {
                    AbstractDungeon.actionManager.addToBottom(new HealAction(AbstractDungeon.player,
                            AbstractDungeon.player, healAmount));
                }
            }
        }
    }
}
