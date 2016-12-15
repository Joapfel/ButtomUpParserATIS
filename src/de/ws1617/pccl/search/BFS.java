package de.ws1617.pccl.search;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import de.ws1617.pccl.grammar.*;
import de.ws1617.pccl.parser.ShiftReduce;

/**
 * Breadht-first search for a {@link ShiftReduce} parser.
 * 
 * @author bjoern
 *
 */
public class BFS {

	/**
	 * Returns a list of successful shift-reduce parse configurations or an
	 * empty set if the sentence is ungrammatical.
	 * 
	 * @param input
	 * @param grammar
	 * @param lexicon
	 * @param startSymbol
	 * @return
	 */
	public static ArrayList<ShiftReduce> search(Terminal[] input, Grammar grammar, Lexicon lexicon,
			NonTerminal startSymbol) {
		// return value
		ArrayList<ShiftReduce> rval = new ArrayList<>();
		// temporary agenda
		Queue<ShiftReduce> agenda = new LinkedList<ShiftReduce>();

		// create a configuration with unprocessed input and the given grammar
		// information
		ShiftReduce initial = new ShiftReduce(input, 0, new Stack<>(), new Stack<>(), grammar, lexicon, startSymbol);
		agenda.add(initial);

		while (!agenda.isEmpty()) {
			ShiftReduce top = agenda.peek();
			if (top.isGoal()) {
				rval.add(top);
			} else {
				ArrayList<ShiftReduce> succs = top.successors();
				for (int i = 0; i < succs.size(); i++) {
					if (succs.get(i).isGoal()) {
						rval.add(succs.get(i));
					} else {
						agenda.add(succs.get(i));
					}
				}
			}
			agenda.poll();
		}

		return rval;
	}

}
