package vsthespire.patches.powers;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDiscardAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.PoisonPower;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import vsthespire.cards.Poison;
import vsthespire.powers.ChivalryPower;

import java.util.ArrayList;

public class PoisonPatch {
    @SpirePatch(clz = PoisonPower.class, method = "atStartOfTurn")
    public static class ReplaceHPLossWithStatus {
        @SpireInsertPatch(locator = Locator.class)
        public static SpireReturn Insert(PoisonPower __instance) {
            if(__instance.owner.hasPower(ChivalryPower.ID)) {
                if(__instance.owner.isPlayer) {
                    AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDiscardAction(new Poison(), 1));
                }
                AbstractDungeon.actionManager.addToBottom(new ReducePowerAction(__instance.owner, __instance.owner,
                        PoisonPower.POWER_ID, 1));

                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(PoisonPower.class, "addToBot");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
            }
        }
    }

}
