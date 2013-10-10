/**
@startuml
null --> MAIN_MENU
MAIN_MENU --> MAIN_MENU : BUTTON_CLICKED
MAIN_MENU --> MATCH_OPTIONS
MATCH_OPTIONS --> MATCH_OPTIONS : BUTTON_CLICKED
MATCH_OPTIONS --> INIT_GAME
INIT_GAME --> INIT_GAME : START_GAME
INIT_GAME --> LOCAL_TURN
LOCAL_TURN --> LOCAL_TURN : CLICKED_COL
LOCAL_TURN --> LOCAL_TURN : MOVE_END
LOCAL_TURN --> AI_TURN
AI_TURN --> AI_TURN : AI_EVALUETED
AI_TURN --> AI_TURN : MOVE_END
AI_TURN --> LOCAL_TURN
LOCAL_TURN --> CHECK_END_MATCH
CHECK_END_MATCH --> CHECK_END_MATCH : GAME_TERMINATED
CHECK_END_MATCH --> CHECK_END_MATCH : BOARD_RESETTED
CHECK_END_MATCH --> MAIN_MENU
@enduml
 *
 * Result of:
 * $> grep -i "PLANTUML" fsm_single | sed 's/PLANTUML://'
 * on the output
 */

// SIMULATED GAME STATE MACHINE
// From: http://vanillajava.blogspot.com/2011/06/java-secret-using-enum-as-state-machine.html

package it.alcacoop.fourinaline.fsm;

