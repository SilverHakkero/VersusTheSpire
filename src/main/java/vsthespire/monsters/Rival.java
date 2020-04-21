package vsthespire.monsters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.*;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateFastAttackAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import vsthespire.VsTheSpire;
import vsthespire.actions.ChivalryModeAction;
import vsthespire.actions.VsRivalWaitAction;
import vsthespire.powers.AggroPower;
import vsthespire.powers.ChivalryPower;
import vsthespire.powers.PassiveAggroPower;
import vsthespire.powers.WrathPower;

import java.util.ArrayList;

public class Rival extends AbstractMonster {
    public static final String ID = "vsthespire:rival";
    private static final MonsterStrings monsterstrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterstrings.NAME; // The name of the monster
    public static final String[] MOVES = monsterstrings.MOVES; // The names of the moves
    public static final String[] DIALOG = monsterstrings.DIALOG; // The dialogue (if any)

    public static String rivalClass = "Null";   //set this here for testing purposes
    public static int rivalMaxHP = 80;
    public static int rivalCurrHP = 60;
    public static boolean hasBonus = true; //If true, gain bonus damage. Otherwise take your turn.

    //watcher eye stuff
    private Bone eyeBone;
    protected TextureAtlas eyeAtlas = null;
    protected Skeleton eyeSkeleton;
    public AnimationState eyeState;
    protected AnimationStateData eyeStateData;

    private static final float HB_X = 0.0f;
    private static final float HB_Y = 0.0f;
    //TODO need to adjust hb w and h to match the class
    private static final float HB_W = 260.0f;
    private static final float HB_H = 260.0f;

    private static final byte ATTACK = 1;
    private static final byte NOTHING = 2;

    private final int BONUS_DMG = 8;

    public Rival(float x, float y){
        super(NAME, ID, rivalMaxHP, HB_X, HB_Y, HB_W, HB_H, null, x, y);
        switch(Rival.rivalClass){
            case "Null":
            case "Ironclad":
                loadAnimation("images/characters/ironclad/idle/skeleton.atlas",
                        "images/characters/ironclad/idle/skeleton.json", 1.0F);
                break;
            case "Silent":
                loadAnimation("images/characters/theSilent/idle/skeleton.atlas",
                        "images/characters/theSilent/idle/skeleton.json", 1.0F);
                break;
            case "Defect":
                loadAnimation("images/characters/defect/idle/skeleton.atlas",
                        "images/characters/defect/idle/skeleton.json", 1.0F);
                break;
            case "Watcher":
                //the watcher's eye is a pain...
                loadAnimation("images/characters/watcher/idle/skeleton.atlas",
                        "images/characters/watcher/idle/skeleton.json", 1.0F);
                loadEyeAnimation();
                this.eyeBone = this.skeleton.findBone("eye_anchor");
        }


        AnimationState.TrackEntry e = this.state.setAnimation(0, "Idle", true);
        this.stateData.setMix("Hit", "Idle", 0.1F);
        e.setTime(e.getEndTime() * MathUtils.random());
        //add hit animation?

        this.flipHorizontal = true;

        this.damage.add(new DamageInfo(this, 0));   //Aggro damage
        this.damage.add(new DamageInfo(this, 0, DamageInfo.DamageType.THORNS));   //Passive damage

    }

    public Rival() {
        this(0.0F, 0.0F);
    }

    //borrowed from the watcher, obviously
    private void loadEyeAnimation() {
        this.eyeAtlas = new TextureAtlas(Gdx.files.internal("images/characters/watcher/eye_anim/skeleton.atlas"));
        SkeletonJson json = new SkeletonJson(this.eyeAtlas);
        json.setScale(Settings.scale / 1.0F);
        SkeletonData skeletonData = json.readSkeletonData(Gdx.files.internal("images/characters/watcher/eye_anim/skeleton.json"));
        this.eyeSkeleton = new Skeleton(skeletonData);
        this.eyeSkeleton.setColor(Color.WHITE);
        this.eyeStateData = new AnimationStateData(skeletonData);
        this.eyeState = new AnimationState(this.eyeStateData);
        this.eyeStateData.setDefaultMix(0.2F);
        this.eyeState.setAnimation(0, "None", true);
    }

    //patches to render the eye (so much work for a little eye)
    @SpirePatch(clz = AbstractMonster.class, method = "render")
    public static class EyeRenderPatch {
        public static void Prefix(AbstractMonster __instance) {
            if(__instance instanceof Rival) {
                if(((Rival) __instance).eyeAtlas != null) {
                    ((Rival) __instance).eyeState.update(Gdx.graphics.getDeltaTime());
                    ((Rival) __instance).eyeState.apply(((Rival) __instance).eyeSkeleton);
                    ((Rival) __instance).eyeSkeleton.updateWorldTransform();
                    ((Rival) __instance).eyeSkeleton.setPosition(((Rival) __instance).skeleton.getX() + ((Rival) __instance).eyeBone.getWorldX(),
                            ((Rival) __instance).skeleton.getY() + ((Rival) __instance).eyeBone.getWorldY());
                    ((Rival) __instance).eyeSkeleton.setColor(__instance.tint.color);
                    ((Rival) __instance).eyeSkeleton.setFlip(((Rival) __instance).flipHorizontal, ((Rival) __instance).flipVertical);
                }
            }
        }

