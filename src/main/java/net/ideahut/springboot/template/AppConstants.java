package net.ideahut.springboot.template;

import java.util.regex.Pattern;

public final class AppConstants {
	private AppConstants() {}
	
	public static final String PACKAGE = "net.ideahut.springboot.template";
	
	// Default
	public static class Default {
		private Default() {}
		public static final String TIMEZONE = "Asia/Jakarta";
	}
	
	// Boolean
	public static class Boolean {
		private Boolean() {}
		public static final Character YES 	= 'Y';
		public static final Character NO 	= 'N';
	}
	
	// Regex
	public static class Regex {
		private Regex() {}
		public static final Pattern EMAIL	= Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");
	}
	
	// Profile
	public static class Profile {
		private Profile() {}
		public static final String SYSTEM 	= "__SYSTEM__";		
		public static class Status {
			private Status() {}
			public static final char REGISTER	= 'R';
			public static final char ACTIVE		= 'A';
			public static final char INACTIVE 	= 'I';
			public static final char BLOCKED 	= 'B';
		}
		public static class Gender {
			private Gender() {}
			public static final char FEMALE = 'F';
			public static final char MALE 	= 'M';
		}
	}
	
	// Bean
	public static class Bean {
		private Bean() {}
		public static final class Redis {
			private Redis() {}
			public static final String COMMON	= "commonRedis";	
		}
		
		public static final class Task {
			private Task() {}
			public static final String COMMON	= "commonTask";
			public static final String AUDIT	= "auditTask";		
		}
		
		public static final class Credential {
			private Credential() {}
			public static final String ADMIN	= "adminCredential";	
		}
		
		public static final class Security {
			private Security() {}
			public static final String ADMIN	= "adminSecurity";	
		}
		
	}
	
}
