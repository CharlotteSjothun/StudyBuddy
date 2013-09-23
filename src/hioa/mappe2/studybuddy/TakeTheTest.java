package hioa.mappe2.studybuddy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class TakeTheTest extends Activity
{
	private ArrayList<MultipleChoiceQuestion> questions;
	private Iterator<MultipleChoiceQuestion> iter;
	private TextView questionView, numOfQuestionsLeftView, numOfRightAndWrongView;
	private Button answerViewA, answerViewB, answerViewC, answerViewD;
	private int numOfRightAnswers = 0;
	private int numOfWrongAnswers = 0;
	private int numOfQuestionsLeft;
	private MultipleChoiceQuestion question;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_take_the_test);
		
		Intent intent = this.getIntent();
		questions = (ArrayList<MultipleChoiceQuestion>)intent.getSerializableExtra("questionsArray");
		
		if (questions == null)
			showFaultMessageToastAndFinish();
		
		boolean shuffle = intent.getBooleanExtra("shuffle", false);
		if (shuffle)Collections.shuffle(questions);
		
		declareViews();
		setViewHeight();
		
		iter = questions.iterator();
		question = iter.next();
		numOfQuestionsLeft = questions.size();
		setUpdateViews();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.take_the_test, menu);
		return true;
	}
	
	public void clickAnswer(View view)
	{
		int id = view.getId();
		int answer = 0;
		
		if (id == R.id.take_the_test_alternativ_a)
			answer = 1;
		else if (id == R.id.take_the_test_alternativ_b)
			answer = 2;
		else if (id == R.id.take_the_test_alternativ_c)
			answer = 3;
		else if (id == R.id.take_the_test_alternativ_d)
			answer = 4;
		
		if (question.getRightAnswer() == answer)
		{
			numOfRightAnswers++;
			createAnswerDialog(R.drawable.rigth, getString(R.string.right_answer));
		}
		else
		{
			numOfWrongAnswers++;
			createAnswerDialog(R.drawable.wrong, getString(R.string.wrong_answer));
		}
	}
	
	private void createAnswerDialog(int icon, String message)
	{
		Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(icon);
		builder.setTitle(message);
		
		if (iter.hasNext())
		{
			builder.setMessage(getString(R.string.right_answer) + ":\n" + question.rightAnswerText());
			
			builder.setPositiveButton(getString(R.string.new_question), new DialogInterface.OnClickListener() 
			{
				public void onClick(DialogInterface dialog, int whichButton) 
				{ 
					question = iter.next();
					setUpdateViews();
				}
			});
		}
		else
			builder.setMessage(getString(R.string.right_answer) + ":\n" + question.rightAnswerText() 
							+ "\n\n" + getString(R.string.no_more_questions));
		
		builder.setNegativeButton(getString(R.string.quit), new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int whichButton) 
			{ 
				createResultDialog();
			}
		});
		
		builder.setCancelable(false);
		builder.create().show();
	}
	
	private void createResultDialog()
	{
		Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.test_completed));
		builder.setMessage(getString(R.string.answered_correctly) + ": " + numOfRightAnswers
						 + "\n" + getString(R.string.answered_incorrectly) + ": " + numOfWrongAnswers);
		
		builder.setPositiveButton(getString(R.string.quit), new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int whichButton) 
			{ 
				finish();
			}
		});
		
		builder.setCancelable(false);
		builder.create().show();
	}
	
	private void declareViews()
	{
		numOfRightAndWrongView = (TextView)findViewById(R.id.numOfRightAndWrong);
		numOfQuestionsLeftView = (TextView)findViewById(R.id.numQuestionsLeft);
		questionView = (TextView)findViewById(R.id.take_the_test_question);
		answerViewA = (Button)findViewById(R.id.take_the_test_alternativ_a);
		answerViewB = (Button)findViewById(R.id.take_the_test_alternativ_b);
		answerViewC = (Button)findViewById(R.id.take_the_test_alternativ_c);
		answerViewD = (Button)findViewById(R.id.take_the_test_alternativ_d);
	}
	
	private void setViewHeight()
	{
		Point size = new Point();
		getWindowManager().getDefaultDisplay().getSize(size);

		int height = size.y;
		int questionViewHeight = (int) Math.ceil(height/3.8);
		int alternativViewsHeight = (questionViewHeight + questionViewHeight) / 5;
		
		questionView.setMinimumHeight(questionViewHeight);
		answerViewA.setMinimumHeight(alternativViewsHeight);
		answerViewB.setMinimumHeight(alternativViewsHeight);
		answerViewC.setMinimumHeight(alternativViewsHeight);
		answerViewD.setMinimumHeight(alternativViewsHeight);
	}
	
	private void setUpdateViews()
	{
		numOfRightAndWrongView.setText(getString(R.string.right) + " " + numOfRightAnswers + " "
									 + getString(R.string.wrong) + " " + numOfWrongAnswers);
		numOfQuestionsLeftView.setText(getText(R.string.questions_left) + " " + --numOfQuestionsLeft);
		questionView.setText(question.getQuestion());
		answerViewA.setText(question.getAnswer1());
		answerViewB.setText(question.getAnswer2());
		answerViewC.setText(question.getAnswer3());
		answerViewD.setText(question.getAnswer4());
	}
	

    private void showFaultMessageToastAndFinish()
    {
    	Toast t = Toast.makeText(getApplicationContext(), getString(R.string.error_choose_test), Toast.LENGTH_LONG);
		t.setGravity(Gravity.CENTER, 0, 0);
		t.show();
		finish();
    }
}