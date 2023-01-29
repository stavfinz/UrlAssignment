package voyagerlabs;

import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlAssignment {

    public static void main(String[] args) {

        Scanner s = new Scanner(System.in);
        try {
            System.out.println("Please enter URL to start the process with:");
            String urlString = s.nextLine();
            System.out.println(
                    "Please enter The maximal amount of different URLs to extract from the page (Natural Number) :");
            int maxDifferentURLs = s.nextInt();
            System.out.println("Please enter How deep the process should run (depth factor) - (Natural Number) :");
            int depthFactor = s.nextInt();
            System.out.println("Please enter Boolean flag indicating cross-level uniqueness( false / true ) :");
            boolean uniqueness = s.nextBoolean();
            DepthIterations depthIterations = new DepthIterations(urlString, maxDifferentURLs, depthFactor, uniqueness);
            if (depthIterations.isValidAndReachableUrl(urlString) && maxDifferentURLs >= 0 && depthFactor >= 0) {
                depthIterations.doIterations();
            } else {
                System.out.println("URL given not in the right format or reachable OR depthFactor/amountDifferentURLs below 0");
            }
        } catch (InputMismatchException e) {
            System.out.println("Some inputs are incorrect nor in the right format - " + e);
        }
        s.close();
    }

}
