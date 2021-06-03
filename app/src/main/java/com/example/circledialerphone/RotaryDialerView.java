package com.example.circledialerphone;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;

public class RotaryDialerView extends View {

    public interface DialListener{
        void onDial(int number);
    }

    private float rotorAngle;
    private Drawable rotorDrawable;
    private int r1 = 250;
    private int r2 = 480; //r1-r2 touch dialer area
    private double lastFi;
    private final List<DialListener> dialListeners = new ArrayList<RotaryDialerView.DialListener>();

    public RotaryDialerView(Context context) {
        super(context);
    }

    public RotaryDialerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        rotorDrawable = context.getResources().getDrawable(R.drawable.dialer);
    }

    public RotaryDialerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }




    public void addDialListener(DialListener d)
    {
        dialListeners.add(d);
    }

    public void removeDialListener(DialListener d)
    {
        dialListeners.remove(d);
    }

    private void fireDialListenerEvent(int number)
    {
        for (DialListener l : dialListeners)
        {
            l.onDial(number);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getRight() - getLeft();
        int height = getBottom() - getTop();
        //center
        int x = width / 2;
        int y = height / 2;

        canvas.save();
        rotorDrawable.setBounds(0,0,rotorDrawable.getIntrinsicWidth(),
                                              rotorDrawable.getIntrinsicHeight());

        if (rotorAngle != 0)
        {
            canvas.rotate(rotorAngle,x,y);
        }

        rotorDrawable.draw(canvas);
        canvas.restore();
    }

    //on touch rotation


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x0 = getWidth() / 2;
        float y0 = getHeight() / 2;
        float x1 = event.getX();
        float y1 = event.getY();
        float x = x0 - x1;
        float y = y0 - y1;

        //radius of dialer
        double r = Math.sqrt(x * x + y * y);
        double sinfi = y / r;
        double fi = Math.toDegrees(Math.asin(sinfi));

        if (x1 > x0 && y0 > y1 )
        {
            fi = 180 - fi;
        }else if (x1 > x0 && y1 > y0)
        {
            fi = 180 -fi;
        }else if (x0 > x1 && y1 > y0)
        {
            fi += 360;
        }

        switch (event.getAction()){
            case MotionEvent.ACTION_MOVE:
                if (r > r1 && r < r2)
                {
                    rotorAngle += Math.abs(fi - lastFi) + 0.25f;
                    rotorAngle %= 360;
                    lastFi = fi;
                    //new position of dialer
                    invalidate();
                    return true;
                }

            case MotionEvent.ACTION_DOWN:
                rotorAngle = 0;
                lastFi = fi;
                return true;

            case MotionEvent.ACTION_UP:
                //end of rotation
                final float angle = rotorAngle % 360;
                int number = Math.round(angle - 20) / 30;

                if (number > 0){
                    if (number == 10){
                        number = 0;
                    }
                    fireDialListenerEvent(number);
                }
                rotorAngle = 0;

                post(new Runnable() {
                    @Override
                    public void run() {
                        float fromDegrees = angle;
                        Animation animation = new RotateAnimation(fromDegrees,0,
                                                Animation.RELATIVE_TO_SELF,0.5f,
                                                Animation.RELATIVE_TO_SELF,0.5f);
                        animation.setInterpolator(getContext(), android.R.anim.accelerate_decelerate_interpolator);
                        animation.setDuration((long)(angle * 5L));
                        startAnimation(animation);
                    }
                });
                return true;
            default:
                break;
        }


        return super.onTouchEvent(event);
    }
}
