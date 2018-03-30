/*
 * Copyright (c) 2018, Brendon Guss. All rights reserved.
 */

package gussproductions.productwiz;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * The SerializableBitmap class is used to make a bitmap serializable.
 * This is needed when sending a product from one activity to another.
 *
 * @author Brendon Guss
 * @since  02/22/2018
 */
class SerializableBitmap implements Serializable
{
    private static final long serialVersionUID = -6298516694275121291L;

    transient Bitmap bitmap;

    /**
     * Constructs a SerializableBitmap given a bitmap.
     *
     * @param bitmap The bitmap to serialize.
     */
    SerializableBitmap(Bitmap bitmap)
    {
        this.bitmap = bitmap;
    }

    /**
     * This is called when the SerializableBitmap needs to be transferred or saved
     * and therefore, needs to be serialized.
     *
     * @param oos The object output stream.
     * @throws IOException Input / Output exception.
     */
    private void writeObject(ObjectOutputStream oos) throws IOException
    {
        // This will serialize all fields that aren't marked transient
        // (Java's default behaviour)
        oos.defaultWriteObject();

        // Now, manually serialize the transient bitmap serialized.
        if (bitmap != null)
        {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            boolean success = bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);

            if(success)
            {
                oos.writeObject(byteStream.toByteArray());
            }
        }
    }

    /**
     * This is called when the SerializableBitmap needs to be read
     * and therefore, needs to be deserialized.
     *
     * @param ois Object input stream.
     * @throws IOException Input / Output Exception
     * @throws ClassNotFoundException Class not found exception.
     */
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException
    {
        // Now, all again, deserializing - in the SAME ORDER!
        // All non-transient fields
        ois.defaultReadObject();

        // The image is deserialized
        byte[] image = (byte[]) ois.readObject();

        if (image != null && image.length > 0)
        {
            bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
        }
    }
}
