package com.archcra.plugin.mob.thirdauth;

import java.text.SimpleDateFormat;

import java.util.HashMap;
import java.util.Map;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;

import android.widget.Toast;


import com.archcra.plugin.mob.auth.OnLoginListener;
import com.archcra.plugin.mob.auth.ThirdPartyLogin;
import com.archcra.plugin.mob.auth.UserInfo;

import cn.sharesdk.framework.FakeActivity;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;

    
public class MobAuth extends CordovaPlugin
implements  Callback, PlatformActionListener{
    	private static final int MSG_AUTH_CANCEL = 2;
	private static final int MSG_AUTH_ERROR= 3;
	private static final int MSG_AUTH_COMPLETE = 4;
        private CallbackContext callbackContext= null;

  	private Handler handler = new Handler(this);
    

	// 填写从短信SDK应用后台注册得到的APPKEY
	//private static String APPKEY = "27fe7909f8e8";
	// 填写从短信SDK应用后台注册得到的APPSECRET
	//private static String APPSECRET = "3c5264e7e05b8860a9b98b34506cfa6e";


	@Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) {
        this.callbackContext = callbackContext;
        
         Context context = this.cordova.getActivity().getApplicationContext();
        initSDK(context);

        if (action.equals("authSina")) {
            auth(SinaWeibo.NAME);
        }else if(action.equals("authQq")) {
            auth(QQ.NAME);
        }
        else {
            return false;
        }
        return true;
    }
     
    
    private void auth(String platformStr){
        final String pStr = platformStr;
        
        cordova.getActivity().runOnUiThread(new Runnable() {
	        @Override
	        public void run() {     
                //新浪微博
                Platform platform = ShareSDK.getPlatform(pStr);
                authorize(platform); 
	        }
	    });
       
    }
    
    //执行授权,获取用户信息
	//文档：http://wiki.mob.com/Android_%E8%8E%B7%E5%8F%96%E7%94%A8%E6%88%B7%E8%B5%84%E6%96%99
	private void authorize(Platform plat) {
		plat.setPlatformActionListener(this);
		//关闭SSO授权
		plat.SSOSetting(true);
		plat.showUser(null);
	}
    
    
    public void onComplete(Platform platform, int action, HashMap<String, Object> res) {
		if (action == Platform.ACTION_USER_INFOR) {
			Message msg = new Message();
			msg.what = MSG_AUTH_COMPLETE;
			msg.obj = new Object[] {platform.getName(), res};
			
            //handler.sendMessage(msg); // For check more info
            
                 try{        
                      Log.i("MyPlugin","Begine to call back");
                     this.callbackContext.success(getJsonOfMsg(msg));
                    }catch(Exception e){
                     Log.i("MyPlugin",e.toString());
                 }
            
		}
	}
    

    private JSONObject getJsonOfMsg(Message msg) {
        Log.i("MyPlugin","in getJsonOfMsg");
        JSONObject result = new JSONObject();
        try{
            switch(msg.what) {
			case MSG_AUTH_CANCEL: {
				//取消授权
				result.put("status","CANCEL");
                
			} break;
			case MSG_AUTH_ERROR: {
				//授权失败
				result.put("status","ERROR");
			} break;
			case MSG_AUTH_COMPLETE: {
				//授权成功
                result.put("status","COMPLETE");
                
                Object[] objs = (Object[]) msg.obj;
				String platform = (String) objs[0];
				HashMap<String, Object> res = (HashMap<String, Object>) objs[1];
                result.put("platform",platform);
                if(platform.equals("QQ")){
                    result.put("name",res.get("nickname"));
                    result.put("id",res.get("figureurl").toString().split("\\/", -1)[4]);
                }else{
                    result.put("name",res.get("name"));
                    result.put("id",res.get("id")); 
                }
                break;
            }
            }
           
        }catch(Exception e){
            Log.i("MyPlugin",e.toString());
        }
        return result;
        
    }
	
	public void onError(Platform platform, int action, Throwable t) {
		if (action == Platform.ACTION_USER_INFOR) {
			handler.sendEmptyMessage(MSG_AUTH_ERROR);
		}
		t.printStackTrace();
	}
	
	public void onCancel(Platform platform, int action) {
		if (action == Platform.ACTION_USER_INFOR) {
			handler.sendEmptyMessage(MSG_AUTH_CANCEL);
		}
	}
	
	@SuppressWarnings("unchecked")
	public boolean handleMessage(Message msg) {
        // This is used to debug
		switch(msg.what) {
			case MSG_AUTH_CANCEL: {
				//取消授权
                Toast.makeText(cordova.getActivity(), "Canceled", Toast.LENGTH_SHORT).show();
                
			} break;
			case MSG_AUTH_ERROR: {
				//授权失败
                Toast.makeText(cordova.getActivity(), "Auth failed", Toast.LENGTH_SHORT).show();
			} break;
			case MSG_AUTH_COMPLETE: {
				//授权成功
                Toast.makeText(cordova.getActivity(), "Auth passed!!!!!!!", Toast.LENGTH_SHORT).show();
                
                // Get user info:
                Object[] objs = (Object[]) msg.obj;
				String platform = (String) objs[0];
				HashMap<String, Object> res = (HashMap<String, Object>) objs[1];
                Log.i("MyPlugin:objs[0]",platform);
                Toast.makeText(cordova.getActivity(), platform, Toast.LENGTH_SHORT).show();
                
                for (Map.Entry<String, Object> entry : res.entrySet()) {
                    Toast.makeText(cordova.getActivity(), "Key = " + entry.getKey() + ", Value = " + entry.getValue().toString(), Toast.LENGTH_SHORT).show();
   
}
			} break;

		}
		return false;
	}
	
    
   private void initSDK(Context context) {
		//初始化sharesdk,具体集成步骤请看文档：	//http://wiki.mob.com/Android_%E5%BF%AB%E9%80%9F%E9%9B%86%E6%88%90%E6%8C%87%E5%8D%97
		ShareSDK.initSDK(context);
	} 
}
