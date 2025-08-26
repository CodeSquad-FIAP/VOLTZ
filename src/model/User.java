package model;
public class User extends Entity {
  private int id;
  private String email;
  private String password;

  public User(String name, int id, String email, String password) {
    super(name);
    this.id = id;
    this.email = email;
    this.password = password;
  }package model;

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


  public int getId() {
    return id;
  }

  public String getEmail() {
    return email;
  }

  public String getPassword() {
    return password;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void showInfo() {
    System.out.println("User: " + name + ", Email: " + email);
  }
}
