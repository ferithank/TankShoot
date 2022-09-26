package com.ferithankarakas.tankwar;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import static com.ferithankarakas.tankwar.GameView.screenRatioX;
import static com.ferithankarakas.tankwar.GameView.screenRatioY;

public class Bullet {

    float x, y;
    Bitmap bullet;
    int width, height;
    Bullet (Resources res)
    {
        bullet = BitmapFactory.decodeResource(res, R.drawable.bullet);

        width = bullet.getWidth();
        height = bullet.getHeight();

        width /= 16;
        height /= 16;

        width *= (int) screenRatioX;
        height *= (int) screenRatioY;
        bullet = Bitmap.createScaledBitmap(bullet, width, height, false);
    }
    Rect getCollisionShape()
    {
        int gx = (int) x;
        int gy = (int) y;
        return new Rect(gx, gy, gx + width, gy + height);
    }
}
