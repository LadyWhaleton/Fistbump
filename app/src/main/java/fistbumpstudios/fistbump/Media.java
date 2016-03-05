package fistbumpstudios.fistbump;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.webkit.MimeTypeMap;

import java.io.File;

/**
 * Created by Sephi on 3/1/2016.
 * This class is used for storing information about the file sent.
 * http://developer.android.com/reference/android/media/package-summary.html
 */
public class Media {

    File mediaFile; // not sure if we will need this.

    private String fileName;
    private String timeReceived;
    private String mediaType;
    private String mimeType;

    // info about the media's original sender
    private String ownerName;
    private String id;
    private Uri ownerProfilePic;

    Media (String fileName, String timeReceived, Buddy sender)
    {
        this.fileName = fileName;
        this.timeReceived = timeReceived;
        this.ownerName = sender.getName();
        this.id = sender.getID();
        this.ownerProfilePic = sender.getProficPic();

        setMediaType();
    }

    // This function should be called when you are creating a new media object aft
    private void setMediaType()
    {
        String fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1,  fileName.length());
        String fileMimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension);

        String[] parsedMimeType = fileMimeType.split("/");

        this.mediaType = parsedMimeType[0];
        this.mimeType = fileMimeType;
    }

    public String getOwnerName()
    {
        return this.ownerName;
    }

    public Uri getOwnerProfilePic()
    {
        return this.ownerProfilePic;
    }

    public String getTimestamp()
    {
        return this.timeReceived;
    }

    public String getMediaType()
    {
        return this.mediaType;
    }

    public String getMediaPath(String fistbumpFolder)
    {
        return fistbumpFolder + fileName;
    }

    public static Bitmap getThumbnail(ContentResolver cr, String path)
    {
        // video thumbnail: http://stackoverflow.com/questions/32517124/how-to-create-video-thumbnail-from-video-file-path-in-android
        // http://androidsrc.net/create-thumbnail-video-android-application/

        // http://stackoverflow.com/questions/8383377/android-get-thumbnail-of-image-stored-on-sdcard-whose-path-is-known
        Cursor ca = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[] { MediaStore.MediaColumns._ID }, MediaStore.MediaColumns.DATA + "=?", new String[] {path}, null);
        if (ca != null && ca.moveToFirst()) {
            int id = ca.getInt(ca.getColumnIndex(MediaStore.MediaColumns._ID));
            ca.close();

            // if you want a larger thumbnail use, MINI_KIND
            return MediaStore.Images.Thumbnails.getThumbnail(cr, id, MediaStore.Images.Thumbnails.MICRO_KIND, null );
        }

        ca.close();
        return null;

    }

    public Uri getUriFromFileName(String fistbumpFolder)
    {
        return Uri.fromFile(new File (getMediaPath(fistbumpFolder)) );
        //Bitmap thumbnail = getPreview(uri);
    }

    /** This function takes the time the file was received and gets the difference between
     * now and then. So if a file was received on 2/1/13 at 1:00 PM and the current time is
     * 2/1/13 at 2:01, it should display "an hour ago".
     *
     */
    public String getElapsedTime()
    {
        return this.timeReceived;
    }

}
