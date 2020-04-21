//See also MonsterPreTurnLogicPatch
package vsthespire.patches.powers;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.powers.BlurPower;

public class BlurPatch {
    private static boolean isJustApplied = false;

    @SpirePatch(clz = BlurPower.class, method = SpirePatch.CONSTRUCTOR)
    public static class BlurConstructorPatch {
        public static void PostFix(BlurPower __instance) {
            if(!__instance.owner.isPlayer) {
                isJustApplied = true;
            }
        }
    }

    @SpirePatch(clz = BlurPower.class, method = "atEndOfRound")
    public static class IsJustAppliedPatch {
        public static SpireReturn PreFix(BlurPower __instance) {
            if(!__instance.owner.isPlayer) {
                if(isJustApplied) {
                    isJustApplied = false;
                    return SpireReturn.Return(null);
                }
            }
            return SpireReturn.Continue();
        }
    }
}
