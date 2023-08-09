package edu.northeastern.coinnect.models;

public class UserModel extends AbstractUserModel {
    public UserModel(String username, String firstName, String lastName, String password, int monthlyBudget) {
        super(username, firstName, lastName, password, monthlyBudget);
    }

    // for google sign in flow that only gets one display name.
    public UserModel(String name, String password, int monthlyBudget) {
        super(name, password, monthlyBudget);
    }
}
