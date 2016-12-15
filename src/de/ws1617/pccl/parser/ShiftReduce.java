package de.ws1617.pccl.parser;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;

import de.ws1617.pccl.grammar.*;

/**
 * A shift-reduce parser. Based on a reference implementation by Veit Rueckert
 * in the PCCL WS13/14.
 * 
 * @author bjoern
 *
 */
public class ShiftReduce {

	// the input to be parsed
	private Terminal[] input;
	// the position in the input
	private int inputIndex;
	// the reduction stack
	private Stack<Symbol> redStack;
	// the analysis stack with rules
	private Stack<Rule> analysisStack;
	// the grammar used for parsing
	private Grammar grammar;
	// the lexicon used for parsing
	private Lexicon lexicon;
	// the start symbol used for parsing
	private NonTerminal startSymbol;

	/**
	 * Creates an empty parser configuration.
	 */
	private ShiftReduce() {
		// for cloning purposes
	}

	/**
	 * Creates a parser configuration with the given parameters.
	 * 
	 * @param input
	 *            the input to be parsed.
	 * @param inputIndex
	 *            the position in the input.
	 * @param redStack
	 *            the reduction stack.
	 * @param analysisStack
	 *            the analysis stack with rules.
	 * @param grammar
	 *            the grammar used for parsing.
	 * @param lexicon
	 *            the lexicon used for parsing.
	 * @param startSymbol
	 *            the start symbol used for parsing.
	 */
	public ShiftReduce(Terminal[] input, int inputIndex, Stack<Symbol> redStack, Stack<Rule> analysisStack,
			Grammar grammar, Lexicon lexicon, NonTerminal startSymbol) {
		super();
		this.input = input;
		this.inputIndex = inputIndex;
		this.redStack = redStack;
		this.analysisStack = analysisStack;
		this.grammar = grammar;
		this.lexicon = lexicon;
		this.startSymbol = startSymbol;
	}

	/**
	 * Generates all successors by shifting or reducing.
	 * 
	 * @return a list of successor parser configurations.
	 */
	public ArrayList<ShiftReduce> successors() {
		ArrayList<ShiftReduce> rval = new ArrayList<>();

		// TODO implement the successors method
		if (!redStack.isEmpty()) {
			rval.addAll(reduce());
		} 
		if (redStack.isEmpty() || inputIndex < input.length) {
			rval.addAll(shift());
		}

		return rval;
	}

	/**
	 * Returns a cloned configuration where the next terminal symbol is shifted.
	 * 
	 * @return a clone where the terminal is shifted and the input pointer
	 *         adjusted.
	 */
	private ArrayList<ShiftReduce> shift() {

		// implement the shift method
				ArrayList<ShiftReduce> clone = new ArrayList<>();
				// clone the current state
				ShiftReduce sr = clone();
				// get the input list from the clone
				Terminal[] terms = sr.getInput();

				// only shift if there remains input after shifting
				if (sr.getInputIndex() < sr.getInput().length) {
					// get the next terminal from the input
					Terminal term = terms[sr.getInputIndex()];
					sr.inputIndex++;
					// push the term on to the reduction stack (of the clone)
					sr.getRedStack().push(term);
					// add the clone to the list above
					clone.add(sr);
					// return the clone list
				}
				return clone;
	}

