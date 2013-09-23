/*
 * Charlotte Sjøthun, s180495
 * Klassen tar ett og ett Folder eller MultiplChoiceTest objekt fra listen og legger dens informasjon i et listView.
 */

package hioa.mappe2.studybuddy;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListArrayAdapter extends ArrayAdapter<CommonListFolderAndTestObject>
{
	private Context con;
	private List<CommonListFolderAndTestObject> folders;
	
	public ListArrayAdapter(Context context, int resource, int textViewResourceId, List<CommonListFolderAndTestObject> objects)
	{
		super(context, resource, textViewResourceId, objects);
		con = context;
		folders = objects;
	} // End of constructor
	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		LayoutInflater inflater = (LayoutInflater)con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View row = inflater.inflate(R.layout.list_layout, parent,false); 
		
		TextView testName = (TextView)row.findViewById(R.id.name);
		TextView numberOfQuestion = (TextView)row.findViewById(R.id.number_of_question);
		ImageView image = (ImageView)row.findViewById(R.id.list_image);
		
		CommonListFolderAndTestObject object = folders.get(position);
		
		if (object instanceof Folder)
		{	
			Folder folder = (Folder)object;
		
			testName.setText(folder.getFolderOrTestName());
			numberOfQuestion.setText(folder.getNumberOfQuestions() + "");
			image.setImageResource(R.drawable.folder_icon);
		}
		else if (object instanceof MultipleChoiceTest)
		{
			MultipleChoiceTest test = (MultipleChoiceTest)object;
			
			testName.setText(test.getFolderOrTestName());
			numberOfQuestion.setText(test.getNumberOfQuestions() + "");
		}
		return row;
	} // End of method getView(...)
} // End of class FolderArrayAdapter