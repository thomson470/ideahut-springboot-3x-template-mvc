package net.ideahut.springboot.template.app;

public final class AppConstants {
	private AppConstants() {}
	
	// Default
	public static class Default {
		private Default() {}
		public static final String TIMEZONE = "+07:00";
		public static final String AUDITOR = "_SYSTEM_";
		public static final String API_ROLE = "PUBLIC";
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
			public static final String COMMON = "commonRedis";
			public static final String ACCESS = "accessRedis";
		}
		
		public static final class Task {
			private Task() {}
			public static final String COMMON = "commonTask";
			public static final String AUDIT = "auditTask";		
			public static final String REST = "restTask";
			public static final String WEB_ASYNC = "webAsyncTask";
		}
		
		public static final class Credential {
			private Credential() {}
			public static final String ADMIN = "adminCredential";	
		}
		
		public static final class Security {
			private Security() {}
			public static final String ADMIN = "adminSecurity";	
		}
		
	}
	
}
