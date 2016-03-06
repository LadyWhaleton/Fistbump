package fistbumpstudios.fistbump;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Sephi on 3/4/2016.
 * We will be using this class to handle SQLite Database queries.
 * At the moment, we will be using this to store information about chats.
 *
 * Currently unused. May be used for future implementations.
 *
 * https://www.youtube.com/watch?v=xKuM3cHO7G8
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "FistbumpDB";

    // Here are the Database table names
    private static final String TABLE_BUDDYLIST = "buddylist",
    TABLE_USER = "user",
    TABLE_CHATLIST = "chatlist",
    TABLE_GALLERY = "gallery",
    TABLE_MESSAGES = "messages",
    KEY_USERNAME = "username",
    KEY_USERID = "user_id",
    KEY_PROFILEPIC = "profile_pic";


    public DatabaseHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE" + TABLE_BUDDYLIST + "(" + KEY_USERNAME + " TEXT");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
