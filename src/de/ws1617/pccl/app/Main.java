package de.ws1617.pccl.app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

import de.ws1617.pccl.grammar.*;
import de.ws1617.pccl.parser.ShiftReduce;
import de.ws1617.pccl.search.BFS;

public class Main {

	public static void main(String[] args) {
		try {
			// argument check
			if (args.length < 4) {
				System.err.println("required arguments: grammar-file lexicon-file start-symbol input-sentence");
				return;
			}
			
			// read parameters
			Grammar grammar = GrammarUtils.readGrammar(args[0]);
			Lexicon lexicon = GrammarUtils.readLexicon(args[1]);
			NonTerminal startSymbol = new NonTerminal(args[2]);
			String inputStr = args[3];
			Terminal[] input = Terminal.splitString(inputStr, "\\s+");
			
			// search and print results
			ArrayList<ShiftReduce> results = BFS.search(input, grammar, lexicon, startSymbol);
			printAnalyses(results);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Prints out the analysis stack of all successful parses.
	 * 
	 * @param results
	 */
	private static void printAnalyses(ArrayList<ShiftReduce> results) {
		if (results.isEmpty()) {
			System.out.println("Sentence is ungrammatical.");
		} else {
			System.out.println("Found " + results.size() + " results:");
			for (ShiftReduce sr : results) {
				System.out.println("----------------");
				Stack<ShiftReduce> stack = (Stack<ShiftReduce>) sr.getAnalysisStack().clone();
				while (!stack.isEmpty()) {
					System.out.println(stack.pop());
				}
				System.out.println("----------------");
			}
		}
	}
}
