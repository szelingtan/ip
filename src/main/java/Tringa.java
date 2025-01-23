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
            try {
                // EmptyInputException
                if (input.isEmpty()) {
                    throw new EmptyInputException("Input cannot be empty. ");
                }
                // List Command
                else if (input.equals("list")) {
                    System.out.println("Here are the tasks in your list:");
                    int i = 1;
                    for (Task e : lst) {
                        System.out.println(i + "." + e.toString());
                        i++;
                    }
                // Mark Command
                } else if (input.startsWith("mark")) { //mark command
                    // create string array
                    String[] parts = input.split(" ");
                    int index = Integer.valueOf(parts[1]);
                    Task change = lst.get(index - 1);
                    change.markDone();
                    System.out.println("Nice! I've marked this task as done:");
                    System.out.println("  " + change.toString());
                } else if (input.startsWith("delete")) {
                    String[] parts = input.split(" ");
                    int index = Integer.valueOf(parts[1]);
                    Task deletedTask = lst.get(index - 1);
                    lst.remove(index - 1); // remove specified task
                    int num = lst.size(); // total number of tasks in list
                    System.out.println("Noted. I've removed this task:");
                    System.out.println("  " + deletedTask.toString());
                    System.out.println("Now you have " + num + " tasks in the list.");
                } else {
                    // ToDOs Command
                    if (input.startsWith("todo")) {
                        //InvalidToDoException
                        if (input.equals("todo")) {
                            throw new InvalidToDoException("Entered empty ToDo. ");
                        }
                        input = input.substring(input.indexOf(" ") + 1);
                        t = new ToDos(input);
                        // Deadline Command
                    } else if (input.startsWith("deadline")) {
                        input = input.substring(input.indexOf(" ") + 1);
                        String[] parts = input.split("/by");
                        String desc = parts[0]; // description for deadline task
                        String deadline = parts[1]; // deadline for deadline task
                        t = new Deadline(desc, deadline);
                        // Event Command
                    } else if (input.startsWith("event")) {
                        input = input.substring(input.indexOf(" ") + 1);
                        // Split the string twice
                        String[] parts = input.split("/from");
                        String desc = parts[0];
                        String[] partsTwo = parts[1].split("/to");
                        String start = partsTwo[0]; // start of event
                        String end = partsTwo[1]; // end of event
                        t = new Event(desc, end, start);
                    } else {
                        throw new InvalidCommandException(input);
                    }
                    System.out.println("Got it. I've added this task: ");
                    lst.add(t); // add task to the list
                    System.out.println("  " + t.toString());
                    int num = lst.size(); // total number of tasks in list
                    System.out.println("Now you have " + num + " tasks in the list.");
                }
            } catch (EmptyInputException e) {
                System.out.print(e.getMessage());
            } catch (InvalidToDoException e) {
                System.out.println(e.getMessage());
                input = sc.nextLine();
                continue;
            } catch (InvalidCommandException e) {
                System.out.println(e.getMessage());
                input = sc.nextLine();
                continue;
            }
            input = sc.nextLine();
        }
        System.out.println("Bye. Hope to see you again soon!");
    }
}


