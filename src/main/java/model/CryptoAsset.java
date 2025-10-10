package model;

public class CryptoAsset {
    private int id;
    private String name;
    private String symbol;
    private double quantity;
    private double price;

    // Construtor sem ID (para novos ativos)
    public CryptoAsset(String name, String symbol, double quantity, double price) {
        this.name = name;
        this.symbol = symbol;
        this.quantity = quantity;
        this.price = price;
    }

    // Construtor com ID (para ativos do banco de dados)
    public CryptoAsset(int id, String name, String symbol, double quantity, double price) {
        this.id = id;
        this.name = name;
        this.symbol = symbol;
        this.quantity = quantity;
        this.price = price;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }

    public double getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    // Método utilitário
    public double getTotalValue() {
        return quantity * price;
    }
}