package edu.northeastern.coinnect.models.persistence.entities;

public class UserEntity {
  public String username;
  public String firstName;
  public String lastName;
  public String password;
  public int monthlyBudget;

  public UserEntity() {}

  public UserEntity(String username,
                    String firstName,
                    String lastName,
                    String password,
                    int monthlyBudget) {
    this.username = username;
    this.firstName = firstName;
    this.lastName = lastName;
    this.password = password;
    this.monthlyBudget = monthlyBudget;
  }

  public int getMonthlyBudget() {
    return monthlyBudget;
  }

  public String getPassword() {
    return password;
  }

  public String getLastName() {
    return lastName;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getUsername() {
    return username;
  }

  public void setMonthlyBudget(int newBudget) {
    this.monthlyBudget = newBudget;
  }
}
