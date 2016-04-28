package com.eyssyapps.fypcms.models;

/**
 * Created by eyssy on 05/04/2016.
 */
public class StudentPerson
{
    private Student student;
    private Person person;

    public StudentPerson(Student student, Person person)
    {
        this.student = student;
        this.person = person;
    }

    public Student getStudent()
    {
        return student;
    }

    public Person getPerson()
    {
        return person;
    }
}
