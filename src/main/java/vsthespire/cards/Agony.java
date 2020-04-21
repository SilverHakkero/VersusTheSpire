package vsthespire.cards;

import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import vsthespire.VsTheSpire;

import java.util.ArrayList;

public class Agony extends CustomCard {
    public static final String ID = "vsthespire:agony";
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String[] EXTENDED_DESCRIPTIONS = cardStrings.EXTENDED_DESCRIPTION;
    public static final String IMG_PATH = VsTheSpire.IMAGEPATH + "cards/CardArtPlaceHolder.png";
    private static final int COST = 0;

    public Agony() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION, CardType.STATUS, CardColor.COLORLESS, CardRarity.COMMON, CardTarget.SELF);
        this.exhaust = true;
    }

    public void setX(int amount) {
        this.magicNumber = amount;
        this.baseMagicNumber = this.magicNumber;
        this.rawDescription = EXTENDED_DESCRIPTIONS[0];

        initializeDescription();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new LoseHPAction(p, p, this.magicNumber));
        addToBot(new DrawCardAction(1));
    }

    @Override
    public void upgrade() {}

    @Override
    public AbstractCard makeCopy() {
        return new Agony();
    }

    @Override
    public AbstractCard makeStatEquivalentCopy() {
        AbstractCard c = super.makeStatEquivalentCopy();
        c.baseMagicNumber = this.baseMagicNumber;
        c.magicNumber = this.magicNumber;
        c.description = (ArrayList) this.description.clone();
        return c;
    }
}
