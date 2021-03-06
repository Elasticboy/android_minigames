package org.es.minigames.platform.drawable;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.util.Log;

import org.es.engine.graphics.animation.Animation;
import org.es.engine.graphics.animation.AnimationCallback;
import org.es.engine.graphics.animation.BitmapAnimation;
import org.es.engine.graphics.drawable.DrawableElement;
import org.es.engine.graphics.sprite.GenericSprite;
import org.es.engine.graphics.sprite.Sprite;
import org.es.engine.graphics.utils.DrawingParam;
import org.es.minigames.BuildConfig;
import org.es.minigames.R;
import org.es.minigames.scrollingbackgrounds.drawable.Background;

import java.util.EnumMap;


/**
 * @author Cyril Leroux
 *         Created on 02/10/13.
 */
public class Hero implements DrawableElement, AnimationCallback {

    private static final String TAG = "Hero";
    private static final int STATE_STATIC = 0;
    private int mState = STATE_STATIC;
    private static final int STATE_WALKING = 1;
    private static final int STATE_JUMPING = 2;

    private static final float START_ACCELERATION_DELAY = 2000;
    /** Action duration to reach the maximum speed (in milliseconds). */
    private static final float REACH_MAX_SPEED_DELAY = 6000;
    /** The walking speed of the character in pixels per second. */
    private static final float WALKING_SPEED = 200;
    /** Max speed of the character in pixels per second. */
    private static final float MAX_SPEED = 800;
    private final Sprite<Hero.AnimId> mSprite;
    private final Background mBackground;
    //    private float mVelocityX = 0;
    //    private float mVelocityY = 0;
    /** Character current speed in pixels per second. */
    private float mCurrentSpeed = 0;
    public Hero(Resources resources, Background background) {
        mSprite = new GenericSprite(getAnimations(resources, this), AnimId.WALK_LEFT);
        stopAnimation();
        mSprite.setDimensions(0, 0, 44, 68);
        mBackground = background;

        mState = STATE_WALKING;
    }

    private static EnumMap<AnimId, Animation> getAnimations(Resources resources, AnimationCallback callback) {

        EnumMap<AnimId, Animation> animations = new EnumMap<>(AnimId.class);

        animations.put(AnimId.WALK_LEFT, new BitmapAnimation(resources,
                new int[]{
                        R.drawable.hero_left_1,
                        R.drawable.hero_left_2,
                        R.drawable.hero_left_3,
                        R.drawable.hero_left_4,
                        R.drawable.hero_left_5,
                        R.drawable.hero_left_6,
                }, 150, true, callback));

        animations.put(AnimId.WALK_RIGHT, new BitmapAnimation(resources,
                new int[]{
                        R.drawable.hero_right_1,
                        R.drawable.hero_right_2,
                        R.drawable.hero_right_3,
                        R.drawable.hero_right_4,
                        R.drawable.hero_right_5,
                        R.drawable.hero_right_6,
                }, 150, true, callback));
        return animations;
    }

    public boolean update() {

        updateSpeed();
        boolean updated = mSprite.updateAnimationFrame();
        updated |= updatePosition();

        return updated;
    }

    private void updateSpeed() {
        if (mState == STATE_WALKING) {
            final float actionElapsedTime = Math.min(System.currentTimeMillis() - getStartTime(), REACH_MAX_SPEED_DELAY);
            if (actionElapsedTime > START_ACCELERATION_DELAY) {
                // speed that will be added to the standard walking speed
                final float extraSpeed = actionElapsedTime * (MAX_SPEED - WALKING_SPEED) / REACH_MAX_SPEED_DELAY;
                mCurrentSpeed = WALKING_SPEED + extraSpeed;
            }
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Current speed : " + mCurrentSpeed);
            }
        }
    }

    private boolean updatePosition() {
        if (isAnimationRunning()) {
            if (AnimId.WALK_LEFT.equals(getAnimationId())) {
                mBackground.setScrollSpeed(mCurrentSpeed, 1000);

            } else if (AnimId.WALK_RIGHT.equals(getAnimationId())) {
                mBackground.setScrollSpeed(-1 * mCurrentSpeed, 1000);
            }
        } else {
            mBackground.setScrollSpeed(0, 1000);
            return false;
        }
        return true;
    }

    @Override
    public void onUpdateSurfaceSize(int surfaceWidth, int surfaceHeight) {
        mSprite.onUpdateSurfaceSize(surfaceWidth, surfaceHeight);
        mSprite.setPosition(surfaceWidth/2f - getWidth()/2f,
                surfaceHeight/2f - getHeight()/2f);
    }

    @Override
    public void draw(Canvas canvas, DrawingParam drawingParam) { mSprite.draw(canvas, drawingParam); }

    @Override
    public float getPosX() { return mSprite.getPosX(); }

    @Override
    public float getPosY() { return mSprite.getPosY(); }

    @Override
    public void setPosition(float x, float y) { mSprite.setPosition(x, y); }

    @Override
    public float getWidth() { return mSprite.getWidth(); }

    @Override
    public float getHeight() { return mSprite.getHeight(); }

    @Override
    public void onAnimationStopped() {
        mCurrentSpeed = 0;
        mState = STATE_STATIC;
    }

    private void startAnimation() { mSprite.startAnimation(); }

    public void stopAnimation() { mSprite.stopAnimation(); }

    private AnimId getAnimationId() { return mSprite.getAnimationId(); }

    private void setAnimationId(AnimId animationId) { mSprite.setAnimationId(animationId); }

    private boolean isAnimationRunning() { return mSprite.getAnimation().isRunning(); }

    private long getStartTime() { return mSprite.getAnimation().getStartTime(); }

    /** Change the animation if necessary. */
    protected void switchState(AnimId state) {
        if (state.equals(mSprite.getAnimationId())) {
            return;
        }
        setAnimationId(state);
    }

    public void walkLeft() {
        mState = STATE_WALKING;
        switchState(AnimId.WALK_LEFT);
        if (!isAnimationRunning()) {
            mCurrentSpeed = WALKING_SPEED;
            startAnimation();
        }
    }

    public void walkRight() {
        mState = STATE_WALKING;
        switchState(AnimId.WALK_RIGHT);
        if (!isAnimationRunning()) {
            mCurrentSpeed = WALKING_SPEED;
            startAnimation();
        }
    }

    public void jump() {
        mState = STATE_JUMPING;
        // switchState();
        //        xt = vx * t;
        //        yt = vy * t - (g * t²)/2;
    }

    // Hero states
    public static enum AnimId {
        WALK_LEFT,
        WALK_RIGHT
    }
}
