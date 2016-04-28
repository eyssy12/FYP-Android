package com.eyssyapps.fypcms.models;

/**
 * Created by eyssy on 04/04/2016.
 */
public class Person
{
    private int personId;

    private String firstName,
        lastName,
        birthDate,
        address,
        mobilePhone;

    public Person(int personId, String firstName, String lastName, String birthDate, String address, String mobilePhone)
    {
        this.personId = personId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.address = address;
        this.mobilePhone = mobilePhone;
    }

    public int getPersonId()
    {
        return personId;
    }

    public String getBirthDate()
    {
        return birthDate;
    }

    public String getAddress()
    {
        return address;
    }

    public String getMobilePhone()
    {
        return mobilePhone;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public String getFullName()
    {
        return this.firstName + " " + this.lastName;
    }
}