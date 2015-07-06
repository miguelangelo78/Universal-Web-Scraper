package com.org.app;

import com.org.scraper.WebScraper;

public class app {

	public static void main(String[] args) {
		// TODO: ADD CORS BYPASS REQUESTS
		// TODO: ADD FILE HANDLING (IN AND OUT, BOTH WITH ARRAYS OR SINGLE)
		// TODO: ADD A LOGGING CLASS
		// TODO: ADD MORE TYPES OF EXPORT
		
		String urlLogin = "https://www.facebook.com/login.php?login_attempt=1";
		String urlHome = "https://www.facebook.com";
		
		WebScraper.manual_auth = true;
		WebScraper result = new WebScraper(urlLogin,urlHome, true);
		
		result.scrape(urlHome, urlLogin, null, "{'html':'html'}");
		result.export("C:\\Users\\Me\\Desktop\\out.json", false, true);

	}
}