	/**
	 * Returns a list of successors where reduce operations have been performed.
	 * Reduces with either the lexicon or the grammar.
	 * 
	 * @return a list of adjusted clones where a reduce operations has been
	 *         performed.
	 */
	private ArrayList<ShiftReduce> reduce() {
		ArrayList<ShiftReduce> rval = new ArrayList<>();

		Symbol top = getRedStack().peek();

		if (top instanceof NonTerminal) {

			NonTerminal topNt = (NonTerminal) top;

			// index grammar by rightmost symbol
			HashMap<NonTerminal, HashSet<ArrayList<Symbol>>> rightMost = grammar.getRulesByRightmost(topNt);
			for (NonTerminal nt : rightMost.keySet()) {
				for (ArrayList<Symbol> rhs : rightMost.get(nt)) {

					// clone input configuration
					ShiftReduce sr = this.clone();

					// 1. clone the reduction stack
					Stack<Symbol> prediction = sr.getRedStack();

					// 2. for the length of the rule RHS:
					ArrayList<Symbol> compare = new ArrayList<>();
					for (Symbol s : rhs) {

						if (!prediction.isEmpty()) {
							compare.add(0, prediction.pop());
						}
						// check whether it matches the corresponding rule rhs
						// symbol
						if (compare.equals(rhs)) {
							sr.getAnalysisStack().push(new Rule(nt, rhs));
							prediction.push(nt);
							sr.setRedStack(prediction);
							rval.add(sr);
						}

					}

				}
			}

			// assuming we have a clear grammar/lexicon separation
		} else if (top instanceof Terminal) {
			// lexical reduction
			Terminal topT = (Terminal) top;

			// clone input configuration
			ShiftReduce sr = clone();

			// clone the reduction stack
			Stack<Symbol> prediction = sr.getRedStack();

			//for every lhs
			for(NonTerminal nt : lexicon.getPosTags(topT)){
				
				//make a rhs
				ArrayList<Symbol> rhs = new ArrayList<>();
				//add the top terminal to the rhs
				rhs.add(prediction.pop());
				//add new rule to the analysis stack
				sr.getAnalysisStack().push(new Rule(nt, rhs));
				//push back after reducing
				prediction.push(nt);
				//apply the change of the prediction stack to the reduction stack
				sr.setRedStack(prediction);
				
				//add the ShiftReduce clone to rval
				rval.add(sr);
				
				
			}
		}

		// TODO lexical reduction
		return rval;
	}

	/**
	 * Returns a clone of the current parser configuration. Static objects like
	 * the grammar or lexicon are passed directly, whereare dynamic objects like
	 * the stacks are cloned.
	 */
	public ShiftReduce clone() {
		ShiftReduce sr = new ShiftReduce();

		sr.setInputIndex(getInputIndex());
		sr.setInput(getInput());
		sr.setRedStack((Stack<Symbol>) getRedStack().clone());
		sr.setAnalysisStack((Stack<Rule>) getAnalysisStack().clone());
		sr.setGrammar(getGrammar());
		sr.setLexicon(getLexicon());
		sr.setStartSymbol(getStartSymbol());
		return sr;
	}

	/**
	 * Checks whether the current parser configuration is a goal configuration.
	 * I.e. all input is processed and only the start symbol is on the reduction
	 * stack.
	 * 
	 * @return true if it is a goal.
	 */
	public boolean isGoal() {
		// TODO implement the isGoal method
		return inputIndex == input.length && getRedStack().size() == 1 && getRedStack().peek().equals(startSymbol);
	}

	public Terminal[] getInput() {
		return input;
	}

	public void setInput(Terminal[] input) {
		this.input = input;
	}

	public int getInputIndex() {
		return inputIndex;
	}

	public void setInputIndex(int inputIndex) {
		this.inputIndex = inputIndex;
	}

	public Stack<Symbol> getRedStack() {
		return redStack;
	}

	public void setRedStack(Stack<Symbol> redStack) {
		this.redStack = redStack;
	}

	public Stack<Rule> getAnalysisStack() {
		return analysisStack;
	}

	public void setAnalysisStack(Stack<Rule> analysisStack) {
		this.analysisStack = analysisStack;
	}

	public Grammar getGrammar() {
		return grammar;
	}

	public void setGrammar(Grammar grammar) {
		this.grammar = grammar;
	}

	public Lexicon getLexicon() {
		return lexicon;
	}

	public void setLexicon(Lexicon lexicon) {
		this.lexicon = lexicon;
	}

	public NonTerminal getStartSymbol() {
		return startSymbol;
	}

	public void setStartSymbol(NonTerminal startSymbol) {
		this.startSymbol = startSymbol;
	}

	@Override
	public String toString() {
		return "ShiftReduce [inputIndex=" + inputIndex + ", redStack=" + redStack + ", analysisStack=" + analysisStack
				+ "]";
	}

}
