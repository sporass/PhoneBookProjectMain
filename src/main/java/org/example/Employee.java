package org.example;

public class Employee {
    private String firstName, lastName, email, phone;

    public Employee(String firstName, String lastName, String email, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
    }

    public String validate() {
        if (firstName.isEmpty() || lastName.isEmpty()) return "Name cannot be empty";
        if (!phone.matches("\\d{9}")) return "Phone must be 9 digits";
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
