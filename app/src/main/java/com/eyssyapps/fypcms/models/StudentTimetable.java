package com.eyssyapps.fypcms.models;

import java.util.List;

/**
 * Created by eyssy on 28/04/2016.
 */
public class StudentTimetable extends Timetable
{
    private int id;
    private String name;
    private Class classObject;

    public StudentTimetable(int id, String name, Class classObject, List<Event> events)
    {
        super(events);

        this.id = id;
        this.name = name;
        this.classObject = classObject;
    }

    public int getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public Class getClassObject()
    {
        return classObject;
    }
}