import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

class Expense {
    private Date date;
    private String category;
    private double amount;
    private String description;

    public Expense(Date date, String category, double amount, String description) {
        this.date = date;
        this.category = category;
        this.amount = amount;
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public String getCategory() {
        return category;
    }

    public double getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date) + ", " + category + ", " + amount + ", " + description;
    }
}

public class Main {
    private static final String EXPENSE_FILE = "expenses.txt";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    // Load expenses from the file
    private static List<Expense> loadExpenses() {
        List<Expense> expenses = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(EXPENSE_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(", ");
                Date date = DATE_FORMAT.parse(parts[0]);
                String category = parts[1];
                double amount = Double.parseDouble(parts[2]);
                String description = parts[3];
                expenses.add(new Expense(date, category, amount, description));
            }
        } catch (IOException | ParseException e) {
            System.out.println("No existing expense file found. A new one will be created.");
        }
        return expenses;
    }

    // Save expenses to the file
    private static void saveExpenses(List<Expense> expenses) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(EXPENSE_FILE))) {
            for (Expense expense : expenses) {
                writer.write(expense.toString() + "\n");
            }
        } catch (IOException e) {
            System.out.println("Error saving expenses to file.");
        }
    }

    // Add a new expense
    private static void addExpense(List<Expense> expenses, Scanner scanner) {
        System.out.print("Enter the date (YYYY-MM-DD, or press Enter for today): ");
        String dateInput = scanner.nextLine();
        Date date;
        if (dateInput.isEmpty()) {
            date = new Date();
        } else {
            try {
                date = DATE_FORMAT.parse(dateInput);
            } catch (ParseException e) {
                System.out.println("Invalid date format. Using today's date.");
                date = new Date();
            }
        }

        System.out.print("Enter the category (e.g., Food, Travel, Utilities): ");
        String category = scanner.nextLine();

        System.out.print("Enter the amount: ");
        double amount = scanner.nextDouble();
        scanner.nextLine(); // Consume newline

        System.out.print("Enter a short description: ");
        String description = scanner.nextLine();

        expenses.add(new Expense(date, category, amount, description));
        saveExpenses(expenses);
        System.out.println("Expense added successfully!");
    }

    // View summary for a specific time period
    private static void viewSummary(List<Expense> expenses, String period) {
        Date today = new Date();
        double total = 0;

        for (Expense expense : expenses) {
            long diffInMillis = today.getTime() - expense.getDate().getTime();
            long diffInDays = diffInMillis / (24 * 60 * 60 * 1000);

            if (period.equals("day") && diffInDays == 0) {
                total += expense.getAmount();
            } else if (period.equals("week") && diffInDays < 7) {
                total += expense.getAmount();
            } else if (period.equals("month") && expense.getDate().getMonth() == today.getMonth() &&
                    expense.getDate().getYear() == today.getYear()) {
                total += expense.getAmount();
            }
        }

        System.out.printf("Total expenses for this %s: $%.2f\n", period, total);
    }

    // Main function to run the application
    public static void main(String[] args) {
        List<Expense> expenses = loadExpenses();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nDaily Expense Tracker");
            System.out.println("1. Add Expense");
            System.out.println("2. View Daily Summary");
            System.out.println("3. View Weekly Summary");
            System.out.println("4. View Monthly Summary");
            System.out.println("5. Exit");

            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    addExpense(expenses, scanner);
                    break;
                case "2":
                    viewSummary(expenses, "day");
                    break;
                case "3":
                    viewSummary(expenses, "week");
                    break;
                case "4":
                    viewSummary(expenses, "month");
                    break;
                case "5":
                    System.out.println("Exiting the application. Goodbye!");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
