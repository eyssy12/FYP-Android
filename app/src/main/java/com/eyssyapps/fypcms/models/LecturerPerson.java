package com.eyssyapps.fypcms.models;

/**
 * Created by eyssy on 15/04/2016.
 */
public class LecturerPerson
{
    private Person person;
    private Lecturer lecturer;

    public LecturerPerson(Lecturer lecturer, Person person)
    {
        this.lecturer = lecturer;
        this.person = person;
    }

    public Person getPerson()
    {
        return person;
    }

    public Lecturer getLecturer()
    {
        return lecturer;
    }
}