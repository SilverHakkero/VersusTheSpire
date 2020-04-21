//handles messages between players

package vsthespire.net;

import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;
import com.megacrit.cardcrawl.vfx.combat.SmokeBombEffect;
import vsthespire.VsTheSpire;
import vsthespire.actions.VsRivalWaitAction;
import vsthespire.helpers.VsPowerHelper;
import vsthespire.monsters.Rival;
import vsthespire.powers.interfaces.OnRivalPlayCardPower;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;


public class VsNetIO{
    private BufferedReader myIn;
    private PrintWriter myOut;

    public VsNetIO(BufferedReader newIn, PrintWriter newOut) {
        this.myIn = newIn;
        this.myOut = newOut;
    }

    //Attempts to write the given msg to the other player
    public boolean trySend(String msg) {
        myOut.println(msg);
        return !myOut.checkError();
    }

    //checks to see if a message was received from the other player
    public String tryReceive() {
        String msg = "";
        try {
            msg = myIn.readLine();
        } catch (IOException e) {}
        return msg;
    }

    //check to see if a message has been received
    public boolean hasInput() {
        boolean bool = false;
        try {
            bool = myIn.ready();
        } catch (IOException e) {
            //close connection
        }
        return bool;
    }

    //used when the players connect. gets the other player's class, max hp, and current hp
    public boolean getRivalData() {
        boolean isSuccess = true;
        String rivalData;

        //class
        rivalData = tryReceive();

        if(rivalData.equals("Null") || rivalData.isEmpty()) {
            isSuccess = false;
        }

        Rival.rivalClass = rivalData;

        //hp values
        rivalData = tryReceive();
        try {
            Rival.rivalMaxHP = Integer.parseInt(rivalData);
        } catch(NumberFormatException e) {
            isSuccess = false;
        }
        rivalData = tryReceive();
        try {
            Rival.rivalCurrHP = Integer.parseInt(rivalData);
        } catch(NumberFormatException e) {
            isSuccess = false;
        }

        //turn order
        if(!VsTheSpire.netManager.isServer()) {
            rivalData = tryReceive();
            if(rivalData.isEmpty())
                isSuccess = false;
            Rival.hasBonus = !Boolean.parseBoolean(rivalData);
        }

        return isSuccess;
    }

    public boolean giveMyData() {
        boolean isSuccess;
        String dataString = "Null";
        AbstractPlayer.PlayerClass c = AbstractDungeon.player.chosenClass;
        switch(c) {
            case IRONCLAD:
                dataString = "Ironclad";
                break;
            case THE_SILENT:
                dataString = "Silent";
                break;
            case DEFECT:
                dataString = "Defect";
                break;
            case WATCHER:
                dataString = "Watcher";
        }

        isSuccess = trySend(dataString);

        dataString = Integer.toString(AbstractDungeon.player.maxHealth);
        isSuccess = trySend(dataString) && isSuccess;
        dataString = Integer.toString(AbstractDungeon.player.currentHealth);
        isSuccess = trySend(dataString) && isSuccess;

        //determine who goes first or gets bonus damage
        if(VsTheSpire.netManager.isServer()){
            boolean bonus = AbstractDungeon.miscRng.randomBoolean();
            Rival.hasBonus = bonus;
            dataString = Boolean.toString(bonus);
            isSuccess = trySend(dataString) && isSuccess;
        }

        return isSuccess;
    }

    public boolean handShake() {
        boolean a, b;
        if(VsTheSpire.netManager.isServer()){
            a = getRivalData();
            b = giveMyData();
        }
        else {
            a = giveMyData();
            b = getRivalData();
        }
        //System.out.println("A is " + a + " and B is " + b);
        return a && b;
    }

