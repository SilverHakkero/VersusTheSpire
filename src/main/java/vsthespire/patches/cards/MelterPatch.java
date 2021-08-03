package vsthespire.patches.cards;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.blue.Melter;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import vsthespire.VsTheSpire;
import vsthespire.powers.ChivalryPower;

public class MelterPatch {
    @SpirePatch(clz = Melter.class, method = "use")
    public static class RemoveBlockPatch{
        public static void Postfix(){
            if(VsTheSpire.isConnected && AbstractDungeon.player.hasPower(ChivalryPower.ID)){
                VsTheSpire.netIO.trySend("loseBlock;you");
            }
        }
    }
}
