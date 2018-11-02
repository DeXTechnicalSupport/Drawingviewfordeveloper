package com.developer.sdc2018.samsungdex.drawingappfordex;

import android.annotation.SuppressLint;
import android.Manifest;
import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Objects;
import java.io.IOException;
import java.io.OutputStream;


public class MainActivity extends AppCompatActivity {
    Context mContext = MainActivity.this;
    final static String TAG = "DrawingAppForDeveloper";

    public CustomView mCustomView;
    private LinearLayout hiddenLayout;
    private int brushSizewithWheel;

    //DeX values
    //ctl key check value
    private boolean ctrlkeyPressed = false;
    View mViewInSencondaryDisplay = null;
    WindowManager mSecondaryWm = null;

    public int iIsDeXMode = 0;
    public int isModeChanged = 0;
    Bundle mBundleModeChange = null;

    //Overay permission
    public static int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 5469;

    ImageView imgbtn_window_close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        mBundleModeChange = new Bundle();
        makeView();
    }

    private void makeView(){
        setContentView(R.layout.activity_main);

        Toolbar mToolbar_top = findViewById(R.id.toolbar_top);
        setSupportActionBar(mToolbar_top);

        //Mouse Right Click
        //Register context menu for mouse right click
        mCustomView = findViewById(R.id.custom_view);
/**     //mouse right click 1/3
        registerForContextMenu(mCustomView);
 */

        //isSamsungDex : Hidden Layout
        //Checking the Samsung DeX and then you have to set up hiddenlayout
        hiddenLayout = findViewById(R.id.hidden);
        if (DeXUtils.isDeXAvailable(getApplication())) {
 /**        //Detect DeX mode 1/7
            hiddenLayout.setVisibility(View.VISIBLE);
            showTheValueChanged();
            CheckPermission();
*/
        }

        iIsDeXMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_TYPE_MASK;

        Toolbar mToolbar_bottom = findViewById(R.id.toolbar_bottom);
        mToolbar_bottom.inflateMenu(R.menu.menu_drawing);
        mToolbar_bottom.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                handleDrawingIconTouched(item.getItemId());
                return false;
            }
        });

        FloatingActionButton fab = findViewById(R.id.email_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Display currentDisplay = getWindow().getDecorView().getDisplay();

                ActivityOptions options = ActivityOptions.makeBasic();
/**             // Floating E-mail 1/1
                options.setLaunchDisplayId(DeXUtils.getOtherDisplayId(getApplication(), currentDisplay));
*/
                Intent intent = getPackageManager().getLaunchIntentForPackage("com.google.android.gm");
                assert intent != null;
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent, options.toBundle());
            }
        });

        //Ctl key + mouse wheel : change the brush size
        mCustomView.setOnGenericMotionListener(new View.OnGenericMotionListener() {
            @Override
            public boolean onGenericMotion(View view, MotionEvent motionEvent) {
/**             //Ctrl key enable(total 2) + mouse wheel 1/1
                int action = motionEvent.getAction();

                if ((action == MotionEvent.ACTION_SCROLL) && ctrlkeyPressed) {
                    float vScroll = motionEvent.getAxisValue(MotionEvent.AXIS_VSCROLL);
                    if (vScroll < 0) {
                        //wheel down
                        if (brushSizewithWheel > getResources().getInteger(R.integer.min_size))
                            brushSizewithWheel--;
                        else
                            brushSizewithWheel = getResources().getInteger(R.integer.min_size);
                    } else {
                        //wheel up
                        if (brushSizewithWheel < getResources().getInteger(R.integer.max_size))
                            brushSizewithWheel++;
                        else
                            brushSizewithWheel = getResources().getInteger(R.integer.max_size);
                    }
                    Toast.makeText(getApplicationContext(), "Brush Size : " + brushSizewithWheel, Toast.LENGTH_LONG).show();
                    mCustomView.setBrushSize(brushSizewithWheel);
                    mCustomView.setLastBrushSize(brushSizewithWheel);
                }
                return true;
            }
*/
                return true;
            }
        });
        //Ctl key + mouse wheel : change the brush size
    }

    //Add window on the external monitor
    void addWindowInSecondaryDisplay() {
        /**     //Added window 1/9
         if (mViewInSencondaryDisplay == null) {
         Display currentDisplay = getWindow().getDecorView().getDisplay();

         //Get the Secondary window manager
         Context displayContext = mContext.createDisplayContext(DeXUtils.getOtherDisplay(getApplication(), getWindowManager(), currentDisplay));
         mSecondaryWm = (WindowManager) displayContext.getSystemService(Context.WINDOW_SERVICE);

         //Get the Layout for secondary window
         LayoutInflater li = (LayoutInflater) displayContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
         LinearLayout layout = new LinearLayout(displayContext);
         mViewInSencondaryDisplay = Objects.requireNonNull(li).inflate(R.layout.activity_dualview, layout, false);

         //Setting the window layout
         WindowManager.LayoutParams params = new WindowManager.LayoutParams();
         params.width = WindowManager.LayoutParams.MATCH_PARENT;
         params.height = WindowManager.LayoutParams.MATCH_PARENT;
         params.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR;
         params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;

         mViewInSencondaryDisplay.setBackgroundColor(Color.WHITE);

         //Immersive mode setting : Full Screen
         DeXUtils.immersiveMode(mViewInSencondaryDisplay);
         mViewInSencondaryDisplay.setOnSystemUiVisibilityChangeListener(
         new View.OnSystemUiVisibilityChangeListener() {
        @Override
        public void onSystemUiVisibilityChange(int visibility) {
        if (mViewInSencondaryDisplay != null) {
        DeXUtils.immersiveMode(mViewInSencondaryDisplay);
        }
        }
        });

         getWindow().setNavigationBarColor(Color.WHITE);

         imgbtn_window_close = (ImageView) mViewInSencondaryDisplay.findViewById(R.id.dualView_Image);
         imgbtn_window_close.setOnClickListener(mBtnClickLisner);


         //Add window display configurations to window manager
         mSecondaryWm.addView(mViewInSencondaryDisplay, params);
         Log.d(TAG, "Secondary Window Added");
         //Toast.makeText(getApplicationContext(),"Secondary Window dded",Toast.LENGTH_LONG).show();
         }
         */
    }

    void removeWindowInSecondaryDisplay() {
        /**     //Added window 2/9
         if(mViewInSencondaryDisplay != null) {
         mSecondaryWm.removeView(mViewInSencondaryDisplay);
         mViewInSencondaryDisplay = null;
         mSecondaryWm = null;
         }
         */
    }

    //isSamsungDex : Share Button
    private void handleDrawingIconTouched(int itemId) {
        switch (itemId) {
            case R.id.action_delete:
                deleteDialog();
                break;
            case R.id.action_undo:
                mCustomView.onClickUndo();
                break;
            case R.id.action_redo:
                mCustomView.onClickRedo();
                break;
            case R.id.action_brush:
                brushSizePicker();
                break;
            case R.id.action_share:
                if (DeXUtils.isDeXAvailable(getApplication())) {
                     /** //Added window 3/9 */
                     AddWindowAfterCheckPermission();
                } else {
                    Toast.makeText(getApplicationContext(), "This function will only work if Samsung's DeX mode is available.", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    //When click the image button, Secondary window will be closed.
    View.OnClickListener mBtnClickLisner = new View.OnClickListener(){

        @Override
        public void onClick(View view) {
         /** //Added window 4/9 */
         switch (view.getId()) {
         case R.id.dualView_Image:
         removeWindowInSecondaryDisplay();
         break;

         default:
         break;
         }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        /** //Added window 5/9 */
        /** //You can find next step of "Added window" source code that is 6 to 9 in the DeXUtils.java file. */
        removeWindowInSecondaryDisplay();
    }

    //isSamsungDex : onConfigurationChanged
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d(TAG,"onConfigurationChanged isDeXMode:"+iIsDeXMode);

        if(iIsDeXMode != (getResources().getConfiguration().uiMode & Configuration.UI_MODE_TYPE_MASK)) {
            iIsDeXMode = (getResources().getConfiguration().uiMode & Configuration.UI_MODE_TYPE_MASK);
            Log.d(TAG,"onConfigurationChanged call makeView() !! isDeXMode:"+iIsDeXMode);
            isModeChanged = 1;
            backupModeChange();
            makeView();
        }

/** //Detect DeX mode 2/7
        if (DeXUtils.isDeXAvailable(getApplication())) {
            hiddenLayout.setVisibility(View.VISIBLE);
            showTheValueChanged();
        } else {
            hiddenLayout.setVisibility(View.GONE);
        }
*/
    }

    //Samsung DeX: Hidden Layout Needs below functions
    @SuppressLint("SetTextI18n")
    private void showTheValueChanged() {

/**     //Detect DeX mode 3/7
        TextView valueText = findViewById(R.id.values);
        valueText.setText("Density : " + getDensity()
                + " | Orientation : " + getOrientation()
                + " | (width, height) : (" + Integer.toString(getResources().getConfiguration().screenWidthDp) + ", " + Integer.toString(getResources().getConfiguration().screenHeightDp) + ")"
                + " | UI Mode : " + getUImode());
*/

    }

    private String getUImode()
    {
/**     //Detect DeX mode 4/7
        int screenUimode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_TYPE_MASK;

        switch(screenUimode){
            case Configuration.UI_MODE_TYPE_DESK :
                return "DESK MODE";
            case Configuration.UI_MODE_TYPE_NORMAL :
                return "NORMAL MODE";
            default :
                return null;
        }
*/
        /** plase remove the "return null" below. */
        return null;
        }

    private String getOrientation()
    {

/**     //Detect DeX mode 5/7
        int orientation = getResources().getConfiguration().orientation;

        if(Configuration.ORIENTATION_LANDSCAPE == orientation){
            return "Landscape";
        } else if(Configuration.ORIENTATION_PORTRAIT == orientation){
            return "Portrait";
        }
*/
        /** plase remove the "return null" below. */
        return null;
    }

    private String getDensity()
    {
/**      //Detect DeX mode 6/7
         float density = getResources().getDisplayMetrics().density;

         if(density >= 4.0){
         return "xxxhdpi";
         } else if(density >= 3.0){
         return "xxhdpi";
         } else if(density >= 2.0){
         return "xhdpi";
         } else if(density >= 1.5){
         return "hdpi";
         } else if(density >= 1.0){
         return "mdpi";
         } else {
         return "ldpi";
         }
 */
        /** plase remove the "return null" below. */
        return null;
    }

    //Keyboard input functions
    //On key press
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

/** //Ctrl key enable 1/2
        switch (keyCode) {
            case KeyEvent.KEYCODE_CTRL_LEFT:
            case KeyEvent.KEYCODE_CTRL_RIGHT:
                ctrlkeyPressed = true;
                break;
             /** //Redo/undo ctrl + y, z
             case KeyEvent.KEYCODE_Y:
             if (ctrlkeyPressed)
             mCustomView.onClickRedo();
             break;
             case KeyEvent.KEYCODE_Z:
             if (ctrlkeyPressed)
             mCustomView.onClickUndo();
             break;

         }
*/
        return super.onKeyDown(keyCode, event);
    }

    //On key release
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
/**     //Ctrl key enable 2/2
        switch (keyCode) {
            case KeyEvent.KEYCODE_CTRL_LEFT:
            case KeyEvent.KEYCODE_CTRL_RIGHT:
                ctrlkeyPressed = false;
                break;
        }
*/
        return super.onKeyDown(keyCode, event);
    }



    //Mouse Right Click : When mouse right click, context menu will appear.
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        Log.d(TAG, "onCreate Context menu :" + v.getId() + " : " + R.id.custom_view);
        if (v.getId() == R.id.custom_view) {
/**         //mouse right click 2/3
            menu.setHeaderTitle("Drawing Option");
            getMenuInflater().inflate(R.menu.menu_drawing, menu);
*/
        }
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
/**     //mouse right click 3/3
         switch (item.getItemId()) {
             case R.id.action_delete:
                 deleteDialog();
             break;
             case R.id.action_undo:
                 mCustomView.onClickUndo();
             break;
             case R.id.action_redo:
                 mCustomView.onClickRedo();
             break;
             case R.id.action_brush:
                brushSizePicker();
             break;
            //Add window
             case R.id.action_share:
             if (DeXUtils.isDeXAvailable(getApplication()))
                AddWindowAfterCheckPermission();
             else
                Toast.makeText(getApplicationContext(), " This function will only work if Samsung's DeX mode is available.", Toast.LENGTH_LONG).show();
             break;

             default:
             return super.onContextItemSelected(item);
         }
 */
        /** plase remove the "return super.onContextItemSelected(item);" below. */
          return super.onContextItemSelected(item);
    }
    //Mouse right click



    @SuppressWarnings("StatementWithEmptyBody")
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    //Overay permission check
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
            if (!Settings.canDrawOverlays(this)) {
                AddWindowAfterCheckPermission();
            } else {
                // Do as per your logic
            }
        }
    }

    //Overay permission
    @SuppressLint("ObsoleteSdkInt")
    private void AddWindowAfterCheckPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
            } else {
                // Add window
                if (mViewInSencondaryDisplay == null) {
                    addWindowInSecondaryDisplay();
                } else {
                    removeWindowInSecondaryDisplay();
                }
            }
        }
    }

    //Storage permission
    private void CheckPermission() {
        if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if(shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
               // Toast.makeText(getApplication(),"If you do not forget your draw on this display, swiching to other display, please allow STORAGE permission.", Toast.LENGTH_LONG).show();

            }
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permission, int[] grantResult){
        switch ( requestCode){
            case 1:
                if(grantResult[0] != PackageManager.PERMISSION_GRANTED && grantResult[1]!=PackageManager.PERMISSION_GRANTED){
                    new AlertDialog.Builder(this)
                            .setTitle("Notics")
                            .setMessage("Please you have to allow STORAGE permission. Because when app is launced an other display, you can lost your draw on this display.")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener(){
                                public void onClick(DialogInterface dialog, int which){
                                    dialog.dismiss();
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).
                                            setData(Uri.parse("package:"+getApplicationContext().getPackageName()));
                                    getApplicationContext().startActivity(intent);
                                }
                            }).setNegativeButton("", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            dialog.dismiss();
                        }
                    }).setCancelable(false).show();
                }
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState");

        backupInstance(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle saveInstanceState){
        super.onRestoreInstanceState(saveInstanceState);
        Log.d(TAG,"onRestoreInstanceState");

        restoreInstance(saveInstanceState);
    }

    private void backupInstance(Bundle backupBundle){
        Log.d(TAG, "backupInstance-- brushSizewithWheel:"+brushSizewithWheel);

        //Brush size save
        backupBundle.putInt("Brush_size", brushSizewithWheel);

        //set up directory path and file name
        String directorypath = Environment.getExternalStorageDirectory().toString();
        String imTitle = "DrawingForDeX.png";
        backupBundle.putString("directorypath",directorypath);
        backupBundle.putString("imTitle",imTitle);

        //Save the drawing to open for on another screen
        saveThisDrawing(directorypath,imTitle);
    }
    private void restoreInstance(Bundle restoreBundle){
        Log.d(TAG, "restoreInstance");

        //Brush size restore
        mCustomView.setBrushSize(restoreBundle.getInt("Brush_size"));
        mCustomView.setLastBrushSize(restoreBundle.getInt("Brush_size"));
        brushSizewithWheel=restoreBundle.getInt("Brush_size");

        //If it has some a picture on the previous screen, it will be re-loading on the screen that is ran this app.
        if(restoreBundle.getString("imTitle") != null) {
            //Toss image to DeX or Device screen.
            mCustomView.setImage(restoreBundle.getString("directorypath"), restoreBundle.getString("imTitle"));

            //Remove the temporary file which is image on the previous display.
            File f = new File(restoreBundle.getString("directorypath"), restoreBundle.getString("imTitle"));
            f.delete();
        }

    }

    private void backupModeChange(){
        Log.d(TAG, "backupModeChange");

        backupInstance(mBundleModeChange);
    }
    private void restoreModeChange(){
        Log.d(TAG, "restoreModeChange");
        restoreInstance(mBundleModeChange);
    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart" );
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume/  isModeChanged: "+isModeChanged );

        if(isModeChanged == 1) {
            restoreModeChange();
            isModeChanged = 0;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause" );
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop" );
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart" );

    }

    //Brush Size Dialog
    private void brushSizePicker() {
        //Implement get/set brush size
        BrushSizeChooserFragment brushDialog = BrushSizeChooserFragment.NewInstance((int) mCustomView.getLastBrushSize());
        brushDialog.setOnNewBrushSizeSelectedListener(new OnNewBrushSizeSelectedListener() {
            @Override
            public void OnNewBrushSizeSelected(float newBrushSize) {
                Log.d(TAG, "Brush size :" + newBrushSize);
                mCustomView.setBrushSize(newBrushSize);
                mCustomView.setLastBrushSize(newBrushSize);

                //Brush szie sycn with Dialog box and mouse wheel
                brushSizewithWheel = (int) newBrushSize;
            }
        });
        brushDialog.show(getSupportFragmentManager(), "Dialog");
    }
    //Brush Size Dialog

    private void deleteDialog() {
        AlertDialog.Builder deleteDialog = new AlertDialog.Builder(this);
        deleteDialog.setTitle(getString(R.string.delete_drawing));
        deleteDialog.setMessage(getString(R.string.new_drawing_warning));
        deleteDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                mCustomView.eraseAll();
                dialog.dismiss();
            }
        });
        deleteDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        deleteDialog.show();
    }

    @SuppressWarnings("unused")
    public void saveDrawingDialog() {
        //save drawing attach to Notification Bar and let User Open Image to share.
        AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
        saveDialog.setTitle("Save drawing");
        saveDialog.setMessage("Save drawing to device Gallery?");
        saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
                String directorypath = Environment.getExternalStorageDirectory().toString() +"/"+ getString(R.string.app_name);
                String imTitle = "DrawingForDeX" + "_" + System.currentTimeMillis()+".png";
                saveThisDrawing(directorypath,imTitle);
            }
        });
        saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
                dialog.cancel();
            }
        });
        saveDialog.show();
    }

    public void saveThisDrawing(String directorypath,String imTitle)
    {
        OutputStream output = null;
        Bitmap bitmap = null;
        FileOutputStream fOut = null;
        File file = null;
        //View capView = getWindow().getDecorView().getRootView();//Get the RootView

        //1. Create Directory
        File dir = new File(directorypath);
        if (!dir.exists()) {
            dir.mkdirs();
        }


        try {
            //save drawing
            mCustomView.setDrawingCacheEnabled(true);
           // capView.setDrawingCacheEnabled(true);//Get the RootView

           file = new File(dir, imTitle);
           file.createNewFile();
           if(file.exists()) {
                fOut = new FileOutputStream(file);
           }

            if (mCustomView.getDrawingCache() != null) {
                bitmap = Bitmap.createBitmap(mCustomView.getDrawingCache());
                //bitmap =Bitmap.createBitmap(capView.getDrawingCache());//Get the RootView
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            } else{
                 bitmap = null;
            }

            if(fOut != null) {
                fOut.flush();
                fOut.close();
            }
        } catch (FileNotFoundException e) {

            e.printStackTrace();
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Uh Oh!");
            alert.setMessage("Oops! Image could not be saved. Do you have enough space in your device? 1");
            //alert.setMessage("File path : " + dir.getPath() +"  "+ check);
            alert.setPositiveButton("OK", null);
            alert.show();
        } catch (IOException e) {
            Toast unsavedToast = Toast.makeText(getApplicationContext(),
                    "Oops! Image could not be saved. Do you have enough space in your device2?", Toast.LENGTH_SHORT);
            unsavedToast.show();
            e.printStackTrace();
        }

        //capView.setDrawingCacheEnabled(false); //Get the RootView
        mCustomView.setDrawingCacheEnabled(false);
        mCustomView.destroyDrawingCache();
    }


    //Arthur Window will be added on the external monitor
    @SuppressWarnings("unused")
    private void shareDrawing() {
        mCustomView.setDrawingCacheEnabled(true);
        mCustomView.invalidate();

        File drawingPath = new File(getCacheDir(), "drawings");
        File newDrawing = new File(drawingPath, "android_drawing_app.jpg");

        //Arthur
        Uri contentUri = FileProvider.getUriForFile(this, BuildConfig.FILES_AUTHORITY, newDrawing);

        Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                .setType("image/jpg").setStream(contentUri).getIntent();
        shareIntent.setData(contentUri);

        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(shareIntent, "Share image"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(getApplicationContext(), "Settings", Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
