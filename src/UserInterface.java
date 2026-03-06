import java.util.Scanner;

public class UserInterface {

    // Constructor
    public UserInterface() {}

    // Method to start the program
    public void start() {
        Scanner input = new Scanner(System.in);

        System.out.println(System.lineSeparator() + "(Consider that threads are working in the meantime. If they have not finished, the outcomes of the analysis may be uncomplete)" + System.lineSeparator());
        while(true){
            System.out.print("Enter an integer number to finish the program: ");
            if (input.hasNextInt()) {
                break;
            } else {
                input.nextLine();
            }
            System.out.println(System.lineSeparator());
        }
        System.out.print(System.lineSeparator());
    }
}
