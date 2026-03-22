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
 * Casino action: play Blackjack (21) against the dealer.
 *
 * <p>Rules:
 * <ul>
 *   <li>Player and dealer each start with 2 cards.</li>
 *   <li>Number cards 2-10 keep value, J/Q/K = 10, Ace = 11 or 1.</li>
 *   <li>Dealer must hit until 17 or higher.</li>
 *   <li>Natural blackjack (exact 21 in first two cards) pays 3x.</li>
 * </ul>
 */
public final class Blackjack implements Action {
    @Override
    public String name() {
        return "Play Blackjack (21)";
    }

    @Override
    public String perform(Sim sim, GameContext ctx) {
        if (sim.getSimcoin() <= 0) {
            return "No Simcoin left. The blackjack table is out of reach.";
        }

        ActionUIAdapter ui = ctx;
        int bet = ui.intRange("Choose blackjack bet (0 to cancel, max $" + sim.getSimcoin() + "): $", 0, sim.getSimcoin());
        if (bet == 0) {
            return "You fold before the hand begins.";
        }

        if (bet < 0 || bet > sim.getSimcoin()) {
            return "Invalid bet amount.";
        }

        return playInteractiveRound(sim, ctx, bet);
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
            return "You fold before the hand begins.";
        }

        // Request mode (tests/non-interactive): basic hit/stand policy, no double.
        return playBasicRound(sim, bet);
    }

    private String playInteractiveRound(Sim sim, GameContext ctx, int bet) {
        sim.spendSimcoin(bet);
        sim.applyEffect(Effect.none()
                .plus(NeedType.FUN, +22)
                .plus(NeedType.ENERGY, -10)
                .plus(NeedType.BLADDER, -8));

        Hand player = new Hand();
        Hand dealer = new Hand();
        player.addCard(drawCard());
        player.addCard(drawCard());
        dealer.addCard(drawCard());
        dealer.addCard(drawCard());

        if (isNaturalBlackjack(player) || isNaturalBlackjack(dealer)) {
            return resolveInitialBlackjack(sim, bet, player, dealer);
        }

        boolean doubled = false;
        boolean firstDecision = true;
        ActionUIAdapter ui = ctx;

        while (!player.isBust() && player.total < 21) {
            if (player.cards >= 5 && player.total <= 21) {
                return resolveFiveCardCharlie(sim, bet, player, dealer, doubled);
            }

            String prompt = "Blackjack: Your total is " + player.total + " (Cards: " + player.cards + ")";
            int decision;

            if (firstDecision) {
                decision = ui.intRange(prompt + ". 1) Hit  2) Stand  3) Double: ", 1, 3);
            } else {
                decision = ui.intRange(prompt + ". 1) Hit  2) Stand: ", 1, 2);
            }

            if (decision == 2) {
                break;
            }

            if (firstDecision && decision == 3) {
                if (sim.getSimcoin() < bet) {
                    System.out.println("Not enough Simcoin to double down. Need an extra $" + bet + ".");
                    continue;
                }
                sim.spendSimcoin(bet);
                doubled = true;
                player.addCard(drawCard());
                // Double rule: exactly one additional card, then auto-stand.
                break;
            }

            player.addCard(drawCard());
            firstDecision = false;
        }

        return resolvePostPlayerTurn(sim, bet, player, dealer, doubled);
    }

    private String playBasicRound(Sim sim, int bet) {
        sim.spendSimcoin(bet);
        sim.applyEffect(Effect.none()
                .plus(NeedType.FUN, +22)
                .plus(NeedType.ENERGY, -10)
                .plus(NeedType.BLADDER, -8));

        Hand player = new Hand();
        Hand dealer = new Hand();
        player.addCard(drawCard());
        player.addCard(drawCard());
        dealer.addCard(drawCard());
        dealer.addCard(drawCard());

        if (isNaturalBlackjack(player) || isNaturalBlackjack(dealer)) {
            return resolveInitialBlackjack(sim, bet, player, dealer);
        }

        while (!player.isBust() && player.total < 21) {
            if (player.cards >= 5 && player.total <= 21) {
                return resolveFiveCardCharlie(sim, bet, player, dealer, false);
            }

            int decision = basicDecision(player.total);
            if (decision == 2) {
                break;
            }
            player.addCard(drawCard());
        }

        return resolvePostPlayerTurn(sim, bet, player, dealer, false);
    }

    private String resolvePostPlayerTurn(Sim sim, int bet, Hand player, Hand dealer, boolean doubled) {
        if (player.isBust()) {
            return bustedMessage(bet, player, dealer, sim.getSimcoin(), doubled);
        }

        // Dealer edge (slight): standard casino-style play to 17+ with proper Ace handling.
        while (dealer.total < 17) {
            dealer.addCard(drawCard());
        }

        // Small risk behavior: dealer occasionally hits a hard 17, allowing slight extra bust chance.
        if (dealer.total == 17 && dealer.softAces == 0
                && ThreadLocalRandom.current().nextInt(100) < 15) {
            dealer.addCard(drawCard());
        }

        if (dealer.isBust() || player.total > dealer.total) {
            int payout = doubled ? bet * 4 : bet * 2;
            sim.earnSimcoin(payout);
            return "BLACKJACK You: " + player.total + " (Cards: " + player.cards + ") | Dealer: "
                    + dealer.total + " (Cards: " + dealer.cards + ") - You win $" + payout
                    + (doubled ? " on a double down." : ".");
        }

        if (player.total == dealer.total) {
            int refund = doubled ? bet * 2 : bet;
            sim.earnSimcoin(refund);
            return "BLACKJACK Push. You: " + player.total + " (Cards: " + player.cards + ") | Dealer: "
                    + dealer.total + " (Cards: " + dealer.cards + "). Bet refunded ($" + refund + ").";
        }

        if (sim.getSimcoin() == 0) {
            return "BLACKJACK You: " + player.total + " (Cards: " + player.cards + ") | Dealer: "
                    + dealer.total + " (Cards: " + dealer.cards + ") - Dealer wins. You are now broke.";
        }

        return "BLACKJACK You: " + player.total + " (Cards: " + player.cards + ") | Dealer: "
                + dealer.total + " (Cards: " + dealer.cards + ") - Dealer wins.";
    }

    private String resolveInitialBlackjack(Sim sim, int bet, Hand player, Hand dealer) {
        boolean playerBj = isNaturalBlackjack(player);
        boolean dealerBj = isNaturalBlackjack(dealer);

        if (playerBj && dealerBj) {
            sim.earnSimcoin(bet);
            return "BLACKJACK Both sides have natural 21 (Cards: 2). Push. Bet refunded ($" + bet + ").";
        }

        if (playerBj) {
            int payout = bet * 3;
            sim.earnSimcoin(payout);
            return "BLACKJACK Natural 21 (Cards: 2)! You win $" + payout + ".";
        }

        if (sim.getSimcoin() == 0) {
            return "BLACKJACK Dealer has natural 21 (Cards: 2). You lost and are now broke.";
        }

        return "BLACKJACK Dealer has natural 21 (Cards: 2). You lost $" + bet + ".";
    }

    private String resolveFiveCardCharlie(Sim sim, int bet, Hand player, Hand dealer, boolean doubled) {
        int payout = doubled ? bet * 4 : bet * 2;
        sim.earnSimcoin(payout);
        return "BLACKJACK Five-Card Charlie! You reached " + player.total + " with " + player.cards
                + " cards and win immediately for $" + payout + ".";
    }

    private String bustedMessage(int bet, Hand player, Hand dealer, int remainingMoney, boolean doubled) {
        int lost = doubled ? bet * 2 : bet;
        if (remainingMoney == 0) {
            return "BLACKJACK You busted at " + player.total + " (Cards: " + player.cards + ")"
                    + " while dealer shows " + dealer.total + " (Cards: " + dealer.cards + ")."
                    + " You lost $" + lost + " and are now broke.";
        }

        return "BLACKJACK You busted at " + player.total + " (Cards: " + player.cards + ")"
                + " while dealer shows " + dealer.total + " (Cards: " + dealer.cards + ")."
                + " You lost $" + lost + ".";
    }

    private boolean isNaturalBlackjack(Hand hand) {
        return hand.cards == 2 && hand.total == 21;
    }

    private int basicDecision(int playerTotal) {
        return playerTotal < 16 ? 1 : 2;
    }

    private int drawCard() {
        int raw = ThreadLocalRandom.current().nextInt(1, 14);
        if (raw == 1) {
            return 11;
        }
        if (raw >= 10) {
            return 10;
        }
        return raw;
    }

    /**
     * Mutable hand state with automatic Ace adjustment (11 -> 1) when busting.
     */
    private static final class Hand {
        private int total;
        private int cards;
        private int softAces;

        private void addCard(int value) {
            cards++;
            total += value;
            if (value == 11) {
                softAces++;
            }

            while (total > 21 && softAces > 0) {
                total -= 10;
                softAces--;
            }
        }

        private boolean isBust() {
            return total > 21;
        }
    }
}
