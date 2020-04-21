package vsthespire.powers.relicpowers;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.core.AbstractCreature;
import vsthespire.powers.VsAbstractPower;

public class CalipersPower extends VsAbstractPower implements InvisiblePower {
    public static final String ID = "vsthespire:calipers";


    public CalipersPower(AbstractCreature owner) {
        super(ID, "Calipers", owner);
    }
}
