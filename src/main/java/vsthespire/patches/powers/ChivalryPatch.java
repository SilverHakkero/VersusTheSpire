package vsthespire.patches.powers;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import vsthespire.powers.ChivalryPower;

import java.util.ArrayList;

public class ChivalryPatch {
    @SpirePatch(clz = AbstractMonster.class, method = "damage")
    public static class ModifyMonsterDamagePreBlockPatch {
        @SpireInsertPatch(locator = Locator.class, localvars = {"info"})
        public static void Insert(AbstractMonster __instance, @ByRef DamageInfo[] info) {
            AbstractCreature source = info[0].owner;
            if(source == null){
                source = AbstractDungeon.player;
            }
            if(source.hasPower(ChivalryPower.ID)) {
                info[0].output = ((ChivalryPower)source.getPower(ChivalryPower.ID)).onAttackToChangeDamagePreBlock(info[0], info[0].output);
            }
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractMonster.class, "hasPower");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
            }
        }
    }

    @SpirePatch(clz = AbstractPlayer.class, method = "damage")
    public static class ModifyPlayerDamagePreBlockPatch {
        @SpireInsertPatch(locator = Locator2.class, localvars = {"damageAmount"})
        public static void Insert(AbstractPlayer __instance, DamageInfo info, @ByRef int[] damageAmount) {
            if(info.owner.hasPower(ChivalryPower.ID)) {
                damageAmount[0] = ((ChivalryPower)info.owner.getPower(ChivalryPower.ID)).onAttackToChangeDamagePreBlock(info, damageAmount[0]);
            }
        }

        private static class Locator2 extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractPlayer.class, "hasPower");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
            }
        }
    }
}
