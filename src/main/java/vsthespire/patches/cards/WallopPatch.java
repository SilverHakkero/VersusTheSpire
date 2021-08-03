package vsthespire.patches.cards;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.cards.purple.Wallop;
import com.megacrit.cardcrawl.cards.red.Reaper;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import vsthespire.powers.AggroPower;
import vsthespire.powers.ChivalryPower;

public class WallopPatch {
    @SpirePatch(clz = Wallop.class, method = "use")
    public static class getBlockPatch {
        public static void Postfix(Wallop __instance) {
            if(AbstractDungeon.player.hasPower(ChivalryPower.ID)) {
                int blockAmount = 0;

                for(AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
                    int mBlockAfterAggro = m.currentBlock;
                    if(AbstractDungeon.player.hasPower(AggroPower.ID)) {
                        mBlockAfterAggro -= AbstractDungeon.player.getPower(AggroPower.ID).amount;
                        if(mBlockAfterAggro < 0) {
                            mBlockAfterAggro = 0;
                        }
                    }
                    if(__instance.damage > mBlockAfterAggro) {
                        blockAmount += __instance.damage - mBlockAfterAggro;
                    }
                }

                if(blockAmount > 0) {
                    AbstractDungeon.actionManager.addToBottom(new GainBlockAction(AbstractDungeon.player, blockAmount));
                }
            }
        }
    }
}
