package com.android.touristguide;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.CountDownTimer;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public static void loadAvatar(String url, ImageView imv, View parent, Context context,int drawableId ){
        if (url != null){
            Glide.with(parent).load(url).into(imv);
        }else{
            Glide.with(parent).load(ContextCompat.getDrawable(context,drawableId)).into(imv);
        }
    }

    public static Task<HttpsCallableResult> signUp(String username,String email, String avatar, FirebaseFunctions mFuntions){
        Map<String, String> data = new HashMap<>();
        data.put("username",username);
        data.put("email",email);
        if (avatar != null){
            data.put("avatar",avatar);
        }
        return mFuntions
                .getHttpsCallable("signUp")
                .call(data);
    }

    public static FirebaseFunctions initFirebaseFunctions(){
        FirebaseFunctions functions = FirebaseFunctions.getInstance();
        functions.useEmulator("192.168.1.3",5001);
        return functions;
    }

    public static String createFirebaseStorageFilename(Uri uri){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        String timestamp = String.valueOf((new Date()).getTime());
//        String filename = uri.getLastPathSegment();
//        String extension = filename.substring(filename.lastIndexOf("."));
        String fbFilename = uid+"_"+timestamp;
        return fbFilename;
    }

    public static List<User> createListUsersForTest(){
        List<User> users = new ArrayList<User>();
        users.add(new User("1","Dao Quan","daoan@gmail.com","0123456789","https://upload.wikimedia.org/wikipedia/commons/thumb/b/b6/Image_created_with_a_mobile_phone.png/1200px-Image_created_with_a_mobile_phone.png"));
        users.add(new User("2","Dao Giang","daoawfwefn@gmail.com","0123456789","https://cdn.pixabay.com/photo/2015/04/23/22/00/tree-736885__340.jpg"));
        users.add(new User("3","Dao Narotu","dawewfwoan@gmail.com","0123456789","https://www.w3schools.com/w3css/img_lights.jpg"));
        users.add(new User("4","Dao Quan","daoan@gmail.com","0123456789","https://upload.wikimedia.org/wikipedia/commons/thumb/b/b6/Image_created_with_a_mobile_phone.png/1200px-Image_created_with_a_mobile_phone.png"));
        users.add(new User("5","Dao Giang","daoawfwefn@gmail.com","0123456789","https://cdn.pixabay.com/photo/2015/04/23/22/00/tree-736885__340.jpg"));
        users.add(new User("6", "Dao Narotu","dawewfwoan@gmail.com","0123456789","https://www.w3schools.com/w3css/img_lights.jpg"));
        return users;
    }
}
