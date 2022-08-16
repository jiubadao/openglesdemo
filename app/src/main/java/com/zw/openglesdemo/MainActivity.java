/**
 *
 * Created by 公众号：字节流动 on 2021/3/12.
 * https://github.com/githubhaohao/NDK_OpenGLES_3_0
 * 最新文章首发于公众号：字节流动，有疑问或者技术交流可以添加微信 Byte-Flow ,领取视频教程, 拉你进技术交流群
 *
 * */

package com.zw.openglesdemo;

import static android.opengl.GLSurfaceView.RENDERMODE_CONTINUOUSLY;
import static com.zw.openglesdemo.MyGLSurfaceView.IMAGE_FORMAT_RGBA;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class MainActivity extends AppCompatActivity implements  ViewTreeObserver.OnGlobalLayoutListener, SensorEventListener {
    private static final String TAG = "MainActivity";



    private MyGLSurfaceView mGLSurfaceView;
    private ViewGroup mRootView;
    private MyGLRender mGLRender = new MyGLRender();
    private SensorManager mSensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRootView = (ViewGroup) findViewById(R.id.rootView);
        mRootView.getViewTreeObserver().addOnGlobalLayoutListener(this);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mGLRender.init();

    }

    @Override
    public void onGlobalLayout() {
        mRootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        mGLSurfaceView = new MyGLSurfaceView(this, mGLRender);
        mRootView.addView(mGLSurfaceView, lp);
        mGLSurfaceView.setRenderMode(RENDERMODE_CONTINUOUSLY);
        loadRGBAImage(R.drawable.board_texture);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY),
                SensorManager.SENSOR_DELAY_FASTEST);

    }




    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGLRender.unInit();
        /*
        * Once the EGL context gets destroyed all the GL buffers etc will get destroyed with it,
        * so this is unnecessary.
        * */
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_GRAVITY:
                Log.d(TAG, "onSensorChanged() called with TYPE_GRAVITY: [x,y,z] = [" + event.values[0] + ", " + event.values[1] + ", " + event.values[2] + "]");
//                if(mSampleSelectedIndex + SAMPLE_TYPE == SAMPLE_TYPE_KEY_AVATAR)
//                {
//                    mGLRender.setGravityXY(event.values[0], event.values[1]);
//                }
                break;
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }



    private Bitmap loadRGBAImage(int resId) {
        InputStream is = this.getResources().openRawResource(resId);
        Bitmap bitmap;
        try {
            bitmap = BitmapFactory.decodeStream(is);
            if (bitmap != null) {
                int bytes = bitmap.getByteCount();
                ByteBuffer buf = ByteBuffer.allocate(bytes);
                bitmap.copyPixelsToBuffer(buf);
                byte[] byteArray = buf.array();
                mGLRender.setImageData(IMAGE_FORMAT_RGBA, bitmap.getWidth(), bitmap.getHeight(), byteArray);
            }
        }
        finally
        {
            try
            {
                is.close();
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
        return bitmap;
    }


}
