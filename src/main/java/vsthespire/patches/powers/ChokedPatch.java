package vsthespire.patches.powers;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.powers.ChokePower;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import vsthespire.powers.ChivalryPower;

import java.util.ArrayList;

public class ChokedPatch {
    @SpirePatch(clz = ChokePower.class, method = "onUseCard")
    public static class NoHpLossPatch {
        @SpireInsertPatch(locator = Locator.class)
        public static SpireReturn Insert(ChokePower __instance){
            if(__instance.owner.hasPower(ChivalryPower.ID)) {
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }

    private static class Locator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(ChokePower.class, "addToBot");
            return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
        }
    }
}