        @SpireInsertPatch(locator = Locator.class)
        public static void Insert(AbstractMonster __instance) {
            if(__instance instanceof Rival)
                if(((Rival) __instance).eyeAtlas != null) {
                    sr.draw(CardCrawlGame.psb, ((Rival) __instance).eyeSkeleton);
                }
        }

        private static class Locator extends SpireInsertLocator{
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(PolygonSpriteBatch.class, "end");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
            }
        }

    }

    //getting hit animation
    @Override
    public void damage(DamageInfo info) {
        super.damage(info);
        if (info.owner != null && info.type != DamageInfo.DamageType.THORNS && info.output > 0) {
            this.state.setAnimation(0, "Hit", false);
            this.state.addAnimation(0, "Idle", true, 0.0F);
        }
    }

    //changeState to control stance animations


    @Override
    public void changeState(String stateName) {
        switch(stateName) {
            case "empty":
            case "calm":
            case "divinity":
                if(this.hasPower(WrathPower.POWER_ID)){
                    addToBot(new RemoveSpecificPowerAction(this, this, WrathPower.POWER_ID));
                }
                break;
            case "wrath":
                addToBot(new ApplyPowerAction(this, this, new WrathPower(this)));
        }
    }

    @Override
    public void usePreBattleAction() {
        if(rivalCurrHP < rivalMaxHP) {
            this.currentHealth = rivalCurrHP;
            this.healthBarUpdatedEvent();
        }

        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new ChivalryPower(this)));
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new ChivalryPower(AbstractDungeon.player)));
        //TODO use hidden aggro for monster if the player has runic dome instead in this file and chivalry power

        //TODO apply hidden powers that handle effects of certain relics (Ex: Calipers)

        if(Rival.hasBonus) {
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this,
                    new PassiveAggroPower(this, BONUS_DMG)));
        }
        else {
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this,
                    new PassiveAggroPower(AbstractDungeon.player, BONUS_DMG)));
            //monster takes it's first turn to get first set of aggro and passive if doesn't get bonus.
            AbstractDungeon.actionManager.addToBottom(new VsRivalWaitAction(VsTheSpire.netIO, this));
        }
    }

    @Override
    public void takeTurn() {
        //Set Chivalry to STRIKE
        AbstractDungeon.actionManager.addToBottom(new ChivalryModeAction(this, ChivalryPower.DamageMode.STRIKE));

        //Set info 1 to current passive, deal thorns damage equal to info 1, and remove passive
        if(this.hasPower(PassiveAggroPower.ID)) {
            this.damage.set(1, new DamageInfo(this, this.getPower(PassiveAggroPower.ID).amount, DamageInfo.DamageType.THORNS));
            AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(1),
                    AbstractGameAction.AttackEffect.NONE));
            AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(this, this, PassiveAggroPower.ID));
        }

        //Deal normal damage equal to info 0 and remove aggro
        if(this.hasPower(AggroPower.ID)) {
            AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(0),
                    AbstractGameAction.AttackEffect.BLUNT_LIGHT));
            AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(this, this, AggroPower.ID));
        }

        //Set Chivalry to CHARGE and opponent's Chivalry to BLOCK
        AbstractDungeon.actionManager.addToBottom(new ChivalryModeAction(this, ChivalryPower.DamageMode.CHARGE));
        AbstractDungeon.actionManager.addToBottom(new ChivalryModeAction(AbstractDungeon.player, ChivalryPower.DamageMode.BLOCK));

        //get this monster's player actions
        AbstractDungeon.actionManager.addToBottom(new VsRivalWaitAction(VsTheSpire.netIO, this));

        //AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new AggroPower(this, 9)));
        //AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new PassiveAggroPower(this, 5)));

        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    @Override
    protected void getMove(int i) {
        //convert aggro to intent and set info 0
        if(this.hasPower(AggroPower.ID)) {
            this.damage.get(0).base = this.getPower(AggroPower.ID).amount;
            //TODO change intent type to reflect attack effects like reaper
            this.setMove(ATTACK, Intent.ATTACK, this.damage.get(0).base);
        }
        else {
            this.damage.get(0).base = 0;
            this.setMove(NOTHING, Intent.UNKNOWN);
        }

        createIntent();
    }
}
