package com.example.myfirstlauncher;

import android.R.integer;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.util.Log;

public class MyAccountManager {
	AccountManager mAccountManager ;
	Context mContext;
	Account[] accounts = null;
	public MyAccountManager(Context ctxt) {
		if (mAccountManager == null) {
			mAccountManager = AccountManager.get(ctxt);
		}
		
	}
	
	
	public void getMyAccount() {
		
		
		
		accounts = mAccountManager.getAccounts();
		
		for (Account ac : accounts) {
			Log.v("TAG", ac.toString());
		}
	}

	public void addMyAccount(String name,String type,String pass){
		Account newAccount=new Account(name, type);
		
		mAccountManager.addAccountExplicitly(newAccount, pass,null);
			}
	
	public void removeMyAccount (String name, String type) {
		
		accounts = mAccountManager.getAccountsByType(type);
		
		for (Account ac : accounts) {
			if(ac.name.toString().equalsIgnoreCase(name)){
			mAccountManager.removeAccount(ac, null,null);
			}
			
		}
	}
}
