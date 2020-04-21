package vsthespire.cards;

import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.PoisonPower;
import vsthespire.VsTheSpire;

public class Poison extends CustomCard {
    public static final String ID = "vsthespire:poison";
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String[] EXTENDED_DESCRIPTION = cardStrings.EXTENDED_DESCRIPTION;
    public static final String IMG_PATH = VsTheSpire.IMAGEPATH + "cards/CardArtPlaceHolder.png";
    private static final int COST = 1;

    private static boolean isHpLost = false;

    public Poison() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION, CardType.STATUS, CardColor.COLORLESS, CardRarity.COMMON, CardTarget.NONE);

        this.baseMagicNumber = 0;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if(this.dontTriggerOnUseCard) {
            addToBot(new LoseHPAction(p, p, this.baseMagicNumber, AbstractGameAction.AttackEffect.POISON));
            isHpLost = false;       //reset after cards have been queued
        }
    }

    @Override
    public void triggerOnEndOfTurnForPlayingCard() {
        this.dontTriggerOnUseCard = true;
        applyPowers();
        if(!isHpLost) {
            this.exhaust = true;
            AbstractDungeon.actionManager.cardQueue.add(new CardQueueItem(this, true));
            if(this.baseMagicNumber > 0) {
                isHpLost = true;
            }
        }
    }

    @Override
    public void applyPowers() {
        if(AbstractDungeon.player.hasPower(PoisonPower.POWER_ID)){
            this.baseMagicNumber = AbstractDungeon.player.getPower(PoisonPower.POWER_ID).amount;
        }
        else {
            this.baseMagicNumber = 0;
        }
        super.applyPowers();
        this.rawDescription = DESCRIPTION + EXTENDED_DESCRIPTION[0];
        initializeDescription();
    }

    @Override
    public void upgrade() {}

    @Override
    public AbstractCard makeCopy() {
        return new Poison();
    }
}
