# Universal-Web-Scraper
Scrapes data from any website provided with a template target for it to fetch.
This project uses the Java libraries JSoup (http://jsoup.org/download) and JSON.Simple (https://code.google.com/p/json-simple/)


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
