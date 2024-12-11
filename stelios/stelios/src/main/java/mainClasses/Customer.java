package mainClasses;

public class Customer {
    int customerId;
    String Name, Email, CreditCardDetails;

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getCreditCardDetails() {
        return CreditCardDetails;
    }

    public void setCreditCardDetails(String creditCardDetails) {
        CreditCardDetails = creditCardDetails;
    }
}