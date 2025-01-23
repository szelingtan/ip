import java.util.*;

public class Tringa {
    public static void main(String[] args) {
        System.out.println("Hello! I'm tringa");
        System.out.println("What can I do for you?");
        Scanner sc = new Scanner(System.in);
        String input = sc.nextLine();
        // List of Tasks
        List<Task> lst = new ArrayList<>();
        while (!input.equals("bye")) {
            if (input.equals("list")) {
                System.out.println("Here are the tasks in your list:");
                for (Task e : lst) {
                    System.out.println(e);
                }
            } else if (input.startsWith("mark")) {
                // create string array
                String[] parts = input.split(" ");
                int index = Integer.valueOf(parts[1]);
                Task change = lst.get(index - 1);
                change.markDone();
                System.out.println("Nice! I've marked this task as done:");
                System.out.println(change.taskOnly());
            } else {
                System.out.println("added: " + input);
                Task t = new Task(input);
                lst.add(t);
            }
            input = sc.nextLine();
        }
        System.out.println("Bye. Hope to see you again soon!");
    }
}


