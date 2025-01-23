import java.util.*;

public class Tringa {
    private static Task t; //task

    public static void main(String[] args) {
        System.out.println("Hello! I'm Tringa.");
        System.out.println("What can I do for you?");
        Scanner sc = new Scanner(System.in);
        String input = sc.nextLine();
        // List of Tasks
        List<Task> lst = new ArrayList<>();
        while (!input.equals("bye")) {
            if (input.equals("list")) { //list command
                System.out.println("Here are the tasks in your list:");
                int i = 1;
                for (Task e : lst) {
                    System.out.println(i + "." + e.toString());
                    i++;
                }
            } else if (input.startsWith("mark")) { //mark command
                // create string array
                String[] parts = input.split(" ");
                int index = Integer.valueOf(parts[1]);
                Task change = lst.get(index - 1);
                change.markDone();
                System.out.println("Nice! I've marked this task as done:");
                System.out.println("  " + change.toString());
            } else {
                //check the different tasks
                if (input.startsWith("todo")) {
                    input = input.substring(input.indexOf(" ") + 1);
                    t = new ToDos(input);
                } else if (input.startsWith("deadline")) {
                    input = input.substring(input.indexOf(" ") + 1);
                    String[] parts = input.split("/by");
                    String desc = parts[0]; // description for deadline task
                    String deadline = parts[1]; // deadline for deadline task
                    t = new Deadline(desc, deadline);
                } else if (input.startsWith("event")) {
                    input = input.substring(input.indexOf(" ") + 1);
                    // Split the string twice
                    String[] parts = input.split("/from");
                    String desc = parts[0];
                    String[] partsTwo = parts[1].split("/to");
                    String start = partsTwo[0]; // start of event
                    String end = partsTwo[1]; // end of event
                    t = new Event(desc, end, start);
                }
                System.out.println("Got it. I've added this task: ");
                lst.add(t); // add task to the list
                System.out.println("  " + t.toString());
                int num = lst.size(); // total number of tasks in list
                System.out.println("Now you have " + num + " tasks in the list.");
            }
            input = sc.nextLine();
        }
        System.out.println("Bye. Hope to see you again soon!");
    }
}


