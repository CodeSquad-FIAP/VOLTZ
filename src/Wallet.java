import java.util.ArrayList;
import java.util.List;

public class Wallet {
    private List<CryptoAsset> assets;

    public Wallet() {
        this.assets = new ArrayList<>();
    }

    public void addAsset(CryptoAsset asset) {
        assets.add(asset);
    }

    public void showAssets() {
        if (assets.isEmpty()) {
            System.out.println("The wallet is empty.");
        } else {
            for (CryptoAsset asset : assets) {
                System.out.println("Asset: " + asset.getName() + " | Quantity: " + asset.getQuantity());
            }
        }
    }

    public List<CryptoAsset> getAssets() {
        return assets;
    }

}
