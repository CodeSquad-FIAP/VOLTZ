package model;

public class User extends Entity {
    private int id;
    private String email;
    private String password;

    // Construtor completo com ID
    public User(String name, int id, String email, String password) {
        super(name);
        this.id = id;
        this.email = email;
        this.password = password;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Exibir informações do usuário
    @Override
    public void showInfo() {
        System.out.println("User: " + name + ", Email: " + email);
    }
}

