package com.example.nitish.mistuorg.app;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.example.nitish.mistuorg.utils.LruBitmapCache;
import com.firebase.client.Firebase;

/**
 * Created by nitish on 25-07-2016.
 */
public class AppController extends Application {

    public static final String TAG =AppController.class.getSimpleName();
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private static AppController mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance=this;
        Firebase.setAndroidContext(getApplicationContext());
    }

    public static synchronized AppController getInstance(){
        return mInstance;
    }

    public RequestQueue getRequestQueue(){
        if(mRequestQueue==null){
            mRequestQueue= Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    public ImageLoader getImageLoader(){
        getRequestQueue();
        if(mImageLoader==null){
            mImageLoader=new ImageLoader(this.mRequestQueue,
                    new LruBitmapCache());
        }
        return mImageLoader;
    }


    public <T> void addToRequestQueue(Request<T> request,String tag ){
        request.setTag(TextUtils.isEmpty(tag)? TAG :tag);
        getRequestQueue().add(request);
    }


    public <T> void addToRequestQueue(Request<T> request){
        request.setTag(TAG);
        getRequestQueue().add(request);
    }

    public void cancelPendingRequest(Object tag){
        if(mRequestQueue!=null){
            mRequestQueue.cancelAll(tag);
        }
    }
}
