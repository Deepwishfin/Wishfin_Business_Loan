package com.wishfin.wishfinbusinessloan;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Objects;

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    Bitmap bitmap;
    String card_name = "", bank_code = "", card_image = "", card_id = "", type = "", journey_stage = "",
            str_occupation = "", extra_fields = "", Lead_id = "", custType = "", custom_bank_code = "",
            fullname = "", mincome = "", company = "", body = "", title = "", imageUri = "", url = "";
    SharedPreferences prefs;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        try {
            Map<String, String> data = remoteMessage.getData();

            body = data.get("body");
            title = data.get("title");
            bank_code = data.get("bank_code");
            card_name = data.get("card_name");
            card_id = data.get("card_id");
            card_image = data.get("card_image");
            type = data.get("api_type");
            journey_stage = data.get("journey_stage");
            extra_fields = data.get("extra_fields");
            url = data.get("url");
            JSONObject jsonObject1;
            if (extra_fields != null) {
                jsonObject1 = new JSONObject(extra_fields);
                str_occupation = jsonObject1.getString("occupation");
                Lead_id = jsonObject1.getString("Lead_id");
                try {
                    custType = jsonObject1.getString("custType");
                } catch (Exception e) {
                    custType = "";
                }
                try {
                    custom_bank_code = jsonObject1.getString("custom_bank_code");
                } catch (Exception e) {
                    custom_bank_code = "";
                }
                try {
                    fullname = jsonObject1.getString("fullname");
                } catch (Exception e) {
                    fullname = "";
                }
                try {
                    mincome = jsonObject1.getString("mincome");
                } catch (Exception e) {
                    mincome = "";
                }
                try {
                    company = jsonObject1.getString("company");
                } catch (Exception e) {
                    company = "";
                }
            }

            if (SessionManager.get_login(prefs).equalsIgnoreCase("True")) {
                if (url != null) {
                    sendnotification_weburl(title, body, url);
                } else if (journey_stage.equalsIgnoreCase("0") || journey_stage == null) {
                    sendnotification(Objects.requireNonNull(remoteMessage.getNotification()).getTitle(), remoteMessage.getNotification().getBody());
                } else {
                    sendnotificationaxis(title, body);
                }
            } else {
                sendnotification(title, body);
            }
        } catch (Exception e) {
            try {
                imageUri = String.valueOf(Objects.requireNonNull(remoteMessage.getNotification()).getImageUrl());
                if (!imageUri.equalsIgnoreCase("null")) {
                    bitmap = getBitmapfromUrl(imageUri);
                    sendnotification_image(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody(), bitmap);
                } else {
                    sendnotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
                }
            } catch (Exception ignored) {
                sendnotification(Objects.requireNonNull(remoteMessage.getNotification()).getTitle(), remoteMessage.getNotification().getBody());
            }
        }

    }

    private void sendnotification_weburl(String title, String body, String url) {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        PendingIntent pendingIntent = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity
                    (this, 0, intent, PendingIntent.FLAG_MUTABLE);
        } else {
            pendingIntent = PendingIntent.getActivity
                    (this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        }

        NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle();
        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        String NOTIFICATION_CHANNEL_ID = "1001";

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.appicon)
                .setContentTitle(title)
                .setAutoCancel(true)
                .setSound(defaultSound)
                .setContentText(body)
                .setContentIntent(pendingIntent)
                .setStyle(style)
                .setWhen(System.currentTimeMillis())
                .setPriority(Notification.PRIORITY_MAX);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant") NotificationChannel notificationChannel =
                    new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Wishfin_Home", NotificationManager.IMPORTANCE_DEFAULT);

            //Configure Notification Channel
            notificationChannel.setDescription("Wishfin_Home");
            notificationChannel.enableLights(true);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);

            }
        }

        if (notificationManager != null) {
            notificationManager.cancelAll();
            notificationManager.notify(0, notificationBuilder.build());

        }

    }

    private void sendnotification(String title, String body) {

        Intent intent = new Intent(getApplicationContext(), Splash.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity
                    (this, 0, intent, PendingIntent.FLAG_MUTABLE);
        } else {
            pendingIntent = PendingIntent.getActivity
                    (this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        }


        NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle();
        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        String NOTIFICATION_CHANNEL_ID = "1001";

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.appicon)
                .setContentTitle(title)
                .setAutoCancel(true)
                .setSound(defaultSound)
                .setContentText(body)
                .setContentIntent(pendingIntent)
                .setStyle(style)
                .setWhen(System.currentTimeMillis())
                .setPriority(Notification.PRIORITY_MAX);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant") NotificationChannel notificationChannel =
                    new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Wishfin_Home", NotificationManager.IMPORTANCE_DEFAULT);

            //Configure Notification Channel
            notificationChannel.setDescription("Wishfin_Home");
            notificationChannel.enableLights(true);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);

            }
        }

        if (notificationManager != null) {
            notificationManager.cancelAll();
            notificationManager.notify(0, notificationBuilder.build());

        }

    }

    private void sendnotificationaxis(String title, String body) {

        Intent intent = null;

        if (journey_stage.equalsIgnoreCase("1")) {
            intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("bank_code", bank_code);
            intent.putExtra("strcardname", card_name);
            intent.putExtra("card_id", card_id);
            intent.putExtra("card_image", card_image);
            intent.putExtra("Lead_id", "" + Lead_id);
            intent.putExtra("custom_bank_code", custom_bank_code);
            intent.putExtra("fullname", fullname);
            intent.putExtra("mincome", mincome);
            intent.putExtra("company", company);
        } else if (journey_stage.equalsIgnoreCase("2")) {
            if (fullname.equalsIgnoreCase("") || mincome.equalsIgnoreCase("") ||
                    company.equalsIgnoreCase("") || custom_bank_code.equalsIgnoreCase("")) {
                intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("bank_code", bank_code);
                intent.putExtra("strcardname", card_name);
                intent.putExtra("card_id", card_id);
                intent.putExtra("card_image", card_image);
                intent.putExtra("Lead_id", "" + Lead_id);
                intent.putExtra("custom_bank_code", custom_bank_code);
                intent.putExtra("fullname", fullname);
                intent.putExtra("mincome", mincome);
                intent.putExtra("company", company);
            } else {
                intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("bank_code", bank_code);
                intent.putExtra("strcardname", card_name);
                intent.putExtra("card_id", card_id);
                intent.putExtra("card_image", card_image);
                intent.putExtra("Lead_id", "" + Lead_id);
                intent.putExtra("custom_bank_code", custom_bank_code);
                intent.putExtra("fullname", fullname);
                intent.putExtra("mincome", mincome);
                intent.putExtra("company", company);
            }
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity
                    (this, 0, intent, PendingIntent.FLAG_MUTABLE);
        } else {
            pendingIntent = PendingIntent.getActivity
                    (this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        }

        NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle();
        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        String NOTIFICATION_CHANNEL_ID = "1001";

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.appicon)
                .setContentTitle(title)
                .setAutoCancel(true)
                .setSound(defaultSound)
                .setContentText(body)
                .setContentIntent(pendingIntent)
                .setStyle(style)
                .setWhen(System.currentTimeMillis())
                .setPriority(Notification.PRIORITY_MAX);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant") NotificationChannel notificationChannel =
                    new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Wishfin_Home", NotificationManager.IMPORTANCE_DEFAULT);

            //Configure Notification Channel
            notificationChannel.setDescription("Wishfin_Home");
            notificationChannel.enableLights(true);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);

            }
        }

        if (notificationManager != null) {
            notificationManager.cancelAll();
            notificationManager.notify(0, notificationBuilder.build());

        }

    }

    private void sendnotification_image(String title, String body, Bitmap bitmap1) {

        Intent intent = new Intent(getApplicationContext(), Splash.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity
                    (this, 0, intent, PendingIntent.FLAG_MUTABLE);
        } else {
            pendingIntent = PendingIntent.getActivity
                    (this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        }
        NotificationCompat.BigPictureStyle style = new NotificationCompat.BigPictureStyle();
        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        String NOTIFICATION_CHANNEL_ID = "1001";

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.appicon)
                .setContentTitle(title)
                .setAutoCancel(true)
                .setSound(defaultSound)
                .setContentText(body)
                .setLargeIcon(bitmap1)
                .setContentIntent(pendingIntent)
                .setStyle(style)
                .setWhen(System.currentTimeMillis())
                .setPriority(Notification.PRIORITY_MAX);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant") NotificationChannel notificationChannel =
                    new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Wishfin_Home", NotificationManager.IMPORTANCE_DEFAULT);

            //Configure Notification Channel
            notificationChannel.setDescription("Wishfin_Home");
            notificationChannel.enableLights(true);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);

            }
        }

        if (notificationManager != null) {
            notificationManager.cancelAll();
            notificationManager.notify(0, notificationBuilder.build());

        }

    }

    public Bitmap getBitmapfromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();

            return BitmapFactory.decodeStream(input);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;

        }
    }

}
