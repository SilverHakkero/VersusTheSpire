package vsthespire.patches.powers;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.powers.StrengthPower;
import vsthespire.powers.ChivalryPower;

public class StrengthPatch {
    @SpirePatch(clz = StrengthPower.class, method = "atDamageGive")
    public static class NoDamageBoostMonsterPatch{
        public static SpireReturn<Float> Prefix(StrengthPower __instance, float damage) {
            if(!__instance.owner.isPlayer && __instance.owner.hasPower(ChivalryPower.ID)) {
                return SpireReturn.Return(damage);
            }
            return SpireReturn.Continue();
        }
    }
}
