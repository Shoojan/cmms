package com.sujan.bean;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * @author Sujan Maharjan
 * @since 02-25-2019
 */


@Entity
@Table(name = "tbl_member")
public class Member {

    @Id
//    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name = "unique_id", updatable = false, nullable = false)
    private long id;

    private String memberId;

    private String firstName;
    private String middleName;
    private String lastName;

    private String fatherName;
    private String motherName;
    private String spouseName;

    private String maritalStatus;

    private String address;
    private String phone;
    private String email;
    private String gender;

    private LocalDate dob;
    private LocalDate dod;
    private LocalDate dom;

    private String area;

    private String photo;

    private String about;

    private String ageGroup;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public String getMotherName() {
        return motherName;
    }

    public void setMotherName(String motherName) {
        this.motherName = motherName;
    }

    public String getSpouseName() {
        return spouseName;
    }

    public void setSpouseName(String spouseName) {
        this.spouseName = spouseName;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public LocalDate getDod() {
        return dod;
    }

    public void setDod(LocalDate dod) {
        this.dod = dod;
    }

    public LocalDate getDom() {
        return dom;
    }

    public void setDom(LocalDate dom) {
        this.dom = dom;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getAgeGroup() {
        return ageGroup;
    }

    public void setAgeGroup(String ageGroup) {
        this.ageGroup = ageGroup;
    }

    @Override
    public String toString() {
        return "Member{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", middleName='" + middleName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", fatherName='" + fatherName + '\'' +
                ", motherName='" + motherName + '\'' +
                ", spouseName='" + spouseName + '\'' +
                ", maritalStatus='" + maritalStatus + '\'' +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", gender='" + gender + '\'' +
                ", dob=" + dob +
                ", dod=" + dod +
                ", dom=" + dom +
                ", area='" + area + '\'' +
                ", photo='" + photo + '\'' +
                ", about='" + about + '\'' +
                ", ageGroup='" + ageGroup + '\'' +
                '}';
    }
}
