package com.ab15.notification;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    Button btnSimple, btnBigText, btnBigPicture, btnInbox, btnAction;
    String CHANNEL_ID = "MyChannel";
    private static final int NOTIFICATION_PERMISSION_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSimple = findViewById(R.id.btnSimple);
        btnBigText = findViewById(R.id.btnBigText);
        btnBigPicture = findViewById(R.id.btnBigPicture);
        btnInbox = findViewById(R.id.btnInbox);
        btnAction = findViewById(R.id.btnAction);

        createNotificationChannel();

        btnSimple.setOnClickListener(v -> simpleNotification());
        btnBigText.setOnClickListener(v -> bigTextNotification());
        btnBigPicture.setOnClickListener(v -> bigPictureNotification());
        btnInbox.setOnClickListener(v -> inboxNotification());
        btnAction.setOnClickListener(v -> actionNotification());

        checkAndRequestNotificationPermission();
    }

    private void checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean hasNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    private void simpleNotification() {
        if (!hasNotificationPermission()) {
            checkAndRequestNotificationPermission();
            return;
        }

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(android.R.drawable.ic_dialog_info)
                        .setContentTitle("Simple Notification")
                        .setContentText("Hello this is a simple notification")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        manager.notify(1, builder.build());
    }

    private void bigTextNotification() {
        if (!hasNotificationPermission()) {
            checkAndRequestNotificationPermission();
            return;
        }

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(android.R.drawable.ic_dialog_info)
                        .setContentTitle("Big Text Notification")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText("This is a very long notification message used to demonstrate Big Text Style Notification in Android"))
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        manager.notify(2, builder.build());
    }

    private void bigPictureNotification() {
        if (!hasNotificationPermission()) {
            checkAndRequestNotificationPermission();
            return;
        }

        NotificationCompat.BigPictureStyle style =
                new NotificationCompat.BigPictureStyle()
                        .bigPicture(BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_menu_camera))
                        .bigLargeIcon((Bitmap) null);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(android.R.drawable.ic_menu_camera)
                        .setContentTitle("Big Picture Notification")
                        .setContentText("This is big picture style")
                        .setStyle(style);

        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        manager.notify(3, builder.build());
    }

    private void inboxNotification() {
        if (!hasNotificationPermission()) {
            checkAndRequestNotificationPermission();
            return;
        }

        NotificationCompat.InboxStyle style = new NotificationCompat.InboxStyle()
                .addLine("Message from Rahul")
                .addLine("Message from Ankit")
                .addLine("Message from Priya")
                .setSummaryText("+3 more messages");

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(android.R.drawable.ic_dialog_email)
                        .setContentTitle("Inbox Notification")
                        .setStyle(style);

        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        manager.notify(4, builder.build());
    }

    private void actionNotification() {
        if (!hasNotificationPermission()) {
            checkAndRequestNotificationPermission();
            return;
        }

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(android.R.drawable.ic_dialog_info)
                        .setContentTitle("Action Notification")
                        .setContentText("Click action button")
                        .addAction(android.R.drawable.ic_menu_view, "OPEN", null)
                        .addAction(android.R.drawable.ic_delete, "DELETE", null);

        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        manager.notify(5, builder.build());
    }

    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "My Notification Channel",
                NotificationManager.IMPORTANCE_DEFAULT
        );

        NotificationManager manager = getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.createNotificationChannel(channel);
        }
    }
}