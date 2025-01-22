import java.util.*;

public class Tringa {
    public static void main(String[] args) {
        System.out.println("Hello! I'm tringa");
        System.out.println("What can I do for you?");
        Scanner sc = new Scanner(System.in);
        String input = sc.nextLine();
        List<String> lst = new ArrayList<>();
        int count = 1;
        while (!input.equals("bye")) {
            if (input.equals("list")) {
                for (String e : lst) {
                    System.out.println(e);
                }
            } else {
                System.out.println("added: " + input);
                lst.add(count + ". " + input);
                count++;
            }
            input = sc.nextLine();
        }
        System.out.println("Bye. Hope to see you again soon!");
    }
}


