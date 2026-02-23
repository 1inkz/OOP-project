package sim;

import action.Action;
import action.Outcome;
import trait.Trait;

import java.util.ArrayList;
import java.util.List;

public class Sim {
    private final String name;
    private int money;
    private final Needs needs = new Needs();
    private final BankBalance bankbalance = new BankBalance();
    private final List<Trait> traits = new ArrayList<>();

    public Sim(String name, int money){
        this.name = name;
        this.money =money;
    }

    public String getName(){return name;}
    public int getMoney(){return money;}
    public Needs getNeeds(){return needs;}
    public BankBalance getBankbalance(){return bankbalance;}

    public void addTrait(Trait trait){traits.add(trait);}

    public boolean canDo(Action action){
        for(Trait t: traits){
            if(!t.allowAction(action, this))return false;
        }
        return true;
    }

    public void applyTraitEffects(Action action, Outcome out){
        for(Trait t: traits){
            t.modifyOutcome(action, this, out);
        }
    }

    public void apply (Outcome out){
        money += out.moneyDelta;
        needs.apply(out.needDelta);
    }

    public String traitsString(){
        if(traits.isEmpty()) return "(none)";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < traits.size(); i++){
            if (i > 0) sb.append(",");
            sb.append((traits.get(i).name()));
        }
        return sb.toString();
    }
}
