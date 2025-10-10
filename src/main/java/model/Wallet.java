package model;

import java.util.ArrayList;
import java.util.List;

public class Wallet {
    private int id;
    private int userId;
    private String name;
    private List<CryptoAsset> assets = new ArrayList<>();

    // Construtor padrão
    public Wallet() {
        this.assets = new ArrayList<>();
    }

    // Construtor com parâmetros
    public Wallet(int id, int userId, String name) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.assets = new ArrayList<>();
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

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}