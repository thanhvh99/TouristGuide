package com.android.touristguide;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;

public class Helper {
    public static final int LOGIN_MODE = 0;
    public static final int SIGN_UP_MODE = 1;
    public static boolean isValidEmail(String email){
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";
        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

    public static void showEmailVerificationDialog(final Context context, String email, final FirebaseUser user, int mode){
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View emailVerificationDialogView = layoutInflater.inflate(R.layout.activity_email_verification,null);
        final AlertDialog emailVerificationDialog = new AlertDialog.Builder(context).create();
        Button btnCancel = (Button) emailVerificationDialogView.findViewById(R.id.btn_edit_email);
        final Button btnResend = (Button) emailVerificationDialogView.findViewById(R.id.btn_resend);
        Button btnBackToLogin = (Button) emailVerificationDialogView.findViewById(R.id.btn_back_to_login);
        TextView tvEmailAddress = (TextView) emailVerificationDialogView.findViewById(R.id.tv_email_address);

        tvEmailAddress.setText(email);

        if (mode == Helper.LOGIN_MODE){
            btnBackToLogin.setVisibility(View.GONE);
        }

        btnBackToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Activity signUpActivity = (Activity) context;
                signUpActivity.finish();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emailVerificationDialog.cancel();
            }
        });

        btnResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnResend.setEnabled(false);
                user.sendEmailVerification();
                new CountDownTimer(60000,1000){
                    @Override
                    public void onTick(long l) {
                        btnResend.setText(getBoldString(String.valueOf(l/1000)));
                        btnResend.setTextColor(0xFF838181);
                    }

                    @Override
                    public void onFinish() {
                        btnResend.setText(getBoldString(context.getString(R.string.resend)));
                        btnResend.setTextColor(0xFF2196F3);
                        btnResend.setEnabled(true);
                    }
                }.start();
            }
        });

        emailVerificationDialog.setTitle(context.getString(R.string.email_verification));
        emailVerificationDialog.setView(emailVerificationDialogView);
        emailVerificationDialog.show();
    }

    public static SpannableString getBoldString(String text){
        SpannableString spanString = new SpannableString(text);
        spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
        return spanString;
    }

    public static AlertDialog createLoadingDialog(Context context){
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View loadingDialogView = layoutInflater.inflate(R.layout.loading_dialog,null);
        final AlertDialog loadingDialog = new AlertDialog.Builder(context).create();
        loadingDialog.setView(loadingDialogView);
        loadingDialog.setCanceledOnTouchOutside(false);
        return loadingDialog;
    }

    public static void finishActivityFromContext(Context context){
        AppCompatActivity activity = (AppCompatActivity) context;
        activity.finish();
    }

    public static void setTextViewUI(TextView tv, String text, String backgroundColor, String textColor, boolean isBold){
        if (isBold){
            tv.setText(getBoldString(text));
        }else{
            tv.setText(text);
        }
        tv.setTextColor(Color.parseColor(textColor));
        tv.setBackgroundColor(Color.parseColor(backgroundColor));
    }
}
