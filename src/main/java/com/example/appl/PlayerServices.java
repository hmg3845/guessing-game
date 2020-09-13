package com.example.appl;


import com.example.model.GuessGame;
import com.example.model.GuessGame.GuessResult;

/**
 * The object to coordinate the state of the Web Application.
 *
 * This class is an example of the GRASP Controller principle.
 *
 * @author <a href='mailto:jrv@se.rit.edu'>Jim Vallino</a>
 */

public class PlayerServices {

  //
  // Constants
  //

  final static String NO_WINS_MESSAGE = "You have not won a game, yet. But I *feel* your luck changing.";
  final static String GAMES_PLAYED_FORMAT = "You have won an average of %.1f%% of this session's %d game.";
  final static String NO_STAT = "No game stats yet"; //added criteria #1 string

  //
  // Attributes
  //

  // This player's game. There is only one game at a time allowed.
  private GuessGame game;
  // The gameCenter provides sitewide features for all the games and players.
  private final GameCenter gameCenter;

  private int seshGamesNum = 0; //gotta keep track for later on
  private int seshGamesWon = 0;

  /**
   * Construct a new {@Linkplain PlayerServices} but wait for the player to want to start a game.
   *
   * @param gameCenter
   *    the {@Link GameCenter} that has sitewide responsibilities
   */
  PlayerServices(GameCenter gameCenter) {
    game = null;
    this.gameCenter = gameCenter;
  }

  /**
   * Get the current game that the player is playing. Create one if a game has not been started.
   *
   * @return GuessGame
   *    the current game being played
   */
  public synchronized GuessGame currentGame() {
    if(game == null) {
      game = gameCenter.getGame();
    }
    return game;
  }

  /**
   * Indicates that the player is finished with this game.
   */
  public void finishedGame() {
    game = null;
  }

  /**
   * The player makes a guess of the secret number.
   *
   * @param guess
   *      The number guessed
   *
   */
  public synchronized GuessResult makeGuess(int guess) {
    GuessResult result = game.makeGuess(guess);
    if (game.isFinished()) {

      seshGamesNum++; //gotta add so we know that the game has been played
      gameCenter.gameFinished();
    }
    if (game.isWon()) {
      seshGamesWon++; //adding to the num if player won
      gameCenter.setGamesWon();
    }
    return result;
  }

  /**
   * Cleanup the @Linkplain{PlayerServices} object when the session expires.
   * The only cleanup will be to remove the game.
   */
  public void endSession() { game = null; }

  /**
   * Is the player starting a new game?
   *
   * @return true if the player has just started a game
   *
   */
  public boolean isStartingGame() {
    return game.isGameBeginning();
  }

  /**
   * Does the player still have more guesses in the current game?
   *
   * @return true if the player still has guesses left in this game
   */
  public boolean hasMoreGuesses() {
    return game.hasMoreGuesses();
  }

  /**
   * How many guesses does the player have left in this game?
   *
   * @return the number of guesses the player has left in the current game
   *
   */
  public int guessesLeft() {
    return game.guessesLeft();
  }

  //This bit was just me copying from GameCenter and changing the vars to suit this area

  public synchronized String getGameStatsMessage() {
    if (seshGamesNum > 1) {
      //time for percent math
      float percent = ((float) seshGamesWon / (float) seshGamesNum) * 100;
      return String.format(GAMES_PLAYED_FORMAT, seshGamesNum, percent);

    } else if (seshGamesNum == 0) {
      return NO_STAT;
    } else {
      return NO_WINS_MESSAGE;
    }
  }

}
