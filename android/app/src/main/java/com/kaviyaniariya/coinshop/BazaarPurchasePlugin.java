package com.kaviyaniariya.coinshop;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import ir.cafebazaar.poolakey.Payment;
import ir.cafebazaar.poolakey.config.PaymentConfiguration;
import ir.cafebazaar.poolakey.config.SecurityCheck;
import ir.cafebazaar.poolakey.request.PurchaseRequest;
import ir.cafebazaar.poolakey.callback.PurchaseCallback;
import ir.cafebazaar.poolakey.callback.ConnectionCallback;
import ir.cafebazaar.poolakey.entity.PurchaseInfo;

@CapacitorPlugin(name = "BazaarPurchase")
public class BazaarPurchasePlugin extends Plugin {

    private Payment payment;

    @PluginMethod
    public void purchase(PluginCall call) {
        String productId = call.getString("productId");
        if (productId == null) {
            call.reject("productId is required");
            return;
        }

        SecurityCheck.Enable security = new SecurityCheck.Enable("MIHNMA0GCSqGSIb3DQEBAQUAA4G7ADCBtwKBrwDKhiaay+rpeF7n3ZrUXk1TKMBNUxaN96m899G/D5eAiEp40GmdQXUqDaRLa6P6pLUcfBJ/tPt2gekMUHvJSTREnA9JH6f4OqtmciaDOYRrZoV4HNx8mvVyyvL1aRqAPga7vzPfJ4T3O3a/SOitE0GuIb6I+C/OdIFh6dni9B8/jCrZRVLSm6+g02bAcv5KGvDXXz63ughJOMzmj8E8zbrVzbZ5sINBz5voJXqDeS0CAwEAAQ==");
        PaymentConfiguration config = new PaymentConfiguration(security);
        payment = new Payment(getContext(), config);

        payment.connect(new ConnectionCallback() {
            @Override
            public void connectionSucceed() {
                PurchaseRequest request = new PurchaseRequest(productId);
                payment.purchaseProduct(getActivity(), request, new PurchaseCallback() {
                    @Override
                    public void purchaseSucceed(PurchaseInfo purchaseInfo) {
                        JSObject result = new JSObject();
                        result.put("success", true);
                        result.put("productId", purchaseInfo.getProductId());
                        call.resolve(result);
                    }

                    @Override
                    public void purchaseCanceled() {
                        call.reject("Purchase canceled by user");
                    }

                    @Override
                    public void purchaseFailed(Throwable throwable) {
                        call.reject("Purchase failed: " + throwable.getMessage());
                    }
                });
            }

            @Override
            public void connectionFailed(Throwable throwable) {
                call.reject("Connection to Bazaar failed: " + throwable.getMessage());
            }

            @Override
            public void disconnected() {
            }
        });
    }
}
