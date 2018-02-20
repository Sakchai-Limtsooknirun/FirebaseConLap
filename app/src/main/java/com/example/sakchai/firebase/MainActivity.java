package com.example.sakchai.firebase;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private EditText firstNameEditText, lastNameEditText;
    private int id;
    private boolean resume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resume = false;

        firstNameEditText = findViewById(R.id.firstNameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 ,intent, PendingIntent.FLAG_UPDATE_CURRENT);

        final NotificationCompat.Builder notificationAdded = new NotificationCompat.Builder(this);
        notificationAdded.setSmallIcon(android.R.drawable.btn_plus).setContentTitle("Added").setContentText("Added.").setContentIntent(pendingIntent).setAutoCancel(true);

        final NotificationCompat.Builder notificationChanged = new NotificationCompat.Builder(this);
        notificationChanged.setSmallIcon(android.R.drawable.btn_dropdown).setContentTitle("Changed").setContentText("Changed.").setContentIntent(pendingIntent).setAutoCancel(true);

        final NotificationCompat.Builder notificationRemoved = new NotificationCompat.Builder(this);
        notificationRemoved.setSmallIcon(android.R.drawable.btn_dropdown).setContentTitle("Removed").setContentText("Removed.").setContentIntent(pendingIntent).setAutoCancel(true);

        final NotificationCompat.Builder notificationMoved = new NotificationCompat.Builder(this);
        notificationMoved.setSmallIcon(android.R.drawable.btn_dropdown).setContentTitle("Moved").setContentText("Moved.").setContentIntent(pendingIntent).setAutoCancel(true);

        final NotificationCompat.Builder notificationCancelled = new NotificationCompat.Builder(this);
        notificationCancelled.setSmallIcon(android.R.drawable.btn_dropdown).setContentTitle("Cancelled").setContentText("Cancelled.").setContentIntent(pendingIntent).setAutoCancel(true);

        final NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Users");
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User addUser = dataSnapshot.getValue(User.class);
                Log.i("i", "Added: "+ addUser.firstName+","+addUser.lastName);
                if (resume) {
                    Toast.makeText(MainActivity.this, "Added: "+ addUser.firstName+","+addUser.lastName, Toast.LENGTH_SHORT).show();
                } else {
                    notificationManager.notify(id++, notificationAdded.build());
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                User changeUser = dataSnapshot.getValue(User.class);
                Log.i("i", "Changed: "+ changeUser.firstName+","+changeUser.lastName);
                if (resume) {
                    Toast.makeText(MainActivity.this, "Changed: "+ changeUser.firstName+","+changeUser.lastName, Toast.LENGTH_SHORT).show();
                } else {
                    notificationManager.notify(id++, notificationChanged.build());
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                User removeUser = dataSnapshot.getValue(User.class);
                Log.i("i", "Removed: "+ removeUser.firstName+","+removeUser.lastName);
                if (resume) {
                    Toast.makeText(MainActivity.this, "Removed: "+ removeUser.firstName+","+removeUser.lastName, Toast.LENGTH_SHORT).show();
                } else {
                    notificationManager.notify(id++, notificationRemoved.build());
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                User moveUser = dataSnapshot.getValue(User.class);
                Log.i("i", "Moved: "+ moveUser.firstName+","+moveUser.lastName);
                if (resume) {
                    Toast.makeText(MainActivity.this, "Moved", Toast.LENGTH_SHORT).show();
                } else {
                    notificationManager.notify(id++, notificationMoved.build());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i("i", "Cancelled: "+databaseError.getMessage());
                if (resume) {
                    Toast.makeText(MainActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                } else {
                    notificationManager.notify(id++, notificationCancelled.build());
                }
            }
        });
    }

    public void addOnClick(View view) {
        if (!firstNameEditText.getText().toString().equals("") && !lastNameEditText.getText().toString().equals("")) {
            User user = new User(firstNameEditText.getText().toString(),lastNameEditText.getText().toString());
            databaseReference.push().setValue(user);
            firstNameEditText.setText("");
            lastNameEditText.setText("");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        resume = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        resume = false;
    }




}
