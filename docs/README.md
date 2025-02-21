# Tringa User Guide

![](Ui.png)


* Do you have a **busy** schedule?
* Do you want a **better** way to manage your schedule?

**Introducing...**  `Tringa`, a bot that _simplifies_ your task 
management journey and is _free_ for all to use!

## Command Use Summary
1. **Find** tasks with a keyword 
2. Add **todo** tasks 
3. Add **deadline** tasks (tasks with a deadline)
4. Add **event** (tasks between a stipulated period)
5. **List** task listings and completion status
6. **Mark** tasks as done
7. **Delete** unwanted tasks
8. View **upcoming tasks**

## Command Format Summary 
1. find KEYWORD
2. todo DESCRIPTION
3. deadline DESCRIPTION /by DATE
4. event DESCRIPTION /from DATE /to DATE
5. list
6. mark INDEX
7. delete INDEX
8. upcoming tasks

## Command Details
The sections below categorise related commands together and
provide more detailed descriptions of each command.

## Task Creation
`todo`

Users can add a todo task to the list in the format:
`todo DESCRIPTION`

Example: `todo finish homework`

Expected Output: 
```
Got it. I've added this task:
 [T][ ] finish homework
Now you have 1 tasks in the list.
```

>If the above example format is not followed, the bot will prompt the user 
to add a todo task in the correct format.

---
`deadline`

Users can add a deadline task to the list in the format: 
`deadline DESCRIPTION /by DATE`

**Date Format: yyyy-mm-dd**

Example: `deadline finish homework /by 2022-01-09`

Expected Output:
```
Got it. I've added this task:
 [D][ ] finish homework (by: Jan 09 2022)
Now you have 2 tasks in the list.
```

>If the above format is not followed, the bot will prompt the user 
to add a deadline task in the correct format.

---

`event`

Users can add an event task to the list in the format 
`event DESCRIPTION /from DATE /to DATE`

**Date Format: yyyy-mm-dd**

Example: `event finish homework /from 2022-01-09 /to 2022-01-12`

Expected Output:
```
Got it. I've added this task:
 [E][ ] finish homework (from: Jan 09 2022 to: Jan 12 2022)
Now you have 3 tasks in the list.
```

>If the above format is not followed, the bot will prompt the user 
to add an event task in the correct format.

---

## Task Search 
`find`

Users can find a task from the list by searching in the format
`find KEYWORD`

**The bot will return all tasks in the current list that contain
the specified keyword.**

Example: `find homework`

>If there are no matching tasks found in the current list, the bot 
will output "No matching tasks found in current list."

---

## Task Deletion
`delete`

Users can delete a task from the list by deleting in the format
`delete INDEX` 

**INDEX refers to the index of the task to be
deleted in the current list.**

Example: `delete 1`

>If the above format is not followed, the bot will prompt the user 
to delete in the correct format.

>If there is no matching index found in the list, the bot will prompt
the user to input an appropriate index.

>If there are no tasks in the list, the bot will return
> "TringaBot Error: No tasks in list!"

---

## Task Mark 
`mark`

Users can mark a task in the list as done by marking in the format
`mark INDEX` 

**INDEX refers to the index of the task to be 
marked in the current list.**

Example: `mark 1`

>If the above format is not followed, the bot will prompt the user 
to delete in the correct format.

>If there is no matching index found in the list, the bot will prompt
the user to input an appropriate index.

>If there are no tasks in the list, the bot will return 
> "TringaBot Error: No tasks in list!"

---

## Task View
Example TaskList Containing: 
1. [T][] finish homework
2. [E][] workshop (from: May 03 2025 to: May 09 2025)

---
`list`

Users can view their tasks details and completion status in their current
list byt inputting `list`

Expected Output (Refer to above list):
```
Here are the tasks in your list
1. [T][] finish homework
2. [E][] workshop (from: May 03 2025 to: May 09 2025)
```

>If there are no tasks in the current list, the bot will output 
"No tasks in your list yet!"

---

`upcoming tasks`

Users can view upcoming tasks in their list by inputting
`upcoming tasks`

**Upcoming tasks includes all deadline tasks which have not been 
marked as done and the deadline of the task has not passed yet.**

**Upcoming tasks also include all event tasks which have not been
marked as done and the event has not started.**

Expected Output (Refer to above list):
```
Here are your upcoming tasks:
1. [E][] workshop (from: May 03 2025 to: May 09 2025)
```

>If there are no valid upcoming tasks, the bot will output 
"No upcoming tasks!"
