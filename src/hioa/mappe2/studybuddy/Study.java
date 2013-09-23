package hioa.mappe2.studybuddy;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

public class Study extends Activity
{
	private ArrayList<CommonListFolderAndTestObject> allFoldersAndTests;
	private ListView folderAndTestListView;
	private ListArrayAdapter ListAdapter;
	private String user;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_study);
		
		readArrayListFromFile();
		folderAndTestListView = (ListView)findViewById(R.id.folder_and_test_list);      
		ListAdapter = new ListArrayAdapter(this, R.layout.list_layout, R.id.name, allFoldersAndTests);
		folderAndTestListView.setAdapter(ListAdapter);
        
		folderAndTestListView.setOnItemClickListener(new AdapterView.OnItemClickListener() 
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
            {
            	Object object = allFoldersAndTests.get(position);
        		startTest(object);
            }
        });
		
		Intent intent = getIntent();
		user = intent.getStringExtra("username");
		
		if (user == null) setTitle(getString(R.string.empty_title));
		else setTitle(user);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.study, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch(item.getItemId())
		{
			case R.id.log_out: 	goToLogIn();
								return true;
		}
		
		return false;
	} // End of method onOptionsItemSelected(...)
	

	public void clickLibrary(View view)
	{
		Intent intent = new Intent(this, Library.class);
		intent.putExtra("username", user);
		startActivity(intent);
		finish();
	}
	
	public void clickShare(View view)
	{
		Intent intent = new Intent(this, Share.class);
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
	
	private void startTest(Object object)
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
			
			goToTakeTheTest(test);
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
    				Toast t = Toast.makeText(getApplicationContext(), getString(R.string.no_questions_added), Toast.LENGTH_LONG);
    				t.setGravity(Gravity.CENTER, 0, 0);
    				t.show();
    				return;
    			}
            	
            	goToTakeTheTest(test);
            }
        });

		builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int whichButton) 
			{ 
				dialog.cancel();
			}
		});
		
		builder.setCancelable(false);
		builder.create().show();
	} // End of method createChoiceDialog(...)
	
	
	private void goToTakeTheTest(MultipleChoiceTest test)
	{
		CheckBox shuffle= (CheckBox)findViewById(R.id.checkShuffle);
		
		Intent intent = new Intent(getApplicationContext(), TakeTheTest.class);
		intent.putExtra("questionsArray", test.getQuestions());
		intent.putExtra("shuffle", shuffle.isChecked());
		startActivity(intent);
	}
/*
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
	*/
	
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
}
