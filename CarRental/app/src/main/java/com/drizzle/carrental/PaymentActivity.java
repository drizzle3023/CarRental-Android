package com.drizzle.carrental;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class PaymentActivity extends Activity {

    private Button buttonPay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        buttonPay = findViewById(R.id.button_pay);
        buttonPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent=new Intent(PaymentActivity.this,SubscribeSuccessActivity.class);
                startActivity(intent);
            }
        });

    }

}
