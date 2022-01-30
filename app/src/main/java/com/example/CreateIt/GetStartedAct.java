package com.example.CreateIt;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScan;
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions;
import com.huawei.hms.support.account.AccountAuthManager;
import com.huawei.hms.support.account.request.AccountAuthParams;
import com.huawei.hms.support.account.request.AccountAuthParamsHelper;
import com.huawei.hms.support.account.result.AuthAccount;
import com.huawei.hms.support.account.service.AccountAuthService;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.support.account.AccountAuthManager;
import com.huawei.hms.support.account.request.AccountAuthParams;
import com.huawei.hms.support.account.request.AccountAuthParamsHelper;
import com.huawei.hms.support.account.result.AuthAccount;
import com.huawei.hms.support.account.service.AccountAuthService;

public class GetStartedAct extends AppCompatActivity {
    private static final int DEFAULT_VIEW = 0x22;
    private static final int REQUEST_CODE_SCAN = 0x01;
    Button btn_start;

    private AccountAuthService mAuthService;

    // 华为帐号登录授权参数
    // parameter
    private AccountAuthParams mAuthParam;

    // 用户自定义signInIntent请求码
    // User-defined signInIntent request code
    private static final int REQUEST_CODE_SIGN_IN = 1000;

    // 用户自定义日志标记
    // User-defined log mark
    private static final String TAG = "Account";
    private TextView logTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_started);

        findViewById(R.id.HuaweiIdAuthButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                silentSignInByHwId();
            }
        });
        findViewById(R.id.HuaweiIdSignOutButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        findViewById(R.id.HuaweiIdCancelAuthButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelAuthorization();
            }
        });
        logTextView = (TextView) findViewById(R.id.LogText);


        btn_start=findViewById(R.id.btn_start);

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gotomenu = new Intent(GetStartedAct.this,MenuAct.class);
                startActivity(gotomenu);
            }
        });
    }

    private void silentSignInByHwId() {
        // 1、配置登录请求参数AccountAuthParams，包括请求用户id(openid、unionid)、email、profile（昵称、头像）等。
        // 2、DEFAULT_AUTH_REQUEST_PARAM默认包含了id和profile（昵称、头像）的请求。
        // 3、如需要请求获取用户邮箱，需要setEmail();
        // 1. Configure the login request parameters AccountAuthParams, including the requested user id (openid, unionid),
        // email, profile (nickname, avatar), etc.
        // 2. DEFAULT_AUTH_REQUEST_PARAM includes requests for id and profile (nickname, avatar) by default.
        // 3. If you need to get the user mailbox again, you need setEmail();
        mAuthParam = new AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM)
                .setEmail()
                .createParams();

        // 使用请求参数构造华为帐号登录授权服务AccountAuthService
        // Use request parameters to construct a Huawei account login authorization service AccountAuthService
        mAuthService = AccountAuthManager.getService(this, mAuthParam);

        // 使用静默登录进行华为帐号登录
        // Use silent sign in for HUAWEI ID login
        Task<AuthAccount> task = mAuthService.silentSignIn();
        task.addOnSuccessListener(new OnSuccessListener<AuthAccount>() {
            @Override
            public void onSuccess(AuthAccount authAccount) {
                // 静默登录成功，处理返回的帐号对象AuthAccount，获取帐号信息
                // Silent sign in is successful, the returned account object AuthAccount is processed,account information is obtained and processed
                showLog("silent sign in success");
                dealWithResultOfSignIn(authAccount);
            }
        });
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                // 静默登录失败，使用getSignInIntent()方法进行前台显式登录
                // Silent sign in fails, use the getSignInIntent() method to log in from the foreground
                if (e instanceof ApiException) {
                    ApiException apiException = (ApiException) e;
                    Intent signInIntent = mAuthService.getSignInIntent();
                    startActivityForResult(signInIntent, REQUEST_CODE_SIGN_IN);
                }
            }
        });
    }

    private void dealWithResultOfSignIn(AuthAccount authAccount) {
        //获取帐号信息
        Log.i(TAG, "display name:" + authAccount.getDisplayName());
        Log.i(TAG, "photo uri string:" + authAccount.getAvatarUriString());
        Log.i(TAG, "photo uri:" + authAccount.getAvatarUri());
        Log.i(TAG, "email:" + authAccount.getEmail());
        Log.i(TAG, "openid:" + authAccount.getOpenId());
        Log.i(TAG, "unionid:" + authAccount.getUnionId());
        showLog("display name:" + authAccount.getDisplayName() + "photo uri string:" + authAccount.getAvatarUriString() +
                "email:" + authAccount.getEmail() + "openid:" + authAccount.getOpenId() + "unionid:" + authAccount.getUnionId());
        // TODO 获取用户信息后业务逻辑
        // TODO Business logic after obtaining user information

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SIGN_IN) {
            Log.i(TAG, "onActivitResult of sigInInIntent, request code: " + REQUEST_CODE_SIGN_IN);
            Task<AuthAccount> authAccountTask = AccountAuthManager.parseAuthResultFromIntent(data);
            if (authAccountTask.isSuccessful()) {
                showLog("sign in success");
                // 登录成功，获取到登录帐号信息对象authAccount
                // The login is successful, and the login account information object authAccount is obtained
                AuthAccount authAccount = authAccountTask.getResult();
                dealWithResultOfSignIn(authAccount);
                Log.i(TAG, "onActivitResult of sigInInIntent, request code: " + REQUEST_CODE_SIGN_IN);
            } else {
                // 登录失败，status code标识了失败的原因，请参考API中的错误码参考了解详细错误原因
                // Login failed. The status code identifies the reason for the failure. Please refer to the error
                // code reference in the API for detailed error reasons.
                showLog("sign in failed : " + ((ApiException) authAccountTask.getException()).getStatusCode());
                Log.e(TAG, "sign in failed : " + ((ApiException) authAccountTask.getException()).getStatusCode());
            }
        }
    }

    private void signOut() {
        if (mAuthService == null) {
            return;
        }
        Task<Void> signOutTask = mAuthService.signOut();
        signOutTask.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i(TAG, "signOut Success");
                showLog("signOut Success");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.i(TAG, "signOut fail");
                showLog("signOut fail");
            }
        });
    }

    private void cancelAuthorization() {
        if (mAuthService == null) {
            return;
        }
        Task<Void> task = mAuthService.cancelAuthorization();
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i(TAG, "cancelAuthorization success");
                showLog("cancelAuthorization success");
            }
        });
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.i(TAG, "cancelAuthorization failure：" + e.getClass().getSimpleName());
                showLog("cancelAuthorization failure：" + e.getClass().getSimpleName());
            }
        });
    }

    private void showLog(String log) {
        logTextView.setText("log:" + "\n" + log);
    }

}