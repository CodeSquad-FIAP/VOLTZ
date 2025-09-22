package model;

import java.util.ArrayList;
import java.util.List;

public class Wallet {
    private int id;
    private int userId;
    private String name;
    private List<CryptoAsset> assets = new ArrayList<>();

    public Wallet(int id, int userId, String name) {
        this.id = id;
        this.userId = userId;
        this.name = name;
    }

    // Métodos de manipulação da lista
    public void addAsset(CryptoAsset asset) {
        assets.add(asset);
    }

    public void removeAsset(CryptoAsset asset) {
        assets.remove(asset);
    }

    public List<CryptoAsset> getAssets() {
        return assets;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setName(String name) {
        this.name = name;
    }
}


    public List<CryptoAsset> getAssets() {
        return assets;
    }
}
