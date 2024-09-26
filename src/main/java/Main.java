import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ArrayList<Integer> newArray = new ArrayList<Integer>();
        var num = scanner.nextInt();
        ArrayList<Integer> array = new ArrayList<Integer>();
        for (int i = 0; i < num; i++) {
            array.add(scanner.nextInt());
        }
        for (int i = 0; i < num / 2; i++) {
            int price = array.get(array.size() - 1) / 4 * 3;
            newArray.add(Integer.valueOf(price));
            array.remove(array.size() - 1);
            array.remove(Integer.valueOf(price));

        }
        for (int i = 0; i < newArray.size(); i++) {
            System.out.println(newArray.get(newArray.size() - 1 - i));
        }
    }
}

