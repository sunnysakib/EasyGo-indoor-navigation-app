package rijve.shovon.easygo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

public class MyCanvas extends View {
    private Bitmap backgroundImage;
    private Canvas canvas;
    private float zoomFactor = 1.0f;
    //    private float translateX = 0.0f;
//    private float translateY = 0.0f;
    private Paint paint;
    private Paint linePaint;
    float nodeX;
    float nodeY;
    private float previousX;
    private int backgroundColor = Color.CYAN;
    private float previousY;
    private float translateX;
    private float translateY;
    private Paint textPaint;
    private ScaleGestureDetector scaleGestureDetector;
    private float scaleFactor = 1.0f;
    private Path roadPath;


    private ArrayList<CircleCoordinates> circleCoordinatesList;
    private ArrayList<LineCoordinates> lineCoordinatesList;

    public MyCanvas(Context context) {
        super(context);
        init();
    }



    public MyCanvas(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }



    private void init() {

        paint = new Paint();
        paint.setColor(Color.GREEN);
        // paint.setStrokeWidth(10);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(40f);

        linePaint = new Paint();
        linePaint.setColor(Color.BLUE);
        linePaint.setStrokeWidth(50f);

        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(60f);
        textPaint.setTextAlign(Paint.Align.CENTER);
        scaleGestureDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
    }

    public void setBackgroundImage(Bitmap bitmap) {
        backgroundImage = bitmap;
        canvas = new Canvas(backgroundImage);
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public Bitmap getBitmap() {
        return backgroundImage;
    }
    // node 1 node 2  distance....
    public void setNodeData(String nodeDataString , String nodeEdgeString) {
        ArrayList<CircleCoordinates> circleCoordinatesList = new ArrayList<>();
        ArrayList<LineCoordinates> lineCoordinatesList = new ArrayList<>();
        String[] nodeDataArray = nodeDataString.split("___");
        String[] edgeDataArray = nodeEdgeString.split("@");

        for(String edgeInfo: edgeDataArray){
            String[] edgeDetails = edgeInfo.split("_");


            float nodeX1 =Float.parseFloat(edgeDetails[1])*50;
            float nodeY1 = Float.parseFloat(edgeDetails[2])*50;
            float nodeX2 =Float.parseFloat(edgeDetails[5])*50;
            float nodeY2 = Float.parseFloat(edgeDetails[6])*50;

            lineCoordinatesList.add(new LineCoordinates((nodeX1*(-1)), nodeY1,nodeX2*(-1), nodeY2));

        }

        for (String nodeData : nodeDataArray) {
            String[] coordinates = nodeData.split("_");
            //if (coordinates.length == 3) {
                String nodeName = coordinates[0];
                float nodeX = Float.parseFloat(coordinates[1])*50;
                float nodeY = Float.parseFloat(coordinates[2])*50;
                // If you have nodeZ, you can parse it as well (coordinates[2])

                // Add the CircleCoordinates object to the ArrayList
                circleCoordinatesList.add(new CircleCoordinates((nodeX*(-1)), nodeY,nodeName));
            //}
        }
        this.lineCoordinatesList = lineCoordinatesList;
        this.circleCoordinatesList = circleCoordinatesList;
        invalidate();
    }

    public void zoomIn() {
        zoomFactor += 0.1f; // Adjust the increment as per your preference
        invalidate();
    }

    public void zoomOut() {
        zoomFactor -= 0.1f; // Adjust the decrement as per your preference
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Apply zoom and translation to the canvas
        canvas.scale(zoomFactor, zoomFactor);
        canvas.translate(translateX, translateY);

        nodeX = canvas.getWidth() / 2f;
        nodeY = canvas.getHeight() / 2f;
        canvas.scale(scaleFactor, scaleFactor, nodeX, nodeY);
        // Draw the background image
        if (backgroundImage != null) {
            canvas.drawBitmap(backgroundImage, 0, 0, null);
        }
        // nodeName1
        // Draw circles based on CircleCoordinates

        if(lineCoordinatesList!=null){
            for(LineCoordinates lineCoordinates: lineCoordinatesList){
                float startX = getWidth()-lineCoordinates.getStartX();
                float startY =getHeight() - lineCoordinates.getStartY();
                float endX = getWidth()-lineCoordinates.getEndX();
                float endY = getHeight() -lineCoordinates.getEndY();
                canvas.drawLine(startX, startY,endX, endY, linePaint);
            }
        }


        if (circleCoordinatesList != null) {
            for (CircleCoordinates circleCoordinates : circleCoordinatesList) {
                nodeX = circleCoordinates.getX();
                nodeY = circleCoordinates.getY();
                String nodeName = circleCoordinates.getNodeName();
                canvas.drawCircle(getWidth() - nodeX, getHeight() - nodeY, 30, paint);
                float textHeight = textPaint.descent() - textPaint.ascent();
                float textOffset = (textHeight / 2) - textPaint.descent();
                //nodeX *= -1;
                canvas.drawText(String.valueOf(nodeName), getWidth() - nodeX, (getHeight() - nodeY) - textOffset, textPaint);
            }
        }



    }

    public void drawCircleOnCanvas(float x, float y) {
        // Adjust the radius and color as per your preference
        float radius = 20f;
        int color = Color.RED;

        // Calculate the adjusted coordinates based on zoom and translation
        float adjustedX = translateX + (x * zoomFactor);
        float adjustedY = translateY + (y * zoomFactor);

        // Create a new Paint object for the circle
        Paint circlePaint = new Paint();
        circlePaint.setColor(color);
        circlePaint.setStrokeWidth(10);
        circlePaint.setStyle(Paint.Style.STROKE);

        // Draw the circle on the canvas
        canvas.drawCircle(adjustedX, adjustedY, radius, circlePaint);

        // Invalidate the view to trigger a redraw
        invalidate();
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);

        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                previousX = x;
                previousY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = x - previousX;
                float dy = y - previousY;
                translateX += dx;
                translateY += dy;
                invalidate();
                break;
        }

        previousX = x;
        previousY = y;

        return true;
    }
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= detector.getScaleFactor();
            // Limit the scale factor to a certain range (optional)
            scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 5.0f));
            return true;
        }
    }

}