// SIMULATED GAME STATE MACHINE
// From: http://vanillajava.blogspot.com/2011/06/java-secret-using-enum-as-state-machine.html

package it.alcacoop.fourinaline.fsm;

import it.alcacoop.fourinaline.FourInALine;
import it.alcacoop.fourinaline.fsm.FSM.Events;
import it.alcacoop.fourinaline.logic.MatchState;

import java.util.Random;

import com.badlogic.gdx.Gdx;


interface Context {
  State state();

  void state(State state);
}

interface State {
  boolean processEvent(Context ctx, Events evt, Object params);

  void enterState(Context ctx);

  void exitState(Context ctx);
}


// MAIN FSM
public class FSM implements Context {

  public enum Events {
    NOOP, BUTTON_CLICKED, GAME_TERMINATED, BOARD_RESETTED, MOVE_END, CLICKED_COL, AI_EVALUETED
  }

  public enum States implements State {

    MAIN_MENU {
      @Override
      public void enterState(Context ctx) {
        FourInALine.Instance.setScreen(FourInALine.Instance.menuScreen);
      }

      @Override
      public boolean processEvent(Context ctx, Events evt, Object params) {
        switch (evt) {
          case BUTTON_CLICKED:
            String btn = ((String)params);
            if (btn.equals("SINGLE PLAYER")) {
              MatchState.matchType = 0;
              FourInALine.Instance.fsm.state(States.MATCH_OPTIONS);
            } else if (btn.equals("TWO PLAYERS")) {
              MatchState.matchType = 1;
              FourInALine.Instance.fsm.state(States.MATCH_OPTIONS);
            } else if (btn.equals("OPTIONS")) {
              FourInALine.Instance.fsm.state(States.GAME_OPTIONS);
            }
            break;
          default:
            return false;
        }
        return true;
      }
    },


    GAME_OPTIONS {
      @Override
      public void enterState(Context ctx) {
        FourInALine.Instance.setScreen(FourInALine.Instance.optionsScreen);
      }

      @Override
      public boolean processEvent(Context ctx, Events evt, Object params) {
        switch (evt) {
          case BUTTON_CLICKED:
            String btn = ((String)params);
            if (btn.equals("BACK")) {
              FourInALine.Instance.fsm.back();
            }
            break;
          default:
            return false;
        }
        return true;
      }
    },


    MATCH_OPTIONS {
      @Override
      public void enterState(Context ctx) {
        FourInALine.Instance.setScreen(FourInALine.Instance.matchOptionsScreen);
      }

      @Override
      public boolean processEvent(Context ctx, Events evt, Object params) {
        switch (evt) {
          case BUTTON_CLICKED:
            String btn = ((String)params);
            if (btn.equals("BACK")) {
              FourInALine.Instance.fsm.back();
            } else if (btn.equals("PLAY")) {
              MatchState.gamesIntoMatch = 1;
              MatchState.currentAILevel = (MatchState.AILevel >= 3) ? MatchState.AILevel : MatchState.defaultAIStartLevel;
              FourInALine.Instance.fsm.state(States.INIT_GAME);
            }
            break;
          default:
            return false;
        }
        return true;
      }
    },


    INIT_GAME {
      @Override
      public void enterState(Context ctx) {
        int[] a = { 1, 2 };
        if (MatchState.gamesIntoMatch == 1) {
          FourInALine.Instance.setScreen(FourInALine.Instance.gameScreen);
          MatchState.whoStart = new Random().nextInt(2) + 1;
        } else {
          MatchState.whoStart = ((MatchState.whoStart - 1) == 0) ? a[1] : a[0];
        }
        FourInALine.Instance.board.initMatch(MatchState.whoStart);

        System.out.println("WHO: " + MatchState.whoStart);
        switch (MatchState.matchType) {
          case -1: // SIMULATION
            FourInALine.Instance.fsm.state(AI_TURN);
            break;
          case 0: // SINGLE PLAYER
            if (MatchState.whoStart == 1)
              FourInALine.Instance.fsm.state(LOCAL_TURN);
            else FourInALine.Instance.fsm.state(AI_TURN);
            break;
          case 1: // TWO PLAYER
            FourInALine.Instance.fsm.state(LOCAL_TURN);
            break;
          default:
            break;
        }
      };
    },


    LOCAL_TURN {
      @Override
      public boolean processEvent(Context ctx, Events evt, Object params) {
        switch (evt) {
          case CLICKED_COL:
            FourInALine.Instance.board.play((Integer)params);
            break;

          case MOVE_END:
            // int gameState = (Integer)params;
            if (MatchState.matchType == 0)
              FourInALine.Instance.fsm.state(AI_TURN);
            else if (MatchState.matchType == 1)
              FourInALine.Instance.fsm.state(LOCAL_TURN);
            break;

          default:
            return false;
        }
        return true;
      }

    },

    AI_TURN {
      @Override
      public void enterState(Context ctx) {
        if (MatchState.winner == -1)
          FourInALine.Instance.board.playAI();
      }

      @Override
      public boolean processEvent(Context ctx, Events evt, Object params) {
        switch (evt) {
          case AI_EVALUETED:
            FourInALine.Instance.board.play((Integer)params);
            break;

          case MOVE_END:
            if (MatchState.matchType == -1)
              FourInALine.Instance.fsm.state(AI_TURN);
            else FourInALine.Instance.fsm.state(LOCAL_TURN);
            break;

          default:
            return false;
        }

        return true;
      }

    },

    REMOTE_TURN {
    },


    CHECK_END_MATCH {
      @Override
      public boolean processEvent(Context ctx, Events evt, Object params) {
        switch (evt) {
          case GAME_TERMINATED:
            FourInALine.Instance.board.reset();
            break;
          case BOARD_RESETTED:
            MatchState.mCount = 0;
            MatchState.currentAILevel = (MatchState.AILevel >= 3) ? MatchState.AILevel : MatchState.defaultAIStartLevel;
            if (MatchState.gamesIntoMatch < MatchState.nMatchTo) {
              System.out.println("Games: " + MatchState.gamesIntoMatch + " of " + MatchState.nMatchTo);
              MatchState.gamesIntoMatch++;
              FourInALine.Instance.fsm.state(States.INIT_GAME);
            } else {
              FourInALine.Instance.fsm.state(States.MAIN_MENU);
            }
            break;
          default:
            return false;
        }
        return true;
      }
    },

    STOPPED {
      @Override
      public void enterState(Context ctx) {
      }
    };

    // DEFAULT IMPLEMENTATION
    public boolean processEvent(Context ctx, FSM.Events evt, Object params) {
      return false;
    }

    public void enterState(Context ctx) {
    }

    public void exitState(Context ctx) {
    }

  };

  public State currentState;
  public State previousState;


  public void start() {
    state(States.MAIN_MENU);
  }

  public void stop() {
    state(States.STOPPED);
  }

  public void restart() {
    stop();
    start();
  }

  public void processEvent(final Events evt, final Object params) {
    final FSM ctx = this;
    // System.out.println("PROCESS " + evt + " ON " + state());
    Gdx.app.postRunnable(new Runnable() {
      @Override
      public void run() {
        state().processEvent(ctx, evt, params);
      }
    });
  }

  public State state() {
    return currentState;
  }

  public void back() {
    if (previousState != null)
      state(previousState);
  }

  public void state(State state) {
    if (currentState != null)
      currentState.exitState(this);
    previousState = currentState;
    currentState = state;
    if (currentState != null)
      currentState.enterState(this);
  }

  public boolean isStopped() {
    return currentState == States.STOPPED;
  }
}
