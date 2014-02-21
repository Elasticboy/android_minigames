package org.es.minigames.towerdefense.process;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

import org.es.minigames.towerdefense.battleground.Battleground;
import org.es.minigames.towerdefense.unit.Destructible;
import org.es.minigames.towerdefense.unit.Enemy;
import org.es.minigames.towerdefense.unit.EnemyFactory;
import org.es.minigames.towerdefense.unit.Tower;
import org.es.minigames.towerdefense.unit.TowerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Cyril Leroux
 *         Created on 13/02/14.
 */
public class GameMgr {

    private final Context mContext;
    private final Paint mDebugPaint;
    private final Battleground mBattleground;
    // TODO mDrawable should evolve to a list of towers or static elements (handle barricades)
    //private final Set<DrawableElement> mDrawables;
    private final Set<Enemy> mEnemies;
    private final Set<Tower> mTowers;
    private final Set<Destructible> mGarbage;
    private int mSurfaceWidth;
    private int mSurfaceHeight;

    public GameMgr(Context context) {
        mContext = context;
        mDebugPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDebugPaint.setAntiAlias(true);
        mDebugPaint.setStrokeWidth(1f);
        mDebugPaint.setStyle(Paint.Style.STROKE);
        mDebugPaint.setTextSize(20f);

        mSurfaceWidth = 0;
        mSurfaceHeight = 0;

        final Resources resources = context.getResources();

        mBattleground = new Battleground(15, 7,
                new Point[]{new Point(0, 3)},
                new Point[]{new Point(14, 3)},
                resources);
        mEnemies = new HashSet<>();
        mTowers = new HashSet<>();
        mGarbage = new HashSet<>();

        Tower tower = TowerFactory.createTower(Tower.Type.BASIC, resources);
        mTowers.add(tower);
        mBattleground.addTower(tower, 5, 2);

        Tower tower2 = TowerFactory.createTower(Tower.Type.BASIC, resources);
        mTowers.add(tower2);
        mBattleground.addTower(tower2, 9, 4);

        spawnEnemy();
    }

    // TODO move to Wave manager
    public Enemy spawnEnemy() {
        Enemy enemy = EnemyFactory.createEnemy(Enemy.Type.CRAWLING, mContext.getResources());
        enemy.setCoef(mBattleground.getCoef());
        enemy.onUpdateSurfaceSize(mSurfaceWidth, mSurfaceHeight);
        enemy.startAnimation();
        mEnemies.add(enemy);
        mBattleground.spawnEnemy(enemy, 0);
        return enemy;
    }

    public void updateSurfaceSize(int surfaceWidth, int surfaceHeight) {
        mSurfaceWidth = surfaceWidth;
        mSurfaceHeight = surfaceHeight;
        mBattleground.onUpdateSurfaceSize(surfaceWidth, surfaceHeight);

        for (Enemy enemy : mEnemies) {
            enemy.setCoef(mBattleground.getCoef());
            enemy.onUpdateSurfaceSize(surfaceWidth, surfaceHeight);
        }
    }

    public void update() {
        //mBattleground.update();

        for (Tower tower : mTowers) {
            if (tower.isOutOfPlay()) {
                continue;
            }
            tower.update(mEnemies);
        }

        for (Enemy enemy : mEnemies) {
            if (enemy.isOutOfPlay()) {
                mGarbage.add(enemy);
                mEnemies.remove(enemy);
                // spawn a new enemy
                spawnEnemy();
                continue;
            }
            // TODO not always true
            enemy.update(mBattleground, true);
        }
    }

    public void draw(Canvas canvas) {

        // Draw background
        mBattleground.draw(canvas);

        // Draw the elements
        // It is important to draw towers first if there are flying enemies.
        for (Tower tower : mTowers) {
            tower.draw(canvas);
        }
        for (Enemy enemy : mEnemies) {
            enemy.draw(canvas);
        }

        // Draw animations (such as missiles).
        // TODO Draw the animations

        // Draw the main HUD
        drawHUD(canvas);
    }

    /**
     * Draw all Head-up display.
     * <ul>
     * <li>Draw towers HUD</li>
     * <li>Draw enemies HUD</li>
     * <li>Draw main HUD</li>
     * </ul>
     */
    protected void drawHUD(Canvas canvas) {

        // Draw the elements
        // It is important to draw towers first if there are flying enemies.
        for (Tower tower : mTowers) {
            tower.drawHUD(canvas);
            tower.drawDebugHUD(canvas, mDebugPaint);
        }
        for (Enemy enemy : mEnemies) {
            enemy.drawHUD(canvas);
            enemy.drawDebugHUD(canvas, mDebugPaint);
        }

        drawMainHUD(canvas);
        drawMainHUDDebug(canvas);
    }


    /**
     * Draw the main Head-up display.<br />
     * Scores, GUI, ...
     */
    protected void drawMainHUD(Canvas canvas) {
        // TODO Draw the main HUD
    }

    // TODO update comment
    /**
     * Draw the main Head-up display.<br />
     * Scores, GUI, ...
     */
    protected void drawMainHUDDebug(Canvas canvas) {

        // Draw app version
        try {
            PackageInfo info = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);

            final String versionCode = "Version Code: " + String.valueOf(info.versionCode);
            final String versionName = "Version Name: " + info.versionName;
            Rect textBoundsCode = new Rect();
            mDebugPaint.getTextBounds(versionCode, 0, versionCode.length(), textBoundsCode);
            Rect textBoundsName = new Rect();
            mDebugPaint.getTextBounds(versionName, 0, versionName.length(), textBoundsName);

            float yCode = mSurfaceHeight - textBoundsCode.height();
            float yName = yCode - textBoundsName.height();

            // Save paint style
            Paint.Style initialStyle = mDebugPaint.getStyle();
            mDebugPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            canvas.drawText(versionName, 0, yName, mDebugPaint);
            canvas.drawText(versionCode, 0, yCode, mDebugPaint);
            // Restore paint style
            mDebugPaint.setStyle(initialStyle);

        } catch (PackageManager.NameNotFoundException e) { }

        for (Destructible destructible : mGarbage) {
            if (destructible.isDead()) {
                drawCenteredText("Dead !", canvas, mBattleground.getCenterX(), mBattleground.getCenterY(), mDebugPaint);

            } else if (destructible instanceof Enemy && ((Enemy) destructible).isFinisher()){
                drawCenteredText("Finnisher !", canvas, mBattleground.getCenterX(), mBattleground.getCenterY(), mDebugPaint);
            }
        }

    }

    // TODO move into utils
    private static void drawCenteredText(String text, Canvas canvas, float centerX, float centerY, Paint paint) {
        Rect textBounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), textBounds);
        float posX = centerX - textBounds.width() / 2f;
        float posY = centerY - textBounds.height() / 2f;
        canvas.drawText(text, posX, posY, paint);
    }
}
