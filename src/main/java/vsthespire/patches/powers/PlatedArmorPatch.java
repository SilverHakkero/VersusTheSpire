package vsthespire.patches.powers;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.powers.PlatedArmorPower;
import vsthespire.powers.ChivalryPower;

public class PlatedArmorPatch {
    @SpirePatch(clz = PlatedArmorPower.class, method = "atEndOfTurnPreEndTurnCards")
    public static class noGainBlockPatch {
        public static SpireReturn Prefix(PlatedArmorPower __instance) {
            if(!__instance.owner.isPlayer && __instance.owner.hasPower(ChivalryPower.ID)) {
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }
}
