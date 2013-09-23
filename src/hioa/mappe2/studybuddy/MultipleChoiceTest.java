/*
 * Charlotte Sjøthun, s180495
 * Klassen inneholder informasjon om en prøve.
 */

package hioa.mappe2.studybuddy;

import java.io.ObjectStreamClass;
import java.util.ArrayList;

public class MultipleChoiceTest extends CommonListFolderAndTestObject
{
	private static final long serialVersionUID = ObjectStreamClass.lookup(MultipleChoiceTest.class).getSerialVersionUID();
	
	private ArrayList<MultipleChoiceQuestion> questions;
	
	public MultipleChoiceTest(String name)
	{
		super(name);
		questions = new ArrayList<MultipleChoiceQuestion>();
	} // End of constructor
	
	public void addQuestion(MultipleChoiceQuestion q)
	{
		questions.add(q);
	} // End of method addQuestion(...)
	
	public ArrayList<MultipleChoiceQuestion> getQuestions()
	{
		return questions;
	} // End of method getQuestions()
	
	public int getNumberOfQuestions()
	{
		return questions.size();
	} // End of method getNumberOfQuestions()
}
