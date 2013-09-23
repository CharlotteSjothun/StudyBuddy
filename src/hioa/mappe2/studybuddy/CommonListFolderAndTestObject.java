/*
 * Charlotte Sjøthun, s180495
 * Klassen er superklasse for Folder og MultipleChoiceTest for at disse skal kunne legges i samme liste.
 */

package hioa.mappe2.studybuddy;

import java.io.ObjectStreamClass;
import java.io.Serializable;

public class CommonListFolderAndTestObject implements Serializable
{
	private static final long serialVersionUID = ObjectStreamClass.lookup(CommonListFolderAndTestObject.class).getSerialVersionUID();
	
	private String folderOrTestName;
	
	public CommonListFolderAndTestObject(String name)
	{
		folderOrTestName = name;
	} // End of constructor
	
	public void setFolderOrTestName(String name)
	{
		folderOrTestName = name;
	} // End of method setFolderOrTestName(...)
	
	public String getFolderOrTestName()
	{
		return folderOrTestName;
	} // End of method getFolderOrTestName()
}
