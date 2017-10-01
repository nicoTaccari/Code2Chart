package exceptions;

@SuppressWarnings("serial")
public class UnableToParseFileException extends Throwable{
	
	public UnableToParseFileException(){
		System.out.println("An error ocurred. The file was not parsed");
	}
}
