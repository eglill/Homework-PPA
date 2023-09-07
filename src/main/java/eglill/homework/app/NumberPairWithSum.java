package eglill.homework.app;

import lombok.Data;

@Data
public class NumberPairWithSum implements Comparable<NumberPairWithSum> {
    private final int number1;
    private final int number2;
    private final int sum;

    public NumberPairWithSum(int number1, int number2) {
        this.number1 = number1;
        this.number2 = number2;
        this.sum = number1 + number2;
    }

    @Override
    public int compareTo(NumberPairWithSum numberPairWithSum) {
        return Integer.compare(sum, numberPairWithSum.getSum());
    }
}