package vsthespire.patches.powers;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.EnvenomPower;
import com.megacrit.cardcrawl.powers.PoisonPower;
import vsthespire.powers.ChivalryPower;


public class EnvenomPatch {
    @SpirePatch(clz = EnvenomPower.class, method = "onAttack")
    public static class IgnoreBlockPatch {
        public static SpireReturn Prefix(EnvenomPower __instance, DamageInfo info, int damageAmount, AbstractCreature target) {
            if(__instance.owner.hasPower(ChivalryPower.ID)) {
                if(__instance.owner.isPlayer && info.type == DamageInfo.DamageType.NORMAL
                        && ((ChivalryPower)__instance.owner.getPower(ChivalryPower.ID)).getDamageMode() == ChivalryPower.DamageMode.CHARGE) {
                    AbstractDungeon.actionManager.addToTop(new ApplyPowerAction(target, __instance.owner,
                            new PoisonPower(target, __instance.owner, __instance.amount), __instance.amount, true));
                }
                return SpireReturn.Return(null);
            }

            return SpireReturn.Continue();
        }
    }
}