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
import vsthespire.VsTheSpire;
import vsthespire.monsters.Rival;

public class MirrorShard extends CustomRelic implements OnPlayerDeathRelic{
    public static final String ID = "vsthespire:mirrorshard";

    public MirrorShard() {
        super(ID, new Texture(VsTheSpire.IMAGEPATH + "relics/MirrorShard.png"),
                RelicTier.SPECIAL, LandingSound.CLINK);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    @Override
    public void onEquip() {
        this.counter = 1;
    }

    //call this to increment the shard count after your first shard
    public void gainShard() {
        this.counter++;
    }

    @Override
    public boolean onPlayerDeath(AbstractPlayer abstractPlayer, DamageInfo damageInfo) {
        if(this.counter < 1) {
            return true; //no shards left :(
        }
        else {
            flash();
            addToTop(new RelicAboveCreatureAction(AbstractDungeon.player, this));
            AbstractDungeon.player.heal(AbstractDungeon.player.maxHealth, true);
            AbstractDungeon.actionManager.addToBottom(new AddCardToDeckAction(
                    CardLibrary.getCard("Decay").makeCopy()));
            this.counter--;

            //run away if fighting your rival
            for(AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
                if(m instanceof Rival) {
                    AbstractDungeon.getCurrRoom().smoked = true;
                    AbstractDungeon.player.hideHealthBar();
                    AbstractDungeon.player.isEscaping = true;
                    AbstractDungeon.player.flipHorizontal = !AbstractDungeon.player.flipHorizontal;
                    AbstractDungeon.player.escapeTimer = 2.5F;
                }
            }
            return false;
        }
    }

    @Override
    public CustomRelic makeCopy() {
        return new MirrorShard();
    }
}
