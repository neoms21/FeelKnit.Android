package com.qubittech.feelknit.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

/**
 * Created by Manoj on 15/11/2014.
 */
public class ImageHelper {

    public static void setBitMap(ImageView imageView, Context context, String avatar, int wdth, int hght)
    {
        Bitmap bmp;
        int width = wdth;
        int height = hght;

        try {
            bmp = BitmapFactory.decodeResource(context.getResources(), context.getResources().getIdentifier(
                    avatar, "drawable", context.getPackageName()));//image is your image
            bmp = Bitmap.createScaledBitmap(bmp, width, height, true);
            imageView.setImageBitmap(bmp);
        }
        catch (Exception ex){
            System.out.println(ex.toString());
        }
    }
}
