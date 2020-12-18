package vsthespire.powers;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.BetterOnApplyPowerPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.*;
import com.megacrit.cardcrawl.powers.watcher.VigorPower;
import com.megacrit.cardcrawl.stances.AbstractStance;
import com.megacrit.cardcrawl.stances.CalmStance;
import com.megacrit.cardcrawl.stances.DivinityStance;
import com.megacrit.cardcrawl.stances.WrathStance;
import vsthespire.VsTheSpire;
import vsthespire.actions.ChivalryModeAction;

import static com.badlogic.gdx.math.MathUtils.floor;

public class ChivalryPower extends VsAbstractPower implements BetterOnApplyPowerPower {
    public static final String ID = "vsthespire:chivalry";
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    //private static final Texture img84 = new Texture(VsTheSpire.IMAGEPATH + "powers/Chivalry_84.png");
    //private static final Texture img32 = new Texture(VsTheSpire.IMAGEPATH + "powers/Chivalry_32.png");
    private DamageMode mode;


    public enum DamageMode{
        STRIKE,     //When you are attacking with aggro and passive.
                    // Damage you generate can hit their target.
        CHARGE,     //During your turn.
                    // Damage you generate is stored as aggro or passive.
        SILENT,     //When you are attacked with aggro.
                    //  Stores like CHARGE, but doesn't send messages. For reflect style damage.
        BLOCK       //During the rest of your opponents turn.
                    // All damage you generate is negated and ignored.
    }


    public ChivalryPower(AbstractCreature owner) {
        super(ID, NAME, owner);
        this.mode = DamageMode.CHARGE;
        //image stuffs
        //this.region128 = new TextureAtlas.AtlasRegion(img84, 0, 0, 84, 84);
        //this.region48 = new TextureAtlas.AtlasRegion(img32, 0, 0, 32, 32);
        loadRegion("book");
        updateDescription();
    }

    //Use VsRivalWaitAction instead, this is too fast for most purposes
    public void setDamageMode(DamageMode m) {
        this.mode = m;
    }

    public DamageMode getDamageMode() {return this.mode;}

    public int onAttackToChangeDamagePreBlock(DamageInfo info, int damage) {
        //hp loss goes through (handled by onAttackedToChangeDamage)
        //strike mode also lets damage through
        if(info.type != DamageInfo.DamageType.HP_LOSS && this.mode != DamageMode.STRIKE) {
            flash();
            //convert damage to powers if charging or silent
            if(this.mode == DamageMode.CHARGE || this.mode == DamageMode.SILENT) {
                if(info.type == DamageInfo.DamageType.NORMAL)
                    addToBot(new ApplyPowerAction(this.owner, this.owner, new AggroPower(this.owner, damage)));
                else
                    addToBot(new ApplyPowerAction(this.owner, this.owner, new PassiveAggroPower(this.owner, damage)));
            }
            //negate damage
            damage = 0;
        }
        return damage;
    }

    /*
    @Override
    public int onAttackedToChangeDamage(DamageInfo info, int damageAmount) {
        //prevent hp loss that is not from self

        if(info.owner != this.owner && info.type == DamageInfo.DamageType.HP_LOSS) {
            flash();
            return 0;
        }

        return super.onAttackedToChangeDamage(info, damageAmount);
    }
    */

    @Override
    public void atStartOfTurn() {
        //cause shackled to wear off at the start of the turn instead of the end
        if(this.owner.hasPower(GainStrengthPower.POWER_ID)) {
            addToBot(new ChivalryModeAction(this.owner, DamageMode.SILENT));    //prevent Rival gaining strength twice.
            this.owner.getPower(GainStrengthPower.POWER_ID).atEndOfTurn(true);
            addToBot(new ChivalryModeAction(this.owner, DamageMode.CHARGE));
        }

        this.setDamageMode(DamageMode.CHARGE);
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        if(isPlayer && VsTheSpire.isConnected)
            VsTheSpire.netIO.trySend("done;null");
        setDamageMode(DamageMode.SILENT);
    }

    @Override
    public void onUseCard(AbstractCard c, UseCardAction a) {
        if(VsTheSpire.isConnected && this.owner.isPlayer && this.mode != DamageMode.SILENT) {
            String msg = "playCard;" + c.cardID + ";" + c.timesUpgraded + ";" + c.misc;
            //System.out.println(msg);
            VsTheSpire.netIO.trySend(msg);
        }
    }

    @Override
    public void onGainedBlock(float blockAmount) {
        if(VsTheSpire.isConnected && this.owner.isPlayer && this.mode != DamageMode.SILENT && blockAmount > 0.0F){
            String msg = "gainBlock;" + floor(blockAmount);
            VsTheSpire.netIO.trySend(msg);
        }
    }

    @Override
    public void wasHPLost(DamageInfo info, int damageAmount) {
        if(VsTheSpire.isConnected && damageAmount > 0 && info.owner == this.owner && this.owner.isPlayer && this.mode != DamageMode.SILENT) {
            String msg = "loseHp;" + damageAmount;
            VsTheSpire.netIO.trySend(msg);
        }
    }

    @Override
    public void onChangeStance(AbstractStance oldStance, AbstractStance newStance) {
        if(VsTheSpire.isConnected && this.mode != DamageMode.SILENT) {
            String stanceString = "empty";
            if (newStance instanceof CalmStance) {
                stanceString = "calm";
            }
            if (newStance instanceof WrathStance) {
                stanceString = "wrath";
            }
            if (newStance instanceof DivinityStance) {
                stanceString = "divinity";
            }
            VsTheSpire.netIO.trySend("changeStance;" + stanceString);
        }
    }



    @Override
    public boolean betterOnApplyPower(AbstractPower p, AbstractCreature target, AbstractCreature source) {
        if(VsTheSpire.isConnected && (this.owner == source || source == null) && this.owner.isPlayer && this.mode != DamageMode.SILENT) {
            String targetString;
            if(target.isPlayer) {
                targetString = "me";
            }
            else {
                targetString = "you";
            }
            String pID = p.ID;
            String stackAmount;
            if(p instanceof PanachePower || p instanceof TheBombPower) {
                stackAmount = Integer.toString((Integer) ReflectionHacks.getPrivate(p, PanachePower.class, "damage"));
            }
            else {
                stackAmount = Integer.toString(p.amount);
            }
            String msg = "applyPower;" + targetString + ";" + pID + ";" + stackAmount;
            //System.out.println(msg);
            VsTheSpire.netIO.trySend(msg);
        }
        if(target != this.owner && source == this.owner && p instanceof StrengthPower
                && target.hasPower(AggroPower.ID) && !target.hasPower(ArtifactPower.POWER_ID) && p.amount < 0) {
            ((AggroPower)target.getPower(AggroPower.ID)).reduceAggroByStrength(-p.amount);
        }

        return true;
    }


    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }
}
