package com.example.user;

import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@Entity
@ToString
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

	private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    private String phoneNumber;
    @Column(nullable = false)
    private String name;
    private String nid;
    private Date dateOfBirth;
    private String address;

    @Column(nullable = false)
    private String password;

    public User(String email, String phoneNumber, String name, String nid, Date dateOfBirth, String address) {
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.nid = nid;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
    }

    public User(String name, String email, String password){
        this.name = name;
        this.email = email;
        this.password = password;
    }
    
}
