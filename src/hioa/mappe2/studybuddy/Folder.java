/*
 * Charlotte Sjøthun, s180495
 * Klassen inneholder informasjon om en mappe.
 */

package hioa.mappe2.studybuddy;

import java.io.ObjectStreamClass;
import java.util.ArrayList;

public class Folder extends CommonListFolderAndTestObject
{
	private static final long serialVersionUID = ObjectStreamClass.lookup(Folder.class).getSerialVersionUID();

	private ArrayList<CommonListFolderAndTestObject> multipleChoiceTestsAndFolders;
	
	public Folder(String name)
	{
		super(name);
		multipleChoiceTestsAndFolders = new ArrayList<CommonListFolderAndTestObject>();
	} // End of constructor
	
	public void addTest(MultipleChoiceTest test)
	{
		multipleChoiceTestsAndFolders.add(test);
	} // End of method addTest(...)
	
	public ArrayList<CommonListFolderAndTestObject> getMultipleChoiceTest()
	{
		return multipleChoiceTestsAndFolders;
	} // End of method getMultipleChoiceTest()
	
	public int getNumberOfQuestions()
	{
		return multipleChoiceTestsAndFolders.size();
	} // End of method getNumberOfQuestions()
}
