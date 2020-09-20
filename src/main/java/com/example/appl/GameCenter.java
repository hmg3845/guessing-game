package com.example.appl;

import java.util.logging.Logger;

import com.example.model.GuessGame;

/**
 * The object to coordinate the state of the Web Application and keep sitewide statistics.
 *
 * This class is an example of the Pure Fabrication principle.
 *
 * @author <a href='mailto:bdbvse@rit.edu'>Bryan Basham</a>
 * @author <a href='mailto:jrv@se.rit.edu'>Jim Vallino</a>
 */
public class GameCenter {
  private static final Logger LOG = Logger.getLogger(GameCenter.class.getName());

  //
  // Constants-
  //

  // Output strings made public for unit test access
  public final static String NO_GAMES_MESSAGE = "No games have been played so far.";
  public final static String ONE_GAME_MESSAGE = "One game has been played so far." + "\nYou have won an average of %.1f%% of this session's %d games.";

  //
  // Attributes
  //

  private int totalGames = 0;
  private int gamesWon = 0;
  private PlayerServices playerServices;

  //
  // Constructors
  //

  //
  // Public methods
  //

  /**
   * Get a new {@Linkplain PlayerServices} object to provide client-specific services to
   * the client who just connected to this application.
   *
   * @return
   *   A new {@Link PlayerServices}
   */
  public PlayerServices newPlayerServices() {
    LOG.fine("New player services instance created.");
    playerServices =  new PlayerServices(this);
    return playerServices; //realised i MAY not be able to sue the getter if i dint change this bit here
  }

  public PlayerServices getPlayerServices() { //gotta make this getter in order to grab in route
        return playerServices;
    }

    /**
   * Create a new {@Linkplain GuessGame} game.
   *
   * @return
   *   A new {@link GuessGame}
   */
  public GuessGame getGame(int diff) {
    return new GuessGame(diff);
  }

  /**
   * Collect sitewide statistics when a game is finished.
   */
  public void gameFinished() {
    // do some application-wide book-keeping
    synchronized (this) {  // protect the critical code
      totalGames++;
    }
  }

  /**
   * Collect sitewide statistics when a game is won.
   */
  public void gameWon() {
    synchronized (this) {
      gamesWon++;
    }
  }

  /**
   * Get a user message about the sitewide statistics.
   *
   * @return
   *   The message to the user about global game statistics.
   */
  public synchronized String getGameStatsMessage() {
    if (totalGames == 1) {
      float percent = ((float) gamesWon / (float) totalGames) * 100;
      return String.format(ONE_GAME_MESSAGE, percent, totalGames); //FIX 0.3: adjusted in order not to skip outputs.
    } else if (totalGames == 0){
      return NO_GAMES_MESSAGE;
    }
    else {
      return "";
    }
  }
}
