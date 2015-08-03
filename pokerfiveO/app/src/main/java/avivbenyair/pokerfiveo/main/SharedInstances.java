package avivbenyair.pokerfiveo.main;

import avivbenyair.pokerfiveo.bricks.Opponent;
import avivbenyair.pokerfiveo.bricks.Player;
import avivbenyair.pokerfiveo.views.FragmentMenu;
import avivbenyair.pokerfiveo.views.FragmentStage;

/**
 * Created by and-dev on 02/08/15.
 */
public class SharedInstances {


    private boolean inMatch = false;

    private FragmentMenu fragmentMenu;
    private FragmentStage fragmentStage;

    private Opponent opponent;
    private Player player;


    private static SharedInstances sharedInstances;

    public static SharedInstances ParameterData() {
        if (sharedInstances == null)
            sharedInstances = new SharedInstances();
        return sharedInstances;
    }

    public FragmentStage getFragmentStage() {
        return fragmentStage;
    }

    public void setFragmentStage(FragmentStage fragmentStage) {
        this.fragmentStage = fragmentStage;
    }

    public boolean isInMatch() {
        return inMatch;
    }

    public void setInMatch(boolean inMatch) {
        this.inMatch = inMatch;
    }


    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Opponent getOpponent() {
        return opponent;
    }

    public void setOpponent(Opponent opponent) {
        this.opponent = opponent;
    }

    public void setFragmentMenu(FragmentMenu fragmentMenu) {
        this.fragmentMenu = fragmentMenu;
    }

    public FragmentMenu getFragmentMenu() {
        return fragmentMenu;
    }
}
