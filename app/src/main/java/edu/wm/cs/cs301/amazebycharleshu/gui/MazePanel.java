package edu.wm.cs.cs301.amazebycharleshu.gui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Nullable;

import edu.wm.cs.cs301.amazebycharleshu.R;

public class MazePanel extends View implements P7PanelF22 {
    //Global variables to store drawing info
    private Bitmap bitmap;
    private Paint paint;
    private Canvas tempCanvas;
    private boolean drawManual = true;

    //*************************************
    //AUTOMATICALLY GENERATED CONSTRUCTORS
    //PROBABLY NOT GOING TO BE FULLY USED
    //*************************************

    /**
     * Default constructor
     * @param context as passed context
     */
    public MazePanel(Context context) {
        super(context);
        init(null);
    }

    /**
     * Constructor
     * @param context as passed context
     * @param attrs as passed attributes to use
     */
    public MazePanel(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    /**
     * Constructor
     * @param context as passed context
     * @param attrs as passed attributes to use
     * @param defStyleAttr as passed attribute style
     */
    public MazePanel(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    /**
     * Constructor
     * @param context as passed context
     * @param attrs as passed attributes to use
     * @param defStyleAttr as passed attribute style
     * @param defStyleRes as passed resource style
     */
    public MazePanel(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    //*************************************
    //END OF AUTOMATICALLY GENERATED CONSTRUCTORS
    //*************************************

    /**
     * Initializer
     * Sets up paint, canvas, and bitmap objects
     * @param attrs as passed attributes to use
     */
    private void init(@Nullable AttributeSet attrs) {
        //Create paint object
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(3);
        //Create bitmap
        bitmap = Bitmap.createBitmap(1080, 1080, Bitmap.Config.ARGB_8888);
        //Set to temporary canvas for later drawing on main canvas
        tempCanvas = new Canvas(bitmap);
    }

    /**
     * Draws queued shapes/items on given canvas
     * @param canvas to draw on
     */
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(bitmap, 0, 0, paint);
    }

    /**
     * Tests drawing functions
     */
    public void testImage() {
        addBackground(40);
        setColor(Color.YELLOW);
        addFilledRectangle(10, 10, 100, 100);
        int[] x = {1, 100 ,500};
        int[] y = {4, 500, 800};
        setColor(Color.BLUE);
        addFilledPolygon(x, y, 3);
        setColor(Color.MAGENTA);
        addPolygon(x, y, 3);
        addLine(5, 10, 1000, 1800);
        addLine(100, 200, 1000, 1800);
        addLine(200, 300, 1000, 1800);
        setColor(Color.RED);
        addFilledOval(200, 20, 100, 100);
        setColor(Color.GREEN);
        addFilledOval(300, 30, 100, 100);
        setColor(Color.BLACK);
        addArc(200, 200, 200, 200, 0, 120);
        addMarker(500, 500, "hello");
    }

    /**
     * Commits all accumulated drawings to the UI.
     * Substitute for MazePanel.update method.
     */
    @Override
    public void commit() {
        postInvalidate();
    }

    /**
     * Tells if instance is able to draw. This ability depends on the
     * context, for instance, in a testing environment, drawing
     * may be not possible and not desired.
     * Substitute for code that checks if graphics object for drawing is not null.
     * @return true if drawing is possible, false if not.
     */
    @Override
    public boolean isOperational() {
        if (tempCanvas == null) {
            return false;
        }
        else {
            return true;
        }
    }

    /**
     * Sets the color for future drawing requests. The color setting
     * will remain in effect till this method is called again and
     * with a different color.
     * Substitute for Graphics.setColor method.
     * @param argb gives the alpha, red, green, and blue encoded value of the color
     */
    @Override
    public void setColor(int argb) {
        paint.setColor(argb);
    }

    /**
     * Returns the ARGB value for the current color setting.
     * @return integer ARGB value
     */
    @Override
    public int getColor() {
        return paint.getColor();
    }

    /**
     * Draws two solid rectangles to provide a background.
     * Note that this also erases any previous drawings.
     * The color setting adjusts to the distance to the exit to
     * provide an additional clue for the user.
     * Colors transition from black to gold and from grey to green.
     * Substitute for FirstPersonView.drawBackground method.
     * @param percentToExit gives the distance to exit
     */
    @Override
    public void addBackground(float percentToExit) {
        //Draw 2 rectangles; 1 on top, 1 on bottom of screen
        //If user is not close to exit, use gray bottom and black top
        if (percentToExit < 50) {
            setColor(Color.GRAY);
            addFilledRectangle(0, bitmap.getHeight() / 2, bitmap.getWidth(), bitmap.getHeight());
            setColor(Color.BLACK);
            //Create space texture bitmap and set to paint tool for use in top rectangle
            Bitmap wallBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.space);
            Shader shader = new BitmapShader(wallBitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
            paint.setShader(shader);
            addFilledRectangle(0, 0, bitmap.getWidth(), bitmap.getHeight() / 2);
            paint.setShader(null);
        }
        //Else use gold bottom and green top
        else {
            setColor(Color.YELLOW);
            addFilledRectangle(0, bitmap.getHeight() / 2, bitmap.getWidth(), bitmap.getHeight());
            setColor(Color.GREEN);
            addFilledRectangle(0, 0, bitmap.getWidth(), bitmap.getHeight() / 2);
        }
    }

    /**
     * Adds a filled rectangle.
     * The rectangle is specified with the {@code (x,y)} coordinates
     * of the upper left corner and then its width for the
     * x-axis and the height for the y-axis.
     * Substitute for Graphics.fillRect() method
     * @param x is the x-coordinate of the top left corner
     * @param y is the y-coordinate of the top left corner
     * @param width is the width of the rectangle
     * @param height is the height of the rectangle
     */
    @Override
    public void addFilledRectangle(int x, int y, int width, int height) {
        //Create new rectangle based off given parameters
        //x and y give top left position
        //x + width give right position
        //y + height give bottom position
        Rect rect = new Rect(x, y, x + width, y + height);

        //Make sure rectangle is filled
        paint.setStyle(Paint.Style.FILL);
        //Draw rectangle
        tempCanvas.drawRect(rect, paint);
    }

    /**
     * Adds a filled polygon.
     * The polygon is specified with {@code (x,y)} coordinates
     * for the n points it consists of. All x-coordinates
     * are given in a single array, all y-coordinates are
     * given in a separate array. Both arrays must have
     * same length n. The order of points in the arrays
     * matter as lines will be drawn from one point to the next
     * as given by the order in the array.
     * Substitute for Graphics.fillPolygon() method
     * @param xPoints are the x-coordinates of points for the polygon
     * @param yPoints are the y-coordinates of points for the polygon
     * @param nPoints is the number of points, the length of the arrays
     */
    @Override
    public void addFilledPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        //Create new path instance to store polygon shape
        Path polygon = new Path();

        //Set first point
        polygon.moveTo(xPoints[0], yPoints[0]);
        //For rest of points, parse through point arrays and draw line to such point
        for (int i = 1; i < nPoints; i++) {
            polygon.lineTo(xPoints[i], yPoints[i]);
        }
        //Close shape
        polygon.close();

        //Ensure that shape is filled in
        paint.setStyle(Paint.Style.FILL);
        //Add filled polygon to canvas
        tempCanvas.drawPath(polygon, paint);
    }

    /**
     * Adds a polygon.
     * The polygon is not filled.
     * The polygon is specified with {@code (x,y)} coordinates
     * for the n points it consists of. All x-coordinates
     * are given in a single array, all y-coordinates are
     * given in a separate array. Both arrays must have
     * same length n. The order of points in the arrays
     * matter as lines will be drawn from one point to the next
     * as given by the order in the array.
     * Substitute for Graphics.drawPolygon method
     * @param xPoints are the x-coordinates of points for the polygon
     * @param yPoints are the y-coordinates of points for the polygon
     * @param nPoints is the number of points, the length of the arrays
     */
    @Override
    public void addPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        //Create new path instance to store polygon shape
        Path polygon = new Path();

        //Set first point
        polygon.moveTo(xPoints[0], yPoints[0]);
        //For rest of points, parse through point arrays and draw line to such point
        for (int i = 1; i < nPoints; i++) {
            polygon.lineTo(xPoints[i], yPoints[i]);
        }
        //Close shape
        polygon.close();

        //Ensure that shape is not filled in
        paint.setStyle(Paint.Style.STROKE);
        //Add unfilled polygon to canvas
        tempCanvas.drawPath(polygon, paint);
    }

