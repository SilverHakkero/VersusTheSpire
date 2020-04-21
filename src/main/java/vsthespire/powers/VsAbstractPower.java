package vsthespire.powers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;

public abstract class VsAbstractPower extends AbstractPower {

    public VsAbstractPower(String id, String name, AbstractCreature owner) {
        this.ID = id;
        this.name = name;
        this.owner = owner;
    }
}
