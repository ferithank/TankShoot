package com.ferithankarakas.tankwar;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import static com.ferithankarakas.tankwar.GameView.screenRatioX;
import static com.ferithankarakas.tankwar.GameView.screenRatioY;

public class Enemy {

    public int speed = 15;
    public boolean wasShot = true;
    float x = 0, y = 0;
    int width, height;

    Bitmap enemy;

    Enemy (Resources res){

        enemy = BitmapFactory.decodeResource(res, R.drawable.enemy);

        width = enemy.getWidth();
        height = enemy.getHeight();

        width /= 32;
        height /= 32;

        width *= (int) screenRatioX;
        height *= (int) screenRatioY;
        enemy = Bitmap.createScaledBitmap(enemy, width, height, false);
        x = -width;
    }

    Bitmap getEnemy(){ return enemy; }


    Rect getCollisionShape()
    {
        int gx = (int) x;
        int gy = (int) y;
        return new Rect(gx, gy, gx + width, gy + height);
    }
}
