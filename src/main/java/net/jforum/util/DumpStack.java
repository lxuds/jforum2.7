package net.jforum.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class DumpStack {

	public static void dumpStack() {
		
		BufferedWriter out = null;

		StackTraceElement[] st_elements = Thread.currentThread().getStackTrace();


		try {
		    FileWriter fstream1 = new FileWriter("/Users/lxu/project/jforum2.7/printout/stack_trace.txt", true); //true tells to append data.
		    out = new BufferedWriter(fstream1);
		    
		    out.append("-trace-element========================");
			for (StackTraceElement e : st_elements) {
				out.append(e.toString()+ '\n');
			}
			out.close();
		}
		

		catch (IOException e) {
		    System.err.println("Error: " + e.getMessage());
		}


	}
	
	public static void dumpText(String info) {
		BufferedWriter out = null;

		try {
		    FileWriter fstream1 = new FileWriter("/Users/lxu/project/jforum2.7/printout/stack_trace.txt", true); //true tells to append data.
		    out = new BufferedWriter(fstream1);
		    
		    out.append("-text-info ========================");
			out.append(info + "\n");
			out.close();
		}
		

		catch (IOException e) {
		    System.err.println("Error: " + e.getMessage());
		}

	}
}
