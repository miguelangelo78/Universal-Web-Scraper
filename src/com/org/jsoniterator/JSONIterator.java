package com.org.jsoniterator;

import java.util.HashMap;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class JSONIterator{
	public JSONIterator(){}
	
	enum ITMode{ PRINT, UPDATE, FIND };
	
	private static ITMode mode = ITMode.PRINT;
	private static Document html_doc; // For scraping the web
	
	@SuppressWarnings("unchecked")
	private static void iterate_inner(Object obj, Object[] parent_handle, int child_index){
		if(obj instanceof HashMap){
			
			for(String key: ((HashMap<String, Object>)(obj)).keySet()){
				Object val = ((HashMap<String, Object>)(obj)).get(key);
				
				if(val instanceof HashMap  || val instanceof Object[]){
					// Found another hashmap or array:
					if(mode == ITMode.PRINT)
						System.out.println(key+" : "+val);
					else if(mode == ITMode.UPDATE){
						// Don't update anything here, it just found an hashmap/array
					}
					iterate(val);
				}
				else // End of the road, found value
					if(mode == ITMode.PRINT)
						System.out.println(key+" : "+val);
					else if(mode == ITMode.UPDATE){
						((HashMap<String, Object>)(obj)).put(key, html_doc.select((String) val).toArray());
						
					}
				}
		} else {
			// It's just values inside the Object array. End of the road:
			if(mode == ITMode.PRINT)
				System.out.print(obj+", ");
			else if(mode == ITMode.UPDATE){
				parent_handle[child_index] = html_doc.select((String) obj).toArray().toString();
				
			}
		}
	}
	
	private static void iterate(Object arr){
		if(arr instanceof Object[])
			for(int i=0;i<((Object[])arr).length;i++){
				
				iterate_inner(((Object[])arr)[i], (Object[])arr, i);
			}
		else if(arr instanceof HashMap)
			iterate_inner(arr, null,  0); // Don't need the parent handle here
	}
	
	public static void print(Object arr){
		mode = ITMode.PRINT;
		iterate(arr);
	}
		
	public static void update(Object target, Document document){
		mode = ITMode.UPDATE;
		html_doc = document;
		iterate(target);
	}
	
	public static String simpleArray(Object [] arr, boolean add_slashes){
		String strbuild = "[";
		int last = 0;
		for(Object obj: arr){
			if(add_slashes){
				obj = ((Element)obj).toString().replaceAll("(?<!\\\\)\"", "\\\"");
				obj = ((String) obj).replaceAll("\n",""); // Remove new lines
			}
			
			strbuild+="\""+obj+ ((last++ < arr.length-1) ? "\", ":"\"");
		}
		return strbuild+"]";
	}
}
