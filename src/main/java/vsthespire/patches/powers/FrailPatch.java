package vsthespire.patches.powers;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.powers.FrailPower;
import vsthespire.powers.ChivalryPower;

public class FrailPatch {
    @SpirePatch(clz = FrailPower.class, method = "modifyBlock")
    public static class NoChangeMonsterBlockPatch {
        public static SpireReturn<Float> Prefix(FrailPower __instance, float block) {
            if(!__instance.owner.isPlayer && __instance.owner.hasPower(ChivalryPower.ID)) {
                return SpireReturn.Return(block);
            }
            return SpireReturn.Continue();
        }
    }
}
