//patch to make blur and calipers work on rival
package vsthespire.patches.powers;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import vsthespire.powers.relicpowers.CalipersPower;

public class MonsterPreTurnLogicPatch {
    @SpirePatch(clz = MonsterGroup.class, method = "applyPreTurnLogic")
    public static class KeepBlockPatch {
        //probably a better way to do this
        public static SpireReturn Prefix(MonsterGroup __instance) {

            for (AbstractMonster m : __instance.monsters) {
                if (!m.isDying && !m.isEscaping) {
                    if (!m.hasPower("Barricade") && !m.hasPower("Blur")) {
                        if(m.hasPower(CalipersPower.ID)) {
                            m.loseBlock(15);
                        } else {
                            m.loseBlock();
                        }
                    }
                    m.applyStartOfTurnPowers();
                }
            }
            return SpireReturn.Return(null);
        }
    }
}
