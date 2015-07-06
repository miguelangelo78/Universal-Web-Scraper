# Universal-Web-Scraper
Scrapes data from any website provided with a template target for it to fetch.
This project uses the Java libraries JSoup (http://jsoup.org/download),  JSON.Simple (https://code.google.com/p/json-simple/), JSON in Java (http://www.json.org/java/), Selenium (http://www.seleniumhq.org/), PhantomJS (http://phantomjs.org/) and GhostDriver (https://github.com/detro/ghostdriver).


----------

Example code:
``` Java
String url = "http://www.reddit.com/";

String targets = "{'title':'head > title', 'content':'.thing'}"; // These css selectors point to which elements to scrape

WebScraper result = new WebScraper(url, Method.GET, targets); // Scrape the data

result.print(); // Print 100% of data

Element element = result.elem("content", 2); // Select 'content' and get 3rd element
System.out.println(element.text()); // Output the text
System.out.println(element.className()); // Output the class name
```

And if you're feeling like a one liner:
``` Java
Elements[] result = new WebScraper("www.reddit.com", Method.GET, "{'title':'head > title', 'content':'.thing'}").allElems();
```

It also handles authentication:
``` Java
String urlLogin = "https://www.reddit.com/api/login/yourusername/"; // This url is the actual login page which authenticates and returns the session cookies
String urlHome = "http://www.reddit.com/"; // Home url, reddit is being used as an example

WebScraper result = new WebScraper(
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
WebScraper result = new WebScraper(
	urlLogin, 
	urlHome,
	true,
	"{email: youremail, pass: yourpassword, persistent: 1, default_persistent: 1, timezone: -60, locale: pt_PT}",
	"{lsd, lgndim, lgnrnd, lgnjs, qsstamp}"
);

// Set callback:
result.setProperty(WebScraper.Props.ENGINE_GET_CALLBACK, new EngineCallback() {
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

If you don't want to enter manually the parameters of the POST request for authentication, you can do it manually, which also means you don't have do write your password in your code:
``` Java
String urlLogin = "https://www.facebook.com/login.php?login_attempt=1";
String urlHome = "https://www.facebook.com";

WebScraper.manual_auth = true; // Authenticate manually. A firefox window will popup and will wait for you to login on the website
WebScraper result = new WebScraper(urlLogin,urlHome, true);

result.scrape(urlHome, urlLogin, null, "{'html':'html'}");
result.export("C:\\Users\\Me\\Desktop\\out.json", false, true);
```