    /**
     * Adds a line.
     * A line is described by {@code (x,y)} coordinates for its
     * starting point and its end point.
     * Substitute for Graphics.drawLine method
     * @param startX is the x-coordinate of the starting point
     * @param startY is the y-coordinate of the starting point
     * @param endX is the x-coordinate of the end point
     * @param endY is the y-coordinate of the end point
     */
    @Override
    public void addLine(int startX, int startY, int endX, int endY) {
        tempCanvas.drawLine(startX, startY, endX, endY, paint);
    }

    /**
     * Adds a filled oval.
     * The oval is specified with the {@code (x,y)} coordinates
     * of the upper left corner and then its width for the
     * x-axis and the height for the y-axis. An oval is
     * described like a rectangle.
     * Substitute for Graphics.fillOval method
     * @param x is the x-coordinate of the top left corner
     * @param y is the y-coordinate of the top left corner
     * @param width is the width of the oval
     * @param height is the height of the oval
     */
    @Override
    public void addFilledOval(int x, int y, int width, int height) {
        //Create new oval based off given parameters
        //x and y give top left position
        //x + width give right position
        //y + height give bottom position
        RectF oval = new RectF(x, y, x + width, y + height);

        //Make sure oval is filled
        paint.setStyle(Paint.Style.FILL);
        //Draw oval
        tempCanvas.drawOval(oval, paint);
    }

