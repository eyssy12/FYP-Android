package com.eyssyapps.fypcms.models;

import java.util.List;

/**
 * Created by eyssy on 07/04/2016.
 */
public class Semester
{
    private int id,
        number;

    private List<Student> students;
    private List<Module> modules;

    public Semester(int id, int number, List<Student> students, List<Module> modules)
    {
        this.id = id;
        this.number = number;
        this.students = students;
        this.modules = modules;
    }

    public int getId()
    {
        return id;
    }

    public int getNumber()
    {
        return number;
    }

    public List<Student> getStudents()
    {
        return students;
    }

    public List<Module> getModules()
    {
        return modules;
    }
}
