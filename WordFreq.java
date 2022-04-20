package application;
import java.sql.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A WordFreq object.
 * 
 * This object contains the WordCount method that calculates the word frequency of the given HTML file.
 * @author Kohetsu Coding
 */
public class WordFreq {
	
	/**
	 * A wordCount method.
	 * 
	 * Contains the necessary code to read only the words in an html file and then calculate the frequency of each word used.
	 */
	public void wordCount() {
		System.out.println("Calculating word frequency...");
		
		String text = null;		
		
try {
	try {
		try  {
			String fileName = "https://www.gutenberg.org/files/1065/1065-h/1065-h.htm";
			URL url = new URL(fileName);
			URLConnection conn = url.openConnection();
			LineNumberReader rdr = new LineNumberReader(new InputStreamReader(conn.getInputStream()));
			StringBuilder sb1 = new StringBuilder();
			
			for (text = null; (text = rdr.readLine()) !=null;) {
				if (rdr.getLineNumber() > 11 & rdr.getLineNumber() < 140) {
					sb1.append(text).append(File.pathSeparatorChar);
				} else if (rdr.getLineNumber() > 140) {
					break;
				}
			}
			String textLC = sb1.toString().toLowerCase();
			Pattern pttrn = Pattern.compile("[a-z]+");
			Matcher mtchr = pttrn.matcher(textLC);
			
			TreeMap<String, Integer> freq = new TreeMap<>();
			int longest = 0;
			
			while (mtchr.find()) {
				String word = mtchr.group();
				int letters = word.length();
				
				if (letters > longest) {
					longest = letters;
				}
				if (freq.containsKey(word)) {
					freq.computeIfPresent(word,  (w, c) -> Integer.valueOf(c.intValue() +1));
				}
				else {
					freq.computeIfAbsent(word,  (w) -> Integer.valueOf(1));
				}
			}
			String format = "%-" + longest + "s = %2d%n";
			freq.forEach((k, v) -> System.out.printf(format,  k, v));
			rdr.close();
		
			ArrayList<String> keyList = new ArrayList<String>(freq.keySet());
			String[] freqArray = keyList.toArray(new String[keyList.size()]);
			
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/wordoccurences?characterEncoding=latin1&useConfigs=maxPerformance","root","password123");
			
			String sql = "INSERT INTO wordoccurences.word(word) values (?)";
			
			for (int i = 0; i < 40; i++) {
				PreparedStatement stmt = con.prepareStatement(sql);
				stmt.setString(1, freqArray[i]);
				stmt.executeUpdate();
			}
			
			System.out.println("\nDisplaying values from Database");
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM word");
			while(rs.next())
				
				System.out.println(rs.getString(1));
			
			con.close();
		
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (MalformedURLException ee) {
			ee.printStackTrace();
		}
		  catch (IOException eee) {
			eee.printStackTrace();
		}
	}
}
