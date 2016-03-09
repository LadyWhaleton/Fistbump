package fistbumpstudios.fistbump;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Sephi on 3/1/2016.
 * This class is used for storing information about the file sent.
 * http://developer.android.com/reference/android/media/package-summary.html
 */
public class Media {

    File mediaFile; // not sure if we will need this.

    private String fileName;
    private String mediaPath;
    private long timestamp;
    private String mediaType;
    private String mimeType;
    private String fileExtension;

    // info about the media's original sender
    private String ownerName;
    private String id;
    private Uri ownerProfilePic;


    // This constructor is called when a message is being loaded from a log
    Media (String fileName, String mediaPath, long timestamp, String ownerName, String id)
    {
        this.fileName = fileName;
        this.mediaPath = mediaPath;
        this.timestamp = timestamp;
        this.ownerName = ownerName;
        this.id = id;

        setMediaType();
    }

    // This constructor is called when a message is sent
    Media (String fileName, String mediaPath, long timestamp, Buddy sender)
    {
        this.fileName = fileName;
        this.mediaPath = mediaPath;
        this.timestamp = timestamp;
        this.ownerName = sender.getName();
        this.id = sender.getID();
        this.ownerProfilePic = sender.getProficPic();

        setMediaType();
    }

    // This helper function should be called when you are creating a new media object
    private void setMediaType()
    {
        String fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1,  fileName.length());

        String fileMimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension);

        // Handles cases where there's no file extension
        if (fileMimeType != null)
        {
            String[] parsedMimeType = fileMimeType.split("/");
            this.mediaType = parsedMimeType[0];
            this.mimeType = fileMimeType;
            this.fileExtension = fileExtension;
        }

        else {
            this.mediaType = "?";
            this.mimeType = "?";
            this.fileExtension = "?";
        }
    }

    public String getOwnerName()
    {
        return this.ownerName;
    }

    public Bitmap getOwnerProfilePic()
    {
        File pic = new File (Environment.getExternalStorageDirectory().toString() + "/FistBump/ProfilePics/" + fileName);
        return ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(pic.toString()), 256, 256);
    }

    public long getTimestamp()
    {
        return this.timestamp;
    }

    public Date getDateTime() {
        return new Date(this.timestamp);
    }

    public String getMediaType()
    {
        return this.mediaType;
    }

    public String getMimeType()
    {
        return this.mimeType;
    }

    public String getFileExt()
    {
        return this.fileExtension;
    }

    public String getMediaPath()
    {
        return mediaPath;
    }

    public Bitmap getThumbnail(ContentResolver cr)
    {
        // check the file type of the media. Is it an image, video, or song?
        if (this.mediaType.equals("image"))
            return ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(this.mediaPath), 256, 256);

        else if (this.mediaType.equals("video"))
            return ThumbnailUtils.createVideoThumbnail(mediaPath, MediaStore.Images.Thumbnails.MINI_KIND);

        else
            return null;

    }

    public Uri getUriFromFileName()
    {
        return Uri.fromFile(new File(getMediaPath()));
    }

    public String getFormattedDate()
    {
        DateFormat df = new SimpleDateFormat("MMM d',' yyyy 'at' h:mm a");
        Date date = new Date(timestamp);
        return df.format(date);
    }
}
