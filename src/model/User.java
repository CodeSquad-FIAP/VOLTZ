package model;

public class User extends Entity {
    private String email;
    private String password;

    public User(String name, String email, String password) {
        super(name);
        this.email = email;
        this.password = password;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    @Override
    public void showInfo() {
        System.out.println("User: " + getName() + " | Email: " + email);
    }
}

