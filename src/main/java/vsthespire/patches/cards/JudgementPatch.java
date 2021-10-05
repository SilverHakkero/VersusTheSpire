package vsthespire.patches.cards;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.actions.watcher.JudgementAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import vsthespire.VsTheSpire;
import vsthespire.cards.Judged;
import vsthespire.powers.ChivalryPower;

import java.util.ArrayList;

public class JudgementPatch {
    @SpirePatch(clz = JudgementAction.class, method = "update")
    public static class GiveStatusPatch{
        @SpireInsertPatch(locator = Locator.class)
        public static SpireReturn Insert(JudgementAction __instance){
            if(AbstractDungeon.player.hasPower(ChivalryPower.ID) && VsTheSpire.isConnected){
                VsTheSpire.netIO.trySend("giveCard;" + Judged.ID + ";0;0");
                __instance.isDone = true;
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(JudgementAction.class, "addToTop");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
            }
        }
    }
}
