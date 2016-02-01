package com.noteshareapp.noteshare;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrationActivity extends Activity {

	public Button btnsignUP, btnSignIn;
	public String firstName, lastName, email, password, confirmPassword;
	public EditText registerFirstName, registerLastName, registerEmail, registerPassword, registerConfirmPassword;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.registration_activity);

		// LayoutInflater inflater = (LayoutInflater)
		// this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// inflate your activity layout here!
		// View contentView = inflater.inflate(R.layout.login_activity, null,
		// false);
		// /mDrawerLayout.addView(contentView, 0);
		initlizeUIElement(null);

	}

	void initlizeUIElement(View contentview) {

		btnSignIn = (Button) findViewById(R.id.btnregistersignin);
		btnsignUP = (Button) findViewById(R.id.btnregistersignup);

		registerPassword = (EditText) findViewById(R.id.registerpassword);
		registerEmail = (EditText) findViewById(R.id.registerusername);
		registerConfirmPassword = (EditText) findViewById(R.id.registerconfirmpassword);
		registerFirstName = (EditText) findViewById(R.id.registerfirstname);
		registerLastName = (EditText) findViewById(R.id.registerlastname);

		registerPassword.setTypeface(Typeface.DEFAULT);
		registerPassword.setTransformationMethod(new PasswordTransformationMethod());
		registerConfirmPassword.setTypeface(Typeface.DEFAULT);
		registerConfirmPassword.setTransformationMethod(new PasswordTransformationMethod());

		/*textusername = (EditText) layoutloginusername
				.findViewById(R.id.editTextlogin);
		textpassowrd = (EditText) layoutloginpassowrd
				.findViewById(R.id.editTextlogin);

		textconfirmpassword = (EditText) layoutloginconfirmpassowrd
				.findViewById(R.id.editTextlogin);
		textFirstname = (EditText) layoutloginfirstname
				.findViewById(R.id.editTextlogin);
		textlastname = (EditText) layoutloginlastname
				.findViewById(R.id.editTextlogin);

		textconfirmpassword.setHint("Confirm Password");
		textFirstname.setHint("First Name");
		textlastname.setHint("Last Name");
		textusername.setHint("Email");
		textpassowrd.setHint("Password");
		textpassowrd.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD
				| InputType.TYPE_CLASS_TEXT);
		textconfirmpassword
				.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD
						| InputType.TYPE_CLASS_TEXT);*/
		
		addlistners();

	}

	void addlistners() {

		btnSignIn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent newIntent = new Intent(getApplicationContext(),LoginActivity.class);
				startActivity(newIntent);
			}
		});
		btnsignUP.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				firstName = registerFirstName.getText().toString();
				lastName = registerLastName.getText().toString();
				email = registerEmail.getText().toString();
				password = registerPassword.getText().toString();
				confirmPassword = registerConfirmPassword.getText().toString();

				firstName = firstName.trim();
				lastName = lastName.trim();
				email = email.trim();
				password = password.trim();
				confirmPassword = confirmPassword.trim();

				if (firstName.equals("") || lastName.equals("") || email.equals("") ||
						password.equals("") || confirmPassword.equals("")) {
					Toast.makeText(getApplication(), R.string.enter_fields, Toast.LENGTH_LONG).show();
				} else if (!isValidEmail(email)) {
					registerEmail.setError("Invalid Email");
				} else if (!isValidPassword(password)) {
					registerPassword.setError("Invalid Password");
				} else {
					if (password.equals(confirmPassword)) {
						Intent newIntent = new Intent(getApplicationContext(), UserProfileActivity.class);
						newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(newIntent);
						finish();
					} else {
						Toast.makeText(getApplication(), "Password and Confirm Password doesn't match", Toast.LENGTH_LONG).show();
					}
				}
				//startActivity(new Intent(getApplicationContext(), UserProfileActivity.class));

			}
		});

	}

	private boolean isValidPassword(String password) {
		if (password != null && password.length() >= 6) {
			return true;
		}
		return false;
	}

	private boolean isValidEmail(String email) {
		String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
				+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
		Pattern pattern = Pattern.compile(EMAIL_PATTERN);
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}


}
