package vsthespire.patches.powers;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.powers.watcher.MarkPower;
import vsthespire.powers.ChivalryPower;

public class MarkPatch {
    @SpirePatch(clz = MarkPower.class, method = "triggerMarks")
    public static class ReplaceHPLossWithStatus {
        public static SpireReturn Prefix(MarkPower __instance) {
            if(__instance.owner.hasPower(ChivalryPower.ID)){
                //TODO Give rival paralysis
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }
}
