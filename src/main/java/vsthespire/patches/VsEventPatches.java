package vsthespire.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;

import com.megacrit.cardcrawl.map.RoomTypeAssigner;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.rooms.EventRoom;
import com.megacrit.cardcrawl.random.Random;

import vsthespire.VsTheSpire;
import vsthespire.events.MirrorEvent;

public class VsEventPatches {
    public static boolean hasGottenEvent = false;

    @SpirePatch(clz = AbstractDungeon.class, method = "generateMap")
    public static class MapPatch {
        @SpireInsertPatch(rloc = 20)
        public static void Insert() {
            if (VsTheSpire.isConnected && !hasGottenEvent) {
                RoomTypeAssigner.assignRowAsRoomType(AbstractDungeon.map.get(1), EventRoom.class);
            }
        }
    }

    @SpirePatch(clz = AbstractDungeon.class, method = "generateEvent")
    public static class EventGenPatch {
        public static SpireReturn<AbstractEvent> Prefix(final Random rng) {
            if (VsTheSpire.isConnected && !hasGottenEvent) {
                AbstractDungeon.eventList.remove(MirrorEvent.ID);
                return SpireReturn.Return(new MirrorEvent());
            }
            return SpireReturn.Continue();
        }
    }
}

//line 613 for map
//line 2338 for event