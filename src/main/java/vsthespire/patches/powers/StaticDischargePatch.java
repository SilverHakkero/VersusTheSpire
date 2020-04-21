package vsthespire.patches.powers;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.StaticDischargePower;
import vsthespire.VsTheSpire;
import vsthespire.actions.ChivalryModeAction;
import vsthespire.actions.RivalDoneAction;
import vsthespire.actions.VsRivalWaitAction;
import vsthespire.monsters.Rival;
import vsthespire.powers.ChivalryPower;

public class StaticDischargePatch {
    @SpirePatch(clz = StaticDischargePower.class, method = "onAttacked")
    public static class RequestOrbEffectsPatch {
        public static SpireReturn<Integer> Prefix(StaticDischargePower __instance, DamageInfo info, int damageAmount) {
            if(__instance.owner instanceof Rival) {
                AbstractDungeon.actionManager.addToTop(new VsRivalWaitAction(VsTheSpire.netIO, __instance.owner));
                return SpireReturn.Return(damageAmount);
            }
            if(__instance.owner.isPlayer && __instance.owner.hasPower(ChivalryPower.ID) && VsTheSpire.isConnected) {
                AbstractDungeon.actionManager.addToTop(new ChivalryModeAction(__instance.owner, ChivalryPower.DamageMode.SILENT));
                AbstractDungeon.actionManager.addToTop(new RivalDoneAction(__instance.owner));
            }
            return SpireReturn.Continue();
        }

        public static void Postfix(StaticDischargePower __instance) {
            if(__instance.owner.isPlayer && __instance.owner.hasPower(ChivalryPower.ID) && VsTheSpire.isConnected) {
                AbstractDungeon.actionManager.addToTop(new ChivalryModeAction(__instance.owner, ChivalryPower.DamageMode.CHARGE));
            }
        }
    }
}
