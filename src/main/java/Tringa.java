import java.util.Scanner;

public class Tringa {
    public static void main(String[] args) {
        System.out.println("Hello! I'm tringa");
        System.out.println("What can I do for you?");
        Scanner sc = new Scanner(System.in);
        String input = sc.nextLine();
        while (!input.equals("bye")) {
            System.out.println(input);
            input = sc.nextLine();
        }
        System.out.println("Bye. Hope to see you again soon!");
    }
}
