package com.info.model;

import java.io.Serializable;

public class CardDetailsModel implements Serializable {

    String cardName;
    String price;
    String email;
    String cardNumber;
    String cvcValue;
    String postalCodeValue;
    int expiryMonth;
    int expiryYears;
    String expiryDate;


    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCvcValue() {
        return cvcValue;
    }

    public void setCvcValue(String cvcValue) {
        this.cvcValue = cvcValue;
    }

    public String getPostalCodeValue() {
        return postalCodeValue;
    }

    public void setPostalCodeValue(String postalCodeValue) {
        this.postalCodeValue = postalCodeValue;
    }

    public int getExpiryMonth() {
        return expiryMonth;
    }

    public void setExpiryMonth(int expiryMonth) {
        this.expiryMonth = expiryMonth;
    }

    public int getExpiryYears() {
        return expiryYears;
    }

    public void setExpiryYears(int expiryYears) {
        this.expiryYears = expiryYears;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }
}
