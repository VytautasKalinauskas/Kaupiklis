package com.kaupiklis.vytkal.meniu;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by vytkal on 2/22/2018.
 */

public class MessageHelper{



    public void showToast(Context context, String message)
    {

        Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        View view = toast.getView();

        view.getBackground().setColorFilter(Color.parseColor("#004000"), PorterDuff.Mode.SRC_IN);

        TextView text = view.findViewById(android.R.id.message);
        text.setTextColor(Color.WHITE);

        toast.show();
    }

    public static void showMessage(String title, String message, Context context, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setPositiveButton("Ok", listener);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }


    public static void displayAlertMessage(String title, String message, String positiveButton, DialogInterface.OnClickListener listener, Context context)
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButton, listener)
                .setNegativeButton("CANCEL", null)
                .create()
                .show();
    }

    public static void displayRegistrationDialog(final Context context, final EditText input, DialogInterface.OnClickListener listener) {


        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Įveskite registracijos kodą");


        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("Patvirtinti", listener);

        builder.setNegativeButton("Atšaukti", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

    }


    public static void displayAdminPasswordDialog(final Context context, final EditText input, DialogInterface.OnClickListener listener) {


        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Įveskite slaptažodį");

        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        builder.setPositiveButton("Patvirtinti", listener);

        builder.setNegativeButton("Atšaukti", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

    }


}
