package com.balloon.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.balloon.R;
import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Pattern;

import okhttp3.Response;

public class Utility {

    public static void loadImage(ImageView view, String imageUrl) {
        Glide.with(view.getContext())
                .load(imageUrl)
                .placeholder(R.drawable.logo_balloon)
                .error(ContextCompat.getDrawable(view.getContext(), R.drawable.logo_balloon))
                .into(view);
    }

    public static void loadPicture(ImageView view, String imageUrl) {
        Glide.with(view.getContext())
                .load(imageUrl)
                .placeholder(R.color.color_view)
                .error(ContextCompat.getDrawable(view.getContext(), R.color.color_view))
                .into(view);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void audioCall(Activity activity, String number) {
        activity.startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number)));
    }

    public static void sendEmail(Activity activity, String email) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        String[] recipients = {email};
        intent.putExtra(Intent.EXTRA_EMAIL, recipients);
        intent.setType("text/html");
        intent.setPackage("com.google.android.gm");
        activity.startActivity(Intent.createChooser(intent, "Send mail"));
    }

   /* public static void setAppBar(Activity mActivity) {
        Toolbar toolbar = mActivity.findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);
        DrawerLayout drawerLayout = mActivity.findViewById(R.id.drawer_layout);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }*/

    public static void showToastMessageError(Activity activity, String message) {
        Toast toast = new Toast(activity);
        toast.setDuration(Toast.LENGTH_LONG);
        View toastView = LayoutInflater.from(activity).inflate(R.layout.custom_toast, null);
        toastView.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_rounded_red));
        CheckedTextView txtMessage = toastView.findViewById(R.id.txtMessage);
        txtMessage.setText(message);
        toast.setView(toastView);
        toast.show();

    }

    public static void showSnackBarMsgError(Activity activity, String message) {
        View parentLayout = activity.findViewById(android.R.id.content);
        Snackbar.make(parentLayout, message, Snackbar.LENGTH_LONG)
                .setAction("CLOSE", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        finish();
                    }
                })
                .setActionTextColor(activity.getResources().getColor(R.color.color_FF653B))
                .show();
    }

    public static Bitmap decodeUriToBitmap(Context mContext, Uri sendUri) {
        Bitmap getBitmap = null;
        try {
            InputStream image_stream;
            try {
                image_stream = mContext.getContentResolver().openInputStream(sendUri);
                getBitmap = BitmapFactory.decodeStream(image_stream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getBitmap;
    }

    public static void copyStream(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
    }

    public static File createAppDir(Activity mActivity, File mFileTemp, String TEMP_PHOTO_FILE_NAME) {
        String root = Environment.getExternalStorageDirectory().toString();
        new File(root + "/" + mActivity.getString(R.string.app_name) + "/temp").mkdirs();
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mFileTemp = new File(root + "/" + mActivity.getString(R.string.app_name) + "/temp/", TEMP_PHOTO_FILE_NAME);
        } else {
            mFileTemp = new File(mActivity.getFilesDir(), TEMP_PHOTO_FILE_NAME);
        }
        return mFileTemp;
    }

    public static boolean isValidEmail(String email) {
        return Pattern.compile(
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                        + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                        + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
        ).matcher(email).matches();
    }

    //get complete address in string
    public static String getCompleteAddressString(Context context, double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("Location", strReturnedAddress.toString());
            } else {
                Log.w("Location", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("Location", "Canont get Address!");
        }
        return strAdd;
    }

    public static String toTitleCase(String str) {
        if (str == null) {
            return null;
        }

        boolean space = true;
        StringBuilder builder = new StringBuilder(str);
        final int len = builder.length();

        for (int i = 0; i < len; ++i) {
            char c = builder.charAt(i);
            if (space) {
                if (!Character.isWhitespace(c)) {
                    // Convert to title case and switch out of whitespace mode.
                    builder.setCharAt(i, Character.toTitleCase(c));
                    space = false;
                }
            } else if (Character.isWhitespace(c)) {
                space = true;
            } else {
                builder.setCharAt(i, Character.toLowerCase(c));
            }
        }

        return builder.toString();
    }

    public static String getChangeDate(String ourDate) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            //formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date value = formatter.parse(ourDate);

            SimpleDateFormat dateFormatter = new SimpleDateFormat("dd MMM, yyyy"); //this format changeable
            dateFormatter.setTimeZone(TimeZone.getDefault());  //set local time zone
            ourDate = dateFormatter.format(value);
        } catch (Exception e) {
            ourDate = "";
        }
        return ourDate;
    }

    public static String getFormatChangeDate(String ourDate) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            //formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date value = formatter.parse(ourDate);

            SimpleDateFormat dateFormatter = new SimpleDateFormat("dd MMM, hh:mm a"); //this format changeable
            dateFormatter.setTimeZone(TimeZone.getDefault());  //set local time zone
            ourDate = dateFormatter.format(value);
        } catch (Exception e) {
            ourDate = "";
        }
        return ourDate;
    }

    public static String convertResponseToString(Response response) {
        return response.body().toString();
    }

    public static String commaRemove(String s) {
        return s.replace(",", " ");
    }

    public static boolean isOnline(Context context) {
        boolean isDeviceOnLine = false;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            isDeviceOnLine = netInfo != null && netInfo.isConnected();
        }
        return isDeviceOnLine;

    }

    public static int getTimeInHour(String s) throws ParseException {
        SimpleDateFormat inputFormat = new SimpleDateFormat("HH:mm");
        SimpleDateFormat outputFormat = new SimpleDateFormat("HH");  //K for 12 hour format

        Date date = inputFormat.parse(s);
        String formattedDate = outputFormat.format(date);

        return Integer.parseInt(formattedDate);

    }

    public static int getTotalHour(String s1, String s2) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        Date startDate = null;
        Date endDate = null;
        long difference = 0;
        try {
            startDate = simpleDateFormat.parse(s1);
            endDate = simpleDateFormat.parse(s2);
            difference = endDate.getTime() - startDate.getTime();
            if (difference < 0) {
                Date dateMax = simpleDateFormat.parse("24:00");
                Date dateMin = simpleDateFormat.parse("00:00");
                difference = (dateMax.getTime() - startDate.getTime()) + (endDate.getTime() - dateMin.getTime());
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }


        int days = (int) (difference / (1000 * 60 * 60 * 24));
        int hours = (int) ((difference - (1000 * 60 * 60 * 24 * days)) / (1000 * 60 * 60));
        int min = (int) (difference - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hours)) / (1000 * 60);
        //Log.v(TAG, "Hours: " + hours + ", Mins: " + min);

        return hours;
    }

    public static String getChange12Format(String s) throws ParseException {
        SimpleDateFormat inputFormat = new SimpleDateFormat("HH:mm");
        SimpleDateFormat outputFormat = new SimpleDateFormat("hh:mm");  //K for 12 hour format

        Date date = inputFormat.parse(s);
        String formattedDate = outputFormat.format(date);


        return formattedDate;
    }

}
