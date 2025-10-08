package Helpers;

import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class TextMenu {
    public static int menu(String header, String[] options) {
        if (options.length < 1) {
            return -1;
        }
        StringBuilder menuText = new StringBuilder(header + "\n");
        for (int i = 1; i < options.length; i++) menuText.append(i).append(". ").append(options[i]).append("\n");
        menuText.append("0. ").append(options[0]);
        SafeInput si = new SafeInput(new Scanner(System.in));
        return si.nextInt(menuText.toString(), "No such option in the menu", 0, options.length-1);
    }

    public static void menuLoop(String header, String[] options, Runnable[] choiceCallbacks, boolean singleShot) {
        menuLoop(() -> header, () -> options, () -> choiceCallbacks, singleShot);
    }

    public static void menuLoop(String header, String[] options, Supplier<Runnable[]> choiceCallbacks, boolean singleShot) {
        menuLoop(() -> header, () -> options, choiceCallbacks, singleShot);
    }

    public static void menuLoop(String header, Supplier <String[]> options, Runnable[] choiceCallbacks, boolean singleShot) {
        menuLoop(() -> header, options, () -> choiceCallbacks, singleShot);
    }

    public static void menuLoop(Supplier<String> header, String[] options, Runnable[] choiceCallbacks, boolean singleShot) {
        menuLoop(header, () -> options, () -> choiceCallbacks, singleShot);
    }

    public static void menuLoop(Supplier<String> header, Supplier<String[]> options, Supplier<Runnable[]> choiceCallbacks, boolean singleShot) {
        int choice;
        do {
            choice = menu(header.get(), options.get());
            if (choice > 0) {
                choiceCallbacks.get()[choice-1].run();
            }
        } while(choice > 0 && !singleShot);
    }

    public static <T extends Named> void listMenuLoop(String header, String exit, String emptyListMessage, Supplier<List<T>> listSupplier, Consumer<T> choiceCallback, boolean singleShot) {
        List<T> list = listSupplier.get();
        if (list.isEmpty()) {
            System.out.println(emptyListMessage);
            return;
        }
        String[] options = new String[list.size() + 1];
        options[0] = exit;
        Runnable[] callbacks = new Runnable[list.size()];
        for (int i = 0; i < list.size(); i++) {
            T item = list.get(i);
            options[i+1] = item.getName();
            callbacks[i] = () -> choiceCallback.accept(item);
        }
        menuLoop(header, options, callbacks, singleShot);
    }

    public static <T extends Named> void listMenuLoop(String header, String exit, String emptyListMessage, List<T> list, Consumer<T> choiceCallback, boolean singleShot) {
        listMenuLoop(header,exit,emptyListMessage, () -> list, choiceCallback, singleShot);
/*        if (list.isEmpty()) {
            System.out.println(emptyListMessage);
            return;
        }
        String[] options = new String[list.size() + 1];
        options[0] = exit;
        Runnable[] callbacks = new Runnable[list.size()];
        for (int i = 0; i < list.size(); i++) {
            T item = list.get(i);
            options[i+1] = item.getName();
            callbacks[i] = () -> choiceCallback.accept(item);
        }
        menuLoop(header, options, callbacks, singleShot);*/
    }

    public static boolean yesNoQuestion(String question) {
        System.out.println(question + " (y/N)");
        Scanner sc = new Scanner(System.in);
        String response = sc.nextLine();
        return response.trim().toLowerCase().charAt(0) == 'y';
    }
}
