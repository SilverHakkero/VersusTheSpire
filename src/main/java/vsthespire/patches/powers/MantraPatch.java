package vsthespire.patches.powers;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.watcher.MantraPower;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.ArrayList;

public class MantraPatch {
    @SpirePatch(clz = MantraPower.class, method = "stackPower")
    public static class NoDivinityPatch {
        @SpireInsertPatch(locator = Locator.class)
        public static SpireReturn Insert(MantraPower __instance) {
            if(!__instance.owner.isPlayer) {
                __instance.amount -= 10;
                if (__instance.amount <= 0)
                    AbstractDungeon.actionManager.addToTop(new RemoveSpecificPowerAction(__instance.owner, __instance.owner, "Mantra"));
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(MantraPower.class, "addToTop");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
            }
        }
    }
}
