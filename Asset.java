public abstract class Asset implements Ownable, Sellable{
    private final int id;
    private String name;
    protected int purchaseValue;

    protected Asset(int id, String name, int purchaseValue){
        this.id = id;
        this.name = name;
        this.purchaseValue = purchaseValue;
    }

    // Get ID of asset
    @Override
    public int getId(){
        return id;
    }

    // Get name of asset    
    @Override
    public String getName(){
        return name;
    }

    // Set name of asset
    public void setName(String name){
        this.name = name;
    }

    // Get value of asset
    @Override
    public int getValue(){
        return purchaseValue;
    }

    // Money returned to player when sold (Depreciation happens)
    @Override
    public int sellValue(){
        return (int)(purchaseValue * 0.7); // Example: sell at 70%
    }

    public abstract String getAssetType(); // "House", "Car"
}
