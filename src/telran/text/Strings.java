package telran.text;

import java.util.HashMap;
import java.util.function.BinaryOperator;

import java.util.NoSuchElementException;

public class Strings {
	static HashMap<String, BinaryOperator<Double>> mapOperations;
	static {
		mapOperations = new HashMap<>();
		mapOperations.put("-", (a, b) -> a - b);
		mapOperations.put("+", (a, b) -> a + b);
		mapOperations.put("*", (a, b) -> a * b);
		mapOperations.put("/", (a, b) -> a / b);
	}

	public static String javaVariableName() {
		return "([a-zA-Z$][\\w$]*|_[\\w$]+)";
	}

	public static String zero_300() {
		return "[1-9]\\d?|[1-2]\\d\\d|300|0";
	}

	public static String ipV4Octet() {
		return "([01]?\\d\\d?|2([0-4]\\d|5[0-5]))";
	}

	public static String ipV4() {
		String octetRegex = ipV4Octet();
		return String.format("(%s\\.){3}%1$s", octetRegex);
	}

	public static String arithmeticExpression() {
		String operandRE = operand();
		String operatorRE = operator();
		return String.format("%1$s(%2$s%1$s)*", operandRE, operatorRE);
	}

	public static String operator() {
		return "\\s*([-+*/])\\s*";
	}

	public static String operand() {
		return "(\\d+(\\.\\d+)?|" + javaVariableName() + ")";
	}

	public static boolean isArithmeticExpression(String expression) {
		expression = expression.trim();
		return expression.matches(arithmeticExpression());
	}

	public static double computeExpression(String expression) {
		return computeExpression(expression, new HashMap<>());
	}

	public static double computeExpression(String expression, HashMap<String, Double> mapVariables) {
		if (!isArithmeticExpression(expression)) {
			throw new IllegalArgumentException("Wrong arithmetic expression");
		}
		expression = expression.replaceAll("\\s+", "");
		String[] operands = expression.split(operator());
		String[] operators = expression.split(operand());
		double res = parseOperand(operands[0], mapVariables);
		for (int i = 1; i < operands.length; i++) {
			double operand = parseOperand(operands[i], mapVariables);
			res = mapOperations.get(operators[i]).apply(res, operand);
		}

		return res;
	}

	private static double parseOperand(String operand, HashMap<String, Double> mapVariables) {
		try {
			return Double.parseDouble(operand);
		} catch (NumberFormatException e) {
			if (mapVariables.containsKey(operand)) {
				return mapVariables.get(operand);
			} else {
				throw new NoSuchElementException("Missing value for variable: " + operand);
			}
		}
	}
}
