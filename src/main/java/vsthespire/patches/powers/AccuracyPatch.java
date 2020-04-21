package vsthespire.patches.powers;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.powers.AccuracyPower;

public class AccuracyPatch {
    @SpirePatch(clz = AccuracyPower.class, method = "updateExistingShivs")
    public static class noTouchShivsPatch {
        public static SpireReturn Prefix(AccuracyPower __instance) {
            if(!__instance.owner.isPlayer) {
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }
}
