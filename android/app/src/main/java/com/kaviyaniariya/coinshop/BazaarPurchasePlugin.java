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

import kotlin.Unit;

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

        payment.connect(connectionCallback -> {
            connectionCallback.connectionSucceed(() -> {
                PurchaseRequest request = new PurchaseRequest(productId, "payload", null);
                payment.purchaseProduct(getActivity().getActivityResultRegistry(), request, purchaseCallback -> {
                    purchaseCallback.purchaseSucceed(purchaseEntity -> {
                        JSObject result = new JSObject();
                        result.put("success", true);
                        result.put("productId", productId);
                        call.resolve(result);
                        return Unit.INSTANCE;
                    });
                    purchaseCallback.purchaseCanceled(() -> {
                        call.reject("Purchase canceled by user");
                        return Unit.INSTANCE;
                    });
                    purchaseCallback.purchaseFailed(throwable -> {
                        call.reject("Purchase failed: " + throwable.getMessage());
                        return Unit.INSTANCE;
                    });
                    purchaseCallback.failedToBeginFlow(throwable -> {
                        call.reject("Failed to begin purchase: " + throwable.getMessage());
                        return Unit.INSTANCE;
                    });
                    return Unit.INSTANCE;
                });
                return Unit.INSTANCE;
            });
            connectionCallback.connectionFailed(throwable -> {
                call.reject("Connection to Bazaar failed: " + throwable.getMessage());
                return Unit.INSTANCE;
            });
            connectionCallback.disconnected(() -> {
                return Unit.INSTANCE;
            });
            return Unit.INSTANCE;
        });
    }
}
