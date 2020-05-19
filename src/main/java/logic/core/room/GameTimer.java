package logic.core.room;

import java.util.Timer;
import java.util.TimerTask;

abstract class GameTimer extends TimerTask {

    final static int firstTurnTime = 180;  //180s 自定义环节
    final static int secondTurnTime = 60;  //60s 互删环节
    final static int thirdTurnTime = 120;  //120s 战略布局环节

    private int turn = 1; //游戏回合;
    private Timer timer;

    GameTimer() {
        timer = new Timer();
    }

    public void start() {
        timer.schedule(this, firstTurnTime * 1000);
    }

    public int getStage() {
        return turn;
    }

    @Override
    public void run() {
        switch (turn) {
            case 1: {
                onTurn1Finished();
                turn++;
                timer.schedule(this, secondTurnTime * 1000);
                break;
            }
            case 2: {
                onTurn2Finished();
                turn++;
                timer.schedule(this, thirdTurnTime * 1000);
                break;
            }
            case 3: {
                onTurn3Finished();
                break;
            }
            default:
                break;
        }

    }

    abstract void onTurn1Finished();

    abstract void onTurn2Finished();

    abstract void onTurn3Finished();

}
