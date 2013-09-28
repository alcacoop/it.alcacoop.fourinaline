// SIMULATED GAME STATE MACHINE
// From: http://vanillajava.blogspot.com/2011/06/java-secret-using-enum-as-state-machine.html

package it.alcacoop.fourinaline.fsm;

import it.alcacoop.fourinaline.FourInALine;
import it.alcacoop.fourinaline.fsm.FSM.Events;
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
    NOOP,
    BUTTON_CLICKED,
    GAME_TERMINATED
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
            FourInALine.Instance.fsm.state(States.GAME_SCREEN);
            break;
          default:
            return false;
        }
        return true;
      }
    },
    
    GAME_SCREEN {
      @Override
      public void enterState(Context ctx) {
        FourInALine.Instance.setScreen(FourInALine.Instance.gameScreen);
      };
      
      @Override
      public boolean processEvent(Context ctx, Events evt, Object params) {
        switch (evt) {
        case GAME_TERMINATED:
          FourInALine.Instance.fsm.state(States.MAIN_MENU);
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

    //DEFAULT IMPLEMENTATION
    public boolean processEvent(Context ctx, FSM.Events evt, Object params) {return false;}
    public void enterState(Context ctx) {}
    public void exitState(Context ctx) {}

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
    if(previousState != null)
      state(previousState);
  }

  public void state(State state) {
    if(currentState != null)
      currentState.exitState(this);
    previousState = currentState;
    currentState = state;
    if(currentState != null)
      currentState.enterState(this);        
  }

  public boolean isStopped() {
    return currentState == States.STOPPED;
  }
}
