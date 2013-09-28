// SIMULATED GAME STATE MACHINE
// From: http://vanillajava.blogspot.com/2011/06/java-secret-using-enum-as-state-machine.html

package it.alcacoop.fourinaline.fsm;

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
    NOOP
  }

  public enum States implements State {
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
