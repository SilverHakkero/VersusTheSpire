package vsthespire;


import basemod.helpers.RelicType;
import basemod.interfaces.*;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;

import basemod.BaseMod;
import basemod.ModPanel;
import basemod.ModLabel;
import basemod.ModButton;
import basemod.ModLabeledToggleButton;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.megacrit.cardcrawl.localization.*;

import com.megacrit.cardcrawl.unlock.UnlockTracker;
import vsthespire.cards.*;
import vsthespire.events.*;
import vsthespire.relics.*;
import vsthespire.monsters.*;
import vsthespire.net.*;

@SpireInitializer
public class VsTheSpire implements PostInitializeSubscriber,
        StartGameSubscriber,
        EditStringsSubscriber,
        EditRelicsSubscriber,
        EditCardsSubscriber {

    public static final String MODNAME = "Versus The Spire";
    public static final String AUTHOR = "SilverHakkero";
    public static final String DESCRIPTION = "Mod to allow two players to play against each other as they climb.";
    public static final String IMAGEPATH = "VsTheSpireResources/images/";
    public static final String STRINGPATH = "VsTheSpireResources/localization/";

    public static final float ONLINETOGGLE_X = 400.0f;
    public static final float ONLINETOGGLE_Y = 700.0f;
    public static final float IPDIRECT_X = 400.0f;
    public static final float IPDIRECT_Y = 650.0f;
    public static final float IPBUTTON_X = 400.0f;
    public static final float IPBUTTON_Y = 500.0f;
    public static final float IPLABEL_X = 525.0f;
    public static final float IPLABEL_Y = 550.0f;
    public static final float PORTBUTTON_X = 400.0f;
    public static final float PORTBUTTON_Y = 400.0f;
    public static final float PORTLABEL_X = 525.0f;
    public static final float PORTLABEL_Y = 450.0f;

    public static NetworkManager netManager;
    public static VsNetIO netIO = null;

    private boolean onlineEnabled = false;
    public static boolean isConnected = false;
    private String IPString = "127.0.0.1";
    private String PortString = "9999";
    private int optionSelected = 0;

    private String validChars = "abcdefghijklmnopqrstuvwxyz.-:0123456789";

    private InputProcessor oldInputProcessor;

    public VsTheSpire() {
        BaseMod.subscribe(this);
        netManager = new NetworkManager();
    }


    @SuppressWarnings("unused")
    public static void initialize() {
        new VsTheSpire();
    }

    @Override
    public void receivePostInitialize() {
        //----Config Panel start----//

        //Load texture for mod badge
        Texture badgeTexture = new Texture(IMAGEPATH + "VsModBadge.png");

        //create settings panel
        ModPanel settingsPanel = new ModPanel();

        //Add option to turn online on/off
        ModLabeledToggleButton enableOnineButton = new ModLabeledToggleButton("Enable Online Mode",
                ONLINETOGGLE_X, ONLINETOGGLE_Y, Settings.CREAM_COLOR, FontHelper.charDescFont, onlineEnabled,
                settingsPanel, (Label) -> {}, (button) -> {onlineEnabled = button.enabled;}
        );
        settingsPanel.addUIElement(enableOnineButton);

        //Add IP and port edit directions
        ModLabel directionsLabel = new ModLabel(
                "Click the buttons to edit IP and port, then hit enter. The host puts 'host' for IP.",
                IPDIRECT_X, IPDIRECT_Y, settingsPanel, (me) -> {});
        settingsPanel.addUIElement(directionsLabel);

        //Add button to edit IP
        ModButton IPButton = new ModButton(IPBUTTON_X, IPBUTTON_Y, settingsPanel, (me) -> {
            this.IPString = "";
            me.parent.waitingOnEvent = true;
            this.optionSelected = 1;
            oldInputProcessor = Gdx.input.getInputProcessor();
            Gdx.input.setInputProcessor(new InputAdapter() {
                @Override
                public boolean keyTyped(char myChar) {
                    if(validChars.contains(Character.toString(myChar))) {
                        IPString += myChar;
                    }
                    return true;
                }
                @Override
                public boolean keyUp(int keycode) {
                    if(keycode == Keys.ENTER) {
                        me.parent.waitingOnEvent = false;
                        Gdx.input.setInputProcessor(oldInputProcessor);
                        optionSelected = 0;
                    }
                    return true;
                }
            });
        });
        settingsPanel.addUIElement(IPButton);

        //Add label showing IP
        ModLabel IPLabel = new ModLabel("", IPLABEL_X, IPLABEL_Y, settingsPanel, (me) -> {
            if(me.parent.waitingOnEvent && optionSelected == 1) {
                me.text = IPString + "_";
            }
            else {
                me.text = IPString;
            }
        });
        settingsPanel.addUIElement(IPLabel);

        //Add button to edit port
        ModButton portButton = new ModButton(PORTBUTTON_X, PORTBUTTON_Y, settingsPanel, (me) -> {
            this.PortString = "";
            me.parent.waitingOnEvent = true;
            this.optionSelected = 2;
            oldInputProcessor = Gdx.input.getInputProcessor();
            Gdx.input.setInputProcessor(new InputAdapter() {
                @Override
                public boolean keyTyped(char myChar) {
                    if(validChars.contains(Character.toString(myChar))) {
                        PortString += myChar;
                    }
                    return true;
                }
                @Override
                public boolean keyUp(int keycode) {
                    if(keycode == Keys.ENTER) {
                        me.parent.waitingOnEvent = false;
                        optionSelected = 0;
                        Gdx.input.setInputProcessor(oldInputProcessor);
                    }
                    return true;
                }
            });});
        settingsPanel.addUIElement(portButton);

        //Add label showing port
        ModLabel portLabel = new ModLabel(PortString, PORTLABEL_X, PORTLABEL_Y, settingsPanel, (me) -> {
            if(me.parent.waitingOnEvent && optionSelected == 2) {
                me.text = PortString + "_";
            }
            else {
                me.text = PortString;
            }
        });
        settingsPanel.addUIElement(portLabel);

        BaseMod.registerModBadge(badgeTexture, MODNAME, AUTHOR, DESCRIPTION, settingsPanel);
        //----Config Panel end----//

        BaseMod.addEvent("vsthespire:mirrorevent", MirrorEvent.class);

        BaseMod.addMonster(Rival.ID, () -> new Rival());
    }

    @Override
    public void receiveEditStrings() {
        BaseMod.loadCustomStringsFile(EventStrings.class, STRINGPATH + "VsEventStrings.json");
        BaseMod.loadCustomStringsFile(RelicStrings.class, STRINGPATH + "VsRelicStrings.json");
        BaseMod.loadCustomStringsFile(MonsterStrings.class, STRINGPATH + "VsMonsterStrings.json");
        BaseMod.loadCustomStringsFile(PowerStrings.class, STRINGPATH + "VsPowerStrings.json");
        BaseMod.loadCustomStringsFile(CardStrings.class, STRINGPATH + "VsCardStrings.json");
    }

    @Override
    public void receiveEditCards() {
        BaseMod.addCard(new Lamentation());
        BaseMod.addCard(new Poison());
        BaseMod.addCard(new Agony());
        BaseMod.addCard(new Judged());
        BaseMod.addCard(new Paralysis());
        UnlockTracker.unlockCard(Lamentation.ID);
        UnlockTracker.unlockCard(Poison.ID);
        UnlockTracker.unlockCard(Agony.ID);
        UnlockTracker.unlockCard(Judged.ID);
        UnlockTracker.unlockCard(Paralysis.ID);
    }

    @Override
    public void receiveEditRelics() {
        BaseMod.addRelic(new MirrorShard(), RelicType.SHARED);
    }

    @Override
    public void receiveStartGame() {
        if(onlineEnabled) {
            isConnected = true;
            if (netManager.initialize(IPString, PortString)) {
                if (netManager.isServer() && !netManager.tryBind()) {
                    isConnected = false;
                }
            } else {
                isConnected = false;
            }
        }
    }
}
