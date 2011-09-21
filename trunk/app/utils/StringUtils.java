package utils;

public class StringUtils {
	
	public static boolean compareOnlyText(String str1, String str2) {
		str1 = str1 + "";
		str2 = str2 + "";
		do {
			int length = str1.length();
			if(length == (str1 = str1.trim().replace("  ", " ")).length()){
				break;
			}
			
		}while(true);
		
		do {
			int length = str2.length();
			if(length == (str2 = str2.trim().replace("  ", " ")).length()){
				break;
			}
			
		}while(true);
		
		return str1.equalsIgnoreCase(str2);
	}
	
	public static void main(String[] args) {
		boolean b = compareOnlyText(" to ti            tu", "to ti tu");
		System.out.println(b);
	}

}
