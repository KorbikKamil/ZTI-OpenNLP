import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        while(true) {
            System.out.println("*=================== Menu ===================*");
            System.out.println("|1 - Przewidywanie rodzaju odpowiedzi        |");
            System.out.println("|2 - Wyjdz                                   |");
            System.out.println("|============================================|");

            Scanner scanner = new Scanner(System.in);
            int menu_val = scanner.nextInt();

            if (menu_val == 1){
                System.out.println("Hello world");
            } else if(menu_val == 2){
                break;
            }
        }
    }
}
