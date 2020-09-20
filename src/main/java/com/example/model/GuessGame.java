package com.example.model;

import java.util.Random;
import java.util.logging.Logger;

/**
 * A single "guessing game".
 *
 * @author <a href='mailto:bdbvse@rit.edu'>Bryan Basham</a>
 * @author <a href='mailto:jrv@se.rit.edu'>Jim Vallino</a>
 */
public class GuessGame {
  private static final Logger LOG = Logger.getLogger(GuessGame.class.getName());

  // The results for a player making a guess.
  public enum GuessResult {INVALID, WRONG, WON, LOST, HIGHER, LOWER}

  //
  // Constants
  //

  /**
   * The upper bound on the numbers to be guessed. This is public so that other
   * components to read it; especially for unit testing.
   */
  public static final int UPPER_BOUND = 10;
  public static final int UPPER_BOUND_DIFF = 25; //added for difficulty enhancement

  /**
   * The number of guess attempts alloted. This is public so that other
   * components to read it; especially for unit testing.
   */
  public static final int NUM_OF_GUESSES = 3;
  public static final int NUM_OF_GUESSES_MODERATE = 5; //added for difficulty enhancement

  private static final Random RANDOM = new Random();

  //
  // Attributes
  //

  private final int numberToGuess;
  private GuessResult lastResult = null;
  private int howManyGuessesLeft;
  private int game_type;
  private int bound;

  //
  // Constructors
  //

  /**
   * Create a guessing game with a known number.
   *
   * @param diff
   *          The player's selected difficulty.
   *
   * @throws IllegalArgumentException
   *    when the {@code numberToGuess} is out of range.
   */
  public GuessGame(final int diff) { //CHANGE 1: added actually difficulty edits for bounds and guess num
    game_type = diff;
    //bound = 0;

    if (diff == 1) {
      howManyGuessesLeft = NUM_OF_GUESSES; //standard
      bound = UPPER_BOUND;
    }
    else if (diff == 2) {
      howManyGuessesLeft = NUM_OF_GUESSES_MODERATE; //moderate
      bound = UPPER_BOUND_DIFF;
    }
    else {
      howManyGuessesLeft = NUM_OF_GUESSES_MODERATE; //difficult
      bound = UPPER_BOUND;
    }

    final int numberToGuess = RANDOM.nextInt(bound); //made into final int cuz line 85 kept getting made and idk about the temps

    // validate arguments
    if (numberToGuess < 0 || numberToGuess >= UPPER_BOUND) {
      throw new IllegalArgumentException("numberToGuess is out of range");
    }
    //
    LOG.fine("Game created " + numberToGuess);
    this.numberToGuess = numberToGuess;
  }

  //CHANGE: Got rid of "making a game with a random number", games are dependent on the difficulty the player chooses

  //
  // Public methods
  //

  /**
   * Queries whether the game is at the beginning; meaning no guesses have yet
   * been made.
   *
   * @return true if no guesses have been made, otherwise, false
   */
  public synchronized boolean isGameBeginning() {
    if (game_type != 2) { //not moderate
      return howManyGuessesLeft == NUM_OF_GUESSES;
    }
    return howManyGuessesLeft == NUM_OF_GUESSES_MODERATE;
  }

  /**
   * Queries whether the supplied guess is a valid guess. This does not actually
   * issue a guess.
   *
   * @param guess
   *          The hypothetical guess.
   *
   * @return true if the guess falls within the game bounds, otherwise, false
   */
  public boolean isValidGuess(int guess) {
    if (game_type == 1) { //is standard
      return guess >= 0 && guess < UPPER_BOUND;
    }
    return guess >= 0 && guess < UPPER_BOUND_DIFF;
  }

  /**
   * Makes a guess on the game.
   *
   * @param myGuess
   *          The user's guess.
   *
   * @throws IllegalStateException
   *    when a guess is made on a game that is finished
   *
   * @return a {@link GuessResult} indicating the result of this guess
   */
  public synchronized GuessResult makeGuess(final int myGuess) {
    final GuessResult thisResult;
    int bound; //cuz we have difficulties now

    if (game_type == 1) {
      bound = UPPER_BOUND;
    }
    else {
      bound = UPPER_BOUND_DIFF;
    }

    // validate arguments
    if (myGuess < 0 || myGuess >= bound) { //CHANGE: UPPER_BOUND -> bound (legit seems to be only change needed)
      thisResult = GuessResult.INVALID;
    } else {
      // assert that the game isn't over
      if (howManyGuessesLeft == 0) {
        throw new IllegalStateException("No more guesses allowed.");
      }
      // mark this guess
      howManyGuessesLeft--;
      // decide if this game is finished
      final boolean isCorrect = myGuess == numberToGuess;
      if (isCorrect) {
        thisResult = GuessResult.WON;
      } else if (hasMoreGuesses()) {
        if (myGuess > numberToGuess) { //FIX: Changed "<" to ">" (AKA LOCAL DUMBASS MIXES UP SIGNS)
          thisResult = GuessResult.LOWER; //HINTS
        }
        else {
          thisResult = GuessResult.HIGHER;
        }
      } else {
        thisResult = GuessResult.LOST;
      }
    }
    lastResult = thisResult;
    return thisResult;
  }

  /**
   * Queries whether the game is finished.
   *
   * @return true if the game was won or lost with the last guess
   */
  public synchronized boolean isFinished() {
    return GuessResult.WON.equals(lastResult) || GuessResult.LOST.equals(lastResult);
  }

  public synchronized boolean isWon() { //for counting wins later
    return GuessResult.WON.equals((lastResult));
  }

  /**
   * Queries whether the user has more guesses for this game.
   *
   * @return true if there are more guesses to be made, otherwise, false
   */
  public synchronized boolean hasMoreGuesses() {
    return howManyGuessesLeft > 0;
  }

  /**
   * Queries for the number of guesses left in the game.
   *
   * @return the number of guesses left in this game
   */
  public synchronized int guessesLeft() {
    return howManyGuessesLeft;
  }

  /**
   * Queries for game's bound
   *
   * @return the upper bound of this game
   */

  public synchronized int bound() {
    return bound;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public synchronized String toString() {
    return "{Game " + numberToGuess + "}";
  }
}
