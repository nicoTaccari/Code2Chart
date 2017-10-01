package exceptions;

@SuppressWarnings("serial")
public class UnableToCreateFileException extends Throwable{
	
	public UnableToCreateFileException(String name){
		System.out.println("An error ocurred while creating the " + name + "xml file, try again");
	}
}
