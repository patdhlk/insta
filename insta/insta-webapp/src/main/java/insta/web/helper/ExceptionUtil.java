package insta.web.helper;

import java.util.ArrayList;
import java.util.List;

public class ExceptionUtil {
	public static List<Throwable> getThrowableList(Throwable throwable) {
		List<Throwable> result = new ArrayList<Throwable>();
		
		while (throwable != null)
		{
			result.add(throwable);
			throwable = throwable.getCause();
		}
		return result;
	}
	
	public static String getThrowableListAsString(Throwable throwable) {
		String result = "";
		
		while (throwable != null)
		{
			if (result != "")
				result += ";";
			
			result += throwable.getMessage();
			throwable = throwable.getCause();
		}
		return result;
	}
	
	public static Throwable getRootCause(Throwable throwable) {
		Throwable result = throwable;

		while(result.getCause() != null) {
			result = result.getCause();
		}
		
		return result;
	}
}
