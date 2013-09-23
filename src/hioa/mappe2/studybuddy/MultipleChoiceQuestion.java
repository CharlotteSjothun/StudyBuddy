/*
 * Charlotte Sjøthun, s180495
 * Klassen inneholder informasjon om et spørsmål.
 */

package hioa.mappe2.studybuddy;

import java.io.ObjectStreamClass;
import java.io.Serializable;

public class MultipleChoiceQuestion implements Serializable
{
	private static final long serialVersionUID = ObjectStreamClass.lookup(MultipleChoiceQuestion.class).getSerialVersionUID();
	
	public static final int A = 1;
	public static final int B = 2;
	public static final int C = 3;
	public static final int D = 4;
	private int rightAnswer;
	
	private String question, answer1, answer2, answer3, answer4;
	
	public MultipleChoiceQuestion(String question, String answer1, String answer2, String answer3, String answer4, int rightAnswer)
	{
		this.question = question;
		this.answer1 = answer1;
		this.answer2 = answer2;
		this.answer3 = answer3;
		this.answer4 = answer4;
		this.rightAnswer = rightAnswer;
	} // End of constructor
	
	
	public void setQuestion(String q)
	{
		question = q;
	} // End of method setQuestion(...)
	
	
	public void setAnswer1(String alternative1)
	{
		answer1 = alternative1;
	} // End of method setAnswer1(...)
	
	
	public void setAnswer2(String alternative2)
	{
		answer2 = alternative2;
	} // End of method setAnswer2(...)
	
	
	public void setAnswer3(String alternative3)
	{
		answer3 = alternative3;
	} // End of method setAnswer3(...)
	
	
	public void setAnswer4(String alternative4)
	{
		answer4 = alternative4;
	} // End of method setAnswer4(...)
	
	
	public void setRightAnswer(int rightAnswerNum)
	{
		rightAnswer = rightAnswerNum;
	} // End of method setRightAnswer(...)
	
	
	public String getQuestion()
	{
		return question;
	} // End of method getQuestion()
	
	
	public String getAnswer1()
	{
		return answer1;
	} // End of method getAnswer1()
	
	
	public String getAnswer2()
	{
		return answer2;
	} // End of method getAnswer2()
	
	
	public String getAnswer3()
	{
		return answer3;
	} // End of method getAnswer3()
	
	
	public String getAnswer4()
	{
		return answer4;
	} // End of method getAnswer4()
	
	
	public int getRightAnswer()
	{
		return rightAnswer;
	} // End of method getRightAnswer()
	
	
	// Metoden returnerer teksten til det riktige svaret.
	public String rightAnswerText()
	{
		switch (rightAnswer)
		{
			case A : return answer1;
			case B : return answer2;
			case C : return answer3;
			case D : return answer4;
		}
		return "";
	} // End of method rightAnswerText()
}
