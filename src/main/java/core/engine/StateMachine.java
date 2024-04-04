package core.engine;

public class StateMachine {

    public enum State {
        PLAY, STOP,
    }

    private static State currentState = State.PLAY;

    private StateMachine(){};

    public static void changeState(){
        if(StateMachine.currentState == State.STOP)
            StateMachine.currentState = State.PLAY;
        else
            StateMachine.currentState = State.STOP;
    }

    public static boolean play(){
        return StateMachine.currentState == State.PLAY;
    }

}
