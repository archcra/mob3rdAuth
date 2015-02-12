package com.archcra.plugin.mob.thirdauth;

import org.json.JSONObject;
import android.util.Log;


public class UserInfo {
	private String userIcon;
	private String userName;
	private String userGender;
	private String userId;
	

	public String getUserIcon() {
		return userIcon;
	}

	public void setUserIcon(String userIcon) {
		this.userIcon = userIcon;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserGender() {
		return userGender;
	}

	public void setUserGender(String userGender) {
		this.userGender = userGender;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}


	public JSONObject toJson(){
        JSONObject result = null;
       try{
		 result = new JSONObject();
		result.put("userIcon",this.userIcon);
		result.put("userName",this.userName);
		result.put("userGender",this.userGender);
		result.put("userId",this.userId);
  }catch(Exception e){
            Log.i("MyPlugin",e.toString());
        }
		return result;
	}


}
