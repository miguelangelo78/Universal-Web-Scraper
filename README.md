# Universal-Web-Scraper
Scrapes data from any website provided with a template target for it to fetch.
This project uses the Java libraries JSoup (http://jsoup.org/download),  JSON.Simple (https://code.google.com/p/json-simple/), JSON in Java (http://www.json.org/java/) and HtmlUnit (http://htmlunit.sourceforge.net/).


----------

Example code:
``` Java
String url = "http://www.reddit.com/";

String targets = "{'title':'head > title', 'content':'.thing'}"; // These css selectors point to which elements to scrape

Scraper result = new Scraper(url, Scraper.Method.GET, targets); // Scrape the data

result.print(); // Print 100% of data

Element element = result.elem("content", 2); // Select 'content' and get 3rd element
System.out.println(element.text()); // Output the text
System.out.println(element.className()); // Output the class name
```

And if you're feeling like a one liner:
``` Java
Elements[] result = new Scraper("www.reddit.com", Scraper.Method.GET, "{'title':'head > title', 'content':'.thing'}").allElems();
```

It also handles authentication:
``` Java
String urlLogin = "https://www.reddit.com/api/login/yourusername/"; // This url is the actual login page which authenticates and returns the session cookies
String urlHome = "http://www.reddit.com/"; // Home url, reddit is being used as an example

Scraper result = new Scraper(
		urlLogin, 
		urlHome,
		Method.GET,
		"{op : login-main, api_type : json, user : yourusername, passwd : yourpassword}", // These are the headers required for the login process
		"{'content':'html'}" // Fetch the root element
);

System.out.println(result); // Output the result after authentication
```

If you want to scrape from a more complex website (like facebook) AND also take a screenshot of it, you can do it like this:

``` Java
String urlLogin = "https://www.facebook.com/login.php?login_attempt=1";
String urlHome = "https://www.facebook.com/";

// Initialize:
Scraper result = new Scraper(
	urlLogin, 
	urlHome,
	true,
	"{email: youremail, pass: yourpassword, persistent: 1, default_persistent: 1, timezone: -60, locale: pt_PT}",
	"{lsd, lgndim, lgnrnd, lgnjs, qsstamp}"
);

// Set callback:
result.setProperty(Scraper.Props.ENGINE_GET_CALLBACK, new EngineCallback() {
	public void after_get(PhantomJS ctx) {
		// Take screenshot of your facebook page
		ctx.take_screenshot("C:\\Users\\Me\\Desktop\\screenshot.png", true);
	}

	public void before_get(PhantomJS ctx) { }
});

// Scrape the whole document:
result.scrape(urlHome, Method.GET, "{'html':'html'}") 
      .export("C:\\Users\\Me\\Desktop\\scraped.json", false, true);

System.out.println("Done scraping");
```
