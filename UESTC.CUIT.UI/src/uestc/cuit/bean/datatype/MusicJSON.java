package uestc.cuit.bean.datatype;
import java.util.ArrayList;

public class MusicJSON {
	private String sentence;
	private ArrayList<String> wordlist = new ArrayList<String>();
	
	public String getSentence() {
		return sentence;
	}
	
	public void setSentence(String sentence) {
		this.sentence = sentence;
	}
	public ArrayList<String> getWordlist() {
		return wordlist;
	}
	public void setWordlist(ArrayList<String> wordlist) {
		this.wordlist = wordlist;
	}
}
