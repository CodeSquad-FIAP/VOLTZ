import java.time.LocalDate;

public class UserCompanyRelation {
  private User user;
  private Company company;
  private double investedAmount;
  private LocalDate startDate;

  public UserCompanyRelation(User user, Company company, double investedAmount, LocalDate startDate) {
    this.user = user;
    this.company = company;
    this.investedAmount = investedAmount;
    this.startDate = startDate;
  }

  public User getUser() {
    return user;
  }

  public Company getCompany() {
    return company;
  }

  public double getInvestedAmount() {
    return investedAmount;
  }

  public void setInvestedAmount(double investedAmount) {
    this.investedAmount = investedAmount;
  }

  public LocalDate getStarted() {
    return startDate;
  }

  public void setStartDate(LocalDate startDate) {
    this.startDate = startDate;
  }

  public void showRelation() {
    System.out.println("User: " + user.getName() + " | Company: " + company.getName() + " | Invested: $" + investedAmount + " | Since: " + startDate);
  }
}