    /**
     * Adds the outline of a circular or elliptical arc covering the specified rectangle.
     * The resulting arc begins at startAngle and extends for arcAngle degrees,
     * using the current color. Angles are interpreted such that 0 degrees
     * is at the 3 o'clock position. A positive value indicates a counter-clockwise
     * rotation while a negative value indicates a clockwise rotation.
     * The center of the arc is the center of the rectangle whose origin is
     * (x, y) and whose size is specified by the width and height arguments.
     * The resulting arc covers an area width + 1 pixels wide
     * by height + 1 pixels tall.
     * The angles are specified relative to the non-square extents of
     * the bounding rectangle such that 45 degrees always falls on the
     * line from the center of the ellipse to the upper right corner of
     * the bounding rectangle. As a result, if the bounding rectangle is
     * noticeably longer in one axis than the other, the angles to the start
     * and end of the arc segment will be skewed farther along the longer
     * axis of the bounds.
     * Substitute for Graphics.drawArc method
     * @param x the x coordinate of the upper-left corner of the arc to be drawn.
     * @param y the y coordinate of the upper-left corner of the arc to be drawn.
     * @param width the width of the arc to be drawn.
     * @param height the height of the arc to be drawn.
     * @param startAngle the beginning angle.
     * @param arcAngle the angular extent of the arc, relative to the start angle.
     */
    @Override
    public void addArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        //Set up rectangle defining outline of arc area
        RectF oval = new RectF(x, y, x + width, y + height);

        //Ensure that shape is not filled in
        paint.setStyle(Paint.Style.STROKE);
        //Draw arc
        tempCanvas.drawArc(oval, startAngle, -(arcAngle), false, paint);
    }

    /**
     * Adds a string at the given position.
     * Substitute for CompassRose.drawMarker method
     * @param x the x coordinate
     * @param y the y coordinate
     * @param str the string
     */
    @Override
    public void addMarker(float x, float y, String str) {
        //Ensure that shape is filled in
        paint.setStyle(Paint.Style.FILL);
        //Set text size
        paint.setTextSize(48);
        //Draw text
        tempCanvas.drawText(str, x, y, paint);
    }

    /**
     * Sets the value of a single preference for the rendering algorithms.
     * It internally maps given parameter values into corresponding java.awt.RenderingHints
     * and assigns that to the internal graphics object.
     * Hint categories include controls for rendering quality
     * and overall time/quality trade-off in the rendering process.
     *
     * Refer to the awt RenderingHints class for definitions of some common keys and values.
     *
     * Note for Android: start with an empty default implementation.
     * Postpone any implementation efforts till the Android default rendering
     * results in unsatisfactory image quality.
     *
     * @param hintKey the key of the hint to be set.
     * @param hintValue the value indicating preferences for the specified hint category.
     */
    @Override
    public void setRenderingHint(P7RenderingHints hintKey, P7RenderingHints hintValue) {

    }

    /**
     * Sets paint tool shader to use preset texture mimicking the Death Star's walls
     */
    public void setWallTexture() {
        Bitmap wallBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.death_star_texture);
        Shader shader = new BitmapShader(wallBitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        paint.setShader(shader);
    }

    /**
     * Sets paint tool shader to null; resets shader
     */
    public void resetWallTexture() {
        paint.setShader(null);
    }
}
