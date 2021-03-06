package org.es.minigames.scrollingbackgrounds;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;

import org.es.engine.gamemechanic.DrawingThread;
import org.es.engine.gamemechanic.DrawingView;

/**
 * @author Cyril Leroux
 *         Created on 18/09/13.
 */
public class ScrollingBackgroundView extends DrawingView {

    public ScrollingBackgroundView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected DrawingThread createDrawingThread(SurfaceHolder holder, Context context) {
        return new ScrollingBgDrawingThread(holder, context);
    }
}
