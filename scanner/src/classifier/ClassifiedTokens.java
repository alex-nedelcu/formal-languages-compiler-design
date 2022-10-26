package classifier;

import java.util.ArrayList;
import java.util.List;

public class ClassifiedTokens {

    private final String START_SEPARATORS = "--separators";
    private final String END_SEPARATORS = "##separators";
    private final String START_OPERATORS = "--operators";
    private final String END_OPERATORS = "##operators";
    private final String START_KEYWORDS = "--keywords";
    private final String END_KEYWORDS = "##keywords";

    private final List<String> initialTokens;
    private final List<String> keywords;
    private final List<String> operators;
    private final List<String> excluded;

    public List<String> getExcluded() {
        return excluded;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public List<String> getOperators() {
        return operators;
    }

    public List<String> getSeparators() {
        return separators;
    }

    private final List<String> separators;

    public ClassifiedTokens(List<String> initialTokens) {
        this.initialTokens = initialTokens;
        this.keywords = new ArrayList<>();
        this.operators = new ArrayList<>();
        this.separators = new ArrayList<>();
        this.excluded = List.of(START_SEPARATORS, START_KEYWORDS, START_OPERATORS, END_SEPARATORS, END_KEYWORDS, END_OPERATORS);
        initializeClasses();
    }

    private void initializeClasses() {
        int keywordsStart = initialTokens.indexOf(START_KEYWORDS) + 1;
        int keywordsEnd = initialTokens.indexOf(END_KEYWORDS);

        int operatorsStart = initialTokens.indexOf(START_OPERATORS) + 1;
        int operatorsEnd = initialTokens.indexOf(END_OPERATORS);

        int separatorsStart = initialTokens.indexOf(START_SEPARATORS) + 1;
        int separatorsEnd = initialTokens.indexOf(END_SEPARATORS);

        initializeKeywords(keywordsStart, keywordsEnd);
        initializeOperators(operatorsStart, operatorsEnd);
        initializeSeparators(separatorsStart, separatorsEnd);
    }

    private void initializeKeywords(int start, int end) {
        for (int index = start; index < end; index += 1) {
            keywords.add(initialTokens.get(index));
        }
    }

    private void initializeOperators(int start, int end) {
        for (int index = start; index < end; index += 1) {
            operators.add(initialTokens.get(index));
        }
    }

    private void initializeSeparators(int start, int end) {
        for (int index = start; index < end; index += 1) {
            separators.add(initialTokens.get(index));
        }
    }
}
