package vsthespire.patches.potions;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.potions.SmokeBomb;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import vsthespire.VsTheSpire;
import vsthespire.powers.ChivalryPower;

public class SmokeBombPatch {
    @SpirePatch(clz = SmokeBomb.class, method = "use")
    public static class TellOtherPlayerOnSmoke {
        public static void Prefix(){
            if(AbstractDungeon.player.hasPower(ChivalryPower.ID)
                    && AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT
                    && VsTheSpire.isConnected){
                VsTheSpire.netIO.trySend("flee;null");
            }
        }
    }
}
