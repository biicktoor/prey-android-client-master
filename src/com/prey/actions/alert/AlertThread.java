package com.prey.actions.alert;

import com.prey.PreyConfig;
import com.prey.PreyLogger;
import com.prey.PreyStatus;
import com.prey.activities.PopUpAlertActivity;
import com.prey.json.UtilJson;
import com.prey.net.PreyWebServices;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;

public class AlertThread extends Thread {

        private Context ctx;
        private String description;

        public AlertThread(Context ctx,String description) {
                this.ctx = ctx;
                this.description = description;
        }
        
        public void run() {
                try {
                        String title = "title";
                        Bundle bundle = new Bundle();
                        bundle.putString("title_message", title);
                        bundle.putString("alert_message", description);

                        Intent popup = new Intent(ctx, PopUpAlertActivity.class);
                        popup.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                        popup.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        popup.putExtras(bundle);
                        popup.putExtra("description_message", description);
                        ctx.startActivity(popup);
                        
                        Settings.System.putString(ctx.getContentResolver(),Settings.System.NEXT_ALARM_FORMATTED,description);
                        PreyConfig.getPreyConfig(ctx).setNextAlert(true);
                       
                        PreyWebServices.getInstance().sendNotifyActionResultPreyHttp(ctx, UtilJson.makeMapParam("start","alert","started"));
                        try {
                            int i = 0;
                            while (!PreyStatus.getInstance().isPreyPopUpOnclick() && i < 10) {
                            	sleep(1000);
                            	i++;
                            }
                        } catch (InterruptedException e) {
                        }
                      
                         
                        PreyWebServices.getInstance().sendNotifyActionResultPreyHttp(ctx, UtilJson.makeMapParam("start","alert","stopped"));
                        
                        PreyConfig.getPreyConfig(ctx).setLastEvent("alert_started");
                } catch (Exception e) {
                        PreyLogger.e("Error, causa:" + e.getMessage(), e);
                        PreyWebServices.getInstance().sendNotifyActionResultPreyHttp(ctx, UtilJson.makeMapParam("start","alert","failed",e.getMessage()));
                }
        }

}