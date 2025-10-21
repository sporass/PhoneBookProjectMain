package org.example;

public class Employee {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;

    public Employee(String firstName, String lastName, String email, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
    }

    public String validate() {
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || phone.isEmpty())
            return "Please fill in all fields!";
        if (!firstName.matches("[a-zA-Z]+") || !lastName.matches("[a-zA-Z]+"))
            return "First and Last Name must contain only letters!";
        if (!phone.matches("\\d{9}"))
            return "Phone must contain exactly 9 digits!";
        return null;
    }

    // Gettery i settery
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}
