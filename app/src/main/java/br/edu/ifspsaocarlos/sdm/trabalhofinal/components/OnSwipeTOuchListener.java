package br.edu.ifspsaocarlos.sdm.trabalhofinal.components;


import android.content.Context;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/**
 * Created by LeonardoAlmeida on 22/11/15.
 */
public class OnSwipeTouchListener implements OnTouchListener {

    private final GestureDetector gestureDetector;

    public OnSwipeTouchListener(Context ctx){
        gestureDetector = new GestureDetector(ctx, new GestureListener());
    }

    public boolean onTouch(View v, MotionEvent event) {
       return gestureDetector.onTouchEvent(event);
    }


    private final class GestureListener extends SimpleOnGestureListener {

        private static final int SWIPE_DISTANCE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent e) {
            if (e.getActionMasked() == MotionEvent.ACTION_UP) return true;
            else return false;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            boolean result = false;
            try {
                float distanceX = e2.getX() - e1.getX();
                float distanceY = e2.getY() - e1.getY();
                if (Math.abs(distanceX) > Math.abs(distanceY)) {
                    if (Math.abs(distanceX) > Math.abs(distanceY) && Math.abs(distanceX) > SWIPE_DISTANCE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (distanceX > 0) onSwipeRight();
                        else onSwipeLeft();
                    }
                    result = true;
                }
                else if (Math.abs(distanceY) > SWIPE_DISTANCE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (distanceY > 0) {
                        onSwipeBottom();
                    } else {
                        onSwipeTop();
                    }
                }
                result = true;

            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return result;
        }
    }

    public void onSwipeRight() {
    }

    public void onSwipeLeft() {
    }

    public void onSwipeTop() {
    }

    public void onSwipeBottom() {
    }
}