package com.petadoption.dto;

/**
 * The UserDTO class is a Data Transfer Object used to encapsulate and
 * transfer user data between different layers of the application.
 * It provides a simplified representation of a user entity, containing
 * core attributes such as ID, name, email, and phone.

 * This class includes constructors, getters, and setters, enabling
 * easy mapping and manipulation of user data.
 */
public class UserDTO {

    private Long id;
    private String name;
    private String email;
    private String phone;

    public UserDTO() {}

    public UserDTO(Long id, String name, String email, String phone) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
