/*
 * Charlotte Sjøthun, s180495
 * Klassen er controller for add question.
 */


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
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class AddQuestion extends Activity
{
	private ArrayList<CommonListFolderAndTestObject> allFoldersAndTests;
	private MultipleChoiceTest test;
	private MultipleChoiceQuestion editQuestion;
	private EditText questionView, answerViewA, answerViewB, answerViewC, answerViewD;
	private CheckBox checkBoxA, checkBoxB, checkBoxC, checkBoxD;
	private int rightAnswer;
	private Intent intent;
	private InputMethodManager imm;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_question);
		
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		intent = this.getIntent();
		readArrayListFromFile();
		getTest();
		rightAnswer = 0;
		declareViews();
		setEditTextHeight();
		
		int questionPosition = intent.getIntExtra("questionPosition", -1);
		
		if (questionPosition != -1)
		{
			editQuestion = test.getQuestions().get(questionPosition);
			fillOutFieldsForEdit();
			
			setTitle(getString(R.string.edit_question));
		}
		else
			editQuestion = null;
	} // End of method onCreate(...)

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_question, menu);
		return true;
	} // End of method onCreateOptionsMenu(...)

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch(item.getItemId())
		{
			case R.id.save 	 : saveQuestion();
							   return true;
			case R.id.cancel : finish();
							   return true;
		}
		
		return false;
	} // End of method onOptionsItemSelected(...)
	
	
	// Metoden sørger før at bare en checkbox kan være huket av og "lagrer" hvilket svar som er riktig.
	public void checkBoxClick(View view)
	{
		CheckBox checked = (CheckBox) view;
		
		if (checked.isChecked()) 
		{
			if (checked == checkBoxA)
			{
				rightAnswer = MultipleChoiceQuestion.A;
				checkBoxB.setChecked(false);
				checkBoxC.setChecked(false);
				checkBoxD.setChecked(false);
			}
			else if (checked == checkBoxB)
			{
				rightAnswer = MultipleChoiceQuestion.B;
				checkBoxA.setChecked(false);
				checkBoxC.setChecked(false);
				checkBoxD.setChecked(false);
			}
			else if (checked == checkBoxC)
			{
				rightAnswer = MultipleChoiceQuestion.C;
				checkBoxA.setChecked(false);
				checkBoxB.setChecked(false);
				checkBoxD.setChecked(false);
			}
			else if (checked == checkBoxD)
			{
				rightAnswer = MultipleChoiceQuestion.D;
				checkBoxA.setChecked(false);
				checkBoxB.setChecked(false);
				checkBoxC.setChecked(false);
			}
		}
	} // End of method checkBoxClick(...)
	
	// Metoden blir kalt når man trykker utenfor en EditText slik at tastaturet forsvinner.
	public void removeFocusFromEditText(View view)
	{
		view.requestFocus();
		imm.hideSoftInputFromWindow(view.getWindowToken(),0);
	} // End of method removeFocusFromEditText(...)
	
	
	// Hjelpemetode som deklarerer viewene.
	private void declareViews()
	{
		questionView = (EditText)findViewById(R.id.questionView);
		answerViewA = (EditText)findViewById(R.id.alternativ_a);
		answerViewB = (EditText)findViewById(R.id.alternativ_b);
		answerViewC = (EditText)findViewById(R.id.alternativ_c);
		answerViewD = (EditText)findViewById(R.id.alternativ_d);
		
		checkBoxA = (CheckBox)findViewById(R.id.checkA);
		checkBoxB = (CheckBox)findViewById(R.id.checkB);
		checkBoxC = (CheckBox)findViewById(R.id.checkC);
		checkBoxD = (CheckBox)findViewById(R.id.checkD);
	} // End of method declareViews()
	
	
	// Hjelpemetode som fyller ut feltene hvis det er valgt et spørsmål som skal endres.
	private void fillOutFieldsForEdit()
	{
		questionView.setText(editQuestion.getQuestion());
		answerViewA.setText(editQuestion.getAnswer1());
		answerViewB.setText(editQuestion.getAnswer2());
		answerViewC.setText(editQuestion.getAnswer3());
		answerViewD.setText(editQuestion.getAnswer4());
		
		switch(editQuestion.getRightAnswer())
		{
			case 1 : checkBoxA.setChecked(true);
					 break;
			case 2 : checkBoxB.setChecked(true);
			 		 break;
			case 3 : checkBoxC.setChecked(true);
			 		 break;
			case 4 : checkBoxD.setChecked(true);
	 		   		 break;
		}
	} // End of method fillOutFieldsForEdit()

	
	// Hjelpemetode som finner prøven 
	private void getTest()
	{
		int positionMainList = intent.getIntExtra("positionMainList", -1);
		int positionListInList = intent.getIntExtra("positionListInList", -1);
		
		if (positionMainList != -1 && positionListInList == -1)
		{
			CommonListFolderAndTestObject testInList = allFoldersAndTests.get(positionMainList);
			
			if (testInList instanceof MultipleChoiceTest)
				test = (MultipleChoiceTest)testInList;
			else
				showFaultMessageToastAndFinish();
		}
		else if (positionMainList != -1 && positionListInList != -1)
		{
			CommonListFolderAndTestObject testInList = allFoldersAndTests.get(positionMainList);
			Folder folder = null;
			
			if (testInList instanceof Folder)
				folder = (Folder)testInList;
			else
				showFaultMessageToastAndFinish();
			
			if (folder != null)
				testInList = folder.getMultipleChoiceTest().get(positionListInList);
			else
				showFaultMessageToastAndFinish();
			
			if (testInList instanceof MultipleChoiceTest)
				test = (MultipleChoiceTest)testInList;
			else
				showFaultMessageToastAndFinish();
		}
		else
			showFaultMessageToastAndFinish();
	} // End of method fillOutFieldsForEdit()
	
	private void saveQuestion()
	{
		if (rightAnswer == 0)
		{
			Toast.makeText(this, "Du må krysse av for ett riktig svar", Toast.LENGTH_LONG).show();
			return;
		}
		
		String questionText = questionView.getText().toString();
		String alternativAText = answerViewA.getText().toString();
		String alternativBText = answerViewB.getText().toString();
		String alternativCText = answerViewC.getText().toString();
		String alternativDText = answerViewD.getText().toString();
		
		if (editQuestion != null)
		{
			editQuestion.setQuestion(questionText);
			editQuestion.setAnswer1(alternativAText);
			editQuestion.setAnswer2(alternativBText);
			editQuestion.setAnswer3(alternativCText);
			editQuestion.setAnswer4(alternativDText);
			editQuestion.setRightAnswer(rightAnswer);
		}
		else
		{
			MultipleChoiceQuestion question = new MultipleChoiceQuestion(questionText, alternativAText, alternativBText, 
																	 alternativCText, alternativDText, rightAnswer);
		
			test.addQuestion(question);
		}
		
		Toast.makeText(this, "Spørsmålet er lagret!", Toast.LENGTH_LONG).show();
		
		writeArrayListToFile();
		finish();
	}
	
	private void setEditTextHeight()
	{
		Point size = new Point();
		getWindowManager().getDefaultDisplay().getSize(size);

		int height = size.y;
		int questionViewHeight = (int) Math.ceil(height/3.8);
		int alternativViewsHeight = (questionViewHeight + questionViewHeight) / 5;
		
		questionView.setHeight(questionViewHeight);
		answerViewA.setHeight(alternativViewsHeight);
		answerViewB.setHeight(alternativViewsHeight);
		answerViewC.setHeight(alternativViewsHeight);
		answerViewD.setHeight(alternativViewsHeight);
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
			showFaultMessageToastAndFinish();
		} 
		catch (IOException e)
		{
			showFaultMessageToastAndFinish();
		} 
		catch (ClassNotFoundException e)
		{
			showFaultMessageToastAndFinish();
		}
    } // End of method readArrayListFromFile()
    
    private void showFaultMessageToastAndFinish()
    {
    	Toast t = Toast.makeText(getApplicationContext(), "Feil oppstod ved valg av test, prøv på nytt.", Toast.LENGTH_LONG);
		t.setGravity(Gravity.CENTER, 0, 0);
		t.show();
		finish();
    }
}