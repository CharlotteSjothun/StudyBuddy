package hioa.mappe2.studybuddy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

public class LogIn extends Activity
{
	private EditText usernameView, passwordView;
	private String username;
	private InputMethodManager imm;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_log_in);
		
		usernameView = (EditText)findViewById(R.id.username);
		passwordView = (EditText)findViewById(R.id.password);
		
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.log_in, menu);
		return true;
	}

	public void newUser(View v)
	{	
		String dialogMessage = getString(R.string.dialog_creating_user);
		String keyword = "newUser";
		
		UserDatabaseConnectionLogIn makeUser = new UserDatabaseConnectionLogIn(dialogMessage, keyword);
		makeUser.execute();
	}
	
	public void logIn(View v)
	{
		String dialogMessage = getString(R.string.dialog_log_in);
		String keyword = "logIn";
		
		UserDatabaseConnectionLogIn logIn = new UserDatabaseConnectionLogIn(dialogMessage, keyword);
		logIn.execute();
	}
	
	// Metoden blir kalt når man trykker utenfor en EditText slik at tastaturet forsvinner.
	public void removeFocusFromEditText(View view)
	{
		view.requestFocus();
		imm.hideSoftInputFromWindow(view.getWindowToken(),0);
	} // End of method removeFocusFromEditText(...)
	
	private boolean regexUsername(String bName)
	{
		return bName.matches("[A-ZÆØÅa-zæøå 1-9]{1,20}");
	} // End of method regexBrukernavn()
	
	private boolean regexPassword(String password)
	{
		return password.matches("[A-ZÆØÅa-zæøå 1-9]{1,32}");
	} // End of method regexBrukernavn()
	
	
	class UserDatabaseConnectionLogIn extends AsyncTask<String, String, String> 
	{
		private final static String URL_USER = "http://student.iu.hio.no/~s180495/StudyBuddy/user.php";
		private ProgressDialog pDialog = null;
		private String message, keyword;
		
		public UserDatabaseConnectionLogIn(String dialogMessage, String keywordDB)
		{
			message = dialogMessage;
			keyword = keywordDB;
		}		
		
		 
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() 
        {
            super.onPreExecute();
            pDialog = new ProgressDialog(LogIn.this);
            pDialog.setMessage(message);
            pDialog.setCancelable(false);
            pDialog.show();
        }
 
        protected String doInBackground(String... args) 
        {
            username = usernameView.getText().toString();
            String password = passwordView.getText().toString();
            
            if (username.equals("") || password.equals("")) return getString(R.string.error_empty_username_or_password);
            
            if (!regexUsername(username)) return getString(R.string.error_username);
            if (!regexPassword(password)) return getString(R.string.error_password);
 
            List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
            params.add(new BasicNameValuePair("keyword", keyword));
            params.add(new BasicNameValuePair("username", username));
            params.add(new BasicNameValuePair("password", password));
 
            JSONParser jsonParser = new JSONParser();
            
			try
			{
				JSONObject json = jsonParser.makeHttpRequest(URL_USER, "POST", params);
			
                int success = json.getInt("success");
 
                if (success == 1) 
                {
                    Intent intent = new Intent(getApplicationContext(), Library.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                    finish();
                } 
                else 
                {
                	String errorFromSQL = json.getString("message");
                	
                	if (errorFromSQL.equals("1062"))
                		return getString(R.string.username_exist);
                	else
                		return errorFromSQL;
                }
            } 
			catch(IOException e)
			{
				return e.toString();
			}
            catch (JSONException e)
            {
                e.printStackTrace();
            }
 
            return null;
        }
 
        protected void onPostExecute(String message) 
        {
            pDialog.dismiss();
            
            if (message != null)
            {
            	Toast toast = Toast.makeText(LogIn.this, message, Toast.LENGTH_LONG);
            	toast.setGravity(Gravity.CENTER, 0, 0);
            	toast.show();
            }
        }
    }
}
