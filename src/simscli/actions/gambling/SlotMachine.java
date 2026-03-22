package simscli.actions.gambling;

import java.util.concurrent.ThreadLocalRandom;
import simscli.actions.Action;
import simscli.actions.ActionUIAdapter;
import simscli.actions.request.ActionRequest;
import simscli.actions.request.AmountActionRequest;
import simscli.game.GameContext;
import simscli.sims.Sim;
import simscli.stats.Effect;
import simscli.stats.NeedType;

/**
 * Casino action: play the Slot Machine for random payouts.
 *
 * <p>Payout table by 3-slot result:
 * <ul>
 *   <li>Jackpot (Seven-Seven-Seven): 25x bet</li>
 *   <li>Other triple match: 10x bet</li>
 *   <li>Any double match: 2x bet</li>
 *   <li>No match: lose bet</li>
 * </ul>
 *
 * <p>Designed with a house edge so gambling is exciting but not a guaranteed
 * path to easy money.</p>
 */
public final class SlotMachine implements Action {
    private static final String[] REELS = {
        "Cherry", "Bell", "Star", "Seven", "Diamond", "Bar",
        "Lemon", "Horseshoe", "Clover", "Coin", "Crown", "Anchor"
    };

    @Override
    public String name() {
        return "Play Slot Machine";
    }

    @Override
    public String perform(Sim sim, GameContext ctx) {
        if (sim.getSimcoin() <= 0) {
            return "No Simcoin left. The machines are silent for now.";
        }

        ActionUIAdapter ui = ctx;
        int bet = ui.intRange("Choose slot bet (0 to cancel, max $" + sim.getSimcoin() + "): $", 0, sim.getSimcoin());
        return perform(sim, ctx, new AmountActionRequest(bet));
    }

    @Override
    public String perform(Sim sim, GameContext ctx, ActionRequest request) {
        if (!(request instanceof AmountActionRequest amountRequest)) {
            return perform(sim, ctx);
        }

        int bet = amountRequest.amount();
        if (bet < 0 || bet > sim.getSimcoin()) {
            return "Invalid bet amount.";
        }
        if (bet == 0) {
            return "You step away from the slot machine.";
        }

        int r1 = ThreadLocalRandom.current().nextInt(REELS.length);
        int r2 = ThreadLocalRandom.current().nextInt(REELS.length);
        int r3 = ThreadLocalRandom.current().nextInt(REELS.length);

        String symbols = "[" + REELS[r1] + " | " + REELS[r2] + " | " + REELS[r3] + "]";

        sim.spendSimcoin(bet);
        sim.applyEffect(Effect.none()
            .plus(NeedType.FUN, +16)
            .plus(NeedType.ENERGY, -8)
            .plus(NeedType.BLADDER, -6));

        boolean triple = r1 == r2 && r2 == r3;
        boolean hasPair = r1 == r2 || r2 == r3 || r1 == r3;
        boolean tripleSevens = triple && "Seven".equals(REELS[r1]);

        if (tripleSevens) {
            int payout = bet * 25;
            sim.earnSimcoin(payout);
            return "SLOTS " + symbols + " MEGA JACKPOT! You win $" + payout
                    + " on a $" + bet + " bet. The floor erupts.";
        }

        if (triple) {
            int payout = bet * 10;
            sim.earnSimcoin(payout);
            return "SLOTS " + symbols + " JACKPOT! You win $" + payout
                    + " on a $" + bet + " bet.";
        }

        if (hasPair) {
            int payout = bet * 2;
            sim.earnSimcoin(payout);
            return "SLOTS " + symbols + " Nice hit! You win $" + payout
                    + " on a $" + bet + " bet.";
        }

        if (sim.getSimcoin() == 0) {
            return "SLOTS " + symbols + " Tough break. You lost $" + bet
                    + " and you're now completely broke.";
        }

        return "SLOTS " + symbols + " No luck this spin. You lost $" + bet + ".";
    }
}
