package com.smhu.enity;


import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.sql.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="account")
public class Customer implements Serializable {
    @Id
    @Column(name ="ID")
    private String id;
    @Column(name ="USERNAME")
    private String username;
    @Column(name ="PASSWORD")
    private String password;
    @Column(name ="FIRST_NAME")
    private String firstName;
    @Column(name ="MID_NAME")
    private String middleName;
    @Column(name ="LAST_NAME")
    private String lastName;
    @Column(name ="EMAIL")
    private String email;
    @Column(name ="PHONE")
    private String phone;
    @Column(name ="DOB")
    private Date dob;
    @Column(name ="ROLE")
    private int role;
    @Column(name ="CREATED_DATE")
    private Date createDate;
    @Column(name ="NUM_ORDERED")
    private int numOrder;
    @Column(name ="NUM_CANCEL")
    private int numCancel;
    @Column(name ="WALLET")
    private double wallet;
    @Column(name ="STATUS")
    private int status;
    @Column(name ="IS_ACTIVE")
    private boolean isActive;

}
