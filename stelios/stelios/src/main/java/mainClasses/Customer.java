package mainClasses;

public class Customer {
    int customerId;
    String name, email, credit_card_details;

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name; // Correctly assigns to the class field.
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email; // Correctly assigns to the class field.
    }

    public String getCreditCardDetails() {
        return credit_card_details;
    }

    public void setCreditCardDetails(String creditCardDetails) {
        this.credit_card_details = creditCardDetails; // Correctly assigns to the class field.
    }
}
