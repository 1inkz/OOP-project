public class Needs {

    private int hunger;
    private int hygiene;
    private int energy;
    private int fun;
    private int social;

    public Needs() {
        this.hunger = 100;
        this.hygiene = 100;
        this.energy = 100;
        this.fun = 100;
        this.social = 100;
    }

    // Generic adjust method used by Actions
    public void adjustNeed(String needName, int amount) {
        switch (needName.toLowerCase()) {
            case "hunger":
                hunger = clamp(hunger + amount);
                break;
            case "hygiene":
                hygiene = clamp(hygiene + amount);
                break;
            case "energy":
                energy = clamp(energy + amount);
                break;
            case "fun":
                fun = clamp(fun + amount);
                break;
            case "social":
                social = clamp(social + amount);
                break;
        }
    }

    private int clamp(int value) {
        return Math.max(0, Math.min(100, value));
    }

    @Override
    public String toString() {
        return "Needs [Hunger=" + hunger +
               ", Hygiene=" + hygiene +
               ", Energy=" + energy +
               ", Fun=" + fun +
               ", Social=" + social + "]";
    }
}
