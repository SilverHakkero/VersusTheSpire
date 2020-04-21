package vsthespire.patches.powers;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import vsthespire.powers.ChivalryPower;

public class VulnerablePatch {
    @SpirePatch(clz = VulnerablePower.class, method = "atDamageReceive")
    public static class NoMonsterDamageBoostPatch {
        public static SpireReturn<Float> Prefix(VulnerablePower __instance, float damage, DamageInfo.DamageType type) {
            if(__instance.owner.isPlayer && __instance.owner.hasPower(ChivalryPower.ID) && type == DamageInfo.DamageType.NORMAL) {
                return SpireReturn.Return(damage);
            }
            return SpireReturn.Continue();
        }

    }
}
