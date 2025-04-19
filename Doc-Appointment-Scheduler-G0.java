import java.util.*;  // Import utility classes like Scanner, PriorityQueue, ArrayList, etc.

 // Patient class that stores patient information and defines priority for scheduling. 
class Patient implements Comparable<Patient> {
    private String name;
    private int priority; // Lower numbers mean higher priority
    // Constructor to initialize patient name and priority
    public Patient(String name, int priority) {
        this.name = name;
        this.priority = priority;
    }
    // Getter method for patient name
    public String getName() {
        return name;
    }
    // Getter method for priority level
    public int getPriority() {
        return priority;
    }

    // Compare patients based on priority for use in PriorityQueue
    @Override
    public int compareTo(Patient other) {
        return Integer.compare(this.priority, other.priority); // lower value = higher priority
    }
}

//Scheduler class to manage appointment scheduling.
class Scheduler {
    private PriorityQueue<Patient> priorityQueue = new PriorityQueue<>(); // Queue to store urgent patients
    private List<TimeSlot> schedule = new ArrayList<>(); // List to keep all scheduled time slots

    //  To represent a time slot for appointments
    private static class TimeSlot {
        int startTime, endTime; // Appointment time range
        String patientName;

        public TimeSlot(int startTime, int endTime, String patientName) {
            this.startTime = startTime;
            this.endTime = endTime;
            this.patientName = patientName;
        }

        // To check if a new appointment time conflicts with this slot
        public boolean isConflict(int newStart) {
            return newStart >= startTime && newStart < endTime;
        }

        // To display the time slot in a readable format
        @Override
        public String toString() {
            return String.format("%s: %d:00 - %d:00", patientName, startTime, endTime);
        }
    }

    // Check for conflicts in the full schedule
    private boolean isConflict(int newStart) {
        for (TimeSlot slot : schedule) {
            if (slot.isConflict(newStart)) return true;
        }
        return false;
    }
    // Add an urgent patient to the priority queue
    public void addPriorityPatient(Patient patient) {
        priorityQueue.offer(patient);
    }

    //Schedule all patients in the priority queue based on urgency. 
    //Ensures no time conflict, starting from 9:00 AM.
    public void schedulePriorityPatients() {
        int currentTime = 9; // Start scheduling from 9 AM

        // Keep scheduling while patients are in the queue
        while (!priorityQueue.isEmpty()) {
            Patient p = priorityQueue.poll(); // Get highest priority patient

            // Skip to next hour until we find an available time
            while (isConflict(currentTime)) {
                currentTime++;
            }

            // Add the appointment slot
            schedule.add(new TimeSlot(currentTime, currentTime + 1, p.getName()));

            // Print confirmation message
            System.out.printf("Priority Scheduled: %s from %d:00 to %d:00 (Priority %d)%n",
                    p.getName(), currentTime, currentTime + 1, p.getPriority());

            currentTime++; // Move to the next available hour
        }
    }

    //Schedule a non-urgent (standard) appointment at a preferred time if available.
    public void addStandardAppointment(String name, int preferredStart) {
        int end = preferredStart + 1;

        if (!isConflict(preferredStart)) {
            // If no conflict, schedule it
            schedule.add(new TimeSlot(preferredStart, end, name));
            System.out.printf("Standard Scheduled: %s from %d:00 to %d:00%n", name, preferredStart, end);
        } else {
            // Inform user of a conflict
            System.out.println(" Time slot is taken. Please choose another time.");
        }
    }

    // Display all scheduled appointments in order
    public void displaySchedule() {
        System.out.println("\n Final Schedule:");
        // Sort slots based on their starting time
        schedule.sort(Comparator.comparingInt(slot -> slot.startTime));
        for (TimeSlot slot : schedule) {
            System.out.println(slot);
        }
    }
}
//Main class to run the scheduling system.
public class ClinicScheduler {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);  // To read user input
        Scheduler scheduler = new Scheduler();     // Create a new Scheduler

        System.out.println("=== Doctor Appointment Scheduler (1-Hour Sessions) ===");

        boolean more = true;

        // Keep prompting for appointments while user wants to add more
        while (more) {
            System.out.print("\nEnter patient name: ");
            String name = scanner.nextLine();

            System.out.print("Is this an urgent case? (yes/no): ");
            String isUrgent = scanner.nextLine().toLowerCase();

            if (isUrgent.equals("yes")) {
                // For urgent patients, ask for priority level
                System.out.print("Enter priority level (1 = High, 5 = Low): ");
                int priority = Integer.parseInt(scanner.nextLine());
                scheduler.addPriorityPatient(new Patient(name, priority));
            } else {
                // For standard patients, ask for a preferred time
                System.out.print("Enter preferred start time (e.g., 10 for 10:00AM): ");
                int preferredStart = Integer.parseInt(scanner.nextLine());
                scheduler.addStandardAppointment(name, preferredStart);
            }

            // Ask if user wants to add another appointment
            System.out.print("Add another appointment? (yes/no): ");
            more = scanner.nextLine().toLowerCase().startsWith("y");
        }

        // After input is complete, schedule all priority patients
        scheduler.schedulePriorityPatients();

        // Display the final schedule to the user
        scheduler.displaySchedule();
    }
}
