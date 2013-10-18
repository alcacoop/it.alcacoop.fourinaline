package it.alcacoop.fourinaline.gservice;


import it.alcacoop.fourinaline.FourInALine;
import it.alcacoop.fourinaline.fsm.FSM.Events;

import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;


public class GServiceNetHandler {

  private ExecutorService dispatchExecutor;
  private static LinkedBlockingQueue<Evt> queue;
  private int eventRequest;

  private class Evt {
    public Events e;
    public Object o;

    public Evt(Events e, Object o) {
      this.e = e;
      this.o = o;
    }
  }


  private class Dispatcher implements Runnable {
    private Events evt;
    private boolean found;

    public Dispatcher(Events _evt) {
      evt = _evt;
      found = false;
      eventRequest++;
    }

    @Override
    public void run() {
      Evt e = null;
      while (!found) {
        try {
          while (true) {
            e = queue.take();
            if (e.e != null)
              break;
          }
        } catch (InterruptedException e1) {
        }

        if (evt == null) { // PASSO IL PRIMO DISPONIBILE
          FourInALine.Instance.fsm.processEvent(e.e, e.o);
          found = true;
        } else if (evt == e.e) { // PASSO IL PRIMO RICHIESTO DISPONIBILE
          FourInALine.Instance.fsm.processEvent(e.e, e.o);
          found = true;
        }
      }
      eventRequest--;
    }
  }


  public GServiceNetHandler() {
    queue = new LinkedBlockingQueue<Evt>();
    dispatchExecutor = Executors.newSingleThreadExecutor();
    eventRequest = 0;
  }


  // VORREI UN EVT DI TIPO evt...
  public synchronized void pull() {
    pull(null);
  }

  public synchronized void pull(Events evt) {
    dispatchExecutor.submit(new Dispatcher(evt));
  }

  public synchronized void post(Events _e, Object _o) {
    if (_e == null)
      return;
    Evt e = new Evt(_e, _o);
    try {
      queue.put(e);
    } catch (InterruptedException e1) {
      e1.printStackTrace();
    }
    // debug();
  }

  public synchronized void reset() {
    queue.clear();
    dispatchExecutor.shutdownNow();
    dispatchExecutor = Executors.newSingleThreadExecutor();
    eventRequest = 0;
  }

  public synchronized void debug() {
    System.out.println("CODA EVENTI...");
    System.out.println("RICHIESTE IN CODA: " + eventRequest);
    System.out.println("MESSAGGI IN CODA: " + queue.size());
    Iterator<Evt> itr = queue.iterator();
    while (itr.hasNext()) {
      Evt element = itr.next();
      System.out.print("  " + element.e);
    }
  }
}
