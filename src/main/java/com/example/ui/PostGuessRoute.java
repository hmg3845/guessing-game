package com.example.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Session;
import spark.TemplateEngine;
import static spark.Spark.halt;

import com.example.appl.GameCenter;
import com.example.appl.PlayerServices;

/**
 * The {@code POST /guess} route handler.
 *
 * @author <a href='mailto:bdbvse@rit.edu'>Bryan Basham</a>
 * @author <a href='mailto:jrv@se.rit.edu'>Jim Vallino</a>
 */
public class PostGuessRoute implements Route {

  //
  // Constants
  //

  // Values used in the view-model map for rendering the game view after a guess.
  static final String GUESS_PARAM = "myGuess";
  static final String MESSAGE_ATTR = "message";
  static final String MESSAGE_TYPE_ATTR = "messageType";
  static final String YOU_WON_ATTR = "youWon";

  static final String ERROR_TYPE = "error";
  static final String BAD_GUESS = "Nope, try again...";
  static final String VIEW_NAME = "game_form.ftl";
  static final String HIGHER = "The number is higher than your guess."; //adding hints
  static final String LOWER = "The number is lower than your guess.";

  //
  // Static methods
  //

  /**
   * Make an error message when the guess is not a number.
   */
  static String makeBadArgMessage(final String guessStr) {
    return String.format("You entered '%s' but that's not a number.", guessStr);
  }

  /**
   * Make an error message when the guess is not in the guessing range.
   */
  static String makeInvalidArgMessage(final String guessStr, int bound) {
    return String.format("You entered %s; make a guess between zero and %d.", guessStr, bound - 1);
  }

  //
  // Attributes
  //

  private final GameCenter gameCenter;
  private final TemplateEngine templateEngine;

  //
  // Constructor
  //

  /**
   * The constructor for the {@code POST /guess} route handler.
   *
   * @param gameCenter
   *    {@Link GameCenter} that holds over statistics
   * @param templateEngine
   *    template engine to use for rendering HTML page
   *
   * @throws NullPointerException
   *    when the {@code gameCenter} or {@code templateEngine} parameter is null
   */
  PostGuessRoute(GameCenter gameCenter, TemplateEngine templateEngine) {
    // validation
    Objects.requireNonNull(gameCenter, "gameCenter must not be null");
    Objects.requireNonNull(templateEngine, "templateEngine must not be null");
    //
    this.gameCenter = gameCenter;
    this.templateEngine = templateEngine;
  }

  //
  // TemplateViewRoute method
  //

  /**
   * {@inheritDoc}
   *
   * @throws NoSuchElementException
   *    when an invalid result is returned after making a guess
   */
  @Override
  public String handle(Request request, Response response) {
    // start building the View-Model
    final Map<String, Object> vm = new HashMap<>();
    vm.put(GetHomeRoute.TITLE_ATTR, GetGameRoute.TITLE);
    vm.put(GetHomeRoute.NEW_PLAYER_ATTR, Boolean.FALSE);

    // retrieve the game object
    final Session session = request.session();
    final PlayerServices playerServices = session.attribute(GetHomeRoute.PLAYERSERVICES_KEY);

    /* A null playerServices indicates a timed out session or an illegal request on this URL.
     * In either case, we will redirect back to home.
     */
    if(playerServices != null) {
      vm.put(GetGameRoute.GAME_BEGINS_ATTR, playerServices.isStartingGame());
      vm.put(GetGameRoute.GUESSES_LEFT_ATTR, playerServices.guessesLeft());
      vm.put(GetGameRoute.BOUND_ATTR, playerServices.getGame().bound()); //for invalid guesses

      // retrieve request parameter
      final String guessStr = request.queryParams(GUESS_PARAM);

      // convert the input
      int guess = -1;
      try {
        guess = Integer.parseInt(guessStr);
      } catch (NumberFormatException e) {
        // re-display the guess form with an error message
        return templateEngine.render(error(vm, makeBadArgMessage(guessStr)));
      }

      // make the guess and create the appropriate ModelAndView for rendering
      ModelAndView mv;
      switch (playerServices.makeGuess(guess)) {
        case INVALID:
          mv = error(vm, makeInvalidArgMessage(guessStr, playerServices.getGame().bound()));
          break;

        case WRONG:
          vm.put(GetGameRoute.GUESSES_LEFT_ATTR, playerServices.guessesLeft());
          mv = error(vm, BAD_GUESS);
          break;

        case WON:
          mv = youWon(vm, playerServices);
          break;

        case LOST:
          mv = youLost(vm, playerServices);
          break;

        case LOWER:
          vm.put(GetGameRoute.GUESSES_LEFT_ATTR, playerServices.guessesLeft()); //HINT CASE
          mv = error(vm, LOWER);
          break;

        case HIGHER:
          vm.put(GetGameRoute.GUESSES_LEFT_ATTR, playerServices.guessesLeft()); //HINT CASE
          mv = error(vm, HIGHER);
          break;

        default:
          // All the GuessResult values are in case statements so we should never get here.
          throw new NoSuchElementException("Invalid result of guess received.");
      }

      return templateEngine.render(mv);
    }
    else {
      response.redirect(WebServer.HOME_URL);
      halt();
      return null;
    }
  }

  //
  // Private methods
  //

  private ModelAndView error(final Map<String, Object> vm, final String message) {
    vm.put(MESSAGE_ATTR, message);
    vm.put(MESSAGE_TYPE_ATTR, ERROR_TYPE);
    return new ModelAndView(vm, VIEW_NAME);
  }

  private ModelAndView youWon(final Map<String, Object> vm, final PlayerServices playerServices) {
    return endGame(true, vm, playerServices);
  }

  private ModelAndView youLost(final Map<String, Object> vm, final PlayerServices playerServices) {
    return endGame(false, vm, playerServices);
  }

  private ModelAndView endGame(final boolean youWonLost, final Map<String, Object> vm,
                               final PlayerServices playerServices) {
    playerServices.finishedGame();
    // report application-wide game statistics
    vm.put(GetHomeRoute.GAME_STATS_MSG_ATTR, gameCenter.getGameStatsMessage());
    vm.put(YOU_WON_ATTR, youWonLost);
    vm.put(GetHomeRoute.PLAYER_STATS_MSG_ATTR,gameCenter.getPlayerServices().getPlayerStatsMessage()); //FIX 1: used my gettign instead of newPlayerServices withc would obvi NOT WORK
    return new ModelAndView(vm, GetHomeRoute.VIEW_NAME);
  }
}
