package core.engine;

public class StateMachine {

    private enum State {
        PLAY, STOP,
    }

    private State currentState = State.PLAY;

    public StateMachine(){

    }

    public void changeState(){
        if(this.currentState == State.STOP)
            this.currentState = State.PLAY;
        else
            this.currentState = State.STOP;
    }

    public boolean play(){
        return this.currentState == State.PLAY;
    }

}
