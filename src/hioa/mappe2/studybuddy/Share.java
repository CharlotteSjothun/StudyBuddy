package hioa.mappe2.studybuddy;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class Share extends Activity
{
	private static final String DOWNLOAD = "download";
	private static final String SHARE = "share";
	
	private ArrayList<CommonListFolderAndTestObject> allFoldersAndTests;
	private ListView folderAndTestListView;
	private ListArrayAdapter listAdapter;
	private EditText shareWithUser;
	private String user;
	private InputMethodManager imm;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share);
		
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		readArrayListFromFile();
		folderAndTestListView = (ListView)findViewById(R.id.folder_and_test_list);      
		listAdapter = new ListArrayAdapter(this, R.layout.list_layout, R.id.name, allFoldersAndTests);
		folderAndTestListView.setAdapter(listAdapter);
        
		folderAndTestListView.setOnItemClickListener(new AdapterView.OnItemClickListener() 
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
            {
            	Object object = allFoldersAndTests.get(position);
            	shareTest(object);
            }
        });
		
		shareWithUser = (EditText)findViewById(R.id.shareWithUser);
		
		Intent intent = getIntent();
		user = intent.getStringExtra("username");
		
		if (user == null) setTitle(getString(R.string.empty_title));
		else setTitle(user);
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
		writeArrayListToFile();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.share, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch(item.getItemId())
		{
			case R.id.log_out: 	goToLogIn();
								return true;
			case R.id.download: downloadTest();
								return true;
		}
		
		return false;
	} // End of method onOptionsItemSelected(...)
	
	// Metoden blir kalt når man trykker utenfor en EditText slik at tastaturet forsvinner.
	public void removeFocusFromEditText(View view)
	{
		view.requestFocus();
		imm.hideSoftInputFromWindow(view.getWindowToken(),0);
	} // End of method removeFocusFromEditText(...)

	public void clickLibrary(View view)
	{
		Intent intent = new Intent(this, Library.class);
		intent.putExtra("username", user);
		startActivity(intent);
		finish();
	}
	
	public void clickStudy(View view)
	{
		Intent intent = new Intent(this, Study.class);
		intent.putExtra("username", user);
		startActivity(intent);
		finish();
	}
	
	private void goToLogIn()
	{
		Intent intent = new Intent(this, LogIn.class);
		startActivity(intent);
		finish();
	}
	
	private void downloadTest()
	{
		UserDatabaseConnectionShare getTest = new UserDatabaseConnectionShare(getString(R.string.dialog_downloading),  DOWNLOAD);
		getTest.execute();
	}
	
	private void shareTest(Object object)
	{
		if (object instanceof Folder)
		{
			Folder folder = (Folder)object;
			createChooseTestDialog(folder);
		}
		else
		{
			MultipleChoiceTest test = (MultipleChoiceTest)object;
			
			if (test.getNumberOfQuestions() < 1)
			{
				Toast t = Toast.makeText(getApplicationContext(), getString(R.string.no_questions), Toast.LENGTH_LONG);
				t.setGravity(Gravity.CENTER, 0, 0);
				t.show();
				return;
			}
			
			UserDatabaseConnectionShare getTest = new UserDatabaseConnectionShare(getString(R.string.dialog_sharing),  SHARE, test);
			getTest.execute();
		}
	}
	
	private void createChooseTestDialog(Folder folder)
	{
		Builder builder = new AlertDialog.Builder(this);
		
		LayoutInflater li = LayoutInflater.from(this);
		View showListView = li.inflate(R.layout.dialog_list_view, null);
		builder.setView(showListView);
		
		builder.setTitle(getString(R.string.choose_test));
		
		ListView dialogList = (ListView)showListView.findViewById(R.id.dialog_test_list);
		
		final ArrayList<CommonListFolderAndTestObject> testList = folder.getMultipleChoiceTest();
		
		if (testList.size() == 0)
			builder.setMessage(getString(R.string.no_tests_added));
		
		ListArrayAdapter dialogListAdapter = new ListArrayAdapter(this, R.layout.list_layout, R.id.name, testList);
		dialogList.setAdapter(dialogListAdapter);
		
		dialogList.setOnItemClickListener(new AdapterView.OnItemClickListener() 
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
            {
            	MultipleChoiceTest test = (MultipleChoiceTest)testList.get(position);
            	
            	if (test.getNumberOfQuestions() < 1)
    			{
    				Toast t = Toast.makeText(getApplicationContext(), getString(R.string.no_questions), Toast.LENGTH_LONG);
    				t.setGravity(Gravity.CENTER, 0, 0);
    				t.show();
    				return;
    			}
            	
            	
            }
        });

		builder.setNegativeButton(getString(R.id.cancel), new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int whichButton) 
			{ 
				dialog.cancel();
			}
		});
		
		builder.setCancelable(false);
		builder.create().show();
	} // End of method createChoiceDialog(...)
	
	private void writeArrayListToFile()
    {
		try
		{
			FileOutputStream file = openFileOutput("savedArrayList", Context.MODE_PRIVATE);
			ObjectOutputStream toArrayListFile = new ObjectOutputStream(file);
			
			toArrayListFile.writeObject(allFoldersAndTests);
		} 
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
    } // End of method writeArrayListToFile()
	
	
	// Metoden leser arrayList fra fil.
    @SuppressWarnings("unchecked")
	private void readArrayListFromFile()
    {
		try
		{
			FileInputStream file = openFileInput("savedArrayList");
			ObjectInputStream fromArrayListFile = new ObjectInputStream(file);
			
			allFoldersAndTests = (ArrayList<CommonListFolderAndTestObject>)fromArrayListFile.readObject();
			
		} 
		catch (FileNotFoundException e)
		{
			allFoldersAndTests = new ArrayList<CommonListFolderAndTestObject>();
			showFaultMessageToastAndFinish();
		} 
		catch (IOException e)
		{
			allFoldersAndTests = new ArrayList<CommonListFolderAndTestObject>();
			showFaultMessageToastAndFinish();
		} 
		catch (ClassNotFoundException e)
		{
			allFoldersAndTests = new ArrayList<CommonListFolderAndTestObject>();
			showFaultMessageToastAndFinish();
		}
    } // End of method readArrayListFromFile()
    
    private void showFaultMessageToastAndFinish()
    {
    	Toast t = Toast.makeText(getApplicationContext(), getString(R.string.read_from_file_error_message), Toast.LENGTH_LONG);
		t.setGravity(Gravity.CENTER, 0, 0);
		t.show();
    }
    
    private boolean regexUsername(String bName)
	{
		return bName.matches("[A-ZÆØÅa-zæøå 1-9]{1,20}");
	} // End of method regexBrukernavn()
    
    class UserDatabaseConnectionShare extends AsyncTask<String, String, String> 
	{
		private final static String URL_SHARE = "http://student.iu.hio.no/~s180495/StudyBuddy/shareTest.php";
		private final static String URL_DOWNLOAD = "http://student.iu.hio.no/~s180495/StudyBuddy/downloadTest.php";
		private ProgressDialog pDialog = null;
		private String message, keyword;
		private MultipleChoiceTest test;
		
		public UserDatabaseConnectionShare(String dialogMessage, String keywordDB)
		{
			message = dialogMessage;
			keyword = keywordDB;
		}
		
		public UserDatabaseConnectionShare(String dialogMessage, String keywordDB, MultipleChoiceTest quiz)
		{
			message = dialogMessage;
			keyword = keywordDB;
			test = quiz;
		}
		
        @Override
        protected void onPreExecute() 
        {
            super.onPreExecute();
            pDialog = new ProgressDialog(Share.this);
            pDialog.setMessage(message);
            pDialog.setCancelable(false);
            pDialog.show();
        }
 
        protected String doInBackground(String... args) 
        {
            List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
            params.add(new BasicNameValuePair("keyword", keyword));
            
            JSONParser jsonParser = new JSONParser();
            
            if (keyword == DOWNLOAD)
            {
                params.add(new BasicNameValuePair("username", user));
                
            	try
    			{
    				JSONObject json = jsonParser.makeHttpRequest(URL_DOWNLOAD, "POST", params);
    				
    				JSONArray jArray = json.getJSONArray("objects");
    				MultipleChoiceTest newTest = new MultipleChoiceTest("");
    				String testName = "";
    				
    	            for (int i = 0; i < jArray.length();) 
    	            {
    	                JSONObject e = jArray.getJSONObject(i++);
    	                String s = e.getString("object");
    	                JSONObject jObject = new JSONObject(s);
    	                
	                    int success = jObject.getInt("success");
	     
	                    if (success == 1) 
	                    {
	                        testName = jObject.getString("testName");
	                        String question = jObject.getString("question");
	                        String alt1 = jObject.getString("alt1");
	                        String alt2 = jObject.getString("alt2");
	                        String alt3 = jObject.getString("alt3");
	                        String alt4 = jObject.getString("alt4");
	                        int answer = jObject.getInt("answer");
	                        
	                        if (!testName.equals(newTest.getFolderOrTestName()))
	                        {
	                        	newTest = new MultipleChoiceTest(testName);
	                        	allFoldersAndTests.add(newTest);
	                        }
	                        
	                        newTest.addQuestion(new MultipleChoiceQuestion(question, alt1, alt2, alt3, alt4, answer));
	                    } 
	                    else 
	                    {
	                    	String errorFromSQL = jObject.getString("message");
	                    	
	                    	return errorFromSQL;
	                    }
    	            }
    	            
    	            return getString(R.string.downloaded);
                } 
            	catch (UnsupportedEncodingException e) 
    	        {
    	        	return e.toString();
    	        }
            	catch (ClientProtocolException e) 
    	        {
    	        	return e.toString();
    	        }
    			catch(IOException e)
    			{
    				return e.toString();
    			}
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
            else if (keyword == SHARE)
            {
            	String username = shareWithUser.getText().toString();
                
                if (username.equals("")) return getString(R.string.empty_share_with_user);
                
                if (!regexUsername(username)) return getString(R.string.error_username);
                
                if (test == null) return "En feil oppstod. Prøv på nytt!";
                
                JSONObject jsonToSend = new JSONObject();
                
            	try
    			{            		
            		JSONObject userValues = new JSONObject();
            		userValues.put("keyword", keyword);
            		userValues.put("username", username);
            		userValues.put("testName", test.getFolderOrTestName());
            		jsonToSend.put("userValues", userValues);
            		
                    ArrayList<MultipleChoiceQuestion> questions = test.getQuestions();
                    Iterator<MultipleChoiceQuestion> iter = questions.iterator();
                    
                    JSONObject jsonQuestions = new JSONObject();
                    
                    int i = 0;
                    
                    while (iter.hasNext())
                    {
    	                MultipleChoiceQuestion question = iter.next();
    	                
    	                JSONObject jsonQuestion = new JSONObject();
    	                
    	                jsonQuestion.put("questionText", question.getQuestion());
    	                jsonQuestion.put("alt1Text", question.getAnswer1());
    	                jsonQuestion.put("alt2Text", question.getAnswer2());
    	                jsonQuestion.put("alt3Text", question.getAnswer3());
    	                jsonQuestion.put("alt4Text", question.getAnswer4());
    	                jsonQuestion.put("answer", question.getRightAnswer() + "");
    	                
    	                jsonQuestions.put("question" + i++, jsonQuestion);
                   	}
                    
                    jsonToSend.put("questions", jsonQuestions);
            		
    				JSONObject json = jsonParser.makeHttpRequestJson(URL_SHARE, "POST", jsonToSend);
    				
    				if (json == null) return "json er null";
    				
                    int success = json.getInt("success");
     
                    if (success == 1) 
                    {
                    	String messageFromServer = json.getString("message");
                    	
                    	return messageFromServer;
                    } 
                    else 
                    {
                    	String errorFromSQL = json.getString("message");
                    	
                    	return errorFromSQL;
                    }
                } 
            	catch (UnsupportedEncodingException e) 
    	        {
    	        	return e.toString();
    	        }
            	catch (ClientProtocolException e) 
    	        {
    	        	return e.toString();
    	        }
    			catch(IOException e)
    			{
    				return e.toString();
    			}
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
 
            return null;
        }
 
        protected void onPostExecute(String message) 
        {
            pDialog.dismiss();
            
            if (message != null)
            {
            	if (message.equals("Downloaded"))
            	{
            		listAdapter.notifyDataSetChanged();
            	}
            	
            	Toast toast = Toast.makeText(Share.this, message, Toast.LENGTH_LONG);
            	toast.setGravity(Gravity.CENTER, 0, 0);
            	toast.show();
            }
        }
    }
}