    //interprets the data given
    public void interpret(String msg, AbstractCreature owner) {
        //messages are of format
        //"type;arg1;arg2;...;argn"
        //There will always be at least one arg. Is "null" when none are needed.
        int i;
        String type, args;

        System.out.println("Received " + msg);

        i = msg.indexOf(';');
        if(i != -1) {
            type = msg.substring(0, i);
            args = msg.substring(i+1);
            //System.out.println("msg received of type: " + type);
            switch(type) {
                case "done":
                    //Indicates the rival is done taking their turn.
                    // arg 1 is null.
                    AbstractDungeon.actionManager.addToBottom(new RollMoveAction((AbstractMonster)owner));
                    break;
                case "playCard":
                    //Show a card in the center of the screen the rival has played.
                    // arg 1 is card Key, arg 2 is # of upgrades, arg 3 is misc (usually 0).

                    //TODO only show card if player doesn't have runic dome
                    i = args.indexOf(';');
                    String cardKey = args.substring(0, i);
                    args = args.substring(i+1);
                    i = args.indexOf(';');
                    int upgradeCount = Integer.parseInt(args.substring(0, i));
                    int misc = Integer.parseInt(args.substring(i + 1));

                    AbstractCard c = CardLibrary.getCopy(cardKey, upgradeCount, misc);
                    AbstractDungeon.effectList.add(new ShowCardBrieflyEffect(c));
                    AbstractDungeon.actionManager.addToBottom(new VsRivalWaitAction(VsTheSpire.netIO, owner));

                    for(AbstractPower p : owner.powers) {
                        if(p instanceof OnRivalPlayCardPower) {
                            ((OnRivalPlayCardPower) p).onRivalPlayCard(c);
                        }
                    }
                    for(AbstractPower p : AbstractDungeon.player.powers) {
                        if(p instanceof OnRivalPlayCardPower) {
                            ((OnRivalPlayCardPower) p).onRivalPlayCard(c);
                        }
                    }
                    break;
                case "applyPower":
                    //Used when the rival applies a power to someone.
                    // arg 1 is the target of the power,
                    // "me" refers to the rival, "you" is the player
                    // arg 2 is the ID of the power. arg 3 is the number of stacks.
                    i = args.indexOf(';');
                    String targetString = args.substring(0, i);
                    args = args.substring(i+1);
                    AbstractCreature target;
                    if(targetString.equals("me")){
                        target = owner;
                    }
                    else {
                        target = AbstractDungeon.player;
                    }
                    i = args.indexOf(';');
                    String key = args.substring(0, i);
                    args = args.substring(i + 1);
                    int amount = Integer.parseInt(args);
                    AbstractPower p = VsPowerHelper.makePower(target, key, amount);
                    if(p != null) {
                        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(target, owner, p));
                    }
                    AbstractDungeon.actionManager.addToBottom(new VsRivalWaitAction(VsTheSpire.netIO, owner));
                    break;
                case "gainBlock":
                    //Gives the rival block.
                    // arg 1 is amount.
                    int blockGain = Integer.parseInt(args);
                    AbstractDungeon.actionManager.addToBottom(new GainBlockAction(owner, blockGain));
                    AbstractDungeon.actionManager.addToBottom(new VsRivalWaitAction(VsTheSpire.netIO, owner));
                    break;
                case "heal":
                    //Heals the rival.
                    // arg 1 is hp healed.
                    break;
                case "gainMaxHp":
                    //Increases the rival's max hp. (fruit juice)
                    // arg 1 is the amount.
                    break;
                case "loseHp":
                    //Reduces the rival's hp. Generally used for self damage like offering or brutality.
                    // arg 1 is amount.
                    int damage = Integer.parseInt(args);
                    AbstractDungeon.actionManager.addToBottom(new LoseHPAction(owner, owner, damage));
                    AbstractDungeon.actionManager.addToBottom(new VsRivalWaitAction(VsTheSpire.netIO, owner));
                    break;
                case "usePotion":
                    //Plays the bouncing flask animation based on the target.
                    // arg 1 is target of the potion, "me" or "you"
                    break;
                case "giveCard":
                    //Adds a card to the discard. Usually a status.
                    // arg 1 is card key, arg 2 is # of upgrades, arg 3 is misc.
                    i = args.indexOf(';');
                    String newCardKey = args.substring(0, i);
                    args = args.substring(i+1);
                    i = args.indexOf(';');
                    int newUpgradeCount = Integer.parseInt(args.substring(0, i));
                    int newMisc = Integer.parseInt(args.substring(i + 1));

                    AbstractCard card = CardLibrary.getCopy(newCardKey, newUpgradeCount, newMisc);
                    AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDiscardAction(card, 1));
                    AbstractDungeon.actionManager.addToBottom(new VsRivalWaitAction(VsTheSpire.netIO, owner));
                    break;
                case "flee":
                    //Makes the rival flee. (smoke bomb).
                    // arg 1 is null.
                    //TODO implement send for this
                    if(owner instanceof AbstractMonster){
                        AbstractDungeon.actionManager.addToBottom(new VFXAction(new SmokeBombEffect(owner.hb.cX, owner.hb.cY)));
                        AbstractDungeon.actionManager.addToBottom(new EscapeAction((AbstractMonster) owner));
                    }
                    break;
                case "channelOrb":
                    //puts an orb in the rival's slots. Automatically pops the oldest one if full.
                    // arg 1 is the ID of the orb to channel.
                    break;
                case "removeOrb":
                    //Removes the rival's oldest orb. Called by evoking or just remove via fission.
                    //arg 1 is null.
                    break;
                case "changeSlots":
                    //Increases or decreases the number of orb slots the rival has.
                    // arg 1 is number to add. Negative removes orb slots.
                    break;
                case "changeStance":
                    //Changes the rival's stance.
                    // arg 1 is stance to enter: "empty" "calm" "wrath" or "divinity"
                    if(owner instanceof Rival){
                        ((Rival)owner).changeState(args);
                    }
                    AbstractDungeon.actionManager.addToBottom(new VsRivalWaitAction(VsTheSpire.netIO, owner));
                    break;
                default:
                    //the input is no good
            }
        }
    }
}
