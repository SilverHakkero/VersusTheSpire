package vsthespire.patches.relics;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.NeowsLament;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import vsthespire.VsTheSpire;
import vsthespire.cards.Lamentation;
import vsthespire.powers.ChivalryPower;

import java.util.ArrayList;

public class NeowsLamentPatch {
    @SpirePatch(clz = NeowsLament.class, method = "atBattleStart")
    public static class GiveStatusPatch {
        @SpireInsertPatch(locator = Locator.class)
        public static SpireReturn Insert() {
            AbstractMonster m = AbstractDungeon.getCurrRoom().monsters.monsters.get(0);
            if (m.hasPower(ChivalryPower.ID)) {
                VsTheSpire.netIO.trySend("giveCard;" + Lamentation.ID + ";0;0");
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(NeowsLament.class, "flash");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
            }
        }
    }
}
