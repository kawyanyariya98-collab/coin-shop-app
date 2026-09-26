package com.kaviyaniariya.coinshop;

import android.os.Bundle;
import com.getcapacitor.BridgeActivity;

public class MainActivity extends BridgeActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        registerPlugin(BazaarPurchasePlugin.class);
        super.onCreate(savedInstanceState);
    }
}
