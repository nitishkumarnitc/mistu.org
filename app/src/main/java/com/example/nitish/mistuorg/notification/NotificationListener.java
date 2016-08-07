package com.example.nitish.mistuorg.notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;
import com.example.nitish.mistuorg.R;
import com.example.nitish.mistuorg.utils.Constants;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class NotificationListener extends Service {
    public NotificationListener() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SharedPreferences sharedPreferences=getSharedPreferences(Constants.NOTIF_SHARED_PREF,MODE_PRIVATE);
        String id=sharedPreferences.getString(Constants.FCM_UNIQUE_ID,null);

        Log.d("NotificationListener","uuniqueId:"+id);
        Log.d("Firebase api",Constants.FIREBASE_URL+id);

        Firebase firebase=new Firebase(Constants.FIREBASE_URL+id);

        // adding a value event listener to firebase
        // this will help us to track the value changes on firebase

        firebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String msg=dataSnapshot.child("msg").getValue().toString();
                Log.d("Notification",msg);
                Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();
                if(msg.equals("none")){
                    return;
                }
                showNotification(msg);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e("The read failed",firebaseError.getMessage());
            }
        });
        return START_STICKY;
    }
    public void showNotification(String msg){
        NotificationCompat.Builder builder=new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.simplifiedcoding.net"));
        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intent,0);
        builder.setContentIntent(pendingIntent);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        builder.setContentTitle("FireBase push Notification");
        builder.setContentText(msg);
        NotificationManager notificationManager=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1,builder.build());
    }
}
