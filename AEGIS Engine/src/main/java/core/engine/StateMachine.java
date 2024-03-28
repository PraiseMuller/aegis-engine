package core.engine;

public class StateMachine {

    public enum states {
        PLAY, STOP,
    }

    private static states currentState = states.PLAY;

    private StateMachine(){};

    public static void changeState(){
        if(StateMachine.currentState == states.STOP)
            StateMachine.currentState = states.PLAY;
        else
            StateMachine.currentState = states.STOP;
    }

    public static boolean play(){
        return StateMachine.currentState == states.PLAY;
    }

}
