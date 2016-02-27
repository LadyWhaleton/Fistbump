package fistbumpstudios.fistbump;
import android.net.Uri;

/**
 * Created by Stephanie Tong on 2/5/2016.
 *
 * This file contains stuff for the Buddy class.
 * Each buddy has a name, a unique ID (their MAC address), avatar, online status, etc.
 */
public class Buddy {

    public String name, id;
    public Uri profilePic;

    Buddy (String name, String id, Uri image)
    {
        this.name = name;
        this.id = id;
        this.profilePic = image;

    }

    public String getName()
    {
        return this.name;
    }
    public String getID () {return this.id; }
    public Uri getProficPic() { return this.profilePic; }

    public void changeName(String newName)
    {
        this.name = newName;
    }
}
