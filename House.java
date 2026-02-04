public class House extends Asset{
    private int comfortBonus;
    private int hygieneBonus;

    public House(int id, String name, int purchaseValue, int comfortBonus, int hygieneBonus){
        super(id, name, purchaseValue);
        this.comfortBonus = comfortBonus;
        this.hygieneBonus = hygieneBonus;
    }

    public int getComfortBonus(){
        return comfortBonus;
    }

    public int getHygieneBonus(){
        return hygieneBonus;
    }

    @Override
    public String getAssetType(){
        return "House";
    }

    // Houses depreciate slower
    @Override
    public int sellValue(){
        return (int)(purchaseValue * 0.9); 
    }
}
