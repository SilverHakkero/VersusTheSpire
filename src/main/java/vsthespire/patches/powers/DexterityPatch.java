package vsthespire.patches.powers;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.powers.DexterityPower;
import vsthespire.powers.ChivalryPower;

public class DexterityPatch {
    @SpirePatch(clz = DexterityPower.class, method = "modifyBlock")
    public static class ChangeBlockPatch {
        public static SpireReturn<Float> Prefix(DexterityPower __instance, float amt) {
            if(!__instance.owner.isPlayer && __instance.owner.hasPower(ChivalryPower.ID)){
                return SpireReturn.Return(amt);
            }
            return SpireReturn.Continue();
        }
    }
}
