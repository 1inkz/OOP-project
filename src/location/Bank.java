package location;

import action.Action;
import action.actions.bank.*;
import sim.Sim;
import world.World;

import java.util.List;

public class Bank extends Location{
    public Bank() {super("Bank");}

    @Override public List<Action> availableActions(Sim sim, World world){
        return List.of(
                new GetBalanceAction(),
                new DepositAction(),
                new WithdrawAction(),
                new TakeLoanAction(),
                new RepayLoanAction()
        );

    }
}
