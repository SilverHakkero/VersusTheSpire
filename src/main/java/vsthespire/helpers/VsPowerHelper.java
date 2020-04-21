//Class that helps with applying powers requested by the other player
package vsthespire.helpers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.*;
import com.megacrit.cardcrawl.powers.watcher.*;
import vsthespire.monsters.Rival;
import vsthespire.powers.*;

public class VsPowerHelper {
    //Gives the power you need
    public static AbstractPower makePower(AbstractCreature target, String key, int stacks) {
        AbstractPower power = null;
        if(key.startsWith(TheBombPower.POWER_ID)) {
            power = new MirroredPower(new TheBombPower(target, 3, stacks));
        }
        switch(key) {
            //new powers
            case AggroPower.ID:
                power = new AggroPower(target, stacks);
                break;
            case PassiveAggroPower.ID:
                power = new PassiveAggroPower(target, stacks);
                break;

                //buffs
            case ArtifactPower.POWER_ID:
                power = new ArtifactPower(target, stacks);
                break;
            case BarricadePower.POWER_ID:
                power = new BarricadePower(target);
                break;
            case BufferPower.POWER_ID:
                power = new BufferPower(target, stacks);
                break;
            case DexterityPower.POWER_ID:
                power = new DexterityPower(target, stacks);
                break;
            case DrawCardNextTurnPower.POWER_ID:
                power = new MirroredPower(new DrawCardNextTurnPower(target, stacks), true);
                break;
            case EnergizedPower.POWER_ID:
                power = new MirroredPower(new EnergizedPower(target, stacks), true);
                break;
            case EnergizedBluePower.POWER_ID:
                power = new MirroredPower(new EnergizedBluePower(target, stacks), true);
                break;
            case FocusPower.POWER_ID:
                power = new FocusPower(target, stacks);
                break;
            case IntangiblePlayerPower.POWER_ID:
            case IntangiblePower.POWER_ID:
                power = new IntangiblePower(target, stacks);
                break;
            case MantraPower.POWER_ID:
                power = new MantraPower(target, stacks);
                break;
            case MetallicizePower.POWER_ID:
                power = new MirroredPower(new MetallicizePower(target, stacks));
                break;
            case PlatedArmorPower.POWER_ID:
                power = new PlatedArmorPower(target, stacks);
                break;
            case RitualPower.POWER_ID:
                power = new MirroredPower(new RitualPower(target, stacks, false));
                break;
            case StrengthPower.POWER_ID:
                power = new StrengthPower(target, stacks);
                break;
            case ThornsPower.POWER_ID:
                power = new ThornsPower(target, stacks);
                break;
            case VigorPower.POWER_ID:
                power = new MirroredPower(new VigorPower(target, stacks), MirroredPower.TypeRemoval.ATTACK);
                break;
            case AccuracyPower.POWER_ID:
                power = new MirroredPower(new AccuracyPower(target, stacks));
                break;
            case AfterImagePower.POWER_ID:
                power = new MirroredPower(new AfterImagePower(target, stacks));
                break;
            case AmplifyPower.POWER_ID:
                power = new MirroredPower(new AmplifyPower(target, stacks), true, MirroredPower.TypeRemoval.POWER);
                break;
            case BattleHymnPower.POWER_ID:
                power = new MirroredPower(new BattleHymnPower(target, stacks));
                break;
            case BerserkPower.POWER_ID:
                power = new MirroredPower(new BerserkPower(target, stacks));
                break;
            case EndTurnDeathPower.POWER_ID:
                power = new EndTurnDeathPower(target);
                break;
            case BlurPower.POWER_ID:
                power = new BlurPower(target, stacks);
                break;
            case BrutalityPower.POWER_ID:
                power = new MirroredPower(new BrutalityPower(target, stacks));
                break;
            case BurstPower.POWER_ID:
                power = new MirroredPower(new BurstPower(target, stacks), true, MirroredPower.TypeRemoval.SKILL);
                break;
            case CollectPower.POWER_ID:
                power = new MirroredPower(new CollectPower(target, stacks), MirroredPower.TypeRemoval.TURN);
                break;
            case CombustPower.POWER_ID:
                power = new MirroredPower(new CombustPower(target, 1, stacks));
                break;
            case CorruptionPower.POWER_ID:
                power = new MirroredPower(new CorruptionPower(target));
                break;
            case CreativeAIPower.POWER_ID:
                power = new MirroredPower(new CreativeAIPower(target, stacks));
                break;
            case DarkEmbracePower.POWER_ID:
                power = new MirroredPower(new DarkEmbracePower(target, stacks));
                break;
            case DemonFormPower.POWER_ID:
                power = new MirroredPower(new DemonFormPower(target, stacks));
                break;
            case DevaPower.POWER_ID:
                power = new MirroredPower(new DevaPower(target));
                break;
            case DevotionPower.POWER_ID:
                power = new MirroredPower(new DevotionPower(target, stacks));
                break;
            case DoubleDamagePower.POWER_ID:
                power = new MirroredPower(new DoubleDamagePower(target, stacks, false), MirroredPower.TypeRemoval.TURN);
                break;
            case DoubleTapPower.POWER_ID:
                power = new MirroredPower(new DoubleTapPower(target, stacks), true, MirroredPower.TypeRemoval.ATTACK);
                break;
            case DuplicationPower.POWER_ID:
                power = new MirroredPower(new DuplicationPower(target, stacks), true, MirroredPower.TypeRemoval.CARD);
                break;
            case EchoPower.POWER_ID:
                power = new MirroredPower(new EchoPower(target, stacks));
                break;
            case ElectroPower.POWER_ID:
                power = new ElectroPower(target);
                break;
            case EnvenomPower.POWER_ID:
                power = new MirroredPower(new EnvenomPower(target, stacks));
                break;
            case EquilibriumPower.POWER_ID:
                power = new MirroredPower(new EquilibriumPower(target, stacks), MirroredPower.TypeRemoval.TURN);
                break;
            case EstablishmentPower.POWER_ID:
                power = new MirroredPower(new EstablishmentPower(target, stacks));
                break;
            case EvolvePower.POWER_ID:
                power = new MirroredPower(new EvolvePower(target, stacks));
                break;
            case FeelNoPainPower.POWER_ID:
                power = new MirroredPower(new FeelNoPainPower(target, stacks));
                break;
            case FireBreathingPower.POWER_ID:
                power = new MirroredPower(new FireBreathingPower(target, stacks));
                break;
            case FlameBarrierPower.POWER_ID:
                power = new FlameBarrierPower(target, stacks);
                break;
            case ForesightPower.POWER_ID:
                power = new MirroredPower(new ForesightPower(target, stacks));
                break;
            case FreeAttackPower.POWER_ID:
                power = new MirroredPower(new FreeAttackPower(target, stacks), MirroredPower.TypeRemoval.ATTACK);
                break;
            case HeatsinkPower.POWER_ID:
                power = new MirroredPower(new HeatsinkPower(target, stacks));
                break;
            case HelloPower.POWER_ID:
                power = new MirroredPower(new HelloPower(target, stacks));
                break;
            case InfiniteBladesPower.POWER_ID:
                power = new MirroredPower(new InfiniteBladesPower(target, stacks));
                break;
            case JuggernautPower.POWER_ID:
                power = new MirroredPower(new JuggernautPower(target, stacks));
                break;
            case LikeWaterPower.POWER_ID:
                power = new MirroredPower(new LikeWaterPower(target, stacks));
                break;
            case LoopPower.POWER_ID:
                power = new MirroredPower(new LoopPower(target, stacks));
                break;
            case DrawPower.POWER_ID:
                power = new MirroredPower(new DrawPower(target, stacks));
                break;
            case MagnetismPower.POWER_ID:
                power = new MirroredPower(new MagnetismPower(target ,stacks));
                break;
            case MasterRealityPower.POWER_ID:
                power = new MirroredPower(new MasterRealityPower(target));
                break;
            case MentalFortressPower.POWER_ID:
                power = new MirroredPower(new MentalFortressPower(target, stacks));
                break;
            case NightmarePower.POWER_ID:
                power = new FakeNightmarePower(target, stacks);
                break;
            case NoxiousFumesPower.POWER_ID:
                power = new MirroredPower(new NoxiousFumesPower(target, stacks));
                break;
            case OmegaPower.POWER_ID:
                power = new MirroredPower(new OmegaPower(target, stacks));
                break;
            case PanachePower.POWER_ID:
                power = new FakePanachePower(target, stacks);
                break;
            case PenNibPower.POWER_ID:
                power = new MirroredPower(new PenNibPower(target, stacks), MirroredPower.TypeRemoval.ATTACK);
                break;
            case PhantasmalPower.POWER_ID:
                power = new MirroredPower(new PhantasmalPower(target, stacks), MirroredPower.TypeRemoval.TURN);
                break;
            case RagePower.POWER_ID:
                power = new MirroredPower(new RagePower(target, stacks), true);
                break;
            case ReboundPower.POWER_ID:
                power = new MirroredPower(new ReboundPower(target), true, MirroredPower.TypeRemoval.CARD);
                break;
            case RegenPower.POWER_ID:
                power = new MirroredPower(new RegenPower(target, stacks), MirroredPower.TypeRemoval.TURN);
                break;
            case RushdownPower.POWER_ID:
                power = new MirroredPower(new RushdownPower(target, stacks));
                break;
            case RupturePower.POWER_ID:
                power = new MirroredPower(new RupturePower(target, stacks));
                break;
            case SadisticPower.POWER_ID:
                power = new MirroredPower(new SadisticPower(target, stacks));
                break;
            case WrathNextTurnPower.POWER_ID:
                power = new MirroredPower(new WrathNextTurnPower(target), true);
                break;
            case StaticDischargePower.POWER_ID:
                power = new StaticDischargePower(target, stacks);
                break;
            case StormPower.POWER_ID:
                power = new MirroredPower(new StormPower(target, stacks));
                break;
            case StudyPower.POWER_ID:
                power = new MirroredPower(new StudyPower(target, stacks));
                break;
            case ThousandCutsPower.POWER_ID:
                power = new MirroredPower(new ThousandCutsPower(target, stacks));
                break;
            case ToolsOfTheTradePower.POWER_ID:
                power = new MirroredPower(new ToolsOfTheTradePower(target, stacks));
                break;
            case WaveOfTheHandPower.POWER_ID:
                power = new MirroredPower(new WaveOfTheHandPower(target, stacks), true);
                break;
            case RetainCardPower.POWER_ID:
                power = new MirroredPower(new RetainCardPower(target, stacks));
                break;

                //debuffs
            case FrailPower.POWER_ID:
                power = new FrailPower(target, stacks, true);
                break;
            case VulnerablePower.POWER_ID:
                power = new VulnerablePower(target, stacks, false);
                break;
            case WeakPower.POWER_ID:
                power = new WeakPower(target, stacks, false);
                break;
            case BiasPower.POWER_ID:
                power = new BiasPower(target, stacks);
                break;
            case ChokePower.POWER_ID:
                power = new PlayerChokePower(target, stacks);
                break;
            case ConfusionPower.POWER_ID:
                power = new MirroredPower(new ConfusionPower(target));
                break;
            case CorpseExplosionPower.POWER_ID:
                power = new MirroredPower(new CorpseExplosionPower(target));
                break;
            case LoseDexterityPower.POWER_ID:
                power = new MirroredPower(new LoseDexterityPower(target, stacks), true);
                break;
            case EnergyDownPower.POWER_ID:
                power = new MirroredPower(new EnergyDownPower(target, stacks));
                break;
            case MarkPower.POWER_ID:
                power = new MarkPower(target, stacks);
                break;
            case NoDrawPower.POWER_ID:
                power = new MirroredPower(new NoDrawPower(target), true);
                break;
            case NoBlockPower.POWER_ID:
                power = new MirroredPower(new NoBlockPower(target, stacks, false), MirroredPower.TypeRemoval.TURN);
                break;
            case PoisonPower.POWER_ID:
                power = new PoisonPower(target, AbstractDungeon.getCurrRoom().monsters.getMonster(Rival.ID), stacks);
                break;
            case GainStrengthPower.POWER_ID:
                power = new GainStrengthPower(target, stacks);
                break;
            case LoseStrengthPower.POWER_ID:
                power = new MirroredPower(new LoseStrengthPower(target, stacks), true);
                break;
            case BlockReturnPower.POWER_ID:
                power = new MirroredPower(new BlockReturnPower(target, stacks));
                break;
            case WraithFormPower.POWER_ID:
                power = new MirroredPower(new WraithFormPower(target, stacks));
        }
        return power;
    }
}
