package action;

public class NeedDelta {
    public int hungerDelta;
    public int energyDelta;
    public int hygieneDelta;
    public int funDelta;

    public NeedDelta() {}

    public NeedDelta(int hunger, int energy, int hygiene, int fun){
        this.hungerDelta = hunger;
        this.energyDelta = energy;
        this.hygieneDelta = hygiene;
        this.funDelta = fun;
    }
}
