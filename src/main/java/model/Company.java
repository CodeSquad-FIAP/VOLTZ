package model;
import java.util.ArrayList;
import java.util.List;

public class Company extends Entity {
    private int id;
    private String identifier;
    private List<CryptoAsset> allocatedAssets;

    public Company(String name, int id, String identifier) {
        super(name);
        this.id = id;
        this.identifier = identifier;
        this.allocatedAssets = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public List<CryptoAsset> getAllocatedAssets() {
        return allocatedAssets;
    }

    public void allocateAsset(CryptoAsset asset) {
        allocatedAssets.add(asset);
    }

    public void allocateAsset(CryptoAsset asset, double extraQuantity) {
        asset.setQuantity(asset.getQuantity() + extraQuantity);
        allocatedAssets.add(asset);
    }

    public void showAllocatedAssets() {
        System.out.println("Assets allocated to " + name + ":");
        for (CryptoAsset asset : allocatedAssets) {
            System.out.println("- " + asset.getName() + " (" + asset.getQuantity() + ")");
        }
    }

    @Override
    public void showInfo() {
        System.out.println("Company: " + name + " | ID: " + identifier);
    }
}