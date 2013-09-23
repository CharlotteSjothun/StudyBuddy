package hioa.mappe2.studybuddy;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class Library extends Activity
{
	private static final int FOLDER = 0;
	private static final int TEST = 1;
	
	private ArrayList<CommonListFolderAndTestObject> allFoldersAndTests;
	private ListView folderAndTestListView;
	private ListArrayAdapter listAdapter;
	private int nextFolderPosition = 0;
	private Dialog choiceDialog;
	private String user;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_library);
		
		Intent intent = getIntent();
		user = intent.getStringExtra("username");
		
		if (user == null) setTitle(getString(R.string.empty_title));
		else setTitle(user);
	}
	
	
	@Override
	protected void onResume()
	{
		super.onResume();
		
		/* Oppretting/henting av listen og opprettelse av adapter settes her i onResume på grunn av at denne
		 * activitetes avsluttes ikke når man går til AddQuestion activiteten.
		 */
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
        		createChoiceDialog(object, null);
            }
        });
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
		getMenuInflater().inflate(R.menu.library, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch(item.getItemId())
		{
			case R.id.add : 	createAlertDialogGetType();
								return true;
			case R.id.log_out: 	goToLogIn();
								return true;
		}
		
		return false;
	} // End of method onOptionsItemSelected(...)
	
	
	public void clickStudy(View view)
	{
		Intent intent = new Intent(this, Study.class);
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
	
	private void createAlertDialogGetType()
	{
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

		alertDialogBuilder.setTitle(getString(R.string.dialog_create));
		
		final CharSequence[] items = {getString(R.string.folder), getString(R.string.test)};
		
		alertDialogBuilder.setItems(items, new DialogInterface.OnClickListener() 
		{
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				createAlertDialogGetName(which, null);
				dialog.dismiss();
			}
		});
		
		alertDialogBuilder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() 
		{
			    public void onClick(DialogInterface dialog,int id) 
			    {
			    	dialog.cancel();
			    }
		});
		
		alertDialogBuilder.setCancelable(false);
		alertDialogBuilder.create().show();
	}
	
	// Hjelpemetode
	private void createAlertDialogGetName(final int whichType, final Folder addInFolder)
	{
		LayoutInflater li = LayoutInflater.from(this);
		View getNameView = li.inflate(R.layout.alert_dialog_get_name_view, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

		alertDialogBuilder.setTitle((whichType == TEST ? getString(R.string.write_name_test) : getString(R.string.write_name_folder)));
		alertDialogBuilder.setView(getNameView);

		final EditText userInput = (EditText) getNameView.findViewById(R.id.name);
		userInput.setMinHeight(30);
		
		alertDialogBuilder.setCancelable(false);
		
		alertDialogBuilder.setPositiveButton(getString(R.string.create), new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog,int id) 
			{
				String name = String.valueOf(userInput.getText());
				
				if (name.equals("")) 
				{
					Toast t = Toast.makeText(Library.this, getString(R.string.error_empty_name), Toast.LENGTH_LONG);
					t.setGravity(Gravity.CENTER, 0, 0);
					t.show();
					createAlertDialogGetName(whichType, addInFolder);
					return;
				}
				
				if (whichType == FOLDER)
				{
					allFoldersAndTests.add(nextFolderPosition++, new Folder(name));
					listAdapter.notifyDataSetChanged();
				}
				else if (whichType == TEST)
				{
					if (addInFolder != null)
						addInFolder.addTest(new MultipleChoiceTest(name));
					else	
						allFoldersAndTests.add(new MultipleChoiceTest(name));
					
					listAdapter.notifyDataSetChanged();
				}
			}
		});
		
		alertDialogBuilder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() 
		{
			    public void onClick(DialogInterface dialog,int id) 
			    {
			    	dialog.cancel();
			    }
		});

		alertDialogBuilder.create().show();
	}
	
	
	private void createChoiceDialog(final Object object, final Folder testInFolder)
	{
		Builder builder = new AlertDialog.Builder(this);
		
		LayoutInflater li = LayoutInflater.from(this);
		View showListView = li.inflate(R.layout.dialog_list_view, null);
		builder.setView(showListView);
		
		builder.setTitle(getString(R.string.choice_dialog_title));
		
		ListView dialogList = (ListView)showListView.findViewById(R.id.dialog_test_list);
		
		if (object instanceof Folder)
		{
			choiceDialogSpesificsFolder(object, builder, dialogList);
		}
		else if (object instanceof MultipleChoiceTest)
		{
			choiceDialogSpesificsTest(object, builder, dialogList, testInFolder);
		}
		
		builder.setNegativeButton(getString(R.string.delete), new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int whichButton) 
			{ 
				if (testInFolder != null)
					testInFolder.getMultipleChoiceTest().remove(object);
				else
					allFoldersAndTests.remove(object);
				
				listAdapter.notifyDataSetChanged();
				
				if (object instanceof Folder) nextFolderPosition--;
			}
		});
		builder.setNeutralButton(getString(R.string.cancel), new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int whichButton) 
			{ 
				dialog.cancel();
			}
		});
		
		builder.setCancelable(false);
		choiceDialog = builder.create();
		choiceDialog.show();
	} // End of method createChoiceDialog(...)
	
	
	private void choiceDialogSpesificsFolder(Object object, Builder builder, ListView dialogList)
	{
		final Folder folder = (Folder)object;
		
		final ArrayList<CommonListFolderAndTestObject> testList = folder.getMultipleChoiceTest();
		
		if (testList.size() == 0)
			builder.setMessage(getString(R.string.no_tests_added));
		else
			builder.setMessage(getString(R.string.dialog_folder_message));
		
		ListArrayAdapter dialogListAdapter = new ListArrayAdapter(this, R.layout.list_layout, R.id.name, testList);
		dialogList.setAdapter(dialogListAdapter);
		
		dialogList.setOnItemClickListener(new AdapterView.OnItemClickListener() 
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
            {
            	if (choiceDialog != null) choiceDialog.dismiss();
            	Object object = testList.get(position);
        		createChoiceDialog(object, folder);
            }
        });
		
		builder.setPositiveButton(getString(R.string.add_test), new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int whichButton) 
			{
				createAlertDialogGetName(TEST, folder);
			}
		});
	}
	
	private void choiceDialogSpesificsTest(Object testObject, Builder builder, ListView dialogList, Folder testInFolder)
	{
		int positionMainList = -1;
		int positionListInList = -1;
		
		if (testInFolder != null)
		{
			positionMainList = allFoldersAndTests.indexOf(testInFolder);
			positionListInList = testInFolder.getMultipleChoiceTest().indexOf(testObject);
		}
		else
			positionMainList = allFoldersAndTests.indexOf(testObject);
		
		final int positionMainListFinal = positionMainList;
		final int positionListInListFinal = positionListInList;
		final MultipleChoiceTest test = (MultipleChoiceTest)testObject;
		
		final ArrayList<MultipleChoiceQuestion> questionList = test.getQuestions();
		
		if (questionList.size() == 0)
			builder.setMessage(getString(R.string.no_questions_added));
		else
			builder.setMessage(getString(R.string.dialog_test_message));
		
		QuestionListArrayAdapter dialogListAdapter = new QuestionListArrayAdapter(this, R.layout.list_layout, R.id.name, questionList);
		dialogList.setAdapter(dialogListAdapter);
		
		dialogList.setOnItemClickListener(new AdapterView.OnItemClickListener() 
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
            {
            	if (choiceDialog != null) choiceDialog.dismiss();
            	Intent intent = new Intent(getApplicationContext(), AddQuestion.class);
				intent.putExtra("positionMainList", positionMainListFinal);
				intent.putExtra("positionListInList", positionListInListFinal);
				intent.putExtra("questionPosition", position);
				startActivity(intent);
            }
        });
		
		builder.setPositiveButton(getString(R.string.add_question), new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int whichButton) 
			{
				Intent intent = new Intent(getApplicationContext(), AddQuestion.class);
				intent.putExtra("positionMainList", positionMainListFinal);
				intent.putExtra("positionListInList", positionListInListFinal);
				intent.putExtra("questionPosition", -1);
				startActivity(intent);
			}
		});
	}
	
	
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
		} 
		catch (IOException e)
		{
			allFoldersAndTests = new ArrayList<CommonListFolderAndTestObject>();
			showFaultMessageToast();
		} 
		catch (ClassNotFoundException e)
		{
			allFoldersAndTests = new ArrayList<CommonListFolderAndTestObject>();
			showFaultMessageToast();
		}
    } // End of method readArrayListFromFile()
    
    private void showFaultMessageToast()
    {
    	Toast t = Toast.makeText(getApplicationContext(), getString(R.string.read_from_file_error_message), Toast.LENGTH_LONG);
		t.setGravity(Gravity.CENTER, 0, 0);
		t.show();
    }
}
