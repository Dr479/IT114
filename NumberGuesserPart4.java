import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

public class NumberGuesserPart4 {
	private int level = 1;
	private int strikes = 0;
	private int maxStrikes = 5;
	private int number = 0;
	private boolean isRunning = false;
	final String saveFile = "numberGuesserSave.txt";


	public static int getNumber(int level) {
		int range = 9 + ((level - 1) * 5);
		System.out.println("I picked a random number between 1-" + (range + 1) + ", let's see if you can guess.");
		return new Random().nextInt(range) + 1;
	}
/* 
   private void saveNum() {
	try (FileWriter fw = new FileWriter(saveFile)) {
		 fw.write("" + number);// here we need to convert it to a String to record correctly
        //fw.write("\r\n" + number);
        //fw.write("\r\n" + guesses);
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	}
	}
   }
  */
  
	private void win() {
		System.out.println("That's right!");
		level++;// level up!
		saveLevel();
		strikes = 0;
		System.out.println("Welcome to level " + level);
		number = getNumber(level);
      //System.out.println(number); If you win, it will show the answer for next gam
	}

	private void lose() {
		System.out.println("Uh oh, looks like you need to get some more practice.");
		System.out.println("The correct number was " + number);
		strikes = 0;
		level--;
		if (level < 1) {
			level = 1;
		}
		saveLevel();
      //saveStrike(); //NEW
      //saveNum();  //NEW
		number = getNumber(level);
      //System.out.println(number);  If you lose, it will show the answer for next game

	}

	private void processCommands(String message) {
		if (message.equalsIgnoreCase("quit")) {
			System.out.println("Tired of playing? see you next time.");
			isRunning = false;
         saveLevel();
		}
	}

	private void processGuess(int guess) {
		if (guess < 0) {
			return;
		}
		System.out.println("You guessed " + guess);
		if (guess == number) {
			win();
		} else {
			System.out.println("That's wrong");
			strikes++;
			if (strikes >= maxStrikes) {
				lose();
			} else {
				int remainder = maxStrikes - strikes;
				System.out.println("You have " + remainder + "/" + maxStrikes + " attempts remaining");
				if (guess > number) {
					System.out.println("Lower");
				} else if (guess < number) {
					System.out.println("Higher");
				}
			}
		}
	}

	private int getGuess(String message) {
		int guess = -1;
		try {
			guess = Integer.parseInt(message);
		} catch (NumberFormatException e) {
			System.out.println("You didn't enter a number, please try again");

		}
		return guess;
	}

	private void saveLevel() {
		try (FileWriter fw = new FileWriter(saveFile)) {
			fw.write("" + level + " " + strikes + " " + number); 
        // fw.write("" + number);   .nextline
         //fw.write("" + strikes); .nextline
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}

	private boolean loadLevel() {
		File file = new File(saveFile);
		if (!file.exists()) {
			return false;
		}
		try (Scanner reader = new Scanner(file)) {
			while (reader.hasNextLine()) {
				int _level = reader.nextInt();
    
            int strikeFile = reader.nextInt();
            strikes = strikeFile;
            int numberFile = reader.nextInt();
            
            if (numberFile >= 1){
            
               number = numberFile;
               }
				if (_level > 1) {
					level = _level;
					break;
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (Exception e2) {
			e2.printStackTrace();
			return false;
		}
		return level > 1;
	}

	void run() {
		try (Scanner input = new Scanner(System.in);) {
			System.out.println("Welcome to Number Guesser 4.0!");
         
			System.out.println("I'll ask you to guess a number between a range, and you'll have " + maxStrikes
					+ " attempts to guess.");
               
			if (loadLevel()) {
				System.out.println("If you want to play again enter yes ");
            String newGame = input.nextLine();
            
            if (newGame.equals("Yes") || newGame.equals("yes")) {
					level = 1;
					strikes = 0;
					number = getNumber(level);
					System.out.println("Restarting");
					System.out.println("loaded level " + level + " with " + strikes + " strikes");
					
				} else {
					System.out.println("Not restarting.");
					System.out.println("level loaded " + level + " with " + strikes + " strikes");
				}
			} else {
               number = getNumber(level);
			}
			//number = getNumber(level);
			isRunning = true;
			while (input.hasNext()) {
				String message = input.nextLine();
				processCommands(message);
				if (!isRunning) {
					break;
				}
				int guess = getGuess(message);
				processGuess(guess);
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}

	public static void main(String[] args) {
		NumberGuesserPart4 guesser = new NumberGuesserPart4();
		guesser.run();
	}
}