import it.alcacoop.fourinaline.FourInALine;
import it.alcacoop.fourinaline.actors.UIDialog;
import it.alcacoop.fourinaline.fsm.FSM.Events;
import it.alcacoop.fourinaline.gservice.GServiceClient;
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

  private static long waitTime;
  public enum Events {
    RESIGN_GAME,
    NOOP, BUTTON_CLICKED, GAME_TERMINATED, BOARD_RESETTED, MOVE_END, CLICKED_COL, AI_EVALUETED, START_GAME, LEAVE_GAME, LEAVE_MATCH, GSERVICE_READY, GSERVICE_INIT_RATING, GSERVICE_HANDSHAKE, GSERVICE_BYE, GSERVICE_MOVES, PERFORMED_MOVE
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
          case LEAVE_GAME:
            if ((Boolean)params)
              Gdx.app.exit();
            break;
          case BUTTON_CLICKED:
            String btn = ((String)params);
            if (btn.equals("SINGLE PLAYER")) {
              MatchState.matchType = 0;
              FourInALine.fsm.state(States.MATCH_OPTIONS);
            } else if (btn.equals("TWO PLAYERS")) {
              MatchState.matchType = 1;
              FourInALine.fsm.state(States.MATCH_OPTIONS);
            } else if (btn.equals("ONLINE MULTIPLAYER")) {
              if (FourInALine.Instance.nativeFunctions.isNetworkUp()) {
                MatchState.matchType = 2;
                FourInALine.Instance.nativeFunctions.gserviceStartRoom();
              } else {
                UIDialog.getFlashDialog(Events.NOOP, "Network is down - Multiplayer not available");
              }
            } else if (btn.equals("OPTIONS")) {
              FourInALine.fsm.state(States.GAME_OPTIONS);
            } else if (btn.equals("ABOUT..")) {
              UIDialog.getAboutDialog();
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
              FourInALine.fsm.back();
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
              FourInALine.fsm.back();
            } else if (btn.equals("PLAY")) {
              MatchState.firstGame = true;
              MatchState.currentAILevel = (MatchState.AILevel >= 3) ? MatchState.AILevel : MatchState.defaultAIStartLevel;
              FourInALine.fsm.state(States.INIT_GAME);
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
        if (MatchState.matchType == 2) {
          FourInALine.Instance.setScreen(FourInALine.Instance.gameScreen);
          FourInALine.Instance.board.initMatch(MatchState.whoStart);
        } else {
          int[] a = { 1, 2 };
          if (MatchState.firstGame) {
            FourInALine.Instance.setScreen(FourInALine.Instance.gameScreen);
            MatchState.whoStart = new Random().nextInt(2) + 1;
          } else {
            MatchState.whoStart = ((MatchState.whoStart - 1) == 0) ? a[1] : a[0];
          }
          FourInALine.Instance.board.initMatch(MatchState.whoStart);
        }
        super.enterState(ctx);
      }

      @Override
      public boolean processEvent(Context ctx, Events evt, Object params) {
        if (evt == Events.START_GAME) {
          System.out.println("WHO: " + MatchState.whoStart);
          switch (MatchState.matchType) {
            case -1: // SIMULATION
              FourInALine.fsm.state(AI_TURN);
              break;
            case 0: // SINGLE PLAYER
              if (MatchState.whoStart == 1)
                FourInALine.fsm.state(LOCAL_TURN);
              else
                FourInALine.fsm.state(AI_TURN);
              break;
            case 1: // TWO PLAYER
              FourInALine.fsm.state(LOCAL_TURN);
              break;
            case 2: // MULTIPLAYER
              if (MatchState.whoStart == 1)
                FourInALine.fsm.state(LOCAL_TURN);
              else
                FourInALine.fsm.state(REMOTE_TURN);
              break;
            default:
              break;
          }
          return true;
        }
        return false;
      }
    },


    LOCAL_TURN {
      @Override
      public void enterState(Context ctx) {
        if (MatchState.winner == -1) {
          FourInALine.Instance.gameScreen.highlightPlayer(MatchState.currentPlayer);
        }
      }

      @Override
      public boolean processEvent(Context ctx, Events evt, Object params) {
        switch (evt) {
          case CLICKED_COL:
            FourInALine.Instance.board.play((Integer)params);
            break;

          case MOVE_END:
            if (MatchState.matchType == 0)
              FourInALine.fsm.state(AI_TURN);
            else if (MatchState.matchType == 1)
              FourInALine.fsm.state(LOCAL_TURN);
            else if (MatchState.matchType == 2) {
              int col = (Integer)params;
              FourInALine.fsm.state(REMOTE_TURN);
              GServiceClient.getInstance().sendMessage("6 " + col);
            }
            break;

          case LEAVE_MATCH:
            if ((Boolean)params) {
              // LEAVE MATCH
              MatchState.nMatchTo = 0; // DIRTY HACK
              FourInALine.fsm.state(CHECK_END_MATCH);
              FourInALine.fsm.processEvent(Events.GAME_TERMINATED, null);
            }

          default:
            return false;
        }
        return true;
      }

    },

    REMOTE_TURN {
      @Override
      public void enterState(Context ctx) {
        if (MatchState.winner == -1) {
          FourInALine.Instance.gameScreen.highlightPlayer(MatchState.currentPlayer);
        } else {
          GServiceClient.getInstance().queue.pull(Events.GSERVICE_MOVES);
        }
      }

      @Override
      public boolean processEvent(Context ctx, Events evt, Object params) {
        switch (evt) {
          case GSERVICE_MOVES:
            int col = (Integer)params;
            System.out.println("GSERVICE: process" + col);
            FourInALine.Instance.board.play((Integer)col);
            break;

          case PERFORMED_MOVE:
            // ctx.board().updatePInfo();
            // ctx.board().performNextMove();
            break;

          case MOVE_END:
            if (MatchState.matchType == -1)
              FourInALine.fsm.state(AI_TURN);
            else FourInALine.fsm.state(LOCAL_TURN);
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
        if (MatchState.winner == -1) {
          FourInALine.Instance.gameScreen.highlightPlayer(MatchState.currentPlayer);
          FourInALine.Instance.board.playAI();
        }
      }

      @Override
      public boolean processEvent(Context ctx, Events evt, Object params) {
        switch (evt) {
          case AI_EVALUETED:
            FourInALine.Instance.board.play((Integer)params);
            break;

          case MOVE_END:
            if (MatchState.matchType == -1)
              FourInALine.fsm.state(AI_TURN);
            else FourInALine.fsm.state(LOCAL_TURN);
            break;

          default:
            return false;
        }

        return true;
      }
    },

    GSERVICE {
      @Override
      public void enterState(Context ctx) {
        GServiceClient.getInstance().queue.reset();
        MatchState.anScore[0] = 0;
        MatchState.anScore[1] = 0;
        MatchState.nMatchTo = 1;

        GServiceClient.getInstance().sendMessage("2");
        GServiceClient.getInstance().queue.pull(Events.GSERVICE_READY);
      }

      @Override
      public boolean processEvent(Context ctx, Events evt, Object params) {
        switch (evt) {
          case GSERVICE_READY:
            // GServiceClient.getInstance().sendMessage("8 " + FourInALine.Instance.optionPrefs.getString("multiboard", "0"));
            GServiceClient.getInstance().sendMessage("8 0");
            GServiceClient.getInstance().queue.pull(Events.GSERVICE_INIT_RATING);
            break;

          case GSERVICE_INIT_RATING:
            double opponentRating = (Double)params;
            // ELORatingManager.getInstance().setRatings(opponentRating);

            Random gen = new Random();
            waitTime = gen.nextLong();
            GServiceClient.getInstance().sendMessage("3 " + waitTime + " " + FourInALine.Instance.nativeFunctions.getAppVersionCode());
            GServiceClient.getInstance().queue.pull(Events.GSERVICE_HANDSHAKE);
            break;

          case GSERVICE_HANDSHAKE:
            long remoteWaitTime = (Long)params;
            System.out.println("GSERVICE: waitTime=" + waitTime + " remoteWaitTime=" + remoteWaitTime);
            if (waitTime < remoteWaitTime) {
              // Tocca a Local Turn
              MatchState.whoStart = 1;
            } else {
              MatchState.whoStart = 2;
            }
            System.out.println("GSERVICE: whoStart" + MatchState.whoStart);
            FourInALine.fsm.state(States.INIT_GAME);
            FourInALine.fsm.processEvent(Events.START_GAME, null);
            break;

          case GSERVICE_BYE:
            ctx.state(MAIN_MENU);
            break;

          default:
            return false;
        }
        return true;
      }
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
            if ((MatchState.anScore[0] < MatchState.nMatchTo) && (MatchState.anScore[1] < MatchState.nMatchTo)) {
              MatchState.firstGame = false;
              FourInALine.fsm.state(States.INIT_GAME);
              FourInALine.fsm.processEvent(Events.START_GAME, null);
            } else {
              FourInALine.fsm.state(States.MAIN_MENU);
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
    // System.out.println("PLANTUML:" + currentState + " --> " + state() + " : " + evt);
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
    // System.out.println("PLANTUML:" + currentState + " --> " + state);
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
