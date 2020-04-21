package vsthespire.events;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.MonsterHelper;
import com.megacrit.cardcrawl.localization.EventStrings;


import com.megacrit.cardcrawl.monsters.MonsterGroup;
import vsthespire.VsTheSpire;
import vsthespire.monsters.Rival;
import vsthespire.net.VsNetIO;
import vsthespire.relics.MirrorShard;
import vsthespire.patches.VsEventPatches;

import java.io.IOException;

public class MirrorEvent extends AbstractImageEvent {
    public static final String ID = "vsthespire:mirrorevent";
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    public static final String NAME = eventStrings.NAME;
    public static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    public static final String[] OPTIONS = eventStrings.OPTIONS;
    private static final String INTRO_DIALOG = DESCRIPTIONS[0];
    private static final String SUCCESS_DIALOG = DESCRIPTIONS[1];
    private static final String POSTFIGHT_DIALOG = DESCRIPTIONS[2];
    private static final String RETRY_DIALOG = DESCRIPTIONS[3];
    private static final String LEAVE_DIALOG = DESCRIPTIONS[4];
    private CurScreen screen = CurScreen.INTRO;

    private enum CurScreen{
        INTRO, SUCCESS, POSTFIGHT, RETRY, LEAVE;
        CurScreen() {}
    }

    public MirrorEvent() {
        super(NAME, INTRO_DIALOG, VsTheSpire.IMAGEPATH + "events/DarkMirror.png");
        VsEventPatches.hasGottenEvent = true;

        this.imageEventText.setDialogOption(OPTIONS[0]);
        this.imageEventText.setDialogOption(OPTIONS[3]);
    }

    protected void buttonEffect(int buttonPressed) {
        boolean connectSuccess = false;
        switch(this.screen) {
            case INTRO:
            case RETRY:
                switch(buttonPressed) {
                    case 0:
                        if(VsTheSpire.isConnected) {
                            if(VsTheSpire.netManager.isServer()) {
                                if(VsTheSpire.netManager.tryAccept()) connectSuccess = true;
                            }
                            else {
                                if(VsTheSpire.netManager.tryConnect()) connectSuccess = true;
                            }
                            //try to get data streams
                            if(connectSuccess) {
                                try {
                                    VsTheSpire.netIO = new VsNetIO(VsTheSpire.netManager.getInput(),
                                            VsTheSpire.netManager.getOutput());
                                } catch(IOException e) {
                                    connectSuccess = false;
                                }
                            }

                            //try to get the opponents data
                            if(connectSuccess) {
                                connectSuccess = VsTheSpire.netIO.handShake();
                                //System.out.println("Handshake is " + connectSuccess);
                            }

                            if(connectSuccess) {
                                //congratulations!! you are connected!!!!
                                AbstractDungeon.getCurrRoom().spawnRelicAndObtain((Settings.WIDTH / 2),
                                        (Settings.HEIGHT / 2),new MirrorShard());
                                this.imageEventText.updateBodyText(SUCCESS_DIALOG);
                                this.imageEventText.updateDialogOption(0, OPTIONS[1]);
                                this.imageEventText.removeDialogOption(1);
                                this.screen = CurScreen.SUCCESS;
                            }
                            else {
                                this.imageEventText.updateBodyText(RETRY_DIALOG);
                                this.imageEventText.updateDialogOption(0, OPTIONS[2]);
                                this.screen = CurScreen.RETRY;
                            }
                        }
                        else {
                            this.imageEventText.updateBodyText(RETRY_DIALOG);
                            this.imageEventText.updateDialogOption(0, OPTIONS[2]);
                            this.screen = CurScreen.RETRY;
                        }
                        return;
                    case 1:
                        this.imageEventText.updateBodyText(LEAVE_DIALOG);
                        this.imageEventText.updateDialogOption(0, OPTIONS[3]);
                        this.imageEventText.removeDialogOption(1);
                        this.screen = CurScreen.LEAVE;
                }
                return;
            case SUCCESS:
                this.imageEventText.updateBodyText(POSTFIGHT_DIALOG);
                this.imageEventText.updateDialogOption(0, OPTIONS[3]);
                this.screen = CurScreen.POSTFIGHT;
                (AbstractDungeon.getCurrRoom()).monsters = new MonsterGroup(new Rival());
                enterCombatFromImage();
                return;
            case POSTFIGHT:
            case LEAVE:
                openMap();
                return;
        }
        openMap();
    }
}
