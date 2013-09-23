/*
 * Charlotte Sjøthun, s180495
 * Klassen tar ett og ett MultiplChoiceQuestion objekt fra listen og legger info om spørsmålet og svaret i et listView.
 */

package hioa.mappe2.studybuddy;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class QuestionListArrayAdapter extends ArrayAdapter<MultipleChoiceQuestion>
{
	private Context con;
	private List<MultipleChoiceQuestion> questions;
	
	public QuestionListArrayAdapter(Context context, int resource, int textViewResourceId, List<MultipleChoiceQuestion> objects)
	{
		super(context, resource, textViewResourceId, objects);
		con = context;
		questions = objects;
	} // End of constructor
	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		LayoutInflater inflater = (LayoutInflater)con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View row = inflater.inflate(R.layout.question_list_layout, parent,false); 
		
		TextView questionText = (TextView)row.findViewById(R.id.question_list_view);
		TextView answerText = (TextView)row.findViewById(R.id.answer_list_view);
		
		MultipleChoiceQuestion question = questions.get(position);
		
		questionText.setText(position+1 + ". " + question.getQuestion());
		answerText.setText(getContext().getString(R.string.answer) + " " + question.rightAnswerText());
		
		return row;
	} // End of method getView(...)
}
