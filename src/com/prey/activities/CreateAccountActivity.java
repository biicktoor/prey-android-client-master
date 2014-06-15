/*******************************************************************************
 * Created by Carlos Yaconi
 * Copyright 2012 Fork Ltd. All rights reserved.
 * License: GPLv3
 * Full license at "/LICENSE"
 ******************************************************************************/
package com.prey.activities;

import android.app.AlertDialog;

import com.prey.R;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.prey.PreyAccountData;
import com.prey.PreyLogger;
import com.prey.exceptions.PreyException;
import com.prey.net.PreyWebServices;

public class CreateAccountActivity extends SetupActivity {
	private static final int ERROR = 1;
	private String password = null;
	private String repassword = null;
	private String name = null;
	private String email = null;
	private String error = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_account);

		Button ok = (Button) findViewById(R.id.new_account_btn_ok);
		ok.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				name = ((EditText) findViewById(R.id.new_account_name)).getText().toString();
				email = ((EditText) findViewById(R.id.new_account_email)).getText().toString();
				password = ((EditText) findViewById(R.id.new_account_pass)).getText().toString();
				repassword = ((EditText) findViewById(R.id.new_account_repass)).getText().toString();

				if (name.equals("") || email.equals("") || password.equals(""))
					Toast.makeText(CreateAccountActivity.this, R.string.error_all_fields_are_required, Toast.LENGTH_LONG).show();
				else if (!password.equals(repassword))
					Toast.makeText(CreateAccountActivity.this, R.string.preferences_passwords_do_not_match, Toast.LENGTH_LONG).show();
				else {
					new CreateAccount().execute(name, email, password);
				}
			}
		});
		
		//Hack to fix password hint's typeface: http://stackoverflow.com/questions/3406534/password-hint-font-in-android
		EditText password = (EditText) findViewById(R.id.new_account_pass);
		password.setTypeface(Typeface.DEFAULT);
		password.setTransformationMethod(new PasswordTransformationMethod());
		
		password = (EditText) findViewById(R.id.new_account_repass);
		password.setTypeface(Typeface.DEFAULT);
		password.setTransformationMethod(new PasswordTransformationMethod());
		
		//To avoid setting these Imeoptions on each layout :)
		EditText name = (EditText) findViewById(R.id.new_account_name);
		name.setImeOptions(EditorInfo.TYPE_TEXT_FLAG_CAP_WORDS | EditorInfo.TYPE_TEXT_VARIATION_PERSON_NAME);
		
		EditText email = (EditText) findViewById(R.id.new_account_email);
		email.setImeOptions(EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
		
	}


	private class CreateAccount extends AsyncTask<String, Void, Void> {

		ProgressDialog progressDialog = null;

		@Override
		protected void onPreExecute() {
			progressDialog = new ProgressDialog(CreateAccountActivity.this);
			progressDialog.setMessage(CreateAccountActivity.this.getText(R.string.creating_account_please_wait).toString());
			progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(false);
			progressDialog.show();
		}

		@Override
		protected Void doInBackground(String... data) {
			try {
				PreyAccountData accountData = PreyWebServices.getInstance().registerNewAccount(CreateAccountActivity.this, data[0], data[1], data[2],
						getDeviceType());
				PreyLogger.d("Response creating account: " + accountData.toString());
				getPreyConfig().saveAccount(accountData);
			} catch (PreyException e) {
				error = e.getMessage();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void unused) {
			try{
				progressDialog.dismiss();
			}catch(Exception e){
			}
			if (error == null) {
				String message = getString(R.string.new_account_congratulations_text, email);
				Bundle bundle = new Bundle();
				bundle.putString("message", message);
				Intent intent = new Intent(CreateAccountActivity.this, PermissionInformationActivity.class);
				intent.putExtras(bundle);
				startActivity(intent);
				finish();
			} else
				showDialog(ERROR);
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog pass = null;
		switch (id) {

		case ERROR:
			return new AlertDialog.Builder(CreateAccountActivity.this).setIcon(R.drawable.error).setTitle(R.string.error_title).setMessage(error)
					.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
						}
					}).setCancelable(false).create();
		}
		return pass;
	}

}
