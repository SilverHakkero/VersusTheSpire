package vsthespire.relics;

import com.evacipated.cardcrawl.mod.stslib.relics.OnPlayerDeathRelic;
import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;

import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.actions.unique.AddCardToDeckAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import vsthespire.VsTheSpire;
import vsthespire.monsters.Rival;

public class MirrorShard extends CustomRelic implements OnPlayerDeathRelic{
    public static final String ID = "vsthespire:mirrorshard";
    private static boolean usedThisCombat = false;

    public MirrorShard() {
        super(ID, new Texture(VsTheSpire.IMAGEPATH + "relics/MirrorShard.png"),
                RelicTier.SPECIAL, LandingSound.CLINK);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    @Override
    public void atPreBattle() {
        usedThisCombat = false;
    }

    @Override
    public boolean onPlayerDeath(AbstractPlayer abstractPlayer, DamageInfo damageInfo) {
        if(!usedThisCombat && AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT) {
            boolean fightingRival = false;
            for(AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
                if(m instanceof Rival) {
                    fightingRival = true;
                }
            }

            int healAmt = AbstractDungeon.player.maxHealth;
            if(!fightingRival) {
                healAmt = healAmt / 2;
                if(healAmt < 1) {
                    healAmt = 1;
                }
            }

            flash();
            addToTop(new RelicAboveCreatureAction(AbstractDungeon.player, this));
            AbstractDungeon.player.heal(healAmt, true);
            AbstractDungeon.actionManager.addToBottom(new AddCardToDeckAction(
                    CardLibrary.getCard("Decay").makeCopy()));
            usedThisCombat = true;
            this.grayscale = true;

            //run away if fighting your rival
            if(fightingRival) {
                AbstractDungeon.getCurrRoom().smoked = true;
                AbstractDungeon.player.hideHealthBar();
                AbstractDungeon.player.isEscaping = true;
                AbstractDungeon.player.flipHorizontal = !AbstractDungeon.player.flipHorizontal;
                AbstractDungeon.player.escapeTimer = 2.5F;
            }
            return false;
        }

        return true;
    }

    @Override
    public void justEnteredRoom(AbstractRoom room) {
        this.grayscale = false;
    }

    @Override
    public CustomRelic makeCopy() {
        return new MirrorShard();
    }
}
