package vsthespire.patches.cards;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.purple.Vault;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import vsthespire.powers.ChivalryPower;

public class VaultPatch {
    @SpirePatch(clz = Vault.class, method = "use")
    public static class NoHitOrPassPatch {
        public static void Prefix() {
            if(AbstractDungeon.player.hasPower(ChivalryPower.ID)) {
                ((ChivalryPower)AbstractDungeon.player.getPower(ChivalryPower.ID)).takeExtraTurn();
            }
        }
    }
}
