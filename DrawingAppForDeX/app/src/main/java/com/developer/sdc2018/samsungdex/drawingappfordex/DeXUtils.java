package com.developer.sdc2018.samsungdex.drawingappfordex;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.hardware.display.DisplayManager;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

public class DeXUtils {
    private final static String TAG = "DeX Utils";

    @SuppressWarnings("unchecked")
    static boolean isDeXAvailable(Context context) {
        //isDeXAvailble
/**     //Detect DeX mode 7/7
        @SuppressLint("WrongConstant")
        Object desktopModeManager = context.getApplicationContext().getSystemService("desktopmode");
        if (desktopModeManager != null) {
            try {
                Method getDesktopModeStateMethod = desktopModeManager.getClass().getDeclaredMethod("getDesktopModeState");
                Object desktopModeState = getDesktopModeStateMethod.invoke(desktopModeManager);

                Class desktopModeStateClass = desktopModeState.getClass();

                Method getEnabledMethod = desktopModeStateClass.getDeclaredMethod("getEnabled");
                int enabled = (int) getEnabledMethod.invoke(desktopModeState);
                boolean isEnabled = enabled == desktopModeStateClass.getDeclaredField("ENABLED").getInt(desktopModeStateClass);

                Method getDisplayTypeMethod = desktopModeStateClass.getDeclaredMethod("getDisplayType");
                int displayType = (int) getDisplayTypeMethod.invoke(desktopModeState);
                boolean isDualMode = isEnabled && displayType == desktopModeStateClass.getDeclaredField("DISPLAY_TYPE_DUAL").getInt(desktopModeStateClass);
                boolean isStandaloneMode = isEnabled && displayType == desktopModeStateClass.getDeclaredField("DISPLAY_TYPE_STANDALONE").getInt(desktopModeStateClass);

                // Check isEnabled, isDualMode or isStandaloneMode as you want
                String msg = "DeX: isEnabled=" + isEnabled + ", isDualMode=" + isDualMode + ", isStandaloneMode=" + isStandaloneMode + ", isAppCurrentlyRunningInDeX=" + isAppCurrentlyRunningInDeX(context);
                Log.d(TAG, msg);
                //Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                return isEnabled | isDualMode | isStandaloneMode | isAppCurrentlyRunningInDeX(context);
            } catch (NoSuchFieldException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                // Device does not support DeX 3.0
                return false;
            }
        } else {
            // Device does not support Samsung DeX or called too early on boot
            return false;
        }
 //isDeXAvailble
*/
        /** plase remove the "return false" below. */
        return false;
    }

    static int getOtherDisplayId(Context context, Display currentDisplay){

        int targetDisplayId;
        targetDisplayId = Display.DEFAULT_DISPLAY;
       /** //Add window 6/9 */
        DisplayManager dm = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
        Display[] displays = Objects.requireNonNull(dm).getDisplays("com.samsung.android.hardware.display.category.DESKTOP");

        if(currentDisplay.getDisplayId() == Display.DEFAULT_DISPLAY){
            targetDisplayId = displays[0].getDisplayId();
        }

        return targetDisplayId;
    }

    static Display getOtherDisplay(Context context, WindowManager wm, Display currentDisplay){
        Display targetDisplay;

            DisplayManager dm = (DisplayManager)context.getSystemService(Context.DISPLAY_SERVICE);
            targetDisplay = Objects.requireNonNull(dm).getDisplay(Display.DEFAULT_DISPLAY);
             /** //Add window 7/9 */
             if(currentDisplay.getDisplayId() == Display.DEFAULT_DISPLAY) {
                 targetDisplay = getExternalDisplay(context, wm);
             }
        return targetDisplay;
    }

    private static Display getExternalDisplay(Context context, WindowManager wm) {
        DisplayManager dm = (DisplayManager)context.getSystemService(Context.DISPLAY_SERVICE);
         /** //Add window 8/9 */
        Display[] displays = Objects.requireNonNull(dm).getDisplays("com.samsung.android.hardware.display.category.DESKTOP");

        if(displays.length == 0) {
            displays = dm.getDisplays(DisplayManager.DISPLAY_CATEGORY_PRESENTATION);
        }

        int count = displays.length;
        Log.d(TAG, "count " + count);
        for(int i = 0; i < count; i++) {
            Log.d(TAG, "Display " + i +  " " + displays[i]);
        }

        if(displays.length > 0) {
            return displays[0];
        }
        return wm.getDefaultDisplay();
    }

    static void immersiveMode(View mViewInSencondaryDisplay){
        /** //Add window 9/9 */
        //Immersive mode setting : Full Screen
        final int flags = View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;


        mViewInSencondaryDisplay.setSystemUiVisibility( flags );
    }

    @SuppressWarnings("JavaReflectionMemberAccess")
    private static boolean isAppCurrentlyRunningInDeX(Context context) {
        Configuration config = context.getResources().getConfiguration();
        try {
            Class configClass = config.getClass();
            if (configClass.getField("SEM_DESKTOP_MODE_ENABLED").getInt(configClass) == configClass.getField("semDesktopModeEnabled").getInt(config)) {
                Toast.makeText(context, "isAppCurrentlyRunningInDeX: true", Toast.LENGTH_LONG).show();
                return true;
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public static boolean isDualDeXMode(Context context) {
        @SuppressLint("WrongConstant")
        Object desktopModeManager = context.getApplicationContext().getSystemService("desktopmode");
        if (desktopModeManager != null) {
            try {
                Method getDesktopModeStateMethod = desktopModeManager.getClass().getDeclaredMethod("getDesktopModeState");
                Object desktopModeState = getDesktopModeStateMethod.invoke(desktopModeManager);
                Class desktopModeStateClass = desktopModeState.getClass();
                Method getEnabledMethod = desktopModeStateClass.getDeclaredMethod("getEnabled");
                int enabled = (int) getEnabledMethod.invoke(desktopModeState);
                boolean isEnabled = enabled == desktopModeStateClass.getDeclaredField("ENABLED").getInt(desktopModeStateClass);

                Method getDisplayTypeMethod = desktopModeStateClass.getDeclaredMethod("getDisplayType");
                int displayType = (int) getDisplayTypeMethod.invoke(desktopModeState);
                return isEnabled && displayType == desktopModeStateClass.getDeclaredField("DISPLAY_TYPE_DUAL").getInt(desktopModeStateClass);

            } catch (NoSuchFieldException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                // Device does not support DeX 3.0
                return false;
            }
        }
        return false;
    }
}
