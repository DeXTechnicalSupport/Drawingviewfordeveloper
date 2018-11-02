package com.developer.sdc2018.samsungdex.drawingappfordex;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.PointerIcon;
import android.view.View;


import java.util.ArrayList;


class CustomView extends View{
    final static String TAG ="CustomeView";

    private static final String LOG_CAT = CustomView.class.getSimpleName();

    //drawing path
    private Path drawPath;

    //defines what to draw
    private Paint canvasPaint;

    //defines how to draw
    private Paint drawPaint;

    //initial color
    private int paintColor = Color.BLACK;

    private Paint _paintBlur;

    //flag to set erase mode
    private boolean eraseMode = false;

    //canvas - holding pen, holds your drawings
    //and transfers them to the view
    private Canvas drawCanvas;


    //canvas bitmap
    private Bitmap canvasBitmap;

    //brush size
    private float currentBrushSize, lastBrushSize;

    private ArrayList<Path> paths = new ArrayList<Path>();
    private ArrayList<Paint> paints =new ArrayList<Paint>();
    private ArrayList<Path> undonePaths = new ArrayList<Path>();
    private ArrayList<Paint> undonePaints = new ArrayList<Paint>();
    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;
    private boolean mouseRightClicked = false;
    Bitmap orgImage = null;

    private void init(){

        //init brushsize setting as 1
        currentBrushSize = getResources().getInteger(R.integer.small_size);
        lastBrushSize = currentBrushSize;

        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(currentBrushSize);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);

        canvasPaint = new Paint(Paint.DITHER_FLAG);

        this._paintBlur = new Paint();
        this._paintBlur.set(drawPaint);
        this._paintBlur.setAntiAlias(true);
        this._paintBlur.setDither(true);
        this._paintBlur.setStyle(Paint.Style.STROKE);
        this._paintBlur.setStrokeJoin(Paint.Join.ROUND);
        this._paintBlur.setStrokeCap(Paint.Cap.ROUND);
        this._paintBlur.setColor(Color.RED);
        this._paintBlur.setStrokeWidth(6);
        this._paintBlur.setMaskFilter(new BlurMaskFilter(10.0F, BlurMaskFilter.Blur.OUTER));
    }


    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        Bitmap pointerIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_pencil);
        setPointerIcon(PointerIcon.create(pointerIcon,0,1));
    }

    @Override
    protected void onDraw(Canvas canvas) {

        int i=0;
        if(orgImage != null){
            canvas.drawBitmap(Bitmap.createBitmap(orgImage), 0, 0, null);
        }

        for (Path p : paths) {
            canvas.drawPath(p, paints.get(i++));
        }
        canvas.drawPath(drawPath, drawPaint);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        //create canvas of certain device size.
        super.onSizeChanged(w, h, oldw, oldh);

        //create Bitmap of certain w,h
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

        //apply bitmap to graphic to start drawing.
        drawCanvas = new Canvas(canvasBitmap);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();

        Log.d(TAG, "CustomView onTouchEvent : "+ event.getAction() +" : "+ event.getButtonState()+ " : " + event.getX() + " : " + event.getY());
        if(event.getButtonState() != MotionEvent.BUTTON_SECONDARY) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                touch_start(touchX, touchY);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(touchX, touchY);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
                invalidate();
                        //mouse right click
                        mouseRightClicked=false;
                break;
            default:
                return false;
        }
        }else{
            //mouse right click
            if (event.getButtonState() == MotionEvent.BUTTON_SECONDARY && event.getAction() == MotionEvent.ACTION_DOWN ) {
                mouseRightClicked = true;
                showContextMenu(touchX, touchY);
            }
        }
        return true;
    }

    public void setErase(boolean isErase){
        eraseMode = isErase;

        if(eraseMode){
            drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        } else {
            drawPaint.setXfermode(null);
        }
    }


    public void eraseAll() {
        drawPath = new Path();
        paths.clear();
        paints.clear();//arthur
        if(orgImage != null) {
            orgImage = null;
        }
        drawCanvas.drawColor(Color.WHITE);
        invalidate();
    }


    private void touch_start(float x, float y) {
        undonePaths.clear();
        drawPath.reset();
        drawPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touch_up() {
        //mouse right click
        //if mouse right button is not click, Draw line save.
        if(!mouseRightClicked) {
        drawPath.lineTo(mX, mY);
        paths.add(drawPath);
        paints.add(drawPaint);
        }

        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(currentBrushSize);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            drawPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
            mX = x;
            mY = y;
        }
    }


    public void onClickUndo () {
        if (paths.size()>0)
        {
            undonePaths.add(paths.remove(paths.size()-1));
            undonePaints.add(paints.remove(paints.size()-1));
            invalidate();
        }
    }

    public void onClickRedo (){
        if (undonePaths.size()>0)
        {
            paths.add(undonePaths.remove(undonePaths.size()-1));
            paints.add(undonePaints.remove(undonePaints.size()-1));
            invalidate();
        }
    }

    //method to set brush size
    public void setBrushSize(float newSize) {
        float pixelAmount = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,newSize, getResources().getDisplayMetrics());
        currentBrushSize = pixelAmount;
        Log.d(TAG,"Brush size :" + newSize);
        drawPaint.setStrokeWidth(currentBrushSize);
    }

    public void setImage(String diretorypath, String filename){

        Log.d(TAG, diretorypath+"/"+filename);
        //Toast.makeText(getContext(), diretorypath+"/"+filename, Toast.LENGTH_LONG).show();
        orgImage = BitmapFactory.decodeFile(diretorypath+"/"+filename);
    }

    public void setLastBrushSize(float lastSize){
        lastBrushSize=lastSize;
    }

    public float getLastBrushSize(){
        return lastBrushSize;
    }

    public void setBrushColor(int Color)
    {
        paintColor=Color;
    }

    public int getBrushColor(){
        return paintColor;
    }

    public ArrayList<Paint> getPaints() {
        return paints;
    }
    public ArrayList<Paint> setPaints(){
        return paints;
    }

    public ArrayList<Path> getPaths() {
        return paths;
    }

}
