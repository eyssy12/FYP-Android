package com.eyssyapps.fypcms.models;

/**
 * Created by eyssy on 26/04/2016.
 */
public class LecturerModule
{
    private Lecturer lecturer;
    private Module module;

    public LecturerModule(Lecturer lecturer, Module module)
    {
        this.lecturer = lecturer;
        this.module = module;
    }

    public Lecturer getLecturer()
    {
        return lecturer;
    }

    public Module getModule()
    {
        return module;
    }
}