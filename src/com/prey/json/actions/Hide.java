package com.prey.json.actions;

import java.util.List;

import org.json.JSONObject;

import com.prey.actions.camouflage.Camouflage;
import com.prey.actions.observer.ActionResult;

 
import android.content.Context;
 

public class Hide {

	public void sms(Context ctx,List<ActionResult> lista,JSONObject parameters){
		//PreyConfig.getPreyConfig(ctx).setCamouflageSet(false);

       Camouflage.hide(ctx, lista, parameters);
        
	}
}
