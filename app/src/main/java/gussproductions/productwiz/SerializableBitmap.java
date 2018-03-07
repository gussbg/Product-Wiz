package gussproductions.productwiz;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;



import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

class SerializableBitmap implements Serializable
{

    private static final long serialVersionUID = -6298516694275121291L;

    transient Bitmap bitmap;

    public SerializableBitmap(Bitmap bitmap)
    {
        this.bitmap = bitmap;
    }

    private void writeObject(ObjectOutputStream oos) throws IOException
    {
        // This will serialize all fields that you did not mark with 'transient'
        // (Java's default behaviour)
        oos.defaultWriteObject();
        // Now, manually serialize all transient fields that you want to be serialized
        if(bitmap!=null)
        {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            boolean success = bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);

            if(success)
            {
                oos.writeObject(byteStream.toByteArray());
            }
        }
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException{
        // Now, all again, deserializing - in the SAME ORDER!
        // All non-transient fields
        ois.defaultReadObject();
        // All other fields that you serialized
        byte[] image = (byte[]) ois.readObject();
        if(image != null && image.length > 0){
            bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
        }
    }

}


