package sim;

import action.NeedDelta;

public class Needs {
    public int hunger = 50, energy = 50, hygiene = 50, fun = 50;

    public void apply(NeedDelta d){
        hunger += d.hungerDelta;
        energy += d.energyDelta;
        hygiene += d.hygieneDelta;
        fun += d.funDelta;
        ClampAll();
    }

    private void ClampAll(){
        hunger = clamp(hunger);
        energy = clamp(energy);
        hygiene = clamp(hygiene);
        fun = clamp(fun);
    }


    private int clamp(int v){return Math.max(0, Math.min(100, v));}

    @Override
    public String toString(){
        return String.format("Hunger=%d Energy=%d Hygiene=%d Fun=%d", hunger, energy, hygiene, fun);
    }
}
