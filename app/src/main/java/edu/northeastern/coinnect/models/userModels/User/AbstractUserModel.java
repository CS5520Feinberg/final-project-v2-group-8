package edu.northeastern.coinnect.models.userModels.User;

public abstract class AbstractUserModel {

    private String username;
    private String firstName;
    private String lastName;
    private String password;
    private String name;
    private int monthlyBudget;

    public AbstractUserModel(String username,
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


    // for sign in flow that only gets one display name (google).
    public AbstractUserModel(String name, String password, int monthlyBudget) {
        this.name = name;
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
