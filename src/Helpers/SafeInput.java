package Helpers;

import java.util.Scanner;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Pattern;

public class SafeInput {
    Scanner scanner;

    public  SafeInput(Scanner sc) {
        scanner = sc;
    }

    public static String readBetweenBraces(Scanner sc) {
        Pattern delim = sc.delimiter();
        sc.useDelimiter("\\(");
        sc.next();
        sc.useDelimiter("[()]");
        String result = sc.next();
        sc.useDelimiter(delim);
        sc.next("\\)");
        return result;
    }

    public Scanner getScanner() {
        return scanner;
    }

    public <T extends Comparable<? extends Number>> T getValue(String prompt, Supplier<T> getMethod) {
        T result = null;
        do {
            try {
                System.out.println(prompt);
                result = getMethod.get();
            } catch (Exception e) {
                System.out.println("Wrong input: (" + e + "). Try again.");
                scanner.nextLine();
            }
        } while(result == null);

        return result;
    }

    public <N extends Number, T extends Comparable<N>> T getValueWithLimits(String prompt, Supplier<T> getMethod, String tryAgain, N min, N max) {
        while(true) {
            T result = getValue(prompt, getMethod);
            if (result.compareTo(max) <= 0 && result.compareTo(min) >= 0)
                return result;
            System.out.println(tryAgain);
        }
    }

    public int nextInt(String prompt) {
        return getValue(prompt, () -> scanner.nextInt());
    }

    public int nextInt(String prompt, String tryAgain, int min, int max) {
        return this.getValueWithLimits(prompt, () -> scanner.nextInt(), tryAgain, min, max);
    }

    public double nextDouble(String prompt) {
        return getValue(prompt, () -> scanner.nextDouble());
    }

    public double nextDouble(String prompt, String tryAgain, double min, double max) {
        return this.getValueWithLimits(prompt, () -> scanner.nextDouble(), tryAgain, min, max);
    }

    public short nextShort(String prompt) {
        return getValue(prompt, () -> scanner.nextShort());
    }

    public long nextLong(String prompt) {
        return getValue(prompt, () -> scanner.nextLong());
    }

    public String nextLine(String prompt) {
        System.out.println(prompt);
        return scanner.nextLine();
    }

    public void nameInputLoop(String prompt, String success, String failure, Predicate<String> actionCallback) {
        while(true) {
            String inputLine = nextLine(prompt);
            if (inputLine.isEmpty()) {
                return;
            }
            if(actionCallback.test(inputLine)) {
                System.out.println(success);
                return;
            } else {
                System.out.println(failure);
            }
        }
    }
}
