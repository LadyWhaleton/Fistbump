package fistbumpstudios.fistbump;

/**
 * Created by Stephanie Tong on 2/5/2016.
 *
 * This file contains stuff for the Buddy class.
 * Each buddy has a name, a unique ID (their MAC address), avatar, online status, etc.
 */
public class Buddy {

    private String name, id;

    Buddy (String name, String id)
    {
        this.name = name;
        this.id = id;
    }

    public String getName()
    {
        return this.name;
    }

    public String getID
    {
        return this.id;
    }

    public void changeName(String newName)
    {
        this.name = newName;
    }
}
