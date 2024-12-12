package mainClasses;

import com.google.gson.annotations.SerializedName;

public class Customer {
    @SerializedName("customer_id") // Maps the JSON field "customer_id" to this field
    private int customerId;

    private String name;
    private String email;

    @SerializedName("credit_card_details") // Maps "credit_card_details" to this field
    private String creditCardDetails;

    // Getters and setters
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
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCreditCardDetails() {
        return creditCardDetails;
    }

    public void setCreditCardDetails(String creditCardDetails) {
        this.creditCardDetails = creditCardDetails;
    }
}
