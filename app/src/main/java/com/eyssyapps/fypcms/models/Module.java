package com.eyssyapps.fypcms.models;

import com.eyssyapps.fypcms.utils.Constants;

/**
 * Created by eyssy on 06/04/2016.
 */
public class Module
{
    private int id,
            credits,
            moduleType;

    private String name;

    public Module(int id, int credits, String name, int moduleType)
    {
        this.id = id;
        this.credits = credits;
        this.name = name;
        this.moduleType = moduleType;
    }

    public int getId()
    {
        return id;
    }

    public int getCredits()
    {
        return credits;
    }

    public String getName()
    {
        return name;
    }

    public String getModuleType()
    {
        return Constants.MODULE_TYPES.get(moduleType);
    }
}
