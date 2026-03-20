package simscli.location;

import java.util.ArrayList;
import java.util.List;
import simscli.actions.Action;
import simscli.actions.pet.BuyPet;
import simscli.actions.interactive.GroomPetMenu;
import simscli.actions.interactive.PlayWithPetMenu;
import simscli.actions.interactive.FeedPetMenu;
import simscli.sims.Sim;

/**
 * Pet Store location: buy and groom pets.
 */
public final class PetStore extends Location {
    @Override
    public String key() {
        return "petstore";
    }

    @Override
    public String name() {
        return "Pet Store";
    }

    @Override
    public List<Action> actions(Sim sim) {
        List<Action> actions = new ArrayList<>();

        // Always available
        actions.add(new BuyPet());

        // Only available if Sim has pets
        if (sim != null && !sim.getPets().isEmpty()) {
            actions.add(new FeedPetMenu());
            actions.add(new GroomPetMenu());
            actions.add(new PlayWithPetMenu());
        }

        return actions;
    }
}
