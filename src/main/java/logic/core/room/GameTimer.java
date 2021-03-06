package logic.core.room;

import java.util.Timer;
import java.util.TimerTask;

abstract class GameTimer {

    final static int firstTurnTime = 10;  //180s 自定义环节
    final static int secondTurnTime = 10;  //60s 战略布局环节
    private final Task turn3 = new Task() {
        @Override
        public void run() {
            onTurn3Finished();
            cancel();
        }
    };
    private final Timer timer;
    private int stage = 0; //游戏回合;
    private final Task turn2 = new Task() {
        @Override
        public void run() {
            onTurn2Finished();
            stage = 3;
            timer.schedule(turn3, 1000);
        }
    };
    private final Task turn1 = new Task() {
        @Override
        public void run() {
            onTurn1Finished();
            stage = 2;
            timer.schedule(turn2, secondTurnTime * 1000);
        }
    };

    GameTimer() {
        timer = new Timer();
    }

//    public static void main(String[] args) {
//        GameTimer gameTimer = new GameTimer() {
//            @Override
//            void onTurn1Finished() {
//                System.out.println("onTurn1Finished");
//            }
//            @Override
//            void onTurn2Finished() {
//                System.out.println("onTurn2Finished");
//            }
//            @Override
//            void onTurn3Finished() {
//                System.out.println("onTurn3Finished");
//            }
//        };
//        gameTimer.start();
//    }

    public void start() {
        stage = 1;
        System.out.println(stage);
        timer.schedule(turn1, firstTurnTime * 1000);
    }

    public int getStage() {
        System.out.println(stage);
        return stage;
    }

    abstract void onTurn1Finished();

    abstract void onTurn2Finished();

    abstract void onTurn3Finished();

    private static class Task extends TimerTask {

        @Override
        public void run() {

        }
    }

}
