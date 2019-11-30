package com.drizzle.carrental;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;

import com.mukesh.OnOtpCompletionListener;
import com.mukesh.OtpView;

public class VerifyCodeActivity extends Activity {

    private Button verifyButton;
    private OtpView otpView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verifycode);

        verifyButton = (Button) findViewById(R.id.button_verify);
        otpView = findViewById(R.id.otp_view);


        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ((MyApplication) getApplication()).setLoggedIn(true);

                Intent intent = new Intent(VerifyCodeActivity.this, PaymentActivity.class);
                startActivity(intent);

            }
        });

        otpView.setOtpCompletionListener(new OnOtpCompletionListener() {
            @Override
            public void onOtpCompleted(String otp) {

                enableVerifyButton(true);

            }
        });

        otpView.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (otpView.getText().length() == 4) {
                    enableVerifyButton(true);
                }
                else {
                    enableVerifyButton(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void enableVerifyButton(boolean isEnable) {

        verifyButton.setEnabled(isEnable);

        if (isEnable) {
            verifyButton.setBackgroundResource(R.drawable.active_button);
        }
        else {
            verifyButton.setBackgroundResource(R.drawable.inactive_button);
        }

    }

}
