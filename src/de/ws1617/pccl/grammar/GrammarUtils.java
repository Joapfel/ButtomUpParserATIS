package de.ws1617.pccl.grammar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class GrammarUtils {

	/**
	 * Reads a grammar from a file into a grammar data structure.
	 * 
	 * @param path the path to the grammar text file.
	 * @return a Grammar object for further processing.
	 * @throws IOException
	 */
	public static Grammar readGrammar(String path) throws IOException {

		Grammar gr = new Grammar();

		String line = "";
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path))));
		
		//lhs is assigned after a empty line
		NonTerminal lhsSymb = null;
		//rhs is assigned after the first line of a block
		ArrayList<Symbol> rhsList = null;
	
		boolean firstLine = true;
		
		while ((line = br.readLine()) != null) {
		
			//operation on lhs
			if(line.trim().equals("") || firstLine){
				
				//needed only for the first block
				firstLine = false;
				
				//if it was empty read the next line which is lhs
				line = br.readLine();
				
				//it should be only one word so no need to split
				String lhs = line.trim();
				
				// the rule left hand side
				lhsSymb = new NonTerminal(lhs);
				
				//go to the next line
				line = br.readLine();
				
			}
			if(!line.trim().equals("")){
				// iterate over rhs
				String[] rhsSplit = line.split("\\s+");
				rhsList = new ArrayList<>();
				for (int i = 0; i < rhsSplit.length; i++) {
					String symb = rhsSplit[i];
					Symbol s = new NonTerminal(symb);
					rhsList.add(s);
				}
				
			}
			gr.addRule(lhsSymb, rhsList);
		}
		br.close();

		return gr;
	}

	/**
	 * Reads lexical rules into a lexicon data structure.
	 * 
	 * @param path the path to the lexicon text file.
	 * @return a Lexicon object for further processing.
	 * @throws IOException
	 */
	public static Lexicon readLexicon(String path) throws IOException {
		Lexicon lex = new Lexicon();

		String line = "";
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path))));

		while ((line = br.readLine()) != null) {
			if (!line.trim().equals("")) {
				String[] splt = line.split("\\s+");
				
				//lexical item in this case
				String lhs = splt[0].trim();
				//the corresponding symbol
				String rhs = splt[1].trim();

				// the rule left hand side
				NonTerminal rhsSymb = new NonTerminal(rhs);

				String[] lhsSplit = lhs.split("\\s+");
				ArrayList<Terminal> lhsList = new ArrayList<>();

				for (int i = 0; i < lhsSplit.length; i++) {
					lhsList.add(new Terminal(lhsSplit[i]));
				}
				lex.addRule(rhsSymb, lhsList);
			
			}
		}
		br.close();

		return lex;
	}
}